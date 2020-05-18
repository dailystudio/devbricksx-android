package com.dailystudio.devbricksx.compiler.processor;

import com.dailystudio.devbricksx.compiler.utils.LogUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public abstract class AbsBaseProcessor extends AbstractProcessor  {

    private ProcessingEnvironment mProcessingEnv;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        mProcessingEnv = processingEnv;
    }

    protected abstract Map<Class<? extends Annotation>, List<? extends AbsSingleTypeElementProcessor>> getTypeElementProcessors();
    protected abstract Map<Class<? extends Annotation>, List<? extends AbsTypeElementsGroupProcessor>> getTypeElementsGroupProcessors();


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        Set<String> supportedAnnotations = getSupportedAnnotationTypes();
        if (supportedAnnotations == null) {
            return false;
        }

        Map<Class<? extends Annotation>, List<TypeElement>> typeElementsMap =
                new HashMap<>();

        Class annotationClass;
        List<TypeElement> typeElements;
        for (String annotation: supportedAnnotations) {
            try {
                annotationClass = Class.forName(annotation);
            } catch (ClassNotFoundException e) {
                error("failed to parse annotation class for: %s", annotation);
                continue;
            }

            Set<? extends Element> elements =
                    roundEnv.getElementsAnnotatedWith(annotationClass);

            typeElements = null;
            for (Element element : elements) {
                if (element instanceof TypeElement) {
                    if (typeElements == null) {
                        typeElements = new ArrayList<>();
                    }

                    typeElements.add((TypeElement) element);
                }
            }

            if (typeElements != null) {
                typeElementsMap.put(annotationClass, typeElements);
            }
        }

        Object preResults = preProcessTypeElements(typeElementsMap);
        applySingleTypeElementProcessor(typeElementsMap, roundEnv, preResults);
        applyTypeElementsGroupProcessors(typeElementsMap, roundEnv, preResults);

        return true;
    }

    protected Object preProcessTypeElements(Map<Class<? extends Annotation>, List<TypeElement>> typeElementsMap) {
        return null;
    }

    private void applySingleTypeElementProcessor(Map<Class<? extends Annotation>, List<TypeElement>> typeElementsMap,
                                                 RoundEnvironment roundEnv,
                                                 Object preResults) {
        Map<Class<? extends Annotation>, List<? extends AbsSingleTypeElementProcessor>> mapOfSingleTypeElementProcessors =
                getTypeElementProcessors();
        if (mapOfSingleTypeElementProcessors == null) {
            return;
        }

        Set<Class<? extends Annotation>> annotations = mapOfSingleTypeElementProcessors.keySet();
        if (annotations == null) {
            return;
        }

        List<? extends AbsSingleTypeElementProcessor> singleTypeElementProcessors;

        List<TypeElement> typeElements;
        for (Class<? extends Annotation> annotation: annotations) {
            singleTypeElementProcessors = mapOfSingleTypeElementProcessors.get(annotation);
            if (singleTypeElementProcessors == null) {
                continue;
            }

            typeElements = typeElementsMap.get(annotation);
            if (typeElements == null) {
                continue;
            }

            for (TypeElement typeElement : typeElements) {
                for (AbsSingleTypeElementProcessor p : singleTypeElementProcessors) {
                    p.attachToProcessEnvironment(mProcessingEnv);
                    p.process(typeElement, roundEnv, preResults);
                    p.detachFromProcessEnvironment();
                }
            }
        }
    }

    private void applyTypeElementsGroupProcessors(Map<Class<? extends Annotation>, List<TypeElement>> typeElementsMap,
                                                  RoundEnvironment roundEnv,
                                                  Object preResults) {
        Map<Class<? extends Annotation>, List<? extends AbsTypeElementsGroupProcessor>> mapOfTypeElementsGroupProcessors =
                getTypeElementsGroupProcessors();
        if (mapOfTypeElementsGroupProcessors == null) {
            return;
        }

        Set<Class<? extends Annotation>> annotations = mapOfTypeElementsGroupProcessors.keySet();
        if (annotations == null) {
            return;
        }

        List<? extends AbsTypeElementsGroupProcessor> typeElementsGroupProcessors;

        List<TypeElement> typeElements;
        for (Class<? extends Annotation> annotation: annotations) {
            typeElementsGroupProcessors = mapOfTypeElementsGroupProcessors.get(annotation);
            if (typeElementsGroupProcessors == null) {
                continue;
            }

            typeElements = typeElementsMap.get(annotation);
            if (typeElements == null) {
                continue;
            }

            for (AbsTypeElementsGroupProcessor p: typeElementsGroupProcessors) {
                p.attachToProcessEnvironment(mProcessingEnv);
                p.process(typeElements, roundEnv, preResults);
                p.detachFromProcessEnvironment();
            }
        }
    }

    public void debug(String format, Object... args) {
        LogUtils.debug(mProcessingEnv.getMessager(), format, args);
    }

    public void info(String format, Object... args) {
        LogUtils.info(mProcessingEnv.getMessager(), format, args);
    }

    public void error(String format, Object... args) {
        LogUtils.error(mProcessingEnv.getMessager(), format, args);
    }

    public void warn(String format, Object... args) {
        LogUtils.warn(mProcessingEnv.getMessager(), format, args);
    }

}
