package com.brotech.autoserializablechecker.core;

import com.intellij.psi.*;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Centralized utility for checking if a class uses @AutoSerializable.
 * Uses IntelliJ's caching mechanism to avoid repeated expensive checks.
 */
public class AutoserializableUtil {
    
    private static final Set<String> AUTOSERIALIZABLE_ANNOTATIONS = Set.of(
            "Autoserializable",
            "com.brotech.Autoserializable",
            "com.yourcompany.Autoserializable"
    );
    
    private static final int MAX_SUPERCLASS_DEPTH = 10; // Prevent infinite loops
    
    /**
     * Checks if a class uses @AutoSerializable annotation or interface.
     * Results are cached and invalidated when PSI changes.
     */
    public static boolean isAutoserializable(@NotNull PsiClass psiClass) {
        return CachedValuesManager.getCachedValue(psiClass, () -> 
            CachedValueProvider.Result.create(
                computeIsAutoserializable(psiClass, 0),
                PsiModificationTracker.MODIFICATION_COUNT
            )
        );
    }
    
    private static boolean computeIsAutoserializable(@NotNull PsiClass psiClass, int depth) {
        // Prevent stack overflow on circular dependencies
        if (depth > MAX_SUPERCLASS_DEPTH) {
            return false;
        }
        
        // Check annotations
        if (hasAutoserializableAnnotation(psiClass)) {
            return true;
        }
        
        // Check interfaces
        if (implementsAutoserializableInterface(psiClass)) {
            return true;
        }
        
        // Check superclass (with depth limit)
        PsiClass superClass = psiClass.getSuperClass();
        if (superClass != null) {
            String qualifiedName = superClass.getQualifiedName();
            if (qualifiedName != null && !qualifiedName.equals("java.lang.Object")) {
                return computeIsAutoserializable(superClass, depth + 1);
            }
        }
        
        return false;
    }
    
    private static boolean hasAutoserializableAnnotation(@NotNull PsiClass psiClass) {
        PsiModifierList modifierList = psiClass.getModifierList();
        if (modifierList == null) {
            return false;
        }
        
        for (String annotationName : AUTOSERIALIZABLE_ANNOTATIONS) {
            if (modifierList.findAnnotation(annotationName) != null) {
                return true;
            }
        }
        
        return false;
    }
    
    private static boolean implementsAutoserializableInterface(@NotNull PsiClass psiClass) {
        PsiReferenceList implementsList = psiClass.getImplementsList();
        if (implementsList == null) {
            return false;
        }
        
        for (PsiJavaCodeReferenceElement ref : implementsList.getReferenceElements()) {
            String qualifiedName = ref.getQualifiedName();
            if (qualifiedName != null) {
                // Use exact matching instead of contains() for better precision
                for (String autoserializableName : AUTOSERIALIZABLE_ANNOTATIONS) {
                    if (qualifiedName.equals(autoserializableName) || 
                        qualifiedName.endsWith("." + autoserializableName)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Quick check if a file might contain AutoSerializable classes.
     * This is a fast pre-check before doing full PSI analysis.
     */
    public static boolean mightContainAutoserializable(@NotNull PsiJavaFile javaFile) {
        // Quick text-based pre-check (much faster than PSI parsing)
        String text = javaFile.getText();
        return text.contains("AutoSerializable") || text.contains("@AutoSerializable");
    }
}

