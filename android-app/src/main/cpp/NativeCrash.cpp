#include <jni.h>
#include <stdexcept>
#include <string>

namespace {

void crashWithinInternalFunction() {
    throw std::runtime_error{"Test"};
}

jstring native_get_string(JNIEnv* env) {
    std::string s = "Hellooooooooooooooo ";
    std::string_view sv = s + "World\n";

    // BUG: Use-after-free. `sv` holds a dangling reference to the ephemeral
    // string created by `s + "World\n"`. Accessing the data here is a
    // use-after-free.
    return env->NewStringUTF(sv.data());
}

} // namespace

extern "C"
JNIEXPORT void JNICALL
Java_com_vsevolodganin_clicktrack_utils_native_NativeCrashKt_nativeExceptionCrash(JNIEnv* env, jclass clazz) {
    crashWithinInternalFunction();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_vsevolodganin_clicktrack_utils_native_NativeCrashKt_nativeDanglingReferenceCrash(
        JNIEnv* env,
        jclass clazz
) {
    // Repeat the buggy code a few thousand times. GWP-ASan has a small chance
    // of detecting the use-after-free every time it happens. A single user who
    // triggers the use-after-free thousands of times will catch the bug once.
    // Alternatively, if a few thousand users each trigger the bug a single time,
    // you'll also get one report (this is the assumed model).
    jstring return_string;
    for (unsigned i = 0; i < 0x10000; ++i) {
        return_string = native_get_string(env);
    }

    (void) reinterpret_cast<jstring>(env->NewGlobalRef(return_string));
}
