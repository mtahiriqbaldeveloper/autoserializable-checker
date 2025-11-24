package com.brotech.autoserializablechecker.core;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

/**
 * Startup activity that registers the file listener ONLY if real-time monitoring is enabled.
 * Performance optimization: By default, checks only happen on-demand (via Action) or during
 * normal IDE inspections, not on every file change.
 */
public class AutoserializableStartupActivity implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        AutoserializableSettingsState settings = AutoserializableSettingsState.getInstance();
        
        // Only register file listener if user explicitly enabled real-time notifications
        if (settings != null && settings.isNotificationsEnabled()) {
            MessageBusConnection connection = project.getMessageBus().connect();
            connection.subscribe(
                    com.intellij.openapi.vfs.VirtualFileManager.VFS_CHANGES,
                    new AutoserializableFileListener(project)
            );
        }
        
        // Note: Inspection and Action always work regardless of this setting
    }
}
