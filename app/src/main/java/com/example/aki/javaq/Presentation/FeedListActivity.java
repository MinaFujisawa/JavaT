package com.example.aki.javaq.Presentation;

import android.support.v4.app.Fragment;

import com.example.aki.javaq.Presentation.DrawerMenuActivity;
import com.example.aki.javaq.Presentation.DrawermenuSingleFragmentActivity;
import com.example.aki.javaq.Presentation.Setting.SettingListFragment;

public class FeedListActivity extends DrawermenuSingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new FeedFragment();
    }
}
