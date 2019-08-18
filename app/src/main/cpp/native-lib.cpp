#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_charles_crazyguy_activities_PlayVideoActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
