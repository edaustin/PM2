LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)  
LOCAL_LDLIBS := -llog
LOCAL_MODULE    := cEngine
LOCAL_SRC_FILES := cEngine.c

#LOCAL_LDLIBS := -static
LOCAL_CFLAGS := -mllvm -xse -mllvm -bcf -mllvm -sub -mllvm -fla
include $(BUILD_SHARED_LIBRARY)

#LOCAL_PATH := $(call my-dir)  
include $(CLEAR_VARS)
#LOCAL_LDLIBS := -llog  
LOCAL_MODULE    := equityAnalysis
LOCAL_SRC_FILES := equityAnalysis.c

#LOCAL_LDLIBS := -static
LOCAL_CFLAGS := -mllvm -xse -mllvm -bcf -mllvm -sub #-mllvm -fla
include $(BUILD_SHARED_LIBRARY)

#LOCAL_PATH := $(call my-dir)  
include $(CLEAR_VARS)
#LOCAL_LDLIBS := -llog  
LOCAL_MODULE    := gCx
LOCAL_SRC_FILES := gCx.c

#LOCAL_LDLIBS := -static
LOCAL_CFLAGS := -mllvm -xse -mllvm -fla -mllvm -bcf -mllvm -sub
include $(BUILD_SHARED_LIBRARY)
