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
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.example.aki.javaq.Domain.Entity.PostComment;
import com.example.aki.javaq.Domain.Entity.PostMain;
import com.example.aki.javaq.Domain.Helper.FirebaseNodes;
import com.example.aki.javaq.Domain.Usecase.Firebase;
import com.example.aki.javaq.R;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

/**
 * Created by MinaFujisawa on 2017/07/13.
 */

public class AddCommentActivity extends AppCompatActivity {

    private EditText mCommentEditTextView;
    private String mCommentText;
    private MenuItem mPostButton;
    private static FirebaseUser mFirebaseUser;
    public DatabaseReference mFirebaseRef;
    private String mPostComBody;
    private String mUserId;
    private long mCurrentTime;
    private static final String POST_KEY = "post_key";
    private static String mPostKey;
    private int mCommentsNumInt = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mPostKey = intent.getStringExtra(POST_KEY);
        setContentView(R.layout.add_post_activity);

        //Toolbar
        this.setTitle("");
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_close);

        //New Child entries
        mFirebaseRef = Firebase.getDatabaseRef();

        mFirebaseUser = Firebase.getCurrentUser();

        mCommentEditTextView = (EditText) findViewById(R.id.edit_post);

        mCommentEditTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCommentText = s.toString();
                invalidateOptionsMenu();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });
        mCommentEditTextView.setInputType(InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
        mCommentEditTextView.setHint(R.string.add_comment_ph);

        //show keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        final DatabaseReference post_ref = mFirebaseRef.child(FirebaseNodes.POSTS_CHILD).child(mPostKey);
        post_ref.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                PostMain postMain = snapshot.getValue(PostMain.class);
                mCommentsNumInt = postMain.getCommentsNum();
            }
            public void onCancelled(DatabaseError firebaseError){
            }
        });

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
                mPostComBody = mCommentEditTextView.getText().toString();
                mUserId = mFirebaseUser.getUid();
                mCurrentTime = System.currentTimeMillis();

                //Save post to the Firebase
                DatabaseReference ref = mFirebaseRef.child(FirebaseNodes.POSTS_COM_CHILD);
                String key = ref.push().getKey();
                PostComment comment = new PostComment(key, mPostKey, mPostComBody, mCurrentTime, mUserId);
                ref.child(key).setValue(comment);

                // Set comment num
                final DatabaseReference post_ref = mFirebaseRef.child(FirebaseNodes.POSTS_CHILD).child(mPostKey);
                mCommentsNumInt++;
                post_ref.child(FirebaseNodes.COMMENTS_NUM).setValue(mCommentsNumInt);

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

        if (mCommentText == null) {
            mPostButton.setEnabled(false);
        } else {
            if (mCommentText.equals("")) {
                mPostButton.setEnabled(false);
            } else {
                mPostButton.setEnabled(true);

            }
        }
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
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
