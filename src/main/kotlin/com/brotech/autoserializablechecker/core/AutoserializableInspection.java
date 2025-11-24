package com.brotech.autoserializablechecker.core;


import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public class AutoserializableInspection extends AbstractBaseJavaLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitClass(PsiClass aClass) {
                super.visitClass(aClass);

                if (isAutoserializable(aClass)) {
                    PsiIdentifier nameIdentifier = aClass.getNameIdentifier();
                    if (nameIdentifier != null) {
                        holder.registerProblem(
                                nameIdentifier,
                                "This class uses @Autoserializable. Be careful when modifying to maintain serialization compatibility.",
                                com.intellij.codeInspection.ProblemHighlightType.WARNING
                        );
                    }
                }
            }
        };
    }

    private boolean isAutoserializable(PsiClass psiClass) {
        PsiModifierList modifierList = psiClass.getModifierList();
        if (modifierList != null) {
            if (modifierList.findAnnotation("Autoserializable") != null ||
                    modifierList.findAnnotation("com.yourcompany.Autoserializable") != null) {
                return true;
            }
        }

        PsiReferenceList implementsList = psiClass.getImplementsList();
        if (implementsList != null) {
            for (PsiJavaCodeReferenceElement ref : implementsList.getReferenceElements()) {
                String name = ref.getQualifiedName();
                if (name != null && name.contains("Autoserializable")) {
                    return true;
                }
            }
        }

        return false;
    }
}
