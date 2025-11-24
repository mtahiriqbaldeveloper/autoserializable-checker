package com.brotech.autoserializablechecker.core;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Persistent settings state for the Autoserializable Checker plugin.
 * 
 * Performance Note: Real-time notifications are DISABLED by default to avoid
 * unnecessary processing on every file change. Users should use:
 * 1. Manual Action (right-click menu or toolbar) - check files on-demand
 * 2. Code Inspection (built-in IDE analysis) - automatic during normal inspections
 * 3. Enable real-time notifications only if needed for your workflow
 */
@State(
    name = "AutoserializableCheckerSettings",
    storages = @Storage("AutoserializableCheckerSettings.xml")
)
public class AutoserializableSettingsState implements PersistentStateComponent<AutoserializableSettingsState> {
    
    public boolean notificationsEnabled = false; // DISABLED by default for performance
    public long cooldownMs = 10000; // 10 seconds default
    
    public static AutoserializableSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(AutoserializableSettingsState.class);
    }
    
    @Nullable
    @Override
    public AutoserializableSettingsState getState() {
        return this;
    }
    
    @Override
    public void loadState(@NotNull AutoserializableSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
    
    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }
    
    public void setNotificationsEnabled(boolean enabled) {
        this.notificationsEnabled = enabled;
    }
    
    public long getCooldownMs() {
        return cooldownMs;
    }
    
    public void setCooldownMs(long cooldownMs) {
        this.cooldownMs = Math.max(1000, cooldownMs); // Min 1 second
    }
}

