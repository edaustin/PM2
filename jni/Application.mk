
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

APP_ABI := armeabi armeabi-v7a #x86 #mips

NDK_TOOLCHAIN_VERSION := clang3.4-obfuscator

include $(BUILD_SHARED_LIBRARY)
