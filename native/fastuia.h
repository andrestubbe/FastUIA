#ifndef FASTUIA_H
#define FASTUIA_H

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

// Export declarations (Matches fastuia.def)
JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_GetFocusedElement(JNIEnv* env, jobject obj);
JNIEXPORT jstring JNICALL Java_fastuia_FastUIA_GetControlType(JNIEnv* env, jobject obj, jlong elementHandle);
JNIEXPORT jintArray JNICALL Java_fastuia_FastUIA_GetBoundingRect(JNIEnv* env, jobject obj, jlong elementHandle);
JNIEXPORT jstring JNICALL Java_fastuia_FastUIA_GetName(JNIEnv* env, jobject obj, jlong elementHandle);
JNIEXPORT jstring JNICALL Java_fastuia_FastUIA_GetValue(JNIEnv* env, jobject obj, jlong elementHandle);
JNIEXPORT void JNICALL Java_fastuia_FastUIA_SetValue(JNIEnv* env, jobject obj, jlong elementHandle, jstring value);
JNIEXPORT jstring JNICALL Java_fastuia_FastUIA_GetSelection(JNIEnv* env, jobject obj, jlong elementHandle);
JNIEXPORT void JNICALL Java_fastuia_FastUIA_SetSelection(JNIEnv* env, jobject obj, jlong elementHandle, jstring selection);
JNIEXPORT void JNICALL Java_fastuia_FastUIA_Invoke(JNIEnv* env, jobject obj, jlong elementHandle);
JNIEXPORT void JNICALL Java_fastuia_FastUIA_Expand(JNIEnv* env, jobject obj, jlong elementHandle);
JNIEXPORT void JNICALL Java_fastuia_FastUIA_Collapse(JNIEnv* env, jobject obj, jlong elementHandle);
JNIEXPORT void JNICALL Java_fastuia_FastUIA_Scroll(JNIEnv* env, jobject obj, jlong elementHandle, jdouble horizontalPercent, jdouble verticalPercent);
JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_GetParent(JNIEnv* env, jobject obj, jlong elementHandle);
JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_GetFirstChild(JNIEnv* env, jobject obj, jlong elementHandle);
JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_GetNextSibling(JNIEnv* env, jobject obj, jlong elementHandle);
JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_GetPreviousSibling(JNIEnv* env, jobject obj, jlong elementHandle);

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_IsValid(JNIEnv* env, jobject obj, jlong elementHandle);
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_SupportsValue(JNIEnv* env, jobject obj, jlong elementHandle);
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_SupportsInvoke(JNIEnv* env, jobject obj, jlong elementHandle);
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_SupportsExpandCollapse(JNIEnv* env, jobject obj, jlong elementHandle);
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_SupportsScroll(JNIEnv* env, jobject obj, jlong elementHandle);
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_SupportsSelection(JNIEnv* env, jobject obj, jlong elementHandle);

#ifdef __cplusplus
}
#endif

#endif // FASTUIA_H
