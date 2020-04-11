package com.dailystudio.devbricksx.compiler.processor.roomcompanion.typeelementprocessor;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.dailystudio.devbricksx.annotations.DaoExtension;
import com.dailystudio.devbricksx.annotations.RoomCompanion;
import com.dailystudio.devbricksx.compiler.processor.Constants;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.AbsRoomCompanionTypeElementProcessor;
import com.dailystudio.devbricksx.compiler.processor.roomcompanion.GeneratedNames;
import com.dailystudio.devbricksx.compiler.utils.NameUtils;
import com.dailystudio.devbricksx.compiler.utils.TextUtils;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class DaoExtensionClassProcessor extends AbsRoomCompanionTypeElementProcessor {

    @Override
    protected TypeSpec.Builder onProcess(TypeElement typeElement, String packageName, String typeName, RoundEnvironment roundEnv) {
        ClassName generatedClassName = ClassName
                .get(packageName, GeneratedNames.getDaoExtensionCompanionName(typeName));
        debug("generated class = [%s]", generatedClassName);

        DaoExtension daoExtension = typeElement.getAnnotation(DaoExtension.class);
        if (daoExtension == null) {
            return null;
        }

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.ABSTRACT)
                .addModifiers(Modifier.PUBLIC);

        List<? extends Element> subElements = typeElement.getEnclosedElements();

        ExecutableElement executableElement;

        for (Element subElement: subElements) {
            if (subElement instanceof ExecutableElement) {
                debug("processing method: %s", subElement);

                executableElement = (ExecutableElement) subElement;
            }
        }

        return classBuilder;
    }

}
