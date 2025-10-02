# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.

set(CMAKE_ANDROID_ARM_PAGE_SIZE 16384)

# Default build type is Debug if not specified
if(NOT CMAKE_BUILD_TYPE)
    set(CMAKE_BUILD_TYPE Debug)
endif()

# Define build configurations
if(CMAKE_BUILD_TYPE STREQUAL "Debug")
    message("Building Debug Configuration")
    add_definitions(-DDEBUG)
else()
    message("Building Release Configuration")
    add_definitions(-DRELEASE)
endif()

#[[
add_library( # Sets the name of the library.
        native-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        # Associated headers in the same location as their source
        # file are automatically included.
        src/main/cpp/native-lib.cpp)]]
