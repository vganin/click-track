#ifndef CLICKTRACK_LOGGER_H
#define CLICKTRACK_LOGGER_H

#include <jni.h>
#include <string>

class Logger {
public:
    explicit Logger(JNIEnv* env, jobject javaLogger);
    ~Logger();

    void logError(std::string tag, std::string message);

private:
    JNIEnv* env;
    jobject javaLogger;
    jmethodID javaLogError;
};


#endif //CLICKTRACK_LOGGER_H
