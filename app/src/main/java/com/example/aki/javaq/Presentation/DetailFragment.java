package com.example.aki.javaq.Presentation;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.aki.javaq.Domain.Entity.PostComment;
import com.example.aki.javaq.Domain.Entity.PostMain;
import com.example.aki.javaq.Domain.Entity.User;
import com.example.aki.javaq.Domain.Helper.FirebaseNodes;
import com.example.aki.javaq.Domain.Usecase.Firebase;
import com.example.aki.javaq.R;
import com.example.aki.javaq.Domain.Helper.TimeUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {
    private TextView mUserNameTextView;
    private CircleImageView mUserIconImageView;
    private TextView mPostTextView;
    private TextView mPostDateTextView;
    private Button mPostDeleteButton;
    private TextView mPostCommentsNumTextView;
    private EditText mAddCommentsEditTextView;
    private CircleImageView mMyIconImageView;
    private int mCommentsNumInt = 0;
    private DatabaseReference mFirebaseRef;
    private String mPostKey;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mCommentsRef;
    private DatabaseReference mUsersRef;
    private CommentsAdapter mCommentsAdapter;
    private RecyclerView mCommentsRecyclerView;
    private String mPostTimeAgo;
    private static final String ARG_POST_KEY = "arg_post_key";
    private View view;
    private LinearLayoutManager mLinearLayoutManager;
    private static final String POST_KEY = "post_key";
    private String mCommentsNum;
    private boolean isSignedIn = false;

    public static DetailFragment newInstance(String postKey) {
        Bundle args = new Bundle();
        args.putString(ARG_POST_KEY, postKey);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPostKey = getArguments().getString(ARG_POST_KEY);

        // Check sign-in status
        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mCurrentUser != null) {
            isSignedIn = true;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.detail_fragment, container, false);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mCommentsRecyclerView = (RecyclerView) view.findViewById(R.id.com_comments_recycler_view);
        mCommentsRecyclerView.setLayoutManager(mLinearLayoutManager);


        //For Post
        mUserNameTextView = (TextView) view.findViewById(R.id.post_user_name);
        mUserIconImageView = (CircleImageView) view.findViewById(R.id.post_user_icon);
        mPostTextView = (TextView) view.findViewById(R.id.post_text);
        mPostDateTextView = (TextView) view.findViewById(R.id.post_date);
        mPostCommentsNumTextView = (TextView) view.findViewById(R.id.post_comment_num);
        mPostDeleteButton = (Button) view.findViewById(R.id.post_delete);
        mFirebaseRef = Firebase.getDatabaseRef();
        mCommentsRef = Firebase.getDatabaseRef().child(FirebaseNodes.POSTS_COM_CHILD);
        mUsersRef = Firebase.getDatabaseRef().child(FirebaseNodes.USER_CHILD);
        mCurrentUser = Firebase.getCurrentUser();


        // Delete button
        mPostDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                mFirebaseRef.child(FirebaseNodes.POSTS_CHILD).child(mPostKey).removeValue();
            }
        });


        mFirebaseRef.child(FirebaseNodes.POSTS_CHILD).child(mPostKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                final PostMain mPostMain = dataSnapshot.getValue(PostMain.class);
                if (mPostMain == null) return;

                mPostTextView.setText(mPostMain.getPostBody());

                mFirebaseRef.child(FirebaseNodes.USER_CHILD).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot snapshot) {

                        User mPostUser = snapshot.child(mPostMain.getUserId()).getValue(User.class);

                        // Display User name
                        mUserNameTextView.setText(mPostUser.getUserName());

                        if (mCurrentUser != null) {
                            if (!mCurrentUser.getUid().equals(mPostUser.getUserId())) {
                                mPostDeleteButton.setVisibility(View.GONE);
                            }
                        }


                        //Display User picture
                        StorageReference rootRef = Firebase.getStorageReference().child(FirebaseNodes.USER_PIC_CHILD);
                        rootRef.child(mPostUser.getUserId()).getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //If there's a picture in the storage, set the picture
                                        Glide.with(getActivity())
                                                .load(uri)
                                                .into(mUserIconImageView);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                //If not, set the default picture
                                int id = R.drawable.image_user_default;
                                Uri mPictureDefaultUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                                        "://" + getResources().getResourcePackageName(id)
                                        + '/' + getResources().getResourceTypeName(id)
                                        + '/' + getResources().getResourceEntryName(id));
                                Glide.with(getActivity())
                                        .load(mPictureDefaultUri)
                                        .into(mUserIconImageView);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mPostDateTextView.setText(TimeUtils.getTimeAgo(mPostMain.getPostTime()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        // Display number of comments
        final DatabaseReference post_ref = mFirebaseRef.child(FirebaseNodes.POSTS_CHILD).child(mPostKey);
        post_ref.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                PostMain mPostMain = snapshot.getValue(PostMain.class);
                if (mPostMain == null) return;

                mCommentsNumInt = mPostMain.getCommentsNum();
                if (mCommentsNumInt == 0) {
                    mCommentsNum = getResources().getString(R.string.comments_zero, mCommentsNumInt);
                } else {
                    mCommentsNum = getResources().getQuantityString(R.plurals.comments_plural, mCommentsNumInt, mCommentsNumInt);
                }
                mPostCommentsNumTextView.setText(mCommentsNum);
            }

            public void onCancelled(DatabaseError firebaseError) {
            }
        });


        //get user info
        mFirebaseAuth = Firebase.getFirebaseAuth();
        mCurrentUser = mFirebaseAuth.getCurrentUser();



        //For Add a comment
        mMyIconImageView = (CircleImageView) view.findViewById(R.id.my_user_icon);

        if (isSignedIn) {
            StorageReference rootRef = Firebase.getStorageReference().child(FirebaseNodes.USER_PIC_CHILD);
            rootRef.child(mCurrentUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    //If there's a picture in the storage, set the picture
                    Glide.with(getActivity())
                            .load(uri)
                            .into(mMyIconImageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //If not, set the default picture
                    int id = R.drawable.image_user_default;
                    Uri mPictureDefaultUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                            "://" + getResources().getResourcePackageName(id)
                            + '/' + getResources().getResourceTypeName(id)
                            + '/' + getResources().getResourceEntryName(id));
                    Glide.with(getActivity())
                            .load(mPictureDefaultUri)
                            .into(mMyIconImageView);
                }
            });
        } else {
            mMyIconImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.image_user_default));
            mPostDeleteButton.setVisibility(view.GONE);
        }


        mAddCommentsEditTextView = (EditText) view.findViewById(R.id.add_new_comment_text);
        mAddCommentsEditTextView.setFocusable(false);
        mAddCommentsEditTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isSignedIn) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), AddCommentActivity.class);
                    intent.putExtra(POST_KEY, mPostKey);
                    startActivity(intent);
                } else {
                    // display dialog
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    LoginDialogFragment dialog = LoginDialogFragment.newInstance();
                    dialog.show(manager, LoginDialogFragment.LOGIN_DIALOG);
                }
            }
        });

        if (mCommentsAdapter == null) {
            mCommentsAdapter = new DetailFragment.CommentsAdapter(mCommentsRef, mUsersRef);
            mCommentsRecyclerView.setAdapter(mCommentsAdapter);
        }



        return view;
    }


    public class CommentsAdapter extends RecyclerView.Adapter<CommentsViewHolder> {
        ArrayList<PostComment> mPostCommentsList = new ArrayList<>();
        HashMap<String, User> mUserMap = new HashMap<>();
        private PostComment mPostComment;

        public CommentsAdapter(DatabaseReference comment_ref, DatabaseReference user_ref) {
            comment_ref.addValueEventListener(new ValueEventListener() {
                public void onDataChange(DataSnapshot snapshot) {
                    mPostCommentsList.clear();
                    for (DataSnapshot commentsSnapshot : snapshot.getChildren()) {
                        PostComment mPostComment = commentsSnapshot.getValue(PostComment.class);
                        if (mPostComment.getPostId().equals(mPostKey)) {
                            mPostCommentsList.add(mPostComment);
                        }
                    }
                    notifyDataSetChanged();
                }

                public void onCancelled(DatabaseError firebaseError) {
                }
            });

            user_ref.addValueEventListener(new ValueEventListener() {
                public void onDataChange(DataSnapshot snapshot) {
                    mUserMap.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        User user = postSnapshot.getValue(User.class);
                        String key = postSnapshot.getKey();
                        mUserMap.put(key, user);
                    }
                    notifyDataSetChanged();
                }

                public void onCancelled(DatabaseError firebaseError) {
                }
            });
        }

        @Override
        public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_comment_item, parent, false);
            return new CommentsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final CommentsViewHolder viewHolder, int position) {
            mPostComment = mPostCommentsList.get(position);
            viewHolder.bind(mPostComment);

            //Display Body text
            viewHolder.mCommentTextView.setText(mPostComment.getCommentBody());

            //Display Time
            long timestamp = mPostComment.getCommentTime();
            mPostTimeAgo = TimeUtils.getTimeAgo(timestamp);
            viewHolder.mCommentTimeTextView.setText(mPostTimeAgo);


            if (mUserMap.containsKey(mPostComment.getUserId().toString())) {

                User mUser = mUserMap.get(mPostComment.getUserId().toString());

                // Set delete button invisible to other people
                FirebaseUser currentUser = Firebase.getCurrentUser();
                if (isSignedIn){
                    if (!currentUser.getUid().equals(mUser.getUserId())) {
                        viewHolder.mCommentDeleteButton.setVisibility(View.GONE);
                    }
                }


                //Display User name
                viewHolder.mCommentUserNameTextView.setText(mUser.getUserName());

                //Display User picture
                StorageReference rootRef = Firebase.getStorageReference().child(FirebaseNodes.USER_PIC_CHILD);
                rootRef.child(mUser.getUserId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //If there's a picture in the storage, set the picture
                        Glide.with(getActivity())
                                .load(uri)
                                .into(viewHolder.mCommentUserIconImageView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //If not, set the default picture
                        int id = R.drawable.image_user_default;
                        Uri mPictureDefaultUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                                "://" + getResources().getResourcePackageName(id)
                                + '/' + getResources().getResourceTypeName(id)
                                + '/' + getResources().getResourceEntryName(id));
                        Glide.with(getActivity())
                                .load(mPictureDefaultUri)
                                .into(viewHolder.mCommentUserIconImageView);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mPostCommentsList.size();
        }

    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder {
        private TextView mCommentUserNameTextView;
        private CircleImageView mCommentUserIconImageView;
        private TextView mCommentTextView;
        private TextView mCommentTimeTextView;
        private Button mCommentDeleteButton;
        private PostComment mPostComment;

        public CommentsViewHolder(View itemView) {
            super(itemView);

            mCommentUserNameTextView = (TextView) itemView.findViewById(R.id.comment_user_name);
            mCommentUserIconImageView = (CircleImageView) itemView.findViewById(R.id.comment_user_icon);
            mCommentTextView = (TextView) itemView.findViewById(R.id.comment_text);
            mCommentTimeTextView = (TextView) itemView.findViewById(R.id.comment_date);
            mCommentDeleteButton = (Button) itemView.findViewById(R.id.comment_delete);

            mCommentDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFirebaseRef.child(FirebaseNodes.POSTS_COM_CHILD).child(mPostComment.getCommentId()).removeValue();

                    final DatabaseReference post_ref = mFirebaseRef.child(FirebaseNodes.POSTS_CHILD).child(mPostKey);
                    mCommentsNumInt--;
                    post_ref.child(FirebaseNodes.COMMENTS_NUM).setValue(mCommentsNumInt);
                }
            });

            if (!isSignedIn){
                mCommentDeleteButton.setVisibility(view.GONE);
            }
        }

        public void bind(final PostComment postComment) {
            mPostComment = postComment;
        }


    }


    // Hide ActionBar
    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }
}

