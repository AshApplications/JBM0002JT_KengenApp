#include <string.h>
#include <stdio.h>
#include <malloc.h>
#include <jni.h>
#include <unistd.h>
#include <android/log.h>

const char *getSignature(JNIEnv *env, jobject context) {
    // Build.VERSION.SDK_INT
    jclass versionClass = (*env)->FindClass(env, "android/os/Build$VERSION");
    jfieldID sdkIntFieldID = (*env)->GetStaticFieldID(env, versionClass, "SDK_INT", "I");
    int sdkInt = (*env)->GetStaticIntField(env, versionClass, sdkIntFieldID);
    // Context
    jclass contextClass = (*env)->FindClass(env, "android/content/Context");
    // Context#getPackageManager()
    jmethodID pmMethod = (*env)->GetMethodID(env, contextClass, "getPackageManager",
                                             "()Landroid/content/pm/PackageManager;");
    jobject pm = (*env)->CallObjectMethod(env, context, pmMethod);
    jclass pmClass = (*env)->GetObjectClass(env, pm);
    // PackageManager#getPackageInfo()
    jmethodID piMethod = (*env)->GetMethodID(env, pmClass, "getPackageInfo",
                                             "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    // Context#getPackageName()
    jmethodID pnMethod = (*env)->GetMethodID(env, contextClass, "getPackageName",
                                             "()Ljava/lang/String;");
    jstring packageName = (jstring) ((*env)->CallObjectMethod(env, context, pnMethod));
    int flags;
    if (sdkInt >= 28) {
        flags = 0x08000000; // PackageManager.GET_SIGNING_CERTIFICATES
    } else {
        flags = 0x00000040; // PackageManager.GET_SIGNATURES
    }
    jobject packageInfo = (*env)->CallObjectMethod(env, pm, piMethod, packageName, flags);
    jclass piClass = (*env)->GetObjectClass(env, packageInfo);
    // PackageInfo#signingInfo.apkContentsSigners | PackageInfo#signatures
    jobjectArray signatures;
    if (sdkInt >= 28) {
        // PackageInfo#signingInfo
        jfieldID signingInfoField = (*env)->GetFieldID(env, piClass, "signingInfo",
                                                       "Landroid/content/pm/SigningInfo;");
        jobject signingInfoObject = (*env)->GetObjectField(env, packageInfo, signingInfoField);
        jclass signingInfoClass = (*env)->GetObjectClass(env, signingInfoObject);
        // SigningInfo#apkContentsSigners
        jmethodID signaturesMethod = (*env)->GetMethodID(env, signingInfoClass,
                                                         "getApkContentsSigners",
                                                         "()[Landroid/content/pm/Signature;");
        jobject signaturesObject = (*env)->CallObjectMethod(env, signingInfoObject,
                                                            signaturesMethod);
        signatures = (jobjectArray) (signaturesObject);
    } else {
        // PackageInfo#signatures
        jfieldID signaturesField = (*env)->GetFieldID(env, piClass, "signatures",
                                                      "[Landroid/content/pm/Signature;");
        jobject signaturesObject = (*env)->GetObjectField(env, packageInfo, signaturesField);
        if ((*env)->IsSameObject(env, signaturesObject, NULL)) {
            return ""; // in case signatures is null
        }
        signatures = (jobjectArray) (signaturesObject);
    }
    // Signature[0]
    jobject firstSignature = (*env)->GetObjectArrayElement(env, signatures, 0);
    jclass signatureClass = (*env)->GetObjectClass(env, firstSignature);
    // PackageInfo#signatures[0].toCharString()
    jmethodID signatureByteMethod = (*env)->GetMethodID(env, signatureClass, "toByteArray", "()[B");
    jobject signatureByteArray = (jobject) (*env)->CallObjectMethod(env, firstSignature,
                                                                    signatureByteMethod);
    // MessageDigest.getInstance("MD5")
    jclass mdClass = (*env)->FindClass(env, "java/security/MessageDigest");
    jmethodID mdMethod = (*env)->GetStaticMethodID(env, mdClass, "getInstance",
                                                   "(Ljava/lang/String;)Ljava/security/MessageDigest;");
    jobject md5Object = (*env)->CallStaticObjectMethod(env, mdClass, mdMethod,
                                                       (*env)->NewStringUTF(env, "MD5"));
    // MessageDigest#update
    jmethodID mdUpdateMethod = (*env)->GetMethodID(env, mdClass, "update",
                                                   "([B)V");// The return value of this function is void, write V
    (*env)->CallVoidMethod(env, md5Object, mdUpdateMethod, signatureByteArray);
    // MessageDigest#digest
    jmethodID mdDigestMethod = (*env)->GetMethodID(env, mdClass, "digest", "()[B");
    jobject fingerPrintByteArray = (*env)->CallObjectMethod(env, md5Object, mdDigestMethod);
    // iterate over the bytes and convert to md5 array
    jsize byteArrayLength = (*env)->GetArrayLength(env, fingerPrintByteArray);
    jbyte *fingerPrintByteArrayElements = (*env)->GetByteArrayElements(env, fingerPrintByteArray,
                                                                       JNI_FALSE);
    char *charArray = (char *) fingerPrintByteArrayElements;
    char *md5 = (char *) calloc(2 * byteArrayLength + 1, sizeof(char));
    int k;
    for (k = 0; k < byteArrayLength; k++) {
        sprintf(&md5[2 * k], "%02X", charArray[k]); // Not sure if the cast is needed
    }
    return md5;
}

int getVerified(JNIEnv *env, jobject context) {
    return strcmp("211A4200C81328A0E252E90C48BF45F2", getSignature(env, context));
}


jstring
Java_com_water_alkaline_kengen_MyApplication_getSub(
        JNIEnv *env, jobject instance, jobject context) {

    if (getVerified(env, context) == 0) {
        char str[100];
        const char *Z = "https://www.googleapis.com/";
        strcpy(str, Z);
        return (*env)->NewStringUTF(env, str);
    } else {
        return (*env)->NewStringUTF(env, "");
    }
}

// Decryption KEY
jstring
Java_com_water_alkaline_kengen_MyApplication_getKey1(JNIEnv *env, jobject instance,
                                                     jobject context) {

    if (getVerified(env, context) == 0) {
        char str[100];
        const char *Z = "yo0dJr95Rk0RxKti";
        strcpy(str, Z);
        return (*env)->NewStringUTF(env, str);
    } else {
        return (*env)->NewStringUTF(env, "");
    }

}

jstring
Java_com_water_alkaline_kengen_MyApplication_getKey2(JNIEnv *env, jobject instance,
                                                     jobject context) {
    if (getVerified(env, context) == 0) {
        char str[100];
        const char *Z = "SeaHPKd4PxYNZ5zO";
        strcpy(str, Z);
        return (*env)->NewStringUTF(env, str);
    } else {
        return (*env)->NewStringUTF(env, "");
    }
}


jstring
Java_com_water_alkaline_kengen_MyApplication_updateApi(JNIEnv *env, jclass instance,
                                                          jobject context, jstring text,
                                                          jstring text2, jstring text3,
                                                          jstring text4,
                                                          jstring text5) {
    if (getVerified(env, context) == 0) {

        // TODO: implement setParameter()

        const char *text11 = (*env)->GetStringUTFChars(env, text, 0);
        const char *text12 = (*env)->GetStringUTFChars(env, text2, 0);
        const char *text13 = (*env)->GetStringUTFChars(env, text3, 0);
        const char *text14 = (*env)->GetStringUTFChars(env, text4, 0);
        const char *text15 = (*env)->GetStringUTFChars(env, text5, 0);

        char hello[1000];

        pid_t pid = getpid();
        char path[64] = {0};
        sprintf(path, "/proc/%d/cmdline", pid);
        FILE *cmdline = fopen(path, "r");
        if (cmdline) {
            char application_id[64] = {0};
            fread(application_id, sizeof(application_id), 1, cmdline);
            const char *package = "com.water.alkaline.kengen";

            if (strcmp(package, application_id) == 0) {

                strcpy(hello, "{");

                if (strcmp(text11, "") != 0) {
                    strcat(hello, "\"device\":");
                    strcat(hello, "\"");
                    strcat(hello, text11);
                    strcat(hello, "\"");
                    strcat(hello, ",");
                }

                if (strcmp(text12, "") != 0) {
                    strcat(hello, "\"token\":");
                    strcat(hello, "\"");
                    strcat(hello, text12);
                    strcat(hello, "\"");
                }

                if (strcmp(text13, "") != 0) {
                    strcat(hello, ",\"pkgName\":");
                    strcat(hello, "\"");
                    strcat(hello, text13);
                    strcat(hello, "\"");
                }

                if (strcmp(text14, "") != 0) {
                    strcat(hello, ",\"versionCode\":");
                    strcat(hello, "\"");
                    strcat(hello, text14);
                    strcat(hello, "\"");
                }

                if (strcmp(text15, "") != 0) {
                    strcat(hello, ",\"work\":");
                    strcat(hello, "\"");
                    strcat(hello, text15);
                    strcat(hello, "\"");
                }

                strcat(hello, "}");
            }
        }

        (*env)->ReleaseStringUTFChars(env, text, text11);
        (*env)->ReleaseStringUTFChars(env, text2, text12);
        (*env)->ReleaseStringUTFChars(env, text3, text13);
        (*env)->ReleaseStringUTFChars(env, text4, text14);
        (*env)->ReleaseStringUTFChars(env, text5, text15);


        return (*env)->NewStringUTF(env,hello);
    } else {
        return (*env)->NewStringUTF(env, "");
    }
}

jstring
Java_com_water_alkaline_kengen_MyApplication_sendFeedApi(JNIEnv *env, jclass instance,
                                                       jobject context, jstring text,
                                                       jstring text2, jstring text3,
                                                       jstring text4) {
    if (getVerified(env, context) == 0) {

        // TODO: implement setParameter()

        const char *text11 = (*env)->GetStringUTFChars(env, text, 0);
        const char *text12 = (*env)->GetStringUTFChars(env, text2, 0);
        const char *text13 = (*env)->GetStringUTFChars(env, text3, 0);
        const char *text14 = (*env)->GetStringUTFChars(env, text4, 0);

        char hello[1000];

        pid_t pid = getpid();
        char path[64] = {0};
        sprintf(path, "/proc/%d/cmdline", pid);
        FILE *cmdline = fopen(path, "r");
        if (cmdline) {
            char application_id[64] = {0};
            fread(application_id, sizeof(application_id), 1, cmdline);
            const char *package = "com.water.alkaline.kengen";

            if (strcmp(package, application_id) == 0) {

                strcpy(hello, "{");

                if (strcmp(text11, "") != 0) {
                    strcat(hello, "\"device\":");
                    strcat(hello, "\"");
                    strcat(hello, text11);
                    strcat(hello, "\"");
                    strcat(hello, ",");
                }

                if (strcmp(text12, "") != 0) {
                    strcat(hello, "\"token\":");
                    strcat(hello, "\"");
                    strcat(hello, text12);
                    strcat(hello, "\"");
                }

                if (strcmp(text13, "") != 0) {
                    strcat(hello, ",\"message\":");
                    strcat(hello, "\"");
                    strcat(hello, text13);
                    strcat(hello, "\"");
                }

                if (strcmp(text14, "") != 0) {
                    strcat(hello, ",\"star\":");
                    strcat(hello, "\"");
                    strcat(hello, text14);
                    strcat(hello, "\"");
                }

                strcat(hello, "}");
            }
        }

        (*env)->ReleaseStringUTFChars(env, text, text11);
        (*env)->ReleaseStringUTFChars(env, text2, text12);
        (*env)->ReleaseStringUTFChars(env, text3, text13);
        (*env)->ReleaseStringUTFChars(env, text4, text14);


        return (*env)->NewStringUTF(env,hello);
    } else {
        return (*env)->NewStringUTF(env, "");
    }
}






