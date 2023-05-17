cmake_minimum_required(VERSION 3.10.2)

project("clicktrack")

add_library(
        clicktrack SHARED
        OboeInit.cpp
        AudioTrackBindings.cpp
        AudioTrack.cpp
        NativeCrash.cpp
)

find_library(log log)
find_package(oboe REQUIRED CONFIG)

include_directories(iolib)

target_link_libraries(clicktrack log oboe::oboe)
