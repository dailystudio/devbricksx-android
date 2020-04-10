package com.dailystudio.devbricksx.compiler.processor;

import com.dailystudio.devbricksx.compiler.utils.LogUtils;

import java.util.ArrayList;
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

    protected abstract Map<String, List<? extends AbsSingleTypeElementProcessor>> getTypeElementProcessors();
    protected abstract Map<String, List<? extends AbsTypeElementsGroupProcessor>> getTypeElementsGroupProcessors();


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        Map<String, List<? extends AbsSingleTypeElementProcessor>> mapOfSingleTypeElementProcessors =
                getTypeElementProcessors();
        Map<String, List<? extends AbsTypeElementsGroupProcessor>> mapOfTypeElementsGroupProcessors =
                getTypeElementsGroupProcessors();

        Set<String> annotations = mapOfSingleTypeElementProcessors.keySet();

        List<? extends AbsSingleTypeElementProcessor> singleTypeElementProcessors;
        List<? extends AbsTypeElementsGroupProcessor> typeElementsGroupProcessors;
        Class annotationClass;
        for (String annotation: annotations) {
            singleTypeElementProcessors = mapOfSingleTypeElementProcessors.get(annotation);
            typeElementsGroupProcessors = mapOfTypeElementsGroupProcessors.get(annotation);

            try {
                annotationClass = Class.forName(annotation);
            } catch (ClassNotFoundException e) {
                error("failed to parse annotation class for: %s", annotation);
                continue;
            }

            Set<? extends Element> elements =
                    roundEnv.getElementsAnnotatedWith(annotationClass);

            List<TypeElement> typeElements = new ArrayList<>();

            TypeElement typeElement;
            for (Element element : elements) {
                if (element instanceof TypeElement) {
                    typeElement = (TypeElement) element;

                    typeElements.add(typeElement);

                    if (singleTypeElementProcessors != null) {
                        for (AbsSingleTypeElementProcessor p : singleTypeElementProcessors) {
                            p.attachToProcessEnvironment(mProcessingEnv);
                            p.process(typeElement, roundEnv);
                            p.detachFromProcessEnvironment();
                        }
                    }
                }
            }

            if (typeElementsGroupProcessors != null) {
                for (AbsTypeElementsGroupProcessor p: typeElementsGroupProcessors) {
                    p.attachToProcessEnvironment(mProcessingEnv);
                    p.process(typeElements, roundEnv);
                    p.detachFromProcessEnvironment();
                }
            }
        }

        return true;
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
