package com.dailystudio.devbricksx.compiler.processor.roomcompanion;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import com.dailystudio.devbricksx.annotations.RoomCompanion;
import com.dailystudio.devbricksx.compiler.processor.AbsBaseProcessor;
import com.dailystudio.devbricksx.compiler.processor.AbsTypeElementProcessor;
import com.dailystudio.devbricksx.compiler.processor.Constants;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.typeelementprocessor.RoomCompanionClassProcessor;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.typeelementprocessor.RoomCompanionDaoClassProcessor;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.typeelementprocessor.RoomCompanionDatabaseClassProcessor;
import com.dailystudio.devbricksx.compiler.utils.NameUtils;
import com.dailystudio.devbricksx.compiler.utils.TextUtils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
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

    private static Map<String, List<? extends AbsTypeElementProcessor>> ELEMENT_PROCESSORS =
            new HashMap<>();

    static {
        List<AbsRoomCompanionTypeElementProcessor> processors;

        processors = new ArrayList<>();
        processors.add(new RoomCompanionClassProcessor());
        processors.add(new RoomCompanionDaoClassProcessor());
        processors.add(new RoomCompanionDatabaseClassProcessor());

        ELEMENT_PROCESSORS.put(RoomCompanion.class.getCanonicalName(), processors);
    }

    @Override
    protected Map<String, List<? extends AbsTypeElementProcessor>> getTypeElementProcessors() {
        return ELEMENT_PROCESSORS;
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
