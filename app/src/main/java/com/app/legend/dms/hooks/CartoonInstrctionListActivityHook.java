package com.app.legend.dms.hooks;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.app.legend.dms.utils.Conf;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class CartoonInstrctionListActivityHook extends BaseHook implements IXposedHookLoadPackage {

    private static final String CLASS="com.dmzj.manhua.ui.CartoonInstrctionListActivity$3";

    private static final String CLASS2="com.dmzj.manhua.ui.CartoonInstrctionListActivity";

    private Activity activity;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (!lpparam.packageName.equals(Conf.PACKAGE)) {
            return;
        }

        /**
         *  破除 360 加固
         * */
        Class<?> clazzStub = XposedHelpers.findClassIfExists("com.stub.StubApp", lpparam.classLoader);
        if (clazzStub != null) {
            XposedHelpers.findAndHookMethod(clazzStub, "attachBaseContext", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    Context context = (Context) param.args[0];
                    _classLoader = context.getClassLoader();
                    XposedBridge.log("release--->> get Stub success!  " + CLASS);
                    init(_classLoader);
                }
            });
        } else {
            _classLoader = lpparam.classLoader;
            XposedBridge.log("release--->> get origin classLoader success!  " + CLASS);
            init(_classLoader);
        }

    }

    @Override
    protected void init(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod(CLASS2, classLoader, "initData", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                activity = (Activity) param.thisObject;
            }
        });

        XposedHelpers.findAndHookMethod(CLASS, classLoader, "onReceiveData", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                if (activity!=null){

                   XposedHelpers.setIntField(activity,"isLock",-1);

                }
            }
        });


        XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                XposedBridge.log("release--->> " + param.thisObject.toString());

            }
        });


    }
}
