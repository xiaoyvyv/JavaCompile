package com.xiaoyv.java.ui.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.blankj.utilcode.util.ToastUtils;
import com.lib.utils.AlipayUtil;
import com.lib.utils.MyUtils;
import com.lib.utils.WeChatUtil;
import com.xiaoyv.java.R;
import com.xiaoyv.java.mode.Setting;
import com.xiaoyv.java.ui.activity.base.BaseActivity;

public class SettingsActivity extends BaseActivity implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    private static final String TITLE_TAG = "settingsActivityTitle";
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
        MyUtils.setToolbarBackToHome(this, toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new HeaderFragment())
                    .commit();
        } else {
            setTitle(savedInstanceState.getCharSequence(TITLE_TAG));
        }
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                setTitle(R.string.title_activity_settings);
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void findViews() {
        toolbar = findViewById(R.id.toolbar);

    }

    @Override
    public void setEvents() {


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("检查更新")
                .setOnMenuItemClickListener(item -> {
                    MyUtils.checkAppVersion(SettingsActivity.this);
                    return false;
                }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(TITLE_TAG, getTitle());
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (getSupportFragmentManager().popBackStackImmediate()) {
            return true;
        }
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(getClassLoader(), pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings, fragment)
                .addToBackStack(null)
                .commit();
        setTitle(pref.getTitle());
        return true;
    }

    public static class HeaderFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            getPreferenceManager().setSharedPreferencesName(Setting.KEY);
            setPreferencesFromResource(R.xml.setting_header, rootKey);
            // 捐赠
            Preference donate = findPreference("setting_restore");
            if (donate != null)
                donate.setOnPreferenceClickListener(preference -> {
                    SettingsActivity activity = (SettingsActivity) getActivity();
                    AlertDialog dialog = new AlertDialog.Builder(activity)
                            .setTitle(R.string.setting_restore)
                            .setMessage("是否恢复默认设置")
                            .setPositiveButton(R.string.done, (dialog1, which) -> {
                                Setting.restore();
                            })
                            .setNegativeButton(R.string.clear, null)
                            .create();
                    dialog.show();
                    return true;
                });

        }
    }

    public static class SettingEditorFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            getPreferenceManager().setSharedPreferencesName(Setting.KEY);
            setPreferencesFromResource(R.xml.setting_editor, rootKey);
        }
    }

    public static class SettingCompileFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            getPreferenceManager().setSharedPreferencesName(Setting.KEY);
            setPreferencesFromResource(R.xml.setting_compile, rootKey);
        }
    }

    public static class SettingAboutFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            getPreferenceManager().setSharedPreferencesName(Setting.KEY);
            setPreferencesFromResource(R.xml.setting_about, rootKey);
            // 捐赠
            Preference donate = findPreference("donate");
            if (donate != null)
                donate.setOnPreferenceClickListener(preference -> {
                    SettingsActivity activity = (SettingsActivity) getActivity();
                    AlertDialog dialog = new AlertDialog.Builder(activity)
                            .setTitle(R.string.donate)
                            .setMessage("请选择捐赠方式：\n\n1.支付宝\n2.微信")
                            .setPositiveButton("支付宝", (dialog1, which) -> {
                                if (activity != null && AlipayUtil.hasInstalledAlipayClient(activity)) {
                                    AlipayUtil.donateAlipay(activity);
                                } else {
                                    ToastUtils.showLong(R.string.no_apply_app);
                                }
                            })
                            .setNegativeButton("微信", (dialog12, which) ->
                                    WeChatUtil.donateWeiXin(activity))
                            .create();
                    dialog.show();
                    return true;
                });

        }
    }
}
