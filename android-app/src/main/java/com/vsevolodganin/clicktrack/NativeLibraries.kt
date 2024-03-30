package com.vsevolodganin.clicktrack

object NativeLibraries {

    fun init() {
        System.loadLibrary("oboe")
        System.loadLibrary("clicktrack")
    }
}
