#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_water_alkaline_kengen_utils_SecUtils_getMain(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "https://jbmapps.online/kangen/";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_water_alkaline_kengen_utils_SecUtils_getSub(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "https://www.googleapis.com/";
    return env->NewStringUTF(hello.c_str());
}

// Decryption KEY
extern "C"
JNIEXPORT jstring JNICALL
Java_com_water_alkaline_kengen_Encrypt_DecScript_getDecKey1(JNIEnv *env, jobject thiz) {

    std::string hello = "B26D9C799955D93AF2F01EA97B6E157E0CC8EE72A457C3AAA35F79627FD13728";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_water_alkaline_kengen_Encrypt_DecScript_getDecKey2(JNIEnv *env, jobject instance) {
    std::string hello = "FB24433EA865DB81308C1319C8CAAE1E0CC8EE72A457C3AAA35F79627FD13728";
    return env->NewStringUTF(hello.c_str());
}


// Encryption KEY
extern "C"
JNIEXPORT jstring JNICALL
Java_com_water_alkaline_kengen_Encrypt_EncScript_getEncKey1(JNIEnv *env, jobject thiz) {

    std::string hello = "2A6A015D1F5FF08783F9CDF639E7D6F60CC8EE72A457C3AAA35F79627FD13728";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_water_alkaline_kengen_Encrypt_EncScript_getEncKey2(JNIEnv *env, jobject instance) {
    std::string hello = "96F5659340F2619C93BF419734B060220CC8EE72A457C3AAA35F79627FD13728";
    return env->NewStringUTF(hello.c_str());
}





