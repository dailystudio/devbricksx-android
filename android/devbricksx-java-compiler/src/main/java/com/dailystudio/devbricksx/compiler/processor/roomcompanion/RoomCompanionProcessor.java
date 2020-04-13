package com.dailystudio.devbricksx.compiler.processor.roomcompanion;

import com.dailystudio.devbricksx.annotations.DaoExtension;
import com.dailystudio.devbricksx.annotations.RoomCompanion;
import com.dailystudio.devbricksx.compiler.processor.AbsBaseProcessor;
import com.dailystudio.devbricksx.compiler.processor.AbsSingleTypeElementProcessor;
import com.dailystudio.devbricksx.compiler.processor.AbsTypeElementsGroupProcessor;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.typeelementprocessor.DaoExtensionClassProcessor;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.typeelementprocessor.RoomCompanionClassProcessor;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.typeelementprocessor.RoomCompanionDaoClassProcessor;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.typeelementprocessor.RoomCompanionDatabaseClassProcessor;
import com.google.auto.service.AutoService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.lang.model.SourceVersion;

@AutoService(Processor.class)
public class RoomCompanionProcessor extends AbsBaseProcessor {

    private static Map<String, List<? extends AbsSingleTypeElementProcessor>> ELEMENT_PROCESSORS =
            new HashMap<>();

    private static Map<String, List<? extends AbsTypeElementsGroupProcessor>> ELEMENT_GROUP_PROCESSORS =
            new HashMap<>();

    static {
        List<AbsSingleTypeElementProcessor> singleProcessors;

        singleProcessors = new ArrayList<>();
        singleProcessors.add(new RoomCompanionClassProcessor());
        singleProcessors.add(new RoomCompanionDaoClassProcessor());

        ELEMENT_PROCESSORS.put(RoomCompanion.class.getCanonicalName(), singleProcessors);

        singleProcessors = new ArrayList<>();
        singleProcessors.add(new DaoExtensionClassProcessor());

        ELEMENT_PROCESSORS.put(DaoExtension.class.getCanonicalName(), singleProcessors);

        List<AbsTypeElementsGroupProcessor> groupProcessors;

        groupProcessors = new ArrayList<>();
        groupProcessors.add(new RoomCompanionDatabaseClassProcessor());

        ELEMENT_GROUP_PROCESSORS.put(RoomCompanion.class.getCanonicalName(), groupProcessors);
    }

    @Override
    protected Map<String, List<? extends AbsSingleTypeElementProcessor>> getTypeElementProcessors() {
        return ELEMENT_PROCESSORS;
    }

    @Override
    protected Map<String, List<? extends AbsTypeElementsGroupProcessor>> getTypeElementsGroupProcessors() {
        return ELEMENT_GROUP_PROCESSORS;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Arrays.asList(
                RoomCompanion.class.getCanonicalName()));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
