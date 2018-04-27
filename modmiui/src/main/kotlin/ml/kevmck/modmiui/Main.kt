package ml.kevmck.modmiui

import android.os.IBinder
import android.os.PowerManager
import android.telephony.TelephonyManager
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager


import de.robv.android.xposed.XposedHelpers.findAndHookMethod

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.callbacks.XC_LoadPackage

class Main : IXposedHookLoadPackage {

    private val b_expended = true


    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        //   if (!lpparam.packageName.equals("com.android.systemui"))
        //      return;

        XposedHelpers.findAndHookMethod("android.view.Window", lpparam.classLoader,
                "setAttributes", WindowManager.LayoutParams::class.java, object : XC_MethodHook() {

            override fun beforeHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                val a = param!!.args[0] as WindowManager.LayoutParams
                a.screenBrightness = -1.0f
            }
        })

        /*     findAndHookMethod("com.android.systemui.statusbar.stack.StackScrollAlgorithm",
                lpparam.classLoader, "updateFirstChildHeightWhileExpanding",android.view.ViewGroup.class,
                new XC_MethodHook() {
                    @Override

                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {


                        View mFirstChildWhileExpanding = (View) XposedHelpers.getObjectField(param.thisObject, "mFirstChildWhileExpanding");
                        ViewGroup.LayoutParams lp = mFirstChildWhileExpanding.getLayoutParams();
                        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        mFirstChildWhileExpanding.setLayoutParams(lp);
                    }

                })
        ;



        findAndHookMethod("com.android.systemui.statusbar.BaseStatusBar",
                lpparam.classLoader, "resetNotificationPile",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {


                               Object mNotificationData = XposedHelpers.getObjectField(param.thisObject, "mNotificationData");

                                if (mNotificationData != null){

                                updateExpansionStates(param.thisObject, mNotificationData);}

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

                        //      updateExpansionStates(param.thisObject, mNotificationData);


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

                        if (!b_expended) XposedHelpers.callMethod(Entry, "setUserExpanded", true);

                        updateExpansionStates(param.thisObject, mNotificationData);
                    }
                });
                */


        /* private fun updateExpansionStates(obj_basebar: Any, mNotificationData: Any) {
             //int N = mNotificationData.size();
             val N = XposedHelpers.callMethod(mNotificationData, "size") as Int


             for (i in 0 until N) {

                 val entry = XposedHelpers.callMethod(mNotificationData, "get", i)
                 //  NotificationData.Entry entry = mNotificationData.get(i);
                 //if (!entry.userLocked()) {

                 if (XposedHelpers.callMethod(entry, "userExpanded") as Boolean) {//  if (!entry.userExpanded()) {
                     XposedHelpers.callMethod(obj_basebar, "expandView", entry, true) //      expandView(entry, false);


                 }
             }

         } */

    }
}
