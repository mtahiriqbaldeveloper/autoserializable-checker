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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AutoserializableFileListener implements BulkFileListener {
    private final Project project;
    private final ConcurrentHashMap<String, Long> notificationCache = new ConcurrentHashMap<>();
    private static final long NOTIFICATION_COOLDOWN = 5000; // 5 seconds

    public AutoserializableFileListener(Project project) {
        this.project = project;
    }

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        for (VFileEvent event : events) {
            if (event instanceof VFileContentChangeEvent) {
                VFileContentChangeEvent changeEvent = (VFileContentChangeEvent) event;

                // Only process Java files
                if (changeEvent.getFile().getName().endsWith(".java")) {
                    checkForAutoserializable(changeEvent);
                }
            }
        }
    }

    private void checkForAutoserializable(VFileContentChangeEvent event) {
        String filePath = event.getFile().getPath();

        // Throttle notifications per file
        Long lastNotification = notificationCache.get(filePath);
        long currentTime = System.currentTimeMillis();
        if (lastNotification != null && (currentTime - lastNotification) < NOTIFICATION_COOLDOWN) {
            return;
        }

        ApplicationManager.getApplication().runReadAction(() -> {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(event.getFile());

            if (psiFile instanceof PsiJavaFile) {
                PsiJavaFile javaFile = (PsiJavaFile) psiFile;

                for (PsiClass psiClass : javaFile.getClasses()) {
                    if (isAutoserializable(psiClass)) {
                        notificationCache.put(filePath, currentTime);
                        showNotification(event.getFile().getName(), psiClass.getName());
                        break;
                    }
                }
            }
        });
    }

    private boolean isAutoserializable(PsiClass psiClass) {
        // Check for @Autoserializable annotation
        PsiModifierList modifierList = psiClass.getModifierList();
        if (modifierList != null) {
            PsiAnnotation annotation = modifierList.findAnnotation("Autoserializable");
            if (annotation != null) {
                return true;
            }

            // Check fully qualified annotation
            annotation = modifierList.findAnnotation("com.yourcompany.Autoserializable");
            if (annotation != null) {
                return true;
            }
        }

        // Check if implements Autoserializable
        PsiReferenceList implementsList = psiClass.getImplementsList();
        if (implementsList != null) {
            for (PsiJavaCodeReferenceElement ref : implementsList.getReferenceElements()) {
                String name = ref.getQualifiedName();
                if (name != null && name.contains("Autoserializable")) {
                    return true;
                }
            }
        }

        // Check superclass recursively
        PsiClass superClass = psiClass.getSuperClass();
        if (superClass != null && !superClass.getQualifiedName().equals("java.lang.Object")) {
            return isAutoserializable(superClass);
        }

        return false;
    }

    private void showNotification(String fileName, String className) {
        Notification notification = NotificationGroupManager.getInstance()
                .getNotificationGroup("Autoserializable Warnings")
                .createNotification(
                        "⚠️ Serialization Warning",
                        String.format(
                                "You modified <b>%s</b> (class: %s) which uses @Autoserializable.<br/>" +
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
