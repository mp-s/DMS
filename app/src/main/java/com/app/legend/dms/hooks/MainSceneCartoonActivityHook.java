package com.app.legend.dms.hooks;


import android.app.Activity;
import android.support.v4.view.ViewPager;

import com.app.legend.dms.utils.Conf;
import com.app.legend.dms.utils.FileUtil;

import org.json.JSONObject;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * hook主界面
 */
public class MainSceneCartoonActivityHook extends BaseHook implements IXposedHookLoadPackage {

    private static final String CLASS_017 = "com.dmzj.manhua.ui.MainSceneCartoonActivity$4";
    private static final String CLASS_022 = "com.dmzj.manhua.ui.home.MainSceneCartoonActivity$4";
    private static final String METHOD = "a";//有个入参，类型是int

    private static final String CLASS2_017 = "com.dmzj.manhua.ui.MainSceneCartoonActivity";
    private static final String CLASS2_022 = "com.dmzj.manhua.ui.home.MainSceneCartoonActivity";

    private static final String CLASS3_017 = "com.dmzj.manhua.ui.MainSceneCartoonActivity$MyAdapter";
    private static final String CLASS3_022 = "com.dmzj.manhua.ui.home.MainSceneCartoonActivity$MyAdapter";

    private static String CLASS;
    private static String CLASS2;
    private static String CLASS3;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals(Conf.PACKAGE)) {
            return;
        }
        Class<?> clz1 = XposedHelpers.findClassIfExists(CLASS_017, lpparam.classLoader);
        Class<?> clz2 = XposedHelpers.findClassIfExists(CLASS_022, lpparam.classLoader);
        if (clz1 != null) {
            XposedBridge.log("--- Founded 013~017!");
            CLASS = CLASS_017;
            CLASS2 = CLASS2_017;
            CLASS3 = CLASS3_017;
        } else if (clz2 != null) {
            XposedBridge.log("--- Founded 018~022 !");
            CLASS = CLASS_022;
            CLASS2 = CLASS2_022;
            CLASS3 = CLASS3_022;
        } else {
            XposedBridge.log("-- 023+? or 013-?");
            return;
        }
        init(lpparam.classLoader);
    }

    public void init(ClassLoader classLoader) {
        /*添加封印界面*/
        XposedHelpers.findAndHookMethod(CLASS, classLoader, "getNaviItem", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                String[] arrays = new String[]{"推荐", "封印", "更新", "分类", "排行", "专题"};

                XposedHelpers.setObjectField(param.thisObject, "tab_main_mine_frag_names", arrays);

            }
        });

        XposedHelpers.findAndHookMethod(CLASS, classLoader, "getCount", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                param.setResult(6);
            }
        });

        /* 将游戏图标去掉
         * 仔细想过了，还是不去掉，我的目的不是断大妈财路
         *
         * */
        XposedHelpers.findAndHookMethod(CLASS2, classLoader, "generateMainLayout", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                Activity activity = (Activity) param.thisObject;

                FileUtil.createFile(activity, "comic", false);
//                FileUtil.createFile(activity,"chapter",false);

                FileUtil.downloadFile(activity);

            }
        });


        /*替换原有的获取操作，当然是在入参为1的时候替换，其他时候不动*/
        XposedHelpers.findAndHookMethod(CLASS3, classLoader, "getItem", int.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {

                int i = (int) param.args[0];//获取入参

                if (i == 1) {//第2个页面

                    Object o = null;

                    try {
                        Class<?> clazz = classLoader.loadClass("com.dmzj.manhua.ui.uifragment.CartoonClassifyFragment");

                        o = clazz.newInstance();

                        XposedHelpers.callMethod(o, "analysisData", "pp");

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    return o;
                } else if (i > 1) {//从第2个页面之后的页面，入参都要减1

                    param.args[0] = i - 1;

                    return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                } else {//第一个页面，不动


                    return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                }
            }
        });

        /*将返回改为6*/
        XposedHelpers.findAndHookMethod(CLASS3, classLoader, "getCount", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                param.setResult(6);

            }
        });

        //禁止弹出强制升级的窗口
        XposedHelpers.findAndHookMethod("com.dmzj.manhua.helper.AppUpDataHelper", classLoader,
                "onVersionDetached", JSONObject.class, Activity.class, Class.class, boolean.class, boolean.class,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {

                        XposedBridge.log("release--->>阻止升级弹窗！");

                        return null;
                    }
                });

        /**
         * 设置viewpager的limit，避免不显示
         */
        XposedHelpers.findAndHookMethod(CLASS2, classLoader, "initData", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                Object viewPager = XposedHelpers.getObjectField(param.thisObject, "mPager");

                XposedHelpers.callMethod(viewPager, "setOffscreenPageLimit", 2);

                XposedBridge.log("设置成功！！！！！！！");
            }
        });

    }


}
