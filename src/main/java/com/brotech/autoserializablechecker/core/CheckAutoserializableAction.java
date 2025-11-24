package com.brotech.autoserializablechecker.core;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CheckAutoserializableAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        if (psiFile == null || !(psiFile instanceof PsiJavaFile)) {
            showNotification(project, "Not a Java File", 
                "Please open a Java file to analyze for @Autoserializable usage.", 
                NotificationType.WARNING);
            return;
        }

        PsiJavaFile javaFile = (PsiJavaFile) psiFile;
        List<String> autoserializableClasses = new ArrayList<>();
        
        // Analyze all classes in the file
        for (PsiClass psiClass : javaFile.getClasses()) {
            if (isAutoserializable(psiClass)) {
                autoserializableClasses.add(psiClass.getName());
            }
        }

        // Show results
        if (autoserializableClasses.isEmpty()) {
            showNotification(project, "✓ Analysis Complete", 
                String.format("File <b>%s</b> does not contain any @Autoserializable classes.", 
                    psiFile.getName()),
                NotificationType.INFORMATION);
        } else {
            StringBuilder message = new StringBuilder();
            message.append("File <b>").append(psiFile.getName()).append("</b> contains ")
                   .append(autoserializableClasses.size()).append(" @Autoserializable class(es):<br/>");
            
            for (String className : autoserializableClasses) {
                message.append("• <b>").append(className).append("</b><br/>");
            }
            
            message.append("<br/>⚠️ Remember to:<br/>")
                   .append("• Maintain backward compatibility<br/>")
                   .append("• Update SerialVersionUID if needed<br/>")
                   .append("• Document all changes");
            
            showNotification(project, "⚠️ Autoserializable Classes Found", 
                message.toString(), 
                NotificationType.WARNING);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // Enable/disable action based on whether a Java file is open
        Project project = e.getProject();
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        
        boolean enabled = project != null && psiFile instanceof PsiJavaFile;
        e.getPresentation().setEnabled(enabled);
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

    private void showNotification(Project project, String title, String content, NotificationType type) {
        Notification notification = NotificationGroupManager.getInstance()
                .getNotificationGroup("Autoserializable Warnings")
                .createNotification(title, content, type);
        
        Notifications.Bus.notify(notification, project);
    }
}

