package com.example.aki.javaq.Presentation;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.aki.javaq.Domain.Entity.User;
import com.example.aki.javaq.Domain.Helper.FirebaseNodes;
import com.example.aki.javaq.Domain.Helper.PictureUtils;
import com.example.aki.javaq.Domain.Usecase.Firebase;
import com.example.aki.javaq.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by MinaFujisawa on 2017/07/13.
 */

public class UserRegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView mMyIconImageView;
    private TextView mEditIconTextView;
    private EditText mUserNameTextView;
    private TextView mErrorTextView;
    private MenuItem mSaveButton;
    private String mPicturePath;
    private String mUserName;
    private boolean mTappable;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseReference;
    private StorageReference mUserPicReference;
    public static final int RESULT_LOAD_IMAGE = 1;
    private final int REQUEST_PERMISSION_PHONE_STATE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_registration_activity);

        this.setTitle("Profile");


        mDatabaseReference = Firebase.getDatabaseRef();
        mUserPicReference = Firebase.getStorageReference();

        // Setup views
        mMyIconImageView = (CircleImageView) findViewById(R.id.add_user_icon);
        mMyIconImageView.setOnClickListener(this);

        mEditIconTextView = (TextView) findViewById(R.id.add_icon_text);
        mEditIconTextView.setOnClickListener(this);

        mUserNameTextView = (EditText) findViewById(R.id.add_user_name);
        mUserNameTextView.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        mErrorTextView = (TextView) findViewById(R.id.error_text);


        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference.child(FirebaseNodes.USER_CHILD)
                .child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    setExistInfo();
                }
                else {
                    mUserNameTextView.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) { }
        });

        updatePhotoView();


        // Check username length
        mUserNameTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                invalidateOptionsMenu();

                //Detect inputted user name
                if (0 < s.toString().trim().length() && s.toString().trim().length() <= 20) {
                    mTappable = true;
                } else {
                    mTappable = false;
                }

                //For error
                if (s.toString().length() > 20) {
                    mErrorTextView.setText(R.string.error_user_name);
                } else {
                    mErrorTextView.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setExistInfo() {
        mDatabaseReference.child(FirebaseNodes.USER_CHILD)
                .child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                User mUser = snapshot.getValue(User.class);

                //Display User name
                mUserNameTextView.setText(mUser.getUserName());

                //Display User picture
                StorageReference rootRef = Firebase.getStorageReference().child(FirebaseNodes.USER_PIC_CHILD);
                rootRef.child(mUser.getUserId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //If there's a picture in the storage, set the picture
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(mMyIconImageView);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private static void removeOnGlobalLayoutListener(ViewTreeObserver observer, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (observer == null) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            observer.removeGlobalOnLayoutListener(listener);
        } else {
            observer.removeOnGlobalLayoutListener(listener);
        }
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
        showPermission();
    }

    private void showPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showExplanation("Permission Needed", "Rationale", android.Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_PERMISSION_PHONE_STATE);
            } else {
                requestPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_PERMISSION_PHONE_STATE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_PHONE_STATE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
        }
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mPicturePath = cursor.getString(columnIndex);
            updatePhotoView();
            cursor.close();
        }
    }

    private void updatePhotoView() {
        if (mPicturePath == null) {
            mMyIconImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.image_user_default));
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPicturePath);
            mMyIconImageView.setImageBitmap(bitmap);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ok, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_ok:

                mUserName = mUserNameTextView.getText().toString();

                //Save name to the database
                User user = new User(mUserName, mCurrentUser.getUid());
                mDatabaseReference.child(FirebaseNodes.USER_CHILD)
                        .child(mCurrentUser.getUid()).setValue(user);

                //Save picture to the storage if only user set the local image
                if (mPicturePath != null) {
                    Uri file = Uri.fromFile(new File(mPicturePath));
                    StorageReference picRef = mUserPicReference.child(FirebaseNodes.USER_PIC_CHILD)
                            .child(mCurrentUser.getUid());
                    UploadTask uploadTask = picRef.putFile(file);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getApplicationContext(), R.string.failure, Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        }
                    });
                }


                Intent intent = new Intent(this, FeedActivity.class);
                startActivity(intent);

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

        mSaveButton = menu.findItem(R.id.action_ok);
        mSaveButton.setEnabled(false);

        if (mTappable) {
            mSaveButton.setEnabled(true);
        } else {
            mSaveButton.setEnabled(false);
        }
        return true;
    }
}
