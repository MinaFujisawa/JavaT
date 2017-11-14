package com.example.aki.javaq.Presentation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.aki.javaq.Domain.Usecase.Firebase;
import com.example.aki.javaq.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new FeedFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mCurrentUser != null){
            // Signed-in
            menu.findItem(R.id.nav_profile).setVisible(true);
            menu.findItem(R.id.nav_sign_out).setVisible(true);
            menu.findItem(R.id.nav_sign_in).setVisible(false);
        }else {
            // Unsigned-in
            menu.findItem(R.id.nav_sign_in).setVisible(true);
            menu.findItem(R.id.nav_profile).setVisible(false);
            menu.findItem(R.id.nav_sign_out).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.nav_profile:
                Intent intent = new Intent(this, UserRegistrationActivity.class);
                startActivity(intent);
                return true;
            case R.id.nav_sign_out:
                Firebase.signOut();
                Toast.makeText(this, "Sign out", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.nav_sign_in:
                FragmentManager manager = this.getSupportFragmentManager();
                LoginDialogFragment dialog = LoginDialogFragment.newInstance();
                dialog.show(manager, LoginDialogFragment.LOGIN_DIALOG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
