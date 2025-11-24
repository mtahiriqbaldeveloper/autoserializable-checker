package com.brotech.autoserializablechecker.core;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import org.jetbrains.annotations.NotNull;

public class AutoserializableStartupActivity implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        // Register our BulkFileListener using the message bus
        MessageBusConnection connection = project.getMessageBus().connect();
        connection.subscribe(
                com.intellij.openapi.vfs.VirtualFileManager.VFS_CHANGES,
                new AutoserializableFileListener(project)
        );
    }
}
