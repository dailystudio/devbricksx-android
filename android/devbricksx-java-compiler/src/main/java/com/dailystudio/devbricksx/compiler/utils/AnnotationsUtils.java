package com.dailystudio.devbricksx.compiler.utils;

import com.squareup.javapoet.ClassName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

public class AnnotationsUtils {

    public static List<ClassName> getClassesValueFromAnnotation(TypeElement typeElement,
                                                                String property) {
        List<ClassName> classNames = null;

        Object value = findPropertyInAnnotation(typeElement, property);
        if (value instanceof List == false) {
            return null;
        }

        List<? extends AnnotationValue> typeMirrors
                = (List<? extends AnnotationValue>) value;
        if (typeMirrors != null) {
            for (AnnotationValue av: typeMirrors) {
                if (classNames == null) {
                    classNames = new ArrayList<>();
                }

                classNames.add(ClassName.bestGuess(av.getValue().toString()));
            }
        }

        return classNames;
    }

    public static ClassName getClassValueFromAnnotation(TypeElement typeElement,
                                                        String property) {
        Object value = findPropertyInAnnotation(typeElement, property);
        if (value instanceof TypeMirror == false) {
            return null;
        }

        TypeMirror typeMirror = (TypeMirror) value;

        return ClassName.bestGuess(typeMirror.toString());
    }

    private static Object findPropertyInAnnotation(TypeElement typeElement,
                                                   String property) {
        if (TextUtils.isEmpty(property)) {
            return null;
        }

        List<? extends AnnotationMirror> annotationMirrors = typeElement.getAnnotationMirrors();
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues
                    = annotationMirror.getElementValues();
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry
                    : elementValues.entrySet()) {
                String key = entry.getKey().getSimpleName().toString();
                Object value = entry.getValue().getValue();
                if (key.equals(property)) {
                    return value;
                }
            }
        }

        return null;
    }
}
