package com.ketal.bcrfullscreen;

import android.view.WindowManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class MainHook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!lpparam.packageName.equals(lpparam.processName))
            return;
        if (!lpparam.packageName.equals("android"))
            return;
        try {
            XC_MethodHook hook = new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    WindowManager.LayoutParams attrs =
                            (WindowManager.LayoutParams) getObjectField(param.args[0], "mAttrs");
                    if (attrs.type > WindowManager.LayoutParams.LAST_APPLICATION_WINDOW)
                        return;
                    //XposedBridge.log("Change Window From" + attrs.packageName);
                    attrs.layoutInDisplayCutoutMode = LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                }
            };
            findAndHookMethod(
                    "com.android.server.wm.DisplayPolicy",
                    lpparam.classLoader,
                    "layoutWindowLw",
                    "com.android.server.wm.WindowState",
                    "com.android.server.wm.WindowState",
                    "com.android.server.wm.DisplayFrames",
                    hook
            );
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }
}
