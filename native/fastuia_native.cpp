#include "fastuia_native.h"
#include <iostream>

FastUIA& FastUIA::instance() {
    static FastUIA inst;
    return inst;
}

FastUIA::FastUIA() {
    CoInitialize(NULL);
    HRESULT hr = CoCreateInstance(__uuidof(CUIAutomation), NULL, CLSCTX_INPROC_SERVER,
                                 IID_PPV_ARGS(&automation));
    if (FAILED(hr)) {
        std::cerr << "[FastUIA] Failed to create IUIAutomation instance" << std::endl;
    }
}

FastUIA::~FastUIA() {
    if (automation) {
        automation->Release();
        automation = nullptr;
    }
    CoUninitialize();
}

IUIAutomationTextPattern* FastUIA::getTextPattern(IUIAutomationElement* pElement) {
    IUIAutomationTextPattern* pPattern = nullptr;
    if (pElement && SUCCEEDED(pElement->GetCurrentPattern(UIA_TextPatternId, (IUnknown**)&pPattern))) {
        return pPattern;
    }
    return nullptr;
}

IUIAutomationValuePattern* FastUIA::getValuePattern(IUIAutomationElement* pElement) {
    IUIAutomationValuePattern* pPattern = nullptr;
    if (pElement && SUCCEEDED(pElement->GetCurrentPattern(UIA_ValuePatternId, (IUnknown**)&pPattern))) {
        return pPattern;
    }
    return nullptr;
}

IUIAutomationInvokePattern* FastUIA::getInvokePattern(IUIAutomationElement* pElement) {
    IUIAutomationInvokePattern* pPattern = nullptr;
    if (pElement && SUCCEEDED(pElement->GetCurrentPattern(UIA_InvokePatternId, (IUnknown**)&pPattern))) {
        return pPattern;
    }
    return nullptr;
}

IUIAutomationExpandCollapsePattern* FastUIA::getExpandCollapsePattern(IUIAutomationElement* pElement) {
    IUIAutomationExpandCollapsePattern* pPattern = nullptr;
    if (pElement && SUCCEEDED(pElement->GetCurrentPattern(UIA_ExpandCollapsePatternId, (IUnknown**)&pPattern))) {
        return pPattern;
    }
    return nullptr;
}

IUIAutomationScrollPattern* FastUIA::getScrollPattern(IUIAutomationElement* pElement) {
    IUIAutomationScrollPattern* pPattern = nullptr;
    if (pElement && SUCCEEDED(pElement->GetCurrentPattern(UIA_ScrollPatternId, (IUnknown**)&pPattern))) {
        return pPattern;
    }
    return nullptr;
}

void FastUIA::releaseElement(IUIAutomationElement* pElement) {
    if (pElement) {
        pElement->Release();
    }
}
