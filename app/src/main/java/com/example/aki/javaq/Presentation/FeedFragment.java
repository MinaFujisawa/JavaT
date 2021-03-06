package com.example.aki.javaq.Presentation;

import android.content.ContentResolver;
import android.content.Intent;

import com.bumptech.glide.Glide;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.aki.javaq.Domain.Entity.PostMain;
import com.example.aki.javaq.Domain.Entity.User;
import com.example.aki.javaq.Domain.Helper.FirebaseNodes;
import com.example.aki.javaq.Domain.Helper.TimeUtils;
import com.example.aki.javaq.Domain.Usecase.Firebase;
import com.example.aki.javaq.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedFragment extends Fragment {

    private View view;
    private RecyclerView mComRecyclerView;
    private FirebaseUser mFirebaseUser;
    private FloatingActionButton mNewPostButton;
    private DatabaseReference mPostsRef;
    private DatabaseReference mUsersRef;
    private LinearLayoutManager mLinearLayoutManager;

    private PostAdapter mPostAdapter;

    private String mPostTimeAgo;
    private int mCommentsNumInt;
    private String mCommentsNum;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.feed_fragment, container, false);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);

        mComRecyclerView = (RecyclerView) view.findViewById(R.id.com_list_recycler_view);
        mComRecyclerView.setLayoutManager(mLinearLayoutManager);

        mPostsRef = Firebase.getDatabaseRef().child(FirebaseNodes.POSTS_CHILD);
        mUsersRef = Firebase.getDatabaseRef().child(FirebaseNodes.USER_CHILD);

        if (mPostAdapter == null) {
            mPostAdapter = new PostAdapter(mPostsRef, mUsersRef);
            mComRecyclerView.setAdapter(mPostAdapter);
        }

        //get user info
        mFirebaseUser = Firebase.getCurrentUser();

        //For the issue floating action button unexpected anchor gravity change
        mComRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mComRecyclerView.removeOnLayoutChangeListener(this);

                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) mNewPostButton.getLayoutParams();
                lp.anchorGravity = Gravity.BOTTOM | GravityCompat.END;
                lp.setMargins(0, 0, 32, 32);
                mNewPostButton.setLayoutParams(lp);
            }
        });


        // FloatingActionButton
        mNewPostButton = (FloatingActionButton) view.findViewById(R.id.new_post_button);
        mNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFirebaseUser == null) {
                    // Not signed in, launch the Sign In activity
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    LoginDialogFragment dialog = LoginDialogFragment.newInstance();
                    dialog.show(manager, LoginDialogFragment.LOGIN_DIALOG);

                } else {
                    Intent intent = new Intent(getActivity(), AddPostActivity.class);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {
        ArrayList<PostMain> mPostMainList = new ArrayList<>();
        HashMap<String, User> mUserMap = new HashMap<>();
        private PostMain mPostMain;


        public PostAdapter(DatabaseReference post_ref, DatabaseReference user_ref) {
            post_ref.addValueEventListener(new ValueEventListener() {
                public void onDataChange(DataSnapshot snapshot) {
                    mPostMainList.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        PostMain mPostMain = postSnapshot.getValue(PostMain.class);
                        mPostMainList.add(mPostMain);
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
        public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
            return new PostViewHolder(view);
        }


        public void onBindViewHolder(final PostViewHolder viewHolder, int position) {
            mPostMain = mPostMainList.get(position);
            PostMain post = mPostMainList.get(position);
            viewHolder.bind(post);

            //Display Body text
            viewHolder.mPostBodyTextView.setText(mPostMain.getPostBody());

            //Set ellipsize
            viewHolder.mPostBodyTextView.setSingleLine(false);
            viewHolder.mPostBodyTextView.setEllipsize(TextUtils.TruncateAt.END);
            viewHolder.mPostBodyTextView.setMaxLines(5);

            //Display Time
            long timestamp = mPostMain.getPostTime();
            mPostTimeAgo = TimeUtils.getTimeAgo(timestamp);
            viewHolder.mPostTimeTextView.setText(mPostTimeAgo);

            //Display Comment num
            mCommentsNumInt = mPostMain.getCommentsNum();
            if(mCommentsNumInt == 0){
                mCommentsNum = getResources().getString(R.string.comments_zero, mCommentsNumInt);
            } else {
                mCommentsNum = getResources().getQuantityString(R.plurals.comments_plural, mCommentsNumInt, mCommentsNumInt);
            }
            viewHolder.mCommentsNumTextView.setText(mCommentsNum);

            if (mUserMap.containsKey(mPostMain.getUserId().toString())) {

                //Display User name
                User mUser = mUserMap.get(mPostMain.getUserId().toString());
                viewHolder.mUserNameTextView.setText(mUser.getUserName());

                //Display User picture
                StorageReference rootRef = Firebase.getStorageReference().child(FirebaseNodes.USER_PIC_CHILD);
                rootRef.child(mUser.getUserId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //If there's a picture in the storage, set the picture
                        Glide.with(getActivity())
                                .load(uri)
                                .into(viewHolder.mUserIconImageView);
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
                                .into(viewHolder.mUserIconImageView);
                    }
                });
            }
        }

        public int getItemCount() {
            return mPostMainList.size();
        }
    }


    public class PostViewHolder extends RecyclerView.ViewHolder {
        private TextView mUserNameTextView;
        private TextView mPostBodyTextView;
        private TextView mPostTimeTextView;
        private TextView mCommentsNumTextView;
        private CircleImageView mUserIconImageView;
        private Button mDeleteButton;

        private PostMain mPostMain;


        public PostViewHolder(View itemView) {
            super(itemView);

            mUserNameTextView = (TextView) itemView.findViewById(R.id.post_user_name);
            mPostBodyTextView = (TextView) itemView.findViewById(R.id.post_text);
            mPostTimeTextView = (TextView) itemView.findViewById(R.id.post_date);
            mCommentsNumTextView = (TextView) itemView.findViewById(R.id.post_comment_num);
            mUserIconImageView = (CircleImageView) itemView.findViewById(R.id.post_user_icon);
            mDeleteButton = (Button) itemView.findViewById(R.id.post_delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = DetailActivity.newIntent(getActivity(), mPostMain.getPostId());
                    startActivity(intent);
                }
            });

            mDeleteButton.setVisibility(View.GONE);
        }

        public void bind(PostMain post) {
            mPostMain = post;
        }
    }


}