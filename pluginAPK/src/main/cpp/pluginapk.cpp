#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_pluginapk_PluginService_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "From c++ : plugin apk v1.0";
    return env->NewStringUTF(hello.c_str());
}