package com.ehart.dingclockinsup.service;

import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.ehart.dingclockinsup.R;

import java.util.List;

import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;

/**
 * 检测钉钉的
 * Created by ehart on 17/11/11.
 */

public class DingAccessibilityService extends BaseAccessibilityService {
    private static final String TAG = "DingAccessibilityServic";

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected");
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        if (eventType == TYPE_WINDOW_STATE_CHANGED) {
            AccessibilityNodeInfo nodeInfo = findBottomWork();

            Log.d(TAG, "TYPE_WINDOW_STATE_CHANGED: find nodeInfo:" + nodeInfo);
            if (nodeInfo != null) {
                performViewClick(nodeInfo);
            }
        }
    }

    private AccessibilityNodeInfo findBottomWork() {
        String key = getString(R.string.ding_work_tab);
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }
        Rect rootRect = new Rect();
        accessibilityNodeInfo.getBoundsInScreen(rootRect);
        int rootWidth = rootRect.width();
        int rootHeight = rootRect.height();

        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(key);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null && (nodeInfo.isClickable())) {

                    Rect r = new Rect();
                    nodeInfo.getBoundsInScreen(r);
                    int left = r.left;
                    int right = r.right;
                    int top = r.top;
                    int bottom = r.bottom;
                    int width = r.width();
                    if (width > rootWidth / 6) {
                        Log.d(TAG, "findBottomWork: 找到一个工作按钮，太宽了，pass," + nodeInfo.getText());
                        continue;
                    }
                    int middleX = (left + right) / 2;
                    if (middleX < rootWidth / 2 - 5 || middleX > rootWidth / 2 + 5) {
                        Log.d(TAG, "findBottomWork: 找到一个工作按钮，不是居中的，pass," + nodeInfo.getText());
                        continue;
                    }
                    int middleY = (top + bottom) / 2;
                    if (middleY < rootHeight * 3 / 4) {
                        Log.d(TAG, "findBottomWork: 找到一个工作按钮，太矮了，pass," + nodeInfo.getText());
                        continue;
                    }
                    Log.i(TAG, "findBottomWork: nodeInfo bounds:" + r);
                    Log.i(TAG, "findBottomWork: rootRect bounds:" + rootRect);
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    @Override
    public void onInterrupt() {

    }
}
