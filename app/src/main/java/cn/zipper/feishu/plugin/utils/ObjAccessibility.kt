package cn.zipper.feishu.plugin.utils

import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import java.lang.Exception

/**
 * Copyright (C), 2016-2020
 * Author: 超人迪加
 * Date: 2020/11/26 3:41 PM
 */
object ObjAccessibility {
    val TAG = ObjAccessibility::class.java.simpleName

    /**
     * 根据View的ID搜索符合条件的节点,精确搜索方式;
     * 这个只适用于自己写的界面，因为ID可能重复
     * api要求18及以上
     *
     * @param viewId
     */
    fun findNodesById(
        rootInActiveWindow: AccessibilityNodeInfo,
        viewId: String?
    ): List<AccessibilityNodeInfo>? {
        val nodeInfo = rootInActiveWindow
        return nodeInfo?.findAccessibilityNodeInfosByViewId(viewId)
    }

    fun findNodesById(
        event: AccessibilityEvent,
        viewId: String?
    ): List<AccessibilityNodeInfo>? {
        val nodeInfo = event.source
        return nodeInfo?.findAccessibilityNodeInfosByViewId(viewId)
    }

    fun findNodesByText(
        rootInActiveWindow: AccessibilityNodeInfo?,
        text: String?
    ): List<AccessibilityNodeInfo>? {
        return rootInActiveWindow?.findAccessibilityNodeInfosByText(text)
    }

    fun findNodesByText(
        event: AccessibilityEvent,
        text: String?
    ): List<AccessibilityNodeInfo?>? {
        val nodeInfo = event.source
        return nodeInfo?.findAccessibilityNodeInfosByText(text)
    }

    fun performClick(
        nodeInfos: List<AccessibilityNodeInfo>?,
        isClickParent: Boolean
    ): Boolean {
        if (nodeInfos != null && !nodeInfos.isEmpty()) {
            var node: AccessibilityNodeInfo
            for (i in nodeInfos.indices) {
                node = nodeInfos[i]
                //打印太多 过滤下
                try {
                    if (node.text != null && !"Đóng".equals(node.text.toString().trim())) {
                        // 获得点击View的类型
                        Log.i(
                            TAG,
                            "View类型：" + node.className
                                    + "  isEnabled= " + node.isEnabled
                                    + "  isClickable= " + node.isClickable
                                    + "  isVisibleToUser= " + node.isVisibleToUser
                                    + "  viewIdResourceName= " + node.viewIdResourceName
                                    + "  text= " + node.text
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                // 进行模拟点击
                if (node.isEnabled && node.isVisibleToUser) {
                    //1.因为imageview给百度设置click false，拿到framelayou还是可以点击到，或者本来就这样设计
                    return if (isClickParent) {
                        if (node.parent.isClickable) {
                            node.parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        } else {
                            false
                        }
                    } else {
                        if (node.isClickable) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        } else {
                            false
                        }
                    }
                }
            }
        }
        return false
    }

    fun performSetText(
        nodeInfos: List<AccessibilityNodeInfo>?,
        text: String
    ): Boolean {
        if (nodeInfos != null && !nodeInfos.isEmpty()) {
            var node: AccessibilityNodeInfo
            for (i in nodeInfos.indices) {
                node = nodeInfos[i]
                // 获得点击View的类型
                Log.i(
                    TAG,
                    "View类型：" + node.className
                            + "  isEnabled= " + node.isEnabled
                            + "  isVisibleToUser= " + node.isVisibleToUser
                )
                // 进行模拟点击
                if (node.isEnabled && node.isVisibleToUser) {
                    val arguments = Bundle()
                    arguments.putCharSequence(
                        AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                        text
                    )
                    return node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                }
            }
        }
        return false
    }

    fun performPaste(
        nodeInfos: List<AccessibilityNodeInfo>?,
        isClickParent: Boolean
    ): Boolean {
        if (nodeInfos != null && !nodeInfos.isEmpty()) {
            var node: AccessibilityNodeInfo
            for (i in nodeInfos.indices) {
                node = nodeInfos[i]
                // 获得点击View的类型
                Log.i(
                    TAG,
                    "View类型：" + node.className
                            + "  isEnabled= " + node.isEnabled
                            + "  isVisibleToUser= " + node.isVisibleToUser
                )
                // 进行模拟点击
                if (node.isEnabled && node.isVisibleToUser) {
                    return if (isClickParent) {
                        node.parent.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
                        node.parent.performAction(AccessibilityNodeInfo.ACTION_PASTE)
                    } else {
                        node.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
                        node.performAction(AccessibilityNodeInfo.ACTION_PASTE)
                    }
                }
            }
        }
        return false
    }
}