package com.aviraxp.xpblocker.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import com.aviraxp.xpblocker.BuildConfig;
import com.aviraxp.xpblocker.R;

import java.io.File;

@SuppressWarnings("deprecation")
@SuppressLint({"WorldReadableFiles", "ExportedPreferenceActivity"})
public class SettingsActivity extends PreferenceActivity {
    static boolean isActivated = false;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWorldReadable();
        addPreferencesFromResource(R.xml.pref_settings);
        checkState();
        showUpdateLog();
        uriListener();
        hideIconListener();
        licensesListener();
    }

    private void showUpdateLog() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sp.getInt("VERSION", 0) != BuildConfig.VERSION_CODE) {
            new LicensesDialog(SettingsActivity.this, true)
                    .setTitle(R.string.updatelog)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            sp.edit().putInt("VERSION", BuildConfig.VERSION_CODE)
              .apply();
        }
    }

    private void uriListener() {
        uriHelper("GITHUB", "https://github.com/AwaisKing/XPBlocker");
        uriHelper("MAINTAINER", "https://github.com/AwaisKing");
        //uriHelper("XDA", "https://forum.xda-developers.com/xposed/modules/xposed-adblocker-reborn-1-0-1-2017-02-11-t3554617");
    }

    private void uriHelper(String pref, final String uri) {
        findPreference(pref).setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW)
                  .setData(Uri.parse(uri));
            startActivity(intent);
            return true;
        });
    }

    private void licensesListener() {
        findPreference("LICENSES").setOnPreferenceClickListener(preference -> {
            new LicensesDialog(SettingsActivity.this, false)
                    .setTitle(R.string.licensedialog)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return true;
        });
    }

    private void checkState() {
        if (!isActivated) {
            new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setMessage(R.string.hint_reboot_not_active)
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("SetWorldReadable")
    private void setWorldReadable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            File dataDir = new File(getApplicationInfo().dataDir);
            File prefsDir = new File(dataDir, "shared_prefs");
            File prefsFile = new File(prefsDir, getPreferenceManager().getSharedPreferencesName() + ".xml");
            if (prefsFile.exists()) {
                for (File file : new File[]{dataDir, prefsDir, prefsFile}) {
                    file.setReadable(true, false);
                    file.setExecutable(true, false);
                }
            }
        } else {
            getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
        }
    }

    private void hideIconListener() {
        findPreference("HIDEICON").setOnPreferenceChangeListener((preference, obj) -> {
            PackageManager packageManager = SettingsActivity.this.getPackageManager();
            ComponentName aliasName = new ComponentName(SettingsActivity.this, BuildConfig.APPLICATION_ID + ".SettingsActivityLauncher");
            if ((boolean) obj) {
                packageManager.setComponentEnabledSetting(aliasName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            } else {
                packageManager.setComponentEnabledSetting(aliasName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            }
            return true;
        });
    }
}