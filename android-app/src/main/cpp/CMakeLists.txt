cmake_minimum_required(VERSION 3.10.2)

project("clicktrack")

add_library(
        clicktrack SHARED

        Bindings.cpp
        Logger.cpp
        NativeCrash.cpp
        PrimitiveAudioPlayer.cpp
        PrimitiveAudio.cpp
)

find_library(log log)
find_package(oboe REQUIRED CONFIG)

target_include_directories(clicktrack SYSTEM PRIVATE third-party/oboe-extra)
target_link_libraries(clicktrack log oboe::oboe)
