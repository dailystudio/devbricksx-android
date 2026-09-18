#include <jni.h>
#include <string>
#include <android/log.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_dailystudio_codebase_core_nativelib_NativeLib_getStringNative(
        JNIEnv* env,
        jobject,
        jobject context,
        jint resId
) {
    __android_log_print(ANDROID_LOG_INFO, "NativeLib", "resId: %d", resId);

    jclass contextClass = env->GetObjectClass(context);
    jmethodID getResources = env->GetMethodID(contextClass, "getResources", "()Landroid/content/res/Resources;");
    jobject resources = env->CallObjectMethod(context, getResources);

    jclass resourcesClass = env->GetObjectClass(resources);
    jmethodID getString = env->GetMethodID(resourcesClass, "getString", "(I)Ljava/lang/String;");

    return (jstring)env->CallObjectMethod(resources, getString, resId);
}