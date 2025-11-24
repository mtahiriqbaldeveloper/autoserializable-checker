package com.brotech.autoserializablechecker.core;



import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;

public class AutoserializableStartupActivity implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        // Register our file listener when project starts
        VirtualFileManager.getInstance().addVirtualFileListener(
                new AutoserializableFileListener(project),
                project
        );
    }
}
