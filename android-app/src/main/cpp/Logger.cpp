#include "Logger.h"

namespace {

jmethodID getJavaLogErrorMethod(JNIEnv* env) {
    const auto clazz = env->FindClass("com/vsevolodganin/clicktrack/utils/log/Logger");
    return env->GetMethodID(clazz, "logError", "(Ljava/lang/String;Ljava/lang/String;)V");
}

} //namespace

Logger::Logger(JNIEnv* env, jobject javaLogger)
        : env(env),
          javaLogger(env->NewGlobalRef(javaLogger)),
          javaLogError(getJavaLogErrorMethod(env)) {}

Logger::~Logger() {
    env->DeleteGlobalRef(javaLogger);
}

void Logger::logError(std::string tag, std::string message) {
    env->CallVoidMethod(
            javaLogger,
            javaLogError,
            env->NewStringUTF(tag.c_str()),
            env->NewStringUTF(message.c_str())
    );
}
