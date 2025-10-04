//
// Created by AHMAD ABU HASAN on 10/4/2025.
//

#include <jni.h>
#include <string>

static std::string serverMode;

#define STRINGIFY(x) #x
#define TOSTRING(x) STRINGIFY(x)

extern "C" jstring
Java_com_ahmadabuhasan_pointofsales_utils_AppConfig_baseUrl(
        JNIEnv *env,
        jclass clazz) {
    std::string baseUrl;

    if (serverMode == "dev" || serverMode == "DEV") {
        baseUrl = "https://dev.example.com";
    } else {
        baseUrl = "https://api.example.com";
    }

    return env->NewStringUTF(baseUrl.c_str());
}

extern "C" JNIEXPORT
void JNICALL
Java_com_ahmadabuhasan_pointofsales_utils_AppConfig_setMode(JNIEnv *env, jclass clazz,
                                                            jstring value) {
    const char *customValueStr = env->GetStringUTFChars(value, nullptr);
    serverMode = customValueStr;
    env->
            ReleaseStringUTFChars(value, customValueStr
    );
}

extern "C" jstring
Java_com_ahmadabuhasan_pointofsales_utils_AppConfig_apiKey(
        JNIEnv *env,
        jclass clazz) {
    const char *apiKey = TOSTRING(api_key);
    return env->
            NewStringUTF(apiKey);
}
