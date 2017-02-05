package ml.kevmck.modmiui;

import android.os.IBinder;


import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Main implements IXposedHookLoadPackage {

  private   boolean b_expended=true;
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.android.systemui"))
            return;


        findAndHookMethod("com.android.systemui.statusbar.BaseStatusBar",
                lpparam.classLoader, "resetNotificationPile",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {


                        Object mNotificationData = XposedHelpers.getObjectField(param.thisObject, "mNotificationData");

                        if (mNotificationData == null) return null;

                        updateExpansionStates(param.thisObject, mNotificationData);
                        return null;
                    }


                })
        ;
        findAndHookMethod("com.android.systemui.statusbar.phone.PhoneStatusBar",
                lpparam.classLoader,
                "updateExpandedViewPos", int.class,

                new XC_MethodHook() {


                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {



                        Object mNotificationData = XposedHelpers.getObjectField(param.thisObject, "mNotificationData");

                        if (mNotificationData == null) return;

                        updateExpansionStates(param.thisObject, mNotificationData);


                    }


                });
        findAndHookMethod("com.android.systemui.statusbar.BaseStatusBar",
                lpparam.classLoader,
                "removeNotificationViews", IBinder.class
                ,
                new XC_MethodHook() {


                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {



                        Object mNotificationData = XposedHelpers.getObjectField(param.thisObject, "mNotificationData");

                        if (mNotificationData == null) return;

                        updateExpansionStates(param.thisObject, mNotificationData);


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



                        Object mNotificationData = XposedHelpers.getObjectField(param.thisObject, "mNotificationData");

                        if (mNotificationData == null) return;

                        Object Entry = XposedHelpers.callMethod(mNotificationData, "findByKey", param.args[0]);

                        if (Entry == null) return;

                        XposedHelpers.callMethod(Entry, "setUserExpanded", true);

                        updateExpansionStates(param.thisObject, mNotificationData);


                    }


                });

        findAndHookMethod("com.android.systemui.statusbar.phone.PhoneStatusBar",
                lpparam.classLoader,
                "updateNotification", IBinder.class, "com.android.systemui.statusbar.ExpandedNotification"
                ,
                new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {


                            Object mNotificationData = XposedHelpers.getObjectField(param.thisObject, "mNotificationData");

                        if (mNotificationData != null) {
                            Object Entry = XposedHelpers.callMethod(mNotificationData, "findByKey", param.args[0]);
                            if (Entry != null) {
                                b_expended = ((boolean) XposedHelpers.callMethod(Entry, "userExpanded"));





                        }
                        }
                    }
                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {



                        Object mNotificationData = XposedHelpers.getObjectField(param.thisObject, "mNotificationData");

                        if (mNotificationData == null) return;

                        Object Entry = XposedHelpers.callMethod(mNotificationData, "findByKey", param.args[0]);

                        if (Entry == null) return;

                        if(!b_expended)XposedHelpers.callMethod(Entry, "setUserExpanded", true);

                        updateExpansionStates(param.thisObject, mNotificationData);
                    }
                });
    }

    private void updateExpansionStates(Object obj_basebar, Object mNotificationData) {
        //int N = mNotificationData.size();
        int N = (int) XposedHelpers.callMethod(mNotificationData, "size");


        for (int i = 0; i < N; i++) {

            Object entry = XposedHelpers.callMethod(mNotificationData, "get", i);
            //  NotificationData.Entry entry = mNotificationData.get(i);
            //if (!entry.userLocked()) {

                    if ((boolean) XposedHelpers.callMethod(entry, "userExpanded")) {//  if (!entry.userExpanded()) {
            XposedHelpers.callMethod(obj_basebar, "expandView", entry, true); //      expandView(entry, false);


        }}

    }
}
