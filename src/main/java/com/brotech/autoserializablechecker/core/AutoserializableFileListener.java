package com.brotech.autoserializablechecker.core;


import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.*;
import com.intellij.util.Alarm;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Optimized file listener that monitors Java files for @AutoSerializable usage.
 * Performance improvements:
 * - Debouncing to avoid processing rapid consecutive changes
 * - Text-based pre-filtering before expensive PSI operations
 * - Uses cached utility for AutoSerializable checks
 * - Respects user settings for enabling/disabling notifications
 */
public class AutoserializableFileListener implements BulkFileListener {
    private final Project project;
    private final ConcurrentHashMap<String, Long> notificationCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lastChangeTime = new ConcurrentHashMap<>();
    private final Alarm alarm = new Alarm();
    private final Set<String> pendingFiles = new HashSet<>();
    
    private static final int DEBOUNCE_DELAY = 1000; // 1 second debounce

    public AutoserializableFileListener(Project project) {
        this.project = project;
    }

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        AutoserializableSettingsState settings = AutoserializableSettingsState.getInstance();
        if (settings == null || !settings.isNotificationsEnabled()) {
            return; // Skip if settings not initialized or notifications disabled
        }
        
        for (VFileEvent event : events) {
            if (event instanceof VFileContentChangeEvent) {
                VFileContentChangeEvent changeEvent = (VFileContentChangeEvent) event;

                // Only process Java files
                if (changeEvent.getFile().getName().endsWith(".java")) {
                    scheduleCheck(changeEvent);
                }
            }
        }
    }

    /**
     * Debounces file checks to avoid processing rapid consecutive changes.
     * This significantly reduces overhead during active typing.
     */
    private void scheduleCheck(VFileContentChangeEvent event) {
        String filePath = event.getFile().getPath();
        long currentTime = System.currentTimeMillis();
        
        lastChangeTime.put(filePath, currentTime);
        
        synchronized (pendingFiles) {
            if (pendingFiles.contains(filePath)) {
                return; // Already scheduled
            }
            pendingFiles.add(filePath);
        }
        
        // Debounce: wait for typing to stop before checking
        alarm.addRequest(() -> {
            synchronized (pendingFiles) {
                pendingFiles.remove(filePath);
            }
            
            Long lastChange = lastChangeTime.get(filePath);
            if (lastChange != null && (currentTime - lastChange) < DEBOUNCE_DELAY) {
                // Still typing, skip this check
                return;
            }
            
            checkForAutoserializable(event);
        }, DEBOUNCE_DELAY);
    }

    private void checkForAutoserializable(VFileContentChangeEvent event) {
        String filePath = event.getFile().getPath();

        // Throttle notifications per file
        Long lastNotification = notificationCache.get(filePath);
        long currentTime = System.currentTimeMillis();
        
        AutoserializableSettingsState settings = AutoserializableSettingsState.getInstance();
        long cooldown = (settings != null) ? settings.getCooldownMs() : 10000L;
        
        if (lastNotification != null && (currentTime - lastNotification) < cooldown) {
            return;
        }

        ApplicationManager.getApplication().runReadAction(() -> {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(event.getFile());

            if (!(psiFile instanceof PsiJavaFile)) {
                return;
            }
            
            PsiJavaFile javaFile = (PsiJavaFile) psiFile;
            
            // Fast pre-check: does the file even contain "Autoserializable" text?
            if (!AutoserializableUtil.mightContainAutoserializable(javaFile)) {
                return; // Skip expensive PSI analysis
            }

            // Now do the full check with caching
            for (PsiClass psiClass : javaFile.getClasses()) {
                if (AutoserializableUtil.isAutoserializable(psiClass)) {
                    notificationCache.put(filePath, currentTime);
                    showNotification(event.getFile().getName(), psiClass.getName());
                    break; // Only notify once per file save
                }
            }
        });
    }

    private void showNotification(String fileName, String className) {
        Notification notification = NotificationGroupManager.getInstance()
                .getNotificationGroup("Autoserializable Warnings")
                .createNotification(
                        "⚠️ Serialization Warning",
                        String.format(
                                "You modified <b>%s</b> (class: %s) which uses @AutoSerializable.<br/>" +
                                        "Please ensure:<br/>" +
                                        "• Backward compatibility is maintained<br/>" +
                                        "• SerialVersionUID is updated if needed<br/>" +
                                        "• Changes are documented",
                                fileName,
                                className
                        ),
                        NotificationType.WARNING
                );

        Notifications.Bus.notify(notification, project);
    }
}
