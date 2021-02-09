package com.dailystudio.devbricksx.compiler.processor.roomcompanion;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.List;

public class TypeNamesUtils {

    public static boolean isTypeNameVoid(TypeName typeName) {
        return (getVoidTypeName().equals(typeName));
    }

    public static boolean isTypeNameOfList(TypeName typeName) {
        return (typeName.toString().contains("java.util.List<"));
    }

    public static TypeName getVoidTypeName() {
        return ClassName.VOID;
    }

    public static TypeName getContextTypeName() {
        return ClassName.get("android.content", "Context");
    }

    public static TypeName getRoomTypeName() {
        return ClassName.get("androidx.room", "Room");
    }

    public static TypeName getRoomDatabaseBuilderTypeName(TypeName className) {
        ClassName builder = ClassName.get("androidx.room.RoomDatabase", "Builder");

        return ParameterizedTypeName.get(builder, className);
    }

    public static TypeName getMigrationTypeName() {
        return ClassName.get("androidx.room.migration", "Migration");
    }

    public static ClassName getTransformationsTypeName() {
        return ClassName.get("androidx.lifecycle", "Transformations");
    }

    public static ClassName getLoggerTypeName() {
        return ClassName.get("com.dailystudio.devbricksx.development", "Logger");
    }

    public static TypeName getMapFunctionOfTypeName(TypeName typeClassNameA,
                                                    TypeName typeClassNameB) {
        ClassName function = ClassName.get("androidx.arch.core.util", "Function");

        return ParameterizedTypeName.get(function, typeClassNameA, typeClassNameB);
    }

    public static TypeName getFunction2OfTypeName(TypeName typeClassNameA,
                                                  TypeName typeClassNameB,
                                                  TypeName typeClassNameC) {
        ClassName function = ClassName.get("kotlin.jvm.functions", "Function2");

        return ParameterizedTypeName.get(function, typeClassNameA, typeClassNameB, typeClassNameC);
    }

    public static TypeName getContinuationOfTypeName(TypeName  typeClassName) {
        ClassName continuation = ClassName.get("kotlin.coroutines", "Continuation");

        return ParameterizedTypeName.get(continuation, typeClassName);
    }

    public static ClassName getTransformationFunctionTypeName() {
        return ClassName.get("androidx.lifecycle", "Transformations");
    }

    public static ClassName getArrayListTypeName() {
        return ClassName.get("java.util", "ArrayList");
    }

    public static ClassName getItemCallbackTypeName() {
        return ClassName.get("androidx.recyclerview.widget.DiffUtil", "ItemCallback");
    }

    public static TypeName getItemCallbackOfTypeName(TypeName typeClassName) {
        ClassName itemCallback = getItemCallbackTypeName();

        return ParameterizedTypeName.get(itemCallback, typeClassName);
    }

    public static TypeName getListOfTypeName(Class<?> primitiveClass) {
        return ParameterizedTypeName.get(List.class, primitiveClass);
    }

    public static TypeName getListOfTypeName(TypeName typeClassName) {
        ClassName list = ClassName.get("java.util", "List");

        return ParameterizedTypeName.get(list, typeClassName);
    }

    public static TypeName getLiveDataOfTypeName(TypeName typeClassName) {
        ClassName list = ClassName.get("androidx.lifecycle", "LiveData");

        return ParameterizedTypeName.get(list, typeClassName);
    }

    public static TypeName getFlowOfTypeName(TypeName typeClassName) {
        ClassName list = ClassName.get("kotlinx.coroutines.flow", "Flow");

        return ParameterizedTypeName.get(list, typeClassName);
    }

    public static TypeName getDataSourceFactoryOfTypeName(TypeName typeClassName) {
        ClassName factory = ClassName.get("androidx.paging.DataSource", "Factory");

        return ParameterizedTypeName.get(factory, ClassName.get(Integer.class), typeClassName);
    }

    public static TypeName getPagingSourceOfTypeName(TypeName typeClassName) {
        ClassName factory = ClassName.get("androidx.paging", "PagingSource");

        return ParameterizedTypeName.get(factory, ClassName.get(Integer.class), typeClassName);
    }

    public static TypeName getPageListOfTypeName(TypeName typeClassName) {
        ClassName pagedList = ClassName.get("androidx.paging", "PagedList");

        return ParameterizedTypeName.get(pagedList, typeClassName);
    }

    public static ClassName getPagedListBuilderTypeName() {
        ClassName builder = ClassName.get("androidx.paging", "LivePagedListBuilder");

        return builder;
    }

    public static ClassName getKotlinCompatibleUtilsTypeName() {
        return ClassName.get("com.dailystudio.devbricksx.utils", "KotlinCompatibleUtils");
    }

    public static ClassName getObjectTypeName(String packageName, String typeElementName) {
        return ClassName.get(packageName, typeElementName);
    }

    public static TypeName getArrayOfTypeName(TypeName typeName) {
        return ArrayTypeName.of(typeName);
    }

    public static TypeName getArrayOfObjectsTypeName(String packageName, String typeElementName) {
        return getArrayOfTypeName(getObjectTypeName(packageName, typeElementName));
    }

    public static TypeName getArrayOfCompanionsTypeName(String packageName, String typeElementName) {
        return getArrayOfTypeName(getCompanionTypeName(packageName, typeElementName));
    }

    public static TypeName getListOfObjectsTypeName(String packageName, String typeElementName) {
        return TypeNamesUtils.getListOfTypeName(getObjectTypeName(packageName, typeElementName));
    }

    public static TypeName getListOfCompanionsTypeName(String packageName, String typeElementName) {
        return TypeNamesUtils.getListOfTypeName(getCompanionTypeName(packageName, typeElementName));
    }

    public static TypeName getLiveDataOfObjectTypeName(String packageName, String typeElementName) {
        return TypeNamesUtils.getLiveDataOfTypeName(getObjectTypeName(packageName, typeElementName));
    }

    public static TypeName getLiveDataOfCompanionTypeName(String packageName, String typeElementName) {
        return TypeNamesUtils.getLiveDataOfTypeName(getCompanionTypeName(packageName, typeElementName));
    }

    public static TypeName getLiveDataOfListOfObjectsTypeName(String packageName, String typeElementName) {
        return TypeNamesUtils.getLiveDataOfTypeName(getListOfObjectsTypeName(packageName, typeElementName));
    }

    public static TypeName getLiveDataOfListOfCompanionsTypeName(String packageName, String typeElementName) {
        return TypeNamesUtils.getLiveDataOfTypeName(getListOfCompanionsTypeName(packageName, typeElementName));
    }

    public static ClassName getCompanionTypeName(String packageName, String typeElementName) {
        return ClassName.get(packageName,
                GeneratedNames.getRoomCompanionName(typeElementName));
    }

    public static TypeName getDataSourceFactoryOfCompanionsTypeName(String packageName, String typeElementName) {
        return TypeNamesUtils.getDataSourceFactoryOfTypeName(getCompanionTypeName(packageName, typeElementName));
    }

    public static TypeName getPagingSourceOfCompanionsTypeName(String packageName, String typeElementName) {
        return TypeNamesUtils.getPagingSourceOfTypeName(getCompanionTypeName(packageName, typeElementName));
    }

    public static TypeName getDataSourceFactoryOfObjectsTypeName(String packageName, String typeElementName) {
        return TypeNamesUtils.getDataSourceFactoryOfTypeName(getObjectTypeName(packageName, typeElementName));
    }

    public static TypeName getPagingSourceOfObjectsTypeName(String packageName, String typeElementName) {
        return TypeNamesUtils.getPagingSourceOfTypeName(getObjectTypeName(packageName, typeElementName));
    }

    public static TypeName getPagedListOfObjectsTypeName(String packageName, String typeElementName) {
        return TypeNamesUtils.getPageListOfTypeName(getObjectTypeName(packageName, typeElementName));
    }

    public static TypeName getPagedListOfCompanionsTypeName(String packageName, String typeElementName) {
        return TypeNamesUtils.getPageListOfTypeName(getCompanionTypeName(packageName, typeElementName));
    }

    public static TypeName getLiveDataOfPagedListOfObjectsTypeName(String packageName, String typeElementName) {
        return TypeNamesUtils.getLiveDataOfTypeName(getPagedListOfObjectsTypeName(packageName, typeElementName));
    }

    public static TypeName getLiveDataOfPagedListOfCompanionsTypeName(String packageName, String typeElementName) {
        return TypeNamesUtils.getLiveDataOfTypeName(getPagedListOfCompanionsTypeName(packageName, typeElementName));
    }

    public static TypeName getDummyMigrationTypeName() {
        return ClassName.get("com.dailystudio.devbricksx.database", "DummyMigration");
    }

    public static TypeName getFlowOfCompanionsTypeName(String packageName, String typeName) {
        return TypeNamesUtils.getFlowOfTypeName(getCompanionTypeName(packageName, typeName));
    }

    public static TypeName getFlowOfObjectsTypeName(String packageName, String typeName) {
        return TypeNamesUtils.getFlowOfTypeName(getObjectTypeName(packageName, typeName));
    }

    public static TypeName getFlowOfListOfCompanionsTypeName(String packageName, String typeName) {
        return TypeNamesUtils.getFlowOfTypeName(getListOfCompanionsTypeName(packageName, typeName));
    }

    public static TypeName getFlowOfListOfObjectsTypeName(String packageName, String typeName) {
        return TypeNamesUtils.getFlowOfTypeName(getListOfObjectsTypeName(packageName, typeName));
    }

}
