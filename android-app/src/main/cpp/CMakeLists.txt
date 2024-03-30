cmake_minimum_required(VERSION 3.10.2)

project("clicktrack")

add_library(
        clicktrack SHARED

        NativeCrash.cpp
        PrimitiveAudioPlayerBinding.cpp
        PrimitiveAudioPlayer.cpp
        PrimitiveAudio.cpp
)

add_library(
        oboe-extra STATIC IMPORTED

        third-party/oboe-extra/resampler/IntegerRatio.cpp
        third-party/oboe-extra/resampler/LinearResampler.cpp
        third-party/oboe-extra/resampler/MultiChannelResampler.cpp
        third-party/oboe-extra/resampler/PolyphaseResampler.cpp
        third-party/oboe-extra/resampler/PolyphaseResamplerMono.cpp
        third-party/oboe-extra/resampler/PolyphaseResamplerStereo.cpp
        third-party/oboe-extra/resampler/SincResampler.cpp
        third-party/oboe-extra/resampler/SincResamplerStereo.cpp
)

find_library(log log)
find_package(oboe REQUIRED CONFIG)

target_include_directories(clicktrack SYSTEM PRIVATE third-party/oboe-extra)
target_link_libraries(clicktrack log oboe::oboe)
