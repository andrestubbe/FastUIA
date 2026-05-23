#include "fastuia.h"
#include "fastuia_native.h"
#include <windows.h>
#include <string>
#include <iostream>
#include <jni.h>

// --- Global Context ---
JavaVM* g_vm = NULL;

extern "C" {

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    g_vm = vm;
    return JNI_VERSION_1_8;
}

// --- Core JNI Implementation ---

JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_nativeGetFocusedElement(JNIEnv* env, jobject obj) {
    IUIAutomationElement* pFocused = NULL;
    if (SUCCEEDED(FastUIA::instance().automation->GetFocusedElement(&pFocused)) && pFocused) {
        return (jlong)pFocused;
    }
    return 0;
}

JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_nativeGetRootElement(JNIEnv* env, jobject obj) {
    IUIAutomationElement* pRoot = NULL;
    if (SUCCEEDED(FastUIA::instance().automation->GetRootElement(&pRoot)) && pRoot) {
        return (jlong)pRoot;
    }
    return 0;
}

JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_nativeGetElementFromPoint(JNIEnv* env, jobject obj, jint x, jint y) {
    IUIAutomationElement* pElement = NULL;
    POINT pt = {x, y};
    if (SUCCEEDED(FastUIA::instance().automation->ElementFromPoint(pt, &pElement)) && pElement) {
        return (jlong)pElement;
    }
    return 0;
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeIsValid(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)handle;
    if (!pElement) return JNI_FALSE;
    BOOL isOffscreen = FALSE;
    return SUCCEEDED(pElement->get_CurrentIsOffscreen(&isOffscreen)) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeRelease(JNIEnv* env, jobject obj, jlong handle) {
    FastUIA::releaseElement((IUIAutomationElement*)handle);
}

JNIEXPORT jstring JNICALL Java_fastuia_FastUIA_nativeGetName(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)handle;
    if (!pElement) return NULL;
    BSTR bstrName;
    if (SUCCEEDED(pElement->get_CurrentName(&bstrName)) && bstrName) {
        jstring result = env->NewString((jchar*)bstrName, (jsize)SysStringLen(bstrName));
        SysFreeString(bstrName);
        return result;
    }
    return NULL;
}

JNIEXPORT jint JNICALL Java_fastuia_FastUIA_nativeGetControlType(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)handle;
    if (!pElement) return 0;
    CONTROLTYPEID id;
    if (SUCCEEDED(pElement->get_CurrentControlType(&id))) return (jint)id;
    return 0;
}

JNIEXPORT jintArray JNICALL Java_fastuia_FastUIA_nativeGetBoundingRect(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)handle;
    if (!pElement) return NULL;
    RECT rect;
    if (SUCCEEDED(pElement->get_CurrentBoundingRectangle(&rect))) {
        jintArray result = env->NewIntArray(4);
        jint coords[4] = {(jint)rect.left, (jint)rect.top, (jint)(rect.right - rect.left), (jint)(rect.bottom - rect.top)};
        env->SetIntArrayRegion(result, 0, 4, coords);
        return result;
    }
    return NULL;
}

JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_nativeGetParent(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationTreeWalker* w; FastUIA::instance().automation->get_ControlViewWalker(&w);
    IUIAutomationElement* p = NULL; w->GetParentElement((IUIAutomationElement*)handle, &p);
    w->Release(); return (jlong)p;
}

JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_nativeGetFirstChild(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationTreeWalker* w; FastUIA::instance().automation->get_ControlViewWalker(&w);
    IUIAutomationElement* p = NULL; w->GetFirstChildElement((IUIAutomationElement*)handle, &p);
    w->Release(); return (jlong)p;
}

JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_nativeGetNextSibling(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationTreeWalker* w; FastUIA::instance().automation->get_ControlViewWalker(&w);
    IUIAutomationElement* p = NULL; w->GetNextSiblingElement((IUIAutomationElement*)handle, &p);
    w->Release(); return (jlong)p;
}

JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_nativeGetPreviousSibling(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationTreeWalker* w; FastUIA::instance().automation->get_ControlViewWalker(&w);
    IUIAutomationElement* p = NULL; w->GetPreviousSiblingElement((IUIAutomationElement*)handle, &p);
    w->Release(); return (jlong)p;
}

JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeSetClickThrough(JNIEnv* env, jobject obj, jstring title, jboolean enabled) {
    const char* nativeTitle = env->GetStringUTFChars(title, NULL);
    HWND hwnd = FindWindowA(NULL, nativeTitle);
    if (hwnd) {
        LONG exStyle = GetWindowLong(hwnd, GWL_EXSTYLE);
        if (enabled) {
            exStyle |= (WS_EX_LAYERED | WS_EX_TRANSPARENT);
        } else {
            exStyle &= ~(WS_EX_TRANSPARENT);
        }
        SetWindowLong(hwnd, GWL_EXSTYLE, exStyle);
    }
    env->ReleaseStringUTFChars(title, nativeTitle);
}

// Patterns & Other exports
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsInvoke(JNIEnv* env, jobject obj, jlong h) { return JNI_FALSE; }
JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeInvoke(JNIEnv* env, jobject obj, jlong h) {}
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsValue(JNIEnv* env, jobject obj, jlong h) { return JNI_FALSE; }
JNIEXPORT jstring JNICALL Java_fastuia_FastUIA_nativeGetValue(JNIEnv* env, jobject obj, jlong h) { return NULL; }
JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeSetValue(JNIEnv* env, jobject obj, jlong h, jstring v) {}
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsSelection(JNIEnv* env, jobject obj, jlong h) { return JNI_FALSE; }
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsSelectionItem(JNIEnv* env, jobject obj, jlong h) { return JNI_FALSE; }
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsGrid(JNIEnv* env, jobject obj, jlong h) { return JNI_FALSE; }
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsGridItem(JNIEnv* env, jobject obj, jlong h) { return JNI_FALSE; }
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsWindow(JNIEnv* env, jobject obj, jlong h) { return JNI_FALSE; }
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsLegacyIAccessible(JNIEnv* env, jobject obj, jlong h) { return JNI_FALSE; }
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsToggle(JNIEnv* env, jobject obj, jlong h) { return JNI_FALSE; }
JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsRangeValue(JNIEnv* env, jobject obj, jlong h) { return JNI_FALSE; }

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsExpandCollapse(JNIEnv* env, jobject obj, jlong h) { return JNI_FALSE; }
JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeExpand(JNIEnv* env, jobject obj, jlong h) {}
JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeCollapse(JNIEnv* env, jobject obj, jlong h) {}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsText(JNIEnv* env, jobject obj, jlong h) { return JNI_FALSE; }
JNIEXPORT jstring JNICALL Java_fastuia_FastUIA_nativeGetSelection(JNIEnv* env, jobject obj, jlong h) { return NULL; }
JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeSetSelection(JNIEnv* env, jobject obj, jlong h, jstring v) {}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsScroll(JNIEnv* env, jobject obj, jlong h) { return JNI_FALSE; }
JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeScroll(JNIEnv* env, jobject obj, jlong h, jdouble x, jdouble y) {}

JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeRegisterFocusChanged(JNIEnv* env, jobject obj) {}
JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeRegisterTextChanged(JNIEnv* env, jobject obj) {}
JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeRegisterStructureChanged(JNIEnv* env, jobject obj) {}

JNIEXPORT jstring JNICALL Java_fastuia_FastUIA_nativeGetFrameworkId(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)handle;
    if (!pElement) return NULL;
    BSTR bstr;
    if (SUCCEEDED(pElement->get_CurrentFrameworkId(&bstr)) && bstr) {
        jstring result = env->NewString((jchar*)bstr, (jsize)SysStringLen(bstr));
        SysFreeString(bstr);
        return result;
    }
    return env->NewStringUTF("Unknown");
}

JNIEXPORT jstring JNICALL Java_fastuia_FastUIA_nativeGetAutomationId(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)handle;
    if (!pElement) return NULL;
    BSTR bstr;
    if (SUCCEEDED(pElement->get_CurrentAutomationId(&bstr)) && bstr) {
        jstring result = env->NewString((jchar*)bstr, (jsize)SysStringLen(bstr));
        SysFreeString(bstr);
        return result;
    }
    return env->NewStringUTF("");
}

JNIEXPORT jint JNICALL Java_fastuia_FastUIA_nativeGetProcessId(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)handle;
    if (!pElement) return 0;
    int pid;
    if (SUCCEEDED(pElement->get_CurrentProcessId(&pid))) return (jint)pid;
    return 0;
}

} // extern "C"
