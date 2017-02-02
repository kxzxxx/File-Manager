package ml.kevmck.modmiui;

import android.os.IBinder;

import java.lang.reflect.Field;

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


                        return null;
                    }


                });


        findAndHookMethod("com.android.systemui.statusbar.BaseStatusBar",
                lpparam.classLoader,
                "addNotificationViews", IBinder.class, "com.android.systemui.statusbar.ExpandedNotification"
                ,
                new XC_MethodHook() {


                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {

                        if (param.thisObject == null) return;

                        Object mNotificationData = XposedHelpers.getObjectField(param.thisObject, "mNotificationData");

                        if (mNotificationData == null) return;

                        Object Entry = XposedHelpers.callMethod(mNotificationData, "findByKey", param.args[0]);

                        if (Entry == null) return;

                        XposedHelpers.callMethod(Entry, "setUserExpanded", true);


                        XposedHelpers.callMethod(param.thisObject, "expandView", Entry, true);

                    }



    });


}
}