package com.ehart.dingclockinsup.service;

import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.ehart.dingclockinsup.R;
import com.ehart.dingclockinsup.utils.ShellUtils;

import java.util.ArrayList;
import java.util.List;

import static android.view.accessibility.AccessibilityEvent.TYPE_VIEW_SCROLLED;
import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;

/**
 * 检测钉钉的
 * Created by ehart on 17/11/11.
 */

public class DingAccessibilityService extends BaseAccessibilityService {
    private static final String TAG = "DingAccessibilityServic";
    private String mCheckInKey;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected");
        mCheckInKey = getString(R.string.ding_check_in_btn);
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
        } else if (eventType == TYPE_WINDOW_CONTENT_CHANGED) {
            AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
            if (accessibilityNodeInfo != null) {
                AccessibilityNodeInfo nodeInfo = findCheckInBtn(accessibilityNodeInfo);
                Log.d(TAG, "TYPE_WINDOW_CONTENT_CHANGED: find nodeInfo:" + nodeInfo);
                if (nodeInfo != null) {
                    Rect rect = new Rect();
                    nodeInfo.getBoundsInScreen(rect);
                    doShellCmdInputTap(rect.centerX(),rect.centerY());
                }
            }
        } else if (eventType == TYPE_VIEW_SCROLLED) {
//            AccessibilityNodeInfo nodeInfo = findCheckInBtn();
//            Log.d(TAG, "TYPE_VIEW_SCROLLED: find nodeInfo:" + nodeInfo);
//            if (nodeInfo != null) {
//                performViewClick(nodeInfo);
//            }
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
                    nodeInfo.getBoundsInScreen(r);
                    int left = r.left;
                    int right = r.right;
                    int top = r.top;
                    int bottom = r.bottom;
                    int width = r.width();
                    if (width > rootWidth / 4) {
                        Log.d(TAG, "findBottomWork: 找到一个工作按钮，太宽了，pass," + nodeInfo.getText() + "," + r);
                        continue;
                    }
                    int middleX = (left + right) / 2;
                    if (middleX < rootWidth / 2 - 5 || middleX > rootWidth / 2 + 5) {
                        Log.d(TAG, "findBottomWork: 找到一个工作按钮，不是居中的，pass," + nodeInfo.getText() + "," + r);
                        continue;
                    }
                    int middleY = (top + bottom) / 2;
                    if (middleY < rootHeight * 3 / 4) {
                        Log.d(TAG, "findBottomWork: 找到一个工作按钮，太矮了，pass," + nodeInfo.getText() + "," + r);
                        continue;
                    }
                    Log.i(TAG, "findBottomWork: nodeInfo bounds:" + r);
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    private AccessibilityNodeInfo findCheckInBtn() {
        String key = getString(R.string.ding_check_in_btn);
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }
        Rect rootRect = new Rect();
        accessibilityNodeInfo.getBoundsInScreen(rootRect);
        int rootWidth = rootRect.width();
        int rootHeight = rootRect.height();

        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(key);
        findCheckInBtn();
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
                    if (width > rootWidth / 3) {
                        Log.d(TAG, "findBottomWork: 找到一个打卡按钮，太宽了，pass," + nodeInfo.getText() + "," + r);
                        continue;
                    }
                    int middleX = (left + right) / 2;
                    if (middleX > rootWidth / 4) {
                        Log.d(TAG, "findBottomWork: 找到一个打卡按钮，不是靠左的，pass," + nodeInfo.getText() + "," + r);
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

    private AccessibilityNodeInfo findCheckInBtn(AccessibilityNodeInfo nodeInfo) {

        int childCount = nodeInfo.getChildCount();
        if (childCount != 0) {
            if (nodeInfo.getClassName().toString().contains("WebView")) {
                Log.d(TAG, "findCheckInBtn: WebView" + nodeInfo);
                if (nodeInfo.getChildCount() > 11) {
                    AccessibilityNodeInfo checkInNodeInfo = nodeInfo.getChild(13);
                    if (checkInNodeInfo == null) {
                        return null;
                    }
                    if (checkInNodeInfo.getContentDescription().equals(mCheckInKey)) {
                        return checkInNodeInfo;
                    } else {
                        Log.d(TAG, "findCheckInBtn: 内容是:" + checkInNodeInfo.getContentDescription());
                    }
                } else {
                    Log.d(TAG, "findCheckInBtn: 个数不足？");
                }
            }
            for (int x = 0; x < childCount; x++) {
                AccessibilityNodeInfo info = findCheckInBtn(nodeInfo.getChild(x));
                if (info != null) {
                    return info;
                }
            }
        }

        return null;

    }

    private ShellUtils.CommandResult doShellCmdInputTap(int x, int y) {
        List<String> mCmds = new ArrayList<>();
        mCmds.add("input tap " + x + " " + y);
        ShellUtils.CommandResult mCommandResult = ShellUtils.execCmd(mCmds, true);

        Log.i("Infoss", "comm:" + mCommandResult.toString());
        return mCommandResult;
    }

    @Override
    public void onInterrupt() {

    }
}
