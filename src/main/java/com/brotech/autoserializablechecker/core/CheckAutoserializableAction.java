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

        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        if (psiFile == null || !(psiFile instanceof PsiJavaFile)) {
            showNotification(project, "Not a Java File", 
                "Please open a Java file to analyze for @Autoserializable usage.", 
                NotificationType.WARNING);
            return;
        }

        // Perform PSI operations in a read action to avoid index mismatch
        com.intellij.openapi.application.ApplicationManager.getApplication().runReadAction(() -> {
            try {
                PsiJavaFile javaFile = (PsiJavaFile) psiFile;
                List<String> autoserializableClasses = new ArrayList<>();
                
                // Analyze all classes in the file
                for (PsiClass psiClass : javaFile.getClasses()) {
                    if (isAutoserializable(psiClass)) {
                        String className = psiClass.getName();
                        if (className != null) {
                            autoserializableClasses.add(className);
                        }
                    }
                }

                // Show results
                String fileName = psiFile.getName();
                if (autoserializableClasses.isEmpty()) {
                    showNotification(project, "✓ Analysis Complete", 
                        String.format("File <b>%s</b> does not contain any @Autoserializable classes.", 
                            fileName),
                        NotificationType.INFORMATION);
                } else {
                    StringBuilder message = new StringBuilder();
                    message.append("File <b>").append(fileName).append("</b> contains ")
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
            } catch (Exception ex) {
                showNotification(project, "❌ Error", 
                    "An error occurred while analyzing the file: " + ex.getMessage(), 
                    NotificationType.ERROR);
            }
        });
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // Always enable if project is available - we'll handle the check in actionPerformed
        Project project = e.getProject();
        e.getPresentation().setEnabled(project != null);
        e.getPresentation().setVisible(true);
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
        if (superClass != null) {
            String qualifiedName = superClass.getQualifiedName();
            if (qualifiedName != null && !qualifiedName.equals("java.lang.Object")) {
                return isAutoserializable(superClass);
            }
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

