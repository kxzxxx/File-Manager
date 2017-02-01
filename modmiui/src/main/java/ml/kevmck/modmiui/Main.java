package ml.kevmck.modmiui;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Main implements IXposedHookLoadPackage {
    private Object obj_basebar = null;

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.android.systemui"))
            return;


        findAndHookMethod("com.android.systemui.statusbar.BaseStatusBar",
                lpparam.classLoader, "resetNotificationPile",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        //      XposedBridge.log("---------kev------replaceHookedMethod");
                        obj_basebar = param.thisObject;

                        return null;
                    }


                });

        findAndHookMethod("com.android.systemui.statusbar.NotificationData",
                lpparam.classLoader,
                "add",
                "com.android.systemui.statusbar.NotificationData$Entry",
                new XC_MethodHook() {


                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {

                        if (obj_basebar != null && param.args[0] != null) {
                            XposedHelpers.callMethod(obj_basebar, "expandView", param.args[0], true);
                            XposedHelpers.callMethod(param.args[0], "setUserExpanded", true);
                        }
                    }


                });


    }
}