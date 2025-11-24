package com.brotech.autoserializablechecker.core;

import com.intellij.openapi.options.Configurable;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Settings UI for the Autoserializable Checker plugin.
 */

public class AutoserializableSettings implements Configurable {

    private JBCheckBox enableNotificationsCheckbox;
    private JBTextField cooldownField;
    private JPanel mainPanel;

    @Nls
    @Override
    public String getDisplayName() {
        return "Autoserializable Checker";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        enableNotificationsCheckbox = new JBCheckBox(
                "Enable real-time file change notifications (may impact performance)"
        );
        cooldownField = new JBTextField();
        
        JBLabel infoLabel = new JBLabel(
                "<html><body style='width: 400px'>" +
                "<b>Recommended:</b> Keep disabled for best performance.<br/><br/>" +
                "<b>Available check methods:</b><br/>" +
                "• <b>Manual Action:</b> Right-click → Check for @AutoSerializable (always available)<br/>" +
                "• <b>Code Inspection:</b> Automatic warnings during normal IDE analysis (always enabled)<br/>" +
                "• <b>Real-time notifications:</b> Shows notification on every file save (enable below if needed)<br/>" +
                "</body></html>"
        );
        
        mainPanel = FormBuilder.createFormBuilder()
                .addComponent(infoLabel)
                .addVerticalGap(10)
                .addComponent(enableNotificationsCheckbox)
                .addLabeledComponent(
                        new JBLabel("Notification cooldown (milliseconds):"),
                        cooldownField
                )
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

        reset(); // Load current values
        return mainPanel;
    }

    @Override
    public boolean isModified() {
        AutoserializableSettingsState settings = AutoserializableSettingsState.getInstance();
        
        if (settings == null) {
            return false; // Can't check if service not initialized
        }
        
        if (enableNotificationsCheckbox.isSelected() != settings.isNotificationsEnabled()) {
            return true;
        }
        
        try {
            long cooldown = Long.parseLong(cooldownField.getText());
            return cooldown != settings.getCooldownMs();
        } catch (NumberFormatException e) {
            return true;
        }
    }

    @Override
    public void apply() {
        AutoserializableSettingsState settings = AutoserializableSettingsState.getInstance();
        
        if (settings == null) {
            return; // Can't save if service not initialized
        }
        
        settings.setNotificationsEnabled(enableNotificationsCheckbox.isSelected());
        
        try {
            long cooldown = Long.parseLong(cooldownField.getText());
            settings.setCooldownMs(cooldown);
        } catch (NumberFormatException e) {
            // Keep current value if invalid
        }
    }
    
    @Override
    public void reset() {
        AutoserializableSettingsState settings = AutoserializableSettingsState.getInstance();
        
        if (settings != null) {
            enableNotificationsCheckbox.setSelected(settings.isNotificationsEnabled());
            cooldownField.setText(String.valueOf(settings.getCooldownMs()));
        } else {
            // Fallback defaults if service not yet initialized
            enableNotificationsCheckbox.setSelected(false); // Disabled by default
            cooldownField.setText("10000");
        }
    }
}