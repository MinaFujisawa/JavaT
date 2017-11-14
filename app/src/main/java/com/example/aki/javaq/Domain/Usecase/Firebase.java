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

public class Firebase {
    public static FirebaseAuth getFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    public static FirebaseUser getCurrentUser() {
        return getFirebaseAuth().getCurrentUser();
    }

    public static DatabaseReference getDatabaseRef() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static StorageReference getStorageReference(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        return storage.getReferenceFromUrl("gs://javaq-86467.appspot.com");
    }

    public static void signOut() {
        Firebase.getFirebaseAuth().signOut();
    }
}
