package com.companion.aura

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AuraScreenParserService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val root = rootInActiveWindow ?: return
        val builder = StringBuilder()
        collectVisibleText(root, builder, depth = 0)
        val filtered = AuraPrivacyFilter.redact(builder.toString()).take(1000)
        if (filtered.isBlank()) return

        val intent = Intent(AuraContracts.ACTION_SCREEN_CONTEXT).apply {
            setPackage(packageName)
            putExtra(AuraContracts.EXTRA_CONTEXT_PAYLOAD, filtered)
        }
        sendBroadcast(intent)
    }

    private fun collectVisibleText(node: AccessibilityNodeInfo, builder: StringBuilder, depth: Int) {
        if (depth > 8) return
        if (node.isPassword || node.isEditable) return

        node.text?.toString()?.takeIf { it.isNotBlank() }?.let { builder.append(' ').append(it) }
        node.contentDescription?.toString()?.takeIf { it.isNotBlank() }?.let { builder.append(' ').append(it) }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            collectVisibleText(child, builder, depth + 1)
        }
    }

    override fun onInterrupt() = Unit
}
