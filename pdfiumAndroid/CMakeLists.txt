cmake_minimum_required(VERSION 3.4.1)

project(jniPdfium)

set(PDFIUM_LIB_DIR ${CMAKE_CURRENT_SOURCE_DIR}/src/main/jni/lib/${ANDROID_ABI})

# Map ANDROID_ABI to NDK architecture triple for sysroot paths
if(ANDROID_ABI STREQUAL "arm64-v8a")
    set(ARCH_TRIPLE "aarch64-linux-android")
elseif(ANDROID_ABI STREQUAL "armeabi-v7a")
    set(ARCH_TRIPLE "arm-linux-androideabi")
elseif(ANDROID_ABI STREQUAL "x86")
    set(ARCH_TRIPLE "i686-linux-android")
elseif(ANDROID_ABI STREQUAL "x86_64")
    set(ARCH_TRIPLE "x86_64-linux-android")
endif()

set(NDK_STL_DIR "${CMAKE_SYSROOT}/usr/lib/${ARCH_TRIPLE}")

# Import NDK's C++ static runtime directly.
# ANDROID_STL cmake argument is silently ignored by CMake 3.21+ / NDK r25+,
# so we bypass it and declare the libraries explicitly.
add_library(libc++_static STATIC IMPORTED)
set_target_properties(libc++_static PROPERTIES
    IMPORTED_LOCATION "${NDK_STL_DIR}/libc++_static.a")

# libc++abi provides __cxa_* exception ABI symbols not included in libc++_static.a
add_library(libc++abi STATIC IMPORTED)
set_target_properties(libc++abi PROPERTIES
    IMPORTED_LOCATION "${NDK_STL_DIR}/libc++abi.a")

# Pre-built pdfium native libraries
add_library(modpdfium SHARED IMPORTED)
set_target_properties(modpdfium PROPERTIES
    IMPORTED_LOCATION ${PDFIUM_LIB_DIR}/libmodpdfium.so)

add_library(modft2 SHARED IMPORTED)
set_target_properties(modft2 PROPERTIES
    IMPORTED_LOCATION ${PDFIUM_LIB_DIR}/libmodft2.so)

add_library(modpng SHARED IMPORTED)
set_target_properties(modpng PROPERTIES
    IMPORTED_LOCATION ${PDFIUM_LIB_DIR}/libmodpng.so)

# Build the JNI bridge from source
add_library(jniPdfium SHARED
    src/main/jni/src/mainJNILib.cpp)

target_compile_options(jniPdfium PRIVATE -fexceptions)

# HAVE_PTHREADS enables the inline pthread implementations in the local Mutex.h header.
# Newer NDK versions no longer define this macro, causing undefined symbol linker errors.
target_compile_definitions(jniPdfium PRIVATE HAVE_PTHREADS)

# -Wl,-z,max-page-size=16384: required for 16KB page-size devices (Android 15+),
# harmless on older 4KB page-size devices.
set_target_properties(jniPdfium PROPERTIES
    LINK_FLAGS "-Wl,-z,max-page-size=16384")

target_include_directories(jniPdfium PRIVATE
    src/main/jni/include
    src/main/jni/src)

target_link_libraries(jniPdfium
    libc++_static
    libc++abi
    modpdfium
    modft2
    modpng
    android
    log
    jnigraphics)
