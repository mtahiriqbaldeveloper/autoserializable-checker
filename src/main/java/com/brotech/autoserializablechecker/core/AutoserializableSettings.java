package com.brotech.autoserializablechecker.core;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AutoserializableSettings implements Configurable {

    private JCheckBox enableNotificationsCheckbox;
    private JTextField cooldownField;
    private JPanel mainPanel;

    @Nls
    @Override
    public String getDisplayName() {
        return "Autoserializable Checker";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mainPanel = new JPanel();
        enableNotificationsCheckbox = new JCheckBox("Enable Notifications");
        cooldownField = new JTextField("5000");

        mainPanel.add(new JLabel("Notification Cooldown (ms):"));
        mainPanel.add(cooldownField);
        mainPanel.add(enableNotificationsCheckbox);

        return mainPanel;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() {
        // Save settings
    }
}