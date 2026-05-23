#ifndef FASTUIA_H
#define FASTUIA_H

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

// Core
JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_nativeGetFocusedElement(JNIEnv*, jobject);
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeIsValid(JNIEnv*, jobject, jlong);
JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeRelease(JNIEnv*, jobject, jlong);

// Properties
JNIEXPORT jstring JNICALL Java_fastuia_FastUIA_nativeGetName(JNIEnv*, jobject, jlong);
JNIEXPORT jint JNICALL Java_fastuia_FastUIA_nativeGetControlType(JNIEnv*, jobject, jlong);
JNIEXPORT jintArray JNICALL Java_fastuia_FastUIA_nativeGetBoundingRect(JNIEnv*, jobject, jlong);

// ValuePattern
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsValue(JNIEnv*, jobject, jlong);
JNIEXPORT jstring JNICALL Java_fastuia_FastUIA_nativeGetValue(JNIEnv*, jobject, jlong);
JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeSetValue(JNIEnv*, jobject, jlong, jstring);

// TextPattern
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsText(JNIEnv*, jobject, jlong);
JNIEXPORT jstring JNICALL Java_fastuia_FastUIA_nativeGetSelection(JNIEnv*, jobject, jlong);
JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeSetSelection(JNIEnv*, jobject, jlong, jstring);

// InvokePattern
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsInvoke(JNIEnv*, jobject, jlong);
JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeInvoke(JNIEnv*, jobject, jlong);

// ExpandCollapsePattern
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsExpandCollapse(JNIEnv*, jobject, jlong);
JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeExpand(JNIEnv*, jobject, jlong);
JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeCollapse(JNIEnv*, jobject, jlong);

// ScrollPattern
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsScroll(JNIEnv*, jobject, jlong);
JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeScroll(JNIEnv*, jobject, jlong, jdouble, jdouble);

// Other Patterns
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsSelection(JNIEnv*, jobject, jlong);
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsWindow(JNIEnv*, jobject, jlong);
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsLegacyIAccessible(JNIEnv*, jobject, jlong);
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsToggle(JNIEnv*, jobject, jlong);
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsRangeValue(JNIEnv*, jobject, jlong);
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsGrid(JNIEnv*, jobject, jlong);
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsGridItem(JNIEnv*, jobject, jlong);
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsSelectionItem(JNIEnv*, jobject, jlong);

// Tree Navigation
JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_nativeGetParent(JNIEnv*, jobject, jlong);
JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_nativeGetFirstChild(JNIEnv*, jobject, jlong);
JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_nativeGetNextSibling(JNIEnv*, jobject, jlong);
JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_nativeGetPreviousSibling(JNIEnv*, jobject, jlong);

// Events
JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeRegisterFocusChanged(JNIEnv*, jobject);
JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeRegisterTextChanged(JNIEnv*, jobject);
JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeRegisterStructureChanged(JNIEnv*, jobject);

// Extended Properties
JNIEXPORT jstring JNICALL Java_fastuia_FastUIA_nativeGetFrameworkId(JNIEnv*, jobject, jlong);
JNIEXPORT jstring JNICALL Java_fastuia_FastUIA_nativeGetAutomationId(JNIEnv*, jobject, jlong);
JNIEXPORT jint JNICALL Java_fastuia_FastUIA_nativeGetProcessId(JNIEnv*, jobject, jlong);

#ifdef __cplusplus
}
#endif

#endif // FASTUIA_H
