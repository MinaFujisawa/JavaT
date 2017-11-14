package com.example.aki.javaq.Presentation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.aki.javaq.Domain.Entity.PostMain;
import com.example.aki.javaq.Domain.Helper.FirebaseNodes;
import com.example.aki.javaq.Domain.Usecase.Firebase;
import com.example.aki.javaq.R;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

/**
 * Created by MinaFujisawa on 2017/07/13.
 */

public class AddPostActivity extends AppCompatActivity {

    private MenuItem mPostButton;
    private boolean mTappable;
    private EditText mEditTextView;
    private String mPostBody;
    private String mUserId;

    private static FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseRef;

    private long mPostTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_post_activity);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);
        ab.setHomeAsUpIndicator(R.drawable.ic_close);


        //New Child entries
        mFirebaseRef = Firebase.getDatabaseRef();
        mFirebaseUser = Firebase.getCurrentUser();


        mEditTextView = (EditText) findViewById(R.id.edit_post);
        mEditTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                invalidateOptionsMenu();

                if (s.toString().trim().length() > 0) {
                    mTappable = true;
                } else {
                    mTappable = false;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });

        // Show Keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_post:
                mPostBody = mEditTextView.getText().toString();
                mUserId = mFirebaseUser.getUid();
                mPostTime = System.currentTimeMillis();

                //Save post to the Firebase
                DatabaseReference ref = mFirebaseRef.child(FirebaseNodes.POSTS_CHILD);
                String key = ref.push().getKey();
                PostMain post = new PostMain(key, mUserId, mPostTime, mPostBody, null);
                ref.child(key).setValue(post);

                finish();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        mPostButton = menu.findItem(R.id.action_post);
        mPostButton.setEnabled(false);

        if (mTappable) {
            mPostButton.setEnabled(true);
        } else {
            mPostButton.setEnabled(false);
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();

        // Hide keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
