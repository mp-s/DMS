package com.app.legend.dms.hooks;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 去除启动页广告
 * 启动页广告有两处method，统一替换然后直接执行启动
 * 我特么的发现居然还有其他地方是显示启动页广告的，不按套路出牌啊，鸡蛋不能全放在一个篮子里的意思？
 * 这里也作废，仅仅是调试时才可以启用
 */
public class SplashAdHook extends BaseHook implements IXposedHookLoadPackage {

    //    private static final String CLASS = "com.lt.adv.b.b";
    private static final String CLASS = "com.dmzj.manhua.ad.adv.channels.LTGDT";
    private static final String METHOD1 = "b";
    private static final String METHOD2 = "d";

    private static final String CLASS2 = "com.lt.adv.a";


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (!lpparam.packageName.equals("com.dmzj.manhua")) {
            return;
        }

        /**
         * 021-022
         */
        final Class<?> clazz = XposedHelpers.findClassIfExists(CLASS, lpparam.classLoader);
        if (clazz == null) {
            XposedBridge.log("release--->> 360加固, 跳过hook");
            return;
        }

        /**
         * 找到字段
         */
        final Field _field = XposedHelpers.findFieldIfExists(clazz, "latform");

        if (_field != null) {
            XposedHelpers.findAndHookMethod(clazz, "loadSplashAD", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    // get Class
                    Object _obj = _field.get(param.thisObject);
                    // 点击关闭
                    XposedHelpers.callMethod(_obj, "onAdCloseView");

                    return null;
                }
            });
        } else {
            XposedBridge.log("release--->> is 013 ");
        }
/*
        XposedHelpers.findAndHookMethod(CLASS, lpparam.classLoader, METHOD1, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {

                Class<?> aClass = lpparam.classLoader.loadClass(CLASS2);

                Method a=aClass.getDeclaredMethod("a");

                Object o=a.invoke(null);//运行a静态方法，获取实例

                Method method = aClass.getDeclaredMethod("b", int.class, String.class);
                method.invoke(o, -1, "Inter onADClosed");




                return null;
            }
        });

        XposedHelpers.findAndHookMethod(CLASS, lpparam.classLoader, METHOD2, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {

                Class<?> c = lpparam.classLoader.loadClass(CLASS2);

                Method a=c.getDeclaredMethod("a");

                Object o=a.invoke(null);//运行a静态方法，获取实例

                Method method = c.getDeclaredMethod("b", int.class, String.class);
                method.invoke(o, -1, "Inter onADClosed");

                return null;
            }
        });


        XposedHelpers.findAndHookMethod("com.lt.adv.b.a", lpparam.classLoader, "e", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {

                Class<?> c = lpparam.classLoader.loadClass(CLASS2);

                Method a=c.getDeclaredMethod("a");

                Object o=a.invoke(null);//运行a静态方法，获取实例

                Method method = c.getDeclaredMethod("b", int.class, String.class);
                method.invoke(o, -1, "Inter onADClosed");


                return null;
            }
        });
*/

    }


}
