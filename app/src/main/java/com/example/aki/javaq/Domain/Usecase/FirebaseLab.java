package com.example.aki.javaq.Domain.Usecase;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AKI on 2017-07-18.
 */

public class FirebaseLab {

    private static FirebaseAuth mFirebaseAuth;
    private static FirebaseUser mFirebaseUser;
    public static DatabaseReference mFirebaseDatabaseReference;
    private static FirebaseRemoteConfig mFirebaseRemoteConfig;
    private static FirebaseAnalytics mFirebaseAnalytics;
    private static final String TAG = "CommunityPostActivity";
    public FirebaseLab(){
    }


    public static FirebaseAuth getFirebaseAuth() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        return mFirebaseAuth;
    }

    public static FirebaseUser getFirebaseUser() {
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        return mFirebaseUser;
    }

    public static DatabaseReference getFirebaseDatabaseReference() {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        return mFirebaseDatabaseReference;
    }

    public static FirebaseRemoteConfig getFirebaseRemoteConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        return mFirebaseRemoteConfig;
    }

    public static FirebaseAnalytics getFirebaseAnalytics(Context context) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        return mFirebaseAnalytics;
    }

    public static StorageReference getStorageReference(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        return storage.getReferenceFromUrl("gs://javaq-86467.appspot.com");
    }



    public static void SetConfig(){
        // Define Firebase Remote Config Settings.
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put("friendly_msg_length", 10L);
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
                new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(true)
                        .build();
        mFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

    }



    public static void fetchConfig() {
        long cacheExpiration = 3600; // 1 hour in seconds
        // If developer mode is enabled reduce cacheExpiration to 0 so that
        // each fetch goes to the server. This should not be used in release
        // builds.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Make the fetched config available via
                        // FirebaseRemoteConfig get<type> calls.
                        mFirebaseRemoteConfig.activateFetched();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // There has been an error fetching the config
                        Log.w(TAG, "Error fetching config: " +
                                e.getMessage());
                    }
                });

    }

}
