package com.example.aki.javaq.Community;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aki.javaq.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A placeholder fragment containing a simple view.
 */
public class CommunityListFragment extends Fragment {

    private View view;
    private RecyclerView mComRecyclerView;
    private ComAdapter mAdapter;
    private int mLastAdapterClickedPosition = -1;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private int mCommentsNumInt = 18; //ダミー


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
//            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.com_list_fragment, container, false);
        mComRecyclerView = (RecyclerView) view.findViewById(R.id.com_list_recycler_view);
        mComRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();

        // FloatingActionButton
        FloatingActionButton mNewPostButton = (FloatingActionButton) view.findViewById(R.id.new_post_button);
        mNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // New Post画面へ遷移
            }
        });
        return view;
    }

    private void updateUI() {

        // ダミーの配列
        ArrayList<String> posts = new ArrayList<>();
        posts.add("A");
        posts.add("B");
        posts.add("C");

//        CrimeLab crimeLab = CrimeLab.get(getActivity());

//        List<Crime> crimes = crimeLab.getCrimes();
        if (mAdapter == null) {
            mAdapter = new ComAdapter(posts);
            mComRecyclerView.setAdapter(mAdapter);
        } else {
            if (mLastAdapterClickedPosition >= 0) {
                mAdapter.notifyItemChanged(mLastAdapterClickedPosition);
                mLastAdapterClickedPosition = -1;
            }
//            mAdapter.setCrimes(crimes);
        }
        mAdapter.notifyDataSetChanged();
    }




    private class PostHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mPostUserName;
        private TextView mPostText;
        private TextView mPostDate;
        private TextView mCommentsNum;
        private CircleImageView mUserIcon;
//        private Post mPost;

        public PostHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.com_list_item, parent, false));

            itemView.setOnClickListener(this);
            mUserIcon = (CircleImageView) itemView.findViewById(R.id.post_user_icon);
            mPostUserName = (TextView) itemView.findViewById(R.id.post_user_name);
            mPostText = (TextView) itemView.findViewById(R.id.post_text);
            mPostDate = (TextView) itemView.findViewById(R.id.post_date);
            mCommentsNum = (TextView) itemView.findViewById(R.id.post_comment_num);

        }

        @Override
        public void onClick(View v) {
            mLastAdapterClickedPosition = getAdapterPosition();
            //ダミー
            UUID uuid = UUID.randomUUID();
            //第二引数はmPost.getId()になる
            Intent intent = CommunityDetailActivity.newIntent(getActivity(), uuid);
            startActivity(intent);
        }

        public void bind() {
//            mPost = post;

            //ダミー
            mPostUserName.setText("getUserName");
            mPostText.setText("getPostText");
            mPostDate.setText("5 h");

            String comments = getResources().getQuantityString(R.plurals.comments_plural, mCommentsNumInt, mCommentsNumInt);
            mCommentsNum.setText(comments);
        }

    }

    private class ComAdapter extends RecyclerView.Adapter<PostHolder> {
        private ArrayList<String> mPosts;
        public ComAdapter(ArrayList<String> posts) {
            mPosts = posts;
        }

        private ArrayList<String> dummyList;

        @Override
        public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new PostHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(PostHolder holder, int position) {
//            Crime crime = mCrimes.get(position);
            holder.bind();
        }


        @Override
        public int getItemCount() {
            return mPosts.size();
        }

//        public void setCrimes(List<Crime> crimes) {
//            mCrimes = crimes;
//        }
    }
}
