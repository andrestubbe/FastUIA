#include "fastuia.h"
#include <windows.h>
#include <uiautomationcore.h>
#include <stdio.h>

/**
 * @file fastuia.cpp
 * @brief Native JNI implementation for FastUIA
 */

// Global UI Automation pointer
IUIAutomation* g_pAutomation = NULL;

// ============================================================================
// DLL Entry Point
// ============================================================================
BOOL APIENTRY DllMain(HMODULE hModule, DWORD ul_reason_for_call, LPVOID lpReserved) {
    switch (ul_reason_for_call) {
        case DLL_PROCESS_ATTACH:
            // Initialize UI Automation
            CoInitialize(NULL);
            CoCreateInstance(__uuidof(CUIAutomation), NULL, CLSCTX_ALL, 
                           __uuidof(IUIAutomation), (void**)&g_pAutomation);
            DisableThreadLibraryCalls(hModule);
            break;
        case DLL_PROCESS_DETACH:
            // Cleanup UI Automation
            if (g_pAutomation) {
                g_pAutomation->Release();
                g_pAutomation = NULL;
            }
            CoUninitialize();
            break;
    }
    return TRUE;
}

// ============================================================================
// JNI Implementations
// ============================================================================

JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_GetFocusedElement(JNIEnv* env, jobject obj) {
    if (!g_pAutomation) return 0;
    
    IUIAutomationElement* pFocused = NULL;
    if (SUCCEEDED(g_pAutomation->GetFocusedElement(&pFocused)) && pFocused) {
        return (jlong)pFocused;
    }
    return 0;
}

JNIEXPORT jint JNICALL Java_fastuia_FastUIA_GetControlType(JNIEnv* env, jobject obj, jlong elementHandle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement) return 0;
    
    CONTROLTYPEID controlType;
    if (SUCCEEDED(pElement->get_CurrentControlType(&controlType))) {
        return (jint)controlType;
    }
    return 0;
}

JNIEXPORT jintArray JNICALL Java_fastuia_FastUIA_GetBoundingRectRaw(JNIEnv* env, jobject obj, jlong elementHandle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
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

JNIEXPORT jstring JNICALL Java_fastuia_FastUIA_GetName(JNIEnv* env, jobject obj, jlong elementHandle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement) return NULL;
    
    BSTR bstrName;
    if (SUCCEEDED(pElement->get_CurrentName(&bstrName))) {
        int len = WideCharToMultiByte(CP_UTF8, 0, bstrName, -1, NULL, 0, NULL, NULL);
        char* buffer = new char[len];
        WideCharToMultiByte(CP_UTF8, 0, bstrName, -1, buffer, len, NULL, NULL);
        jstring result = env->NewStringUTF(buffer);
        delete[] buffer;
        SysFreeString(bstrName);
        return result;
    }
    return NULL;
}

JNIEXPORT jstring JNICALL Java_fastuia_FastUIA_GetValue(JNIEnv* env, jobject obj, jlong elementHandle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement) return NULL;
    
    VARIANT varValue;
    VariantInit(&varValue);
    if (SUCCEEDED(pElement->get_CurrentValue(&varValue)) && varValue.vt == VT_BSTR) {
        int len = WideCharToMultiByte(CP_UTF8, 0, varValue.bstrVal, -1, NULL, 0, NULL, NULL);
        char* buffer = new char[len];
        WideCharToMultiByte(CP_UTF8, 0, varValue.bstrVal, -1, buffer, len, NULL, NULL);
        jstring result = env->NewStringUTF(buffer);
        delete[] buffer;
        VariantClear(&varValue);
        return result;
    }
    VariantClear(&varValue);
    return NULL;
}

JNIEXPORT void JNICALL Java_fastuia_FastUIA_SetValue(JNIEnv* env, jobject obj, jlong elementHandle, jstring value) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement || !value) return;
    
    const char* cValue = env->GetStringUTFChars(value, NULL);
    int wlen = MultiByteToWideChar(CP_UTF8, 0, cValue, -1, NULL, 0);
    BSTR bstrValue = SysAllocStringLen(NULL, wlen);
    MultiByteToWideChar(CP_UTF8, 0, cValue, -1, bstrValue, wlen);
    
    IUIAutomationValuePattern* pValuePattern = NULL;
    if (SUCCEEDED(pElement->GetCurrentPattern(UIA_ValuePatternId, (IUnknown**)&pValuePattern))) {
        pValuePattern->SetValue(bstrValue);
        pValuePattern->Release();
    }
    
    env->ReleaseStringUTFChars(value, cValue);
    SysFreeString(bstrValue);
}

JNIEXPORT jstring JNICALL Java_fastuia_FastUIA_GetSelection(JNIEnv* env, jobject obj, jlong elementHandle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement) return NULL;
    
    IUIAutomationSelectionPattern* pSelectionPattern = NULL;
    if (SUCCEEDED(pElement->GetCurrentPattern(UIA_SelectionPatternId, (IUnknown**)&pSelectionPattern))) {
        IUIAutomationElementArray* pSelection = NULL;
        if (SUCCEEDED(pSelectionPattern->GetCurrentSelection(&pSelection))) {
            int count = 0;
            pSelection->get_Length(&count);
            // For simplicity, return count as string
            char buffer[32];
            sprintf_s(buffer, "%d items selected", count);
            pSelection->Release();
            pSelectionPattern->Release();
            return env->NewStringUTF(buffer);
        }
        pSelectionPattern->Release();
    }
    return NULL;
}

JNIEXPORT void JNICALL Java_fastuia_FastUIA_SetSelection(JNIEnv* env, jobject obj, jlong elementHandle, jstring selection) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement) return;
    
    IUIAutomationSelectionItemPattern* pSelectionItemPattern = NULL;
    if (SUCCEEDED(pElement->GetCurrentPattern(UIA_SelectionItemPatternId, (IUnknown**)&pSelectionItemPattern))) {
        pSelectionItemPattern->Select();
        pSelectionItemPattern->Release();
    }
}

JNIEXPORT void JNICALL Java_fastuia_FastUIA_Invoke(JNIEnv* env, jobject obj, jlong elementHandle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement) return;
    
    IUIAutomationInvokePattern* pInvokePattern = NULL;
    if (SUCCEEDED(pElement->GetCurrentPattern(UIA_InvokePatternId, (IUnknown**)&pInvokePattern))) {
        pInvokePattern->Invoke();
        pInvokePattern->Release();
    }
}

JNIEXPORT void JNICALL Java_fastuia_FastUIA_Expand(JNIEnv* env, jobject obj, jlong elementHandle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement) return;
    
    IUIAutomationExpandCollapsePattern* pExpandPattern = NULL;
    if (SUCCEEDED(pElement->GetCurrentPattern(UIA_ExpandCollapsePatternId, (IUnknown**)&pExpandPattern))) {
        pExpandPattern->Expand();
        pExpandPattern->Release();
    }
}

JNIEXPORT void JNICALL Java_fastuia_FastUIA_Collapse(JNIEnv* env, jobject obj, jlong elementHandle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement) return;
    
    IUIAutomationExpandCollapsePattern* pCollapsePattern = NULL;
    if (SUCCEEDED(pElement->GetCurrentPattern(UIA_ExpandCollapsePatternId, (IUnknown**)&pCollapsePattern))) {
        pCollapsePattern->Collapse();
        pCollapsePattern->Release();
    }
}

JNIEXPORT void JNICALL Java_fastuia_FastUIA_Scroll(JNIEnv* env, jobject obj, jlong elementHandle, jdouble horizontalPercent, jdouble verticalPercent) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement) return;
    
    IUIAutomationScrollPattern* pScrollPattern = NULL;
    if (SUCCEEDED(pElement->GetCurrentPattern(UIA_ScrollPatternId, (IUnknown**)&pScrollPattern))) {
        pScrollPattern->SetScrollPercent(horizontalPercent, verticalPercent);
        pScrollPattern->Release();
    }
}

JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_GetParent(JNIEnv* env, jobject obj, jlong elementHandle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement || !g_pAutomation) return 0;
    
    IUIAutomationTreeWalker* pWalker = NULL;
    if (SUCCEEDED(g_pAutomation->get_ControlViewWalker(&pWalker))) {
        IUIAutomationElement* pParent = NULL;
        if (SUCCEEDED(pWalker->GetParentElement(pElement, &pParent))) {
            pWalker->Release();
            return (jlong)pParent;
        }
        pWalker->Release();
    }
    return 0;
}

JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_GetFirstChild(JNIEnv* env, jobject obj, jlong elementHandle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement || !g_pAutomation) return 0;
    
    IUIAutomationTreeWalker* pWalker = NULL;
    if (SUCCEEDED(g_pAutomation->get_ControlViewWalker(&pWalker))) {
        IUIAutomationElement* pFirstChild = NULL;
        if (SUCCEEDED(pWalker->GetFirstChildElement(pElement, &pFirstChild))) {
            pWalker->Release();
            return (jlong)pFirstChild;
        }
        pWalker->Release();
    }
    return 0;
}

JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_GetNextSibling(JNIEnv* env, jobject obj, jlong elementHandle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement || !g_pAutomation) return 0;
    
    IUIAutomationTreeWalker* pWalker = NULL;
    if (SUCCEEDED(g_pAutomation->get_ControlViewWalker(&pWalker))) {
        IUIAutomationElement* pNextSibling = NULL;
        if (SUCCEEDED(pWalker->GetNextSiblingElement(pElement, &pNextSibling))) {
            pWalker->Release();
            return (jlong)pNextSibling;
        }
        pWalker->Release();
    }
    return 0;
}

JNIEXPORT jlong JNICALL Java_fastuia_FastUIA_GetPreviousSibling(JNIEnv* env, jobject obj, jlong elementHandle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement || !g_pAutomation) return 0;
    
    IUIAutomationTreeWalker* pWalker = NULL;
    if (SUCCEEDED(g_pAutomation->get_ControlViewWalker(&pWalker))) {
        IUIAutomationElement* pPrevSibling = NULL;
        if (SUCCEEDED(pWalker->GetPreviousSiblingElement(pElement, &pPrevSibling))) {
            pWalker->Release();
            return (jlong)pPrevSibling;
        }
        pWalker->Release();
    }
    return 0;
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_IsValid(JNIEnv* env, jobject obj, jlong elementHandle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement) return JNI_FALSE;
    
    BOOL isOffscreen = FALSE;
    HRESULT hr = pElement->get_CurrentIsOffscreen(&isOffscreen);
    return SUCCEEDED(hr) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_SupportsValue(JNIEnv* env, jobject obj, jlong elementHandle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement) return JNI_FALSE;
    
    IUIAutomationValuePattern* pPattern = NULL;
    if (SUCCEEDED(pElement->GetCurrentPattern(UIA_ValuePatternId, (IUnknown**)&pPattern))) {
        if (pPattern) pPattern->Release();
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_SupportsInvoke(JNIEnv* env, jobject obj, jlong elementHandle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement) return JNI_FALSE;
    
    IUIAutomationInvokePattern* pPattern = NULL;
    if (SUCCEEDED(pElement->GetCurrentPattern(UIA_InvokePatternId, (IUnknown**)&pPattern))) {
        if (pPattern) pPattern->Release();
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_SupportsExpandCollapse(JNIEnv* env, jobject obj, jlong elementHandle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement) return JNI_FALSE;
    
    IUIAutomationExpandCollapsePattern* pPattern = NULL;
    if (SUCCEEDED(pElement->GetCurrentPattern(UIA_ExpandCollapsePatternId, (IUnknown**)&pPattern))) {
        if (pPattern) pPattern->Release();
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_SupportsScroll(JNIEnv* env, jobject obj, jlong elementHandle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement) return JNI_FALSE;
    
    IUIAutomationScrollPattern* pPattern = NULL;
    if (SUCCEEDED(pElement->GetCurrentPattern(UIA_ScrollPatternId, (IUnknown**)&pPattern))) {
        if (pPattern) pPattern->Release();
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_SupportsSelection(JNIEnv* env, jobject obj, jlong elementHandle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement) return JNI_FALSE;
    
    IUIAutomationSelectionPattern* pPattern = NULL;
    if (SUCCEEDED(pElement->GetCurrentPattern(UIA_SelectionPatternId, (IUnknown**)&pPattern))) {
        if (pPattern) pPattern->Release();
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_SupportsText(JNIEnv* env, jobject obj, jlong elementHandle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement) return JNI_FALSE;
    
    IUIAutomationTextPattern* pPattern = NULL;
    if (SUCCEEDED(pElement->GetCurrentPattern(UIA_TextPatternId, (IUnknown**)&pPattern))) {
        if (pPattern) pPattern->Release();
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_SupportsWindow(JNIEnv* env, jobject obj, jlong elementHandle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement) return JNI_FALSE;
    
    IUIAutomationWindowPattern* pPattern = NULL;
    if (SUCCEEDED(pElement->GetCurrentPattern(UIA_WindowPatternId, (IUnknown**)&pPattern))) {
        if (pPattern) pPattern->Release();
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_SupportsLegacyIAccessible(JNIEnv* env, jobject obj, jlong elementHandle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement) return JNI_FALSE;
    
    IUIAutomationLegacyIAccessiblePattern* pPattern = NULL;
    if (SUCCEEDED(pElement->GetCurrentPattern(UIA_LegacyIAccessiblePatternId, (IUnknown**)&pPattern))) {
        if (pPattern) pPattern->Release();
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_SupportsToggle(JNIEnv* env, jobject obj, jlong elementHandle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement) return JNI_FALSE;
    
    IUIAutomationTogglePattern* pPattern = NULL;
    if (SUCCEEDED(pElement->GetCurrentPattern(UIA_TogglePatternId, (IUnknown**)&pPattern))) {
        if (pPattern) pPattern->Release();
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_fastuia_FastUIA_SupportsRangeValue(JNIEnv* env, jobject obj, jlong elementHandle) {
    IUIAutomationElement* pElement = (IUIAutomationElement*)elementHandle;
    if (!pElement) return JNI_FALSE;
    
    IUIAutomationRangeValuePattern* pPattern = NULL;
    if (SUCCEEDED(pElement->GetCurrentPattern(UIA_RangeValuePatternId, (IUnknown**)&pPattern))) {
        if (pPattern) pPattern->Release();
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

// Event Tracking (Stubs — full implementation requires IUIAutomationEventHandler)

JNIEXPORT void JNICALL Java_fastuia_FastUIA_StartFocusTracking(JNIEnv* env, jobject obj) {
    // TODO: Register IUIAutomationFocusChangedEventHandler
    // This requires implementing a COM event handler class
    printf("[FastUIA] StartFocusTracking called (stub)\n");
}

JNIEXPORT void JNICALL Java_fastuia_FastUIA_StopFocusTracking(JNIEnv* env, jobject obj) {
    // TODO: Unregister focus event handler
    printf("[FastUIA] StopFocusTracking called (stub)\n");
}
