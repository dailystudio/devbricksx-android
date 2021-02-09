package com.dailystudio.devbricksx.compiler.processor.roomcompanion;

import com.dailystudio.devbricksx.compiler.utils.NameUtils;
import com.dailystudio.devbricksx.compiler.utils.TextUtils;

import javax.lang.model.element.VariableElement;

public class GeneratedNames {

    private final static String COMPANION_PREFIX = "_";
    private final static String DATABASE_SUFFIX = "Database";
    private final static String DAO_SUFFIX = "Dao";
    private final static String DIFF_UTIL_SUFFIX = "DiffUtil";
    private final static String REPOSITORY_SUFFIX = "Repository";

    private final static String DATABASE_PACKAGE_SUFFIX = ".db";
    private final static String REPOSITORY_PACKAGE_SUFFIX = ".repository";

    public final static String KOTLIN_COMPANION_OBJECT_FIELD = "Companion";

    public static String getShadowMethodName(String method) {
        StringBuilder builder = new StringBuilder(method);

        builder.insert(0, COMPANION_PREFIX);

        return builder.toString();
    }

    public static String getShadowParameterName(VariableElement parameter) {
        return getShadowParameterName(parameter.getSimpleName().toString());
    }

    public static String getShadowParameterName(String parameter) {
        StringBuilder builder = new StringBuilder(parameter);

        builder.append(COMPANION_PREFIX);

        return builder.toString();
    }

    public static String getTableName(String className) {
        return className.toLowerCase();
    }

    public static String getDaoExtensionCompanionName(String className) {
        StringBuilder builder = new StringBuilder(className);

        builder.insert(0, COMPANION_PREFIX);

        return builder.toString();
    }

    public static String getRoomCompanionName(String className) {
        StringBuilder builder = new StringBuilder(className);

        builder.insert(0, COMPANION_PREFIX);

        return builder.toString();
    }

    public static String getDiffUtilName(String className) {
        StringBuilder builder = new StringBuilder(className);

        builder.append(DIFF_UTIL_SUFFIX);

        return builder.toString();
    }

    public static String getRoomCompanionDaoName(String className) {
        StringBuilder builder = new StringBuilder(className);

        builder.append(DAO_SUFFIX);

        return builder.toString();
    }

    public static String getRoomCompanionRepositoryName(String className) {
        StringBuilder builder = new StringBuilder(className);

        builder.append(REPOSITORY_SUFFIX);

        return builder.toString();
    }

    public static String getRoomCompanionRepositoryPackageName(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return packageName;
        }

        if (packageName.endsWith(DATABASE_PACKAGE_SUFFIX)) {
            packageName = packageName.substring(0,
                    packageName.length() - DATABASE_PACKAGE_SUFFIX.length());
        }

        StringBuilder builder = new StringBuilder(packageName);

        builder.append(REPOSITORY_PACKAGE_SUFFIX);

        return builder.toString();
    }

    public static String getRoomCompanionDatabaseName(String className) {
        StringBuilder builder = new StringBuilder(className);

        builder.append(DATABASE_SUFFIX);

        return builder.toString();
    }

    public static String databaseToClassName(String database) {
        if (TextUtils.isEmpty(database)) {
            return DATABASE_SUFFIX;
        }

        StringBuilder builder = new StringBuilder(NameUtils.capitalizeName(database));

        if (!database.endsWith(DATABASE_SUFFIX)) {
            builder.append(DATABASE_SUFFIX);
        }

        return builder.toString();
    }

    public static String getRepositoryObjectMethodName(String className) {
        StringBuilder builder = new StringBuilder("get");

        builder.append(className);

        return builder.toString();
    }

    public static String getRepositoryObjectLiveMethodName(String className) {
        StringBuilder builder = new StringBuilder("get");

        builder.append(className);
        builder.append("Live");

        return builder.toString();
    }

    public static String getRepositoryAllObjectsMethodName(String className) {
        StringBuilder builder = new StringBuilder("getAll");

        builder.append(className);
        builder.append('s');

        return builder.toString();
    }

    public static String getRepositoryAllObjectsLiveMethodName(String className) {
        StringBuilder builder = new StringBuilder("getAll");

        builder.append(className);
        builder.append('s');
        builder.append("Live");

        return builder.toString();
    }

    public static String getRepositoryAllObjectsPagedMethodName(String className) {
        StringBuilder builder = new StringBuilder("getAll");

        builder.append(className);
        builder.append('s');
        builder.append("Paged");

        return builder.toString();
    }

    public static String getRepositoryAllObjectsFlowMethodName(String className) {
        StringBuilder builder = new StringBuilder("getAll");

        builder.append(className);
        builder.append('s');
        builder.append("Flow");

        return builder.toString();
    }

    public static String getRepositoryAllObjectsPagingSourceMethodName(String className) {
        StringBuilder builder = new StringBuilder("getAll");

        builder.append(className);
        builder.append('s');
        builder.append("PagingSource");

        return builder.toString();
    }

}
