#pragma once
#include <UIAutomation.h>
#include <map>

/**
 * FastUIA Native Core.
 * Manages IUIAutomation instance and pattern caching.
 */
class FastUIA {
public:
    static FastUIA& instance();

    IUIAutomation* automation = nullptr;

    // Pattern accessors
    IUIAutomationTextPattern* getTextPattern(IUIAutomationElement* pElement);
    IUIAutomationValuePattern* getValuePattern(IUIAutomationElement* pElement);
    IUIAutomationInvokePattern* getInvokePattern(IUIAutomationElement* pElement);
    IUIAutomationExpandCollapsePattern* getExpandCollapsePattern(IUIAutomationElement* pElement);
    IUIAutomationScrollPattern* getScrollPattern(IUIAutomationElement* pElement);

    // Helpers
    static void releaseElement(IUIAutomationElement* pElement);

private:
    FastUIA();
    ~FastUIA();
    FastUIA(const FastUIA&) = delete;
    FastUIA& operator=(const FastUIA&) = delete;
};
