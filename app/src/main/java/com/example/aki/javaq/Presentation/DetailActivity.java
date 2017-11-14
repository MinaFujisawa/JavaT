package com.example.aki.javaq.Presentation;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class DetailActivity extends SingleFragmentActivity {

    public static final String EXTRA_POST_KEY = "com.example.aki.javaq.extra_post_key";


    public static Intent newIntent(Context context, String postKey) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_POST_KEY, postKey);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String postKey = (String) getIntent().getSerializableExtra(EXTRA_POST_KEY);
        return DetailFragment.newInstance(postKey);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),FeedListActivity.class);
        startActivity(intent);
    }
}
