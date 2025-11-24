package com.brotech.autoserializablechecker.core;


import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

/**
 * Optimized inspection that uses cached checks for @AutoSerializable detection.
 */
public class AutoserializableInspection extends AbstractBaseJavaLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitClass(PsiClass aClass) {
                super.visitClass(aClass);

                // Use cached utility - much faster on repeated inspections
                if (AutoserializableUtil.isAutoserializable(aClass)) {
                    PsiIdentifier nameIdentifier = aClass.getNameIdentifier();
                    if (nameIdentifier != null) {
                        holder.registerProblem(
                                nameIdentifier,
                                "This class uses @AutoSerializable. Be careful when modifying to maintain serialization compatibility.",
                                com.intellij.codeInspection.ProblemHighlightType.WARNING
                        );
                    }
                }
            }
        };
    }
}
