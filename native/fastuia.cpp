#include "fastuia.h"
#include "fastuia_native.h"
#include <windows.h>
#include <string>

// Helper to convert BSTR to jstring
jstring BSTRToJString(JNIEnv* env, BSTR bstr) {
    if (!bstr) return NULL;
    int len = WideCharToMultiByte(CP_UTF8, 0, bstr, -1, NULL, 0, NULL, NULL);
    char* buffer = new char[len];
    WideCharToMultiByte(CP_UTF8, 0, bstr, -1, buffer, len, NULL, NULL);
    jstring result = env->NewStringUTF(buffer);
    delete[] buffer;
    return result;
}

// Helper to convert jstring to BSTR
BSTR JStringToBSTR(JNIEnv* env, jstring str) {
    if (!str) return NULL;
    const char* cStr = env->GetStringUTFChars(str, NULL);
    int wlen = MultiByteToWideChar(CP_UTF8, 0, cStr, -1, NULL, 0);
    BSTR bstr = SysAllocStringLen(NULL, wlen);
    MultiByteToWideChar(CP_UTF8, 0, cStr, -1, bstr, wlen);
    env->ReleaseStringUTFChars(str, cStr);
    return bstr;
}

// --- JNI Implementation ---

JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_nativeGetFocusedElement(JNIEnv* env, jobject obj) {
    IUIAutomation* automation = FastUIA::instance().automation;
    if (!automation) return 0;

    IUIAutomationElement* pFocused = NULL;
    if (SUCCEEDED(automation->GetFocusedElement(&pFocused)) && pFocused) {
        return (jlong)pFocused;
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
    if (SUCCEEDED(pElement->get_CurrentName(&bstrName))) {
        jstring result = BSTRToJString(env, bstrName);
        SysFreeString(bstrName);
        return result;
    }
    return NULL;
}

JNIEXPORT jint JNICALL Java_fastuia_FastUIA_nativeGetControlType(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)handle;
    if (!pElement) return 0;
    CONTROLTYPEID typeId;
    if (SUCCEEDED(pElement->get_CurrentControlType(&typeId))) {
        return (jint)typeId;
    }
    return 0;
}

JNIEXPORT jintArray JNICALL Java_fastuia_FastUIA_nativeGetBoundingRect(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)handle;
    if (!pElement) return NULL;
    RECT rect;
    if (SUCCEEDED(pElement->get_CurrentBoundingRectangle(&rect))) {
        jintArray result = env->NewIntArray(4);
        jint values[4] = {rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top};
        env->SetIntArrayRegion(result, 0, 4, values);
        return result;
    }
    return NULL;
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsSelection(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationSelectionPattern* pPattern = NULL;
    if (SUCCEEDED(((IUIAutomationElement*)handle)->GetCurrentPattern(UIA_SelectionPatternId, (IUnknown**)&pPattern)) && pPattern) {
        pPattern->Release();
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsWindow(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationWindowPattern* pPattern = NULL;
    if (SUCCEEDED(((IUIAutomationElement*)handle)->GetCurrentPattern(UIA_WindowPatternId, (IUnknown**)&pPattern)) && pPattern) {
        pPattern->Release();
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsLegacyIAccessible(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationLegacyIAccessiblePattern* pPattern = NULL;
    if (SUCCEEDED(((IUIAutomationElement*)handle)->GetCurrentPattern(UIA_LegacyIAccessiblePatternId, (IUnknown**)&pPattern)) && pPattern) {
        pPattern->Release();
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsToggle(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationTogglePattern* pPattern = NULL;
    if (SUCCEEDED(((IUIAutomationElement*)handle)->GetCurrentPattern(UIA_TogglePatternId, (IUnknown**)&pPattern)) && pPattern) {
        pPattern->Release();
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsRangeValue(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationRangeValuePattern* pPattern = NULL;
    if (SUCCEEDED(((IUIAutomationElement*)handle)->GetCurrentPattern(UIA_RangeValuePatternId, (IUnknown**)&pPattern)) && pPattern) {
        pPattern->Release();
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsGrid(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationGridPattern* pPattern = NULL;
    if (SUCCEEDED(((IUIAutomationElement*)handle)->GetCurrentPattern(UIA_GridPatternId, (IUnknown**)&pPattern)) && pPattern) {
        pPattern->Release();
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsGridItem(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationGridItemPattern* pPattern = NULL;
    if (SUCCEEDED(((IUIAutomationElement*)handle)->GetCurrentPattern(UIA_GridItemPatternId, (IUnknown**)&pPattern)) && pPattern) {
        pPattern->Release();
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsSelectionItem(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationSelectionItemPattern* pPattern = NULL;
    if (SUCCEEDED(((IUIAutomationElement*)handle)->GetCurrentPattern(UIA_SelectionItemPatternId, (IUnknown**)&pPattern)) && pPattern) {
        pPattern->Release();
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsValue(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationValuePattern* pPattern = FastUIA::instance().getValuePattern((IUIAutomationElement*)handle);
    if (pPattern) {
        pPattern->Release();
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT jstring JNICALL Java_fastuia_FastUIA_nativeGetValue(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationValuePattern* pPattern = FastUIA::instance().getValuePattern((IUIAutomationElement*)handle);
    if (pPattern) {
        BSTR bstrValue;
        if (SUCCEEDED(pPattern->get_CurrentValue(&bstrValue))) {
            jstring result = BSTRToJString(env, bstrValue);
            SysFreeString(bstrValue);
            pPattern->Release();
            return result;
        }
        pPattern->Release();
    }
    return NULL;
}

JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeSetValue(JNIEnv* env, jobject obj, jlong handle, jstring value) {
    IUIAutomationValuePattern* pPattern = FastUIA::instance().getValuePattern((IUIAutomationElement*)handle);
    if (pPattern) {
        BSTR bstrValue = JStringToBSTR(env, value);
        pPattern->SetValue(bstrValue);
        SysFreeString(bstrValue);
        pPattern->Release();
    }
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsText(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationTextPattern* pPattern = FastUIA::instance().getTextPattern((IUIAutomationElement*)handle);
    if (pPattern) {
        pPattern->Release();
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT jstring JNICALL Java_fastuia_FastUIA_nativeGetSelection(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationTextPattern* pPattern = FastUIA::instance().getTextPattern((IUIAutomationElement*)handle);
    if (pPattern) {
        IUIAutomationTextRangeArray* pRanges = NULL;
        if (SUCCEEDED(pPattern->GetSelection(&pRanges)) && pRanges) {
            int count = 0;
            pRanges->get_Length(&count);
            pRanges->Release();
            pPattern->Release();
            
            char buffer[64];
            sprintf_s(buffer, "Selection: %d ranges", count);
            return env->NewStringUTF(buffer);
        }
        pPattern->Release();
    }
    return NULL;
}

JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeSetSelection(JNIEnv* env, jobject obj, jlong handle, jstring text) {
    // Requires complex TextRange manipulation
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsInvoke(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationInvokePattern* pPattern = FastUIA::instance().getInvokePattern((IUIAutomationElement*)handle);
    if (pPattern) {
        pPattern->Release();
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeInvoke(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationInvokePattern* pPattern = FastUIA::instance().getInvokePattern((IUIAutomationElement*)handle);
    if (pPattern) {
        pPattern->Invoke();
        pPattern->Release();
    }
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsExpandCollapse(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationExpandCollapsePattern* pPattern = FastUIA::instance().getExpandCollapsePattern((IUIAutomationElement*)handle);
    if (pPattern) {
        pPattern->Release();
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeExpand(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationExpandCollapsePattern* pPattern = FastUIA::instance().getExpandCollapsePattern((IUIAutomationElement*)handle);
    if (pPattern) {
        pPattern->Expand();
        pPattern->Release();
    }
}

JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeCollapse(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationExpandCollapsePattern* pPattern = FastUIA::instance().getExpandCollapsePattern((IUIAutomationElement*)handle);
    if (pPattern) {
        pPattern->Collapse();
        pPattern->Release();
    }
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_nativeSupportsScroll(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationScrollPattern* pPattern = FastUIA::instance().getScrollPattern((IUIAutomationElement*)handle);
    if (pPattern) {
        pPattern->Release();
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeScroll(JNIEnv* env, jobject obj, jlong handle, jdouble h, jdouble v) {
    IUIAutomationScrollPattern* pPattern = FastUIA::instance().getScrollPattern((IUIAutomationElement*)handle);
    if (pPattern) {
        pPattern->SetScrollPercent(h, v);
        pPattern->Release();
    }
}

JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_nativeGetParent(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationTreeWalker* pWalker = NULL;
    if (SUCCEEDED(FastUIA::instance().automation->get_ControlViewWalker(&pWalker))) {
        IUIAutomationElement* pParent = NULL;
        if (SUCCEEDED(pWalker->GetParentElement((IUIAutomationElement*)handle, &pParent))) {
            pWalker->Release();
            return (jlong)pParent;
        }
        pWalker->Release();
    }
    return 0;
}

JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_nativeGetFirstChild(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationTreeWalker* pWalker = NULL;
    if (SUCCEEDED(FastUIA::instance().automation->get_ControlViewWalker(&pWalker))) {
        IUIAutomationElement* pFirst = NULL;
        if (SUCCEEDED(pWalker->GetFirstChildElement((IUIAutomationElement*)handle, &pFirst))) {
            pWalker->Release();
            return (jlong)pFirst;
        }
        pWalker->Release();
    }
    return 0;
}

JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_nativeGetNextSibling(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationTreeWalker* pWalker = NULL;
    if (SUCCEEDED(FastUIA::instance().automation->get_ControlViewWalker(&pWalker))) {
        IUIAutomationElement* pNext = NULL;
        if (SUCCEEDED(pWalker->GetNextSiblingElement((IUIAutomationElement*)handle, &pNext))) {
            pWalker->Release();
            return (jlong)pNext;
        }
        pWalker->Release();
    }
    return 0;
}

JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_nativeGetPreviousSibling(JNIEnv* env, jobject obj, jlong handle) {
    IUIAutomationTreeWalker* pWalker = NULL;
    if (SUCCEEDED(FastUIA::instance().automation->get_ControlViewWalker(&pWalker))) {
        IUIAutomationElement* pPrev = NULL;
        if (SUCCEEDED(pWalker->GetPreviousSiblingElement((IUIAutomationElement*)handle, &pPrev))) {
            pWalker->Release();
            return (jlong)pPrev;
        }
        pWalker->Release();
    }
    return 0;
}

JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeRegisterFocusChanged(JNIEnv* env, jobject obj) {
    // Stub
}

JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeRegisterTextChanged(JNIEnv* env, jobject obj) {
    // Stub
}

JNIEXPORT void JNICALL Java_fastuia_FastUIA_nativeRegisterStructureChanged(JNIEnv* env, jobject obj) {
    // Stub
}
