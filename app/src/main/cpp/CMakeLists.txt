cmake_minimum_required(VERSION 3.10.2)

project("clicktrack")

add_library(
        clicktrack SHARED

        OboeInit.cpp
        AudioTrackBindings.cpp
        AudioTrack.cpp
)

find_library(log log)
find_package(oboe REQUIRED CONFIG)
target_link_libraries(clicktrack ${log} oboe::oboe)

include_directories(iolib)
