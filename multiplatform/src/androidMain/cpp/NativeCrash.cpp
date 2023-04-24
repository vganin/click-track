#include <jni.h>
#include <stdexcept>

namespace {

void crashWithinInternalFunction() {
    throw std::runtime_error{"Test"};
}

} // namespace

extern "C"
JNIEXPORT void JNICALL
Java_com_vsevolodganin_clicktrack_utils_platform_NativeCrashKt_nativeCrash(JNIEnv* env, jclass clazz) {
    crashWithinInternalFunction();
}
