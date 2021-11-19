#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_water_alkaline_kengen_utils_SecUtils_getMain(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "https://kangen.jbmapps.online/";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_water_alkaline_kengen_utils_SecUtils_getSub(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "https://www.googleapis.com/";
    return env->NewStringUTF(hello.c_str());
}





