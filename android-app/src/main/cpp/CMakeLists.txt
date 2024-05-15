cmake_minimum_required(VERSION 3.10.2)

project(clicktrack)

add_library(
        clicktrack SHARED

        ResamplerBindings.cpp
        NativeCrash.cpp
)

find_library(log log)

add_subdirectory(third-party/oboe-extra/resampler)

target_link_libraries(clicktrack log resampler)
