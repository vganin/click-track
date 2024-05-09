project(resampler)

file(GLOB SOURCE_FILES *.cpp)
add_library(resampler ${SOURCE_FILES})
target_include_directories(resampler PUBLIC ${PROJECT_SOURCE_DIR}/..)
