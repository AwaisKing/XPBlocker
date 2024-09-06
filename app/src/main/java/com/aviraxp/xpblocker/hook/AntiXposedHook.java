package com.aviraxp.xpblocker.hook;

import androidx.annotation.NonNull;

import com.aviraxp.xpblocker.helper.PreferencesHelper;
import com.aviraxp.xpblocker.util.LogUtils;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

class AntiXposedHook {
    public static void hook(final XC_LoadPackage.LoadPackageParam lpparam) {
        if (!PreferencesHelper.isDisableXposedEnabled()) return;

        final XC_MethodHook disableXposedHook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(@NonNull final MethodHookParam param) {
                if (!param.args[0].equals("disableHooks") && !param.args[0].equals("sHookedMethodCallbacks")) return;
                param.setThrowable(new NoClassDefFoundError());
                LogUtils.logRecord("AntiXposedHook Success: " + lpparam.packageName);
            }
        };

        XposedHelpers.findAndHookMethod(Class.class, "getDeclaredField", String.class, disableXposedHook);
    }
}