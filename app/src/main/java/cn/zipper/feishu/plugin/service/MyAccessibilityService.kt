package cn.zipper.feishu.plugin.service

import android.accessibilityservice.AccessibilityService
import android.app.Service
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import cn.zipper.feishu.plugin.BuildConfig


class MyAccessibilityService : AccessibilityService() {

    companion object {
        const val TAG = "MyAccessibilityService"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_NOT_STICKY
    }

    override fun onInterrupt() {
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) {
            return
        }
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onAccessibilityEvent---" + event.className)
        }
        val eventType = event.eventType
        var eventText = ""
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "==============Start====================")
        }
        when (eventType) {
            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                eventText = "TYPE_VIEW_CLICKED"
            }
            AccessibilityEvent.TYPE_VIEW_FOCUSED -> {
                eventText = "TYPE_VIEW_FOCUSED"
            }
            AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> {
                eventText = "TYPE_VIEW_LONG_CLICKED"
            }
            AccessibilityEvent.TYPE_VIEW_SELECTED -> {
                eventText = "TYPE_VIEW_SELECTED"
            }
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
                eventText = "TYPE_VIEW_TEXT_CHANGED"
            }
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                eventText = "TYPE_WINDOW_STATE_CHANGED"
                feishuhongbao(event)
            }
            AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> {
                eventText = "TYPE_NOTIFICATION_STATE_CHANGED"
            }
            AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END -> {
                eventText = "TYPE_TOUCH_EXPLORATION_GESTURE_END"
            }
            AccessibilityEvent.TYPE_ANNOUNCEMENT -> {
                eventText = "TYPE_ANNOUNCEMENT"
            }
            AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START -> {
                eventText = "TYPE_TOUCH_EXPLORATION_GESTURE_START"
            }
            AccessibilityEvent.TYPE_VIEW_HOVER_ENTER -> {
                eventText = "TYPE_VIEW_HOVER_ENTER"
            }
            AccessibilityEvent.TYPE_VIEW_HOVER_EXIT -> {
                eventText = "TYPE_VIEW_HOVER_EXIT"
            }
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                eventText = "TYPE_VIEW_SCROLLED"
            }
            AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> {
                eventText = "TYPE_VIEW_TEXT_SELECTION_CHANGED"
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                eventText = "TYPE_WINDOW_CONTENT_CHANGED"
                feishuhongbao(event)
            }
        }
        eventText = "$eventText:$eventType"
        if (BuildConfig.DEBUG) {
            Log.i(TAG, eventText)
        }
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "=============END=====================")
        }
    }

    private fun feishuhongbao(event: AccessibilityEvent?){

    }

}
