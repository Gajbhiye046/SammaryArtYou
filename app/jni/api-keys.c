#include <jni.h>

JNIEXPORT jstring

jstring
Java_com_example_summaryartyou_APIKeyLibrary_00024Companion_getAPIKey(JNIEnv *env, jobject thiz) {
    return (*env)->  NewStringUTF(env, "");
}