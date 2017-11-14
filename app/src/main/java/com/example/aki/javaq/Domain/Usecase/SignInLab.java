package com.example.aki.javaq.Domain.Usecase;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.aki.javaq.Presentation.Community.CommunityPostActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Created by AKI on 2017-07-19.
 */

public class SignInLab extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    private static FirebaseAuth mFirebaseAuth;


    public static void signOut() {
        mFirebaseAuth = Firebase.getFirebaseAuth();
        mFirebaseAuth.signOut();
        //TODO: Google sign-outの検討
        // Google sign out
//            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }

    public static void firebaseAuthWithGoogle(GoogleSignInAccount acct, final Activity activity) {
        // Initialize FirebaseAuth
        mFirebaseAuth = Firebase.getFirebaseAuth();
        Log.d(TAG, "firebaseAuthWithGooogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(activity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            activity.startActivity(new Intent(activity, CommunityPostActivity.class));
                            activity.finish();

                        }
                    }
                });
    }
}
