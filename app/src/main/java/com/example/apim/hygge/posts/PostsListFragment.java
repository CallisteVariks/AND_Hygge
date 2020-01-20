package com.example.apim.hygge.posts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.apim.hygge.R;
import com.example.apim.hygge.data.Post;
import com.example.apim.hygge.postdetail.PostDetailActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Date;


public class PostsListFragment extends Fragment {

    public static final String LIST_STATE = "listState";
    private FirebaseRecyclerAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private Parcelable listState;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);

        /*
         * To make sure newly created Posts have ID so they're clickable (queryable in the Firebase
         * database)
         */
        SnapshotParser<Post> postParser = new SnapshotParser<Post>() {
            @Override
            public Post parseSnapshot(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                if (post != null) {
                    post.setId(dataSnapshot.getKey());
                }

                return post;
            }
        };

        /*
         * Ideally non-UI elements should be refactored out of Fragments:
         */
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        DatabaseReference postsRef = databaseReference.child("posts");

        FirebaseRecyclerOptions<Post> options =
                new FirebaseRecyclerOptions.Builder<Post>()
                        .setQuery(postsRef, postParser)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Post, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
            }


            @Override
            public void onBindViewHolder(ViewHolder holder, int position, Post model) {
                holder.title.setText(model.getTitle());
                holder.description.setText(model.getDescription());
                holder.setPostId(model.getId());


                if (model.getName() != null)
                    holder.name.setText(model.getName());

                if (model.getYear() != null && model.getMinute() != null) {
                    java.text.DateFormat dateFormat = DateFormat.getMediumDateFormat(getContext());
                    java.text.DateFormat timeFormat = DateFormat.getTimeFormat(getContext());

                    Date date = new Date(Integer.parseInt(model.getYear()) - 1900,
                            Integer.parseInt(model.getMonth()),
                            Integer.parseInt(model.getDay()));

                    Date time = new Date(Integer.parseInt(model.getYear()) - 1900,
                            Integer.parseInt(model.getMonth()),
                            Integer.parseInt(model.getDay()),
                            Integer.parseInt(model.getHourOfDay()),
                            Integer.parseInt(model.getMinute()));

                    holder.date.setText(dateFormat.format(date));
                    holder.time.setText(timeFormat.format(time));
                }

                Picasso.with(getContext()).load(model.getPhotoUrl())
                        .error(R.drawable.ic_person_black_24dp)
                        .placeholder(R.drawable.ic_person_black_24dp)
                        .into(holder.imageView);
            }
        };

        /*
         * Scroll to newest post:
         */
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int postsCount = adapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();

                if (lastVisiblePosition == -1 ||
                        (positionStart >= (postsCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }

            }
        });


        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        if (savedInstanceState != null && listState != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
            linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());

            // Newest Posts on top:
            linearLayoutManager.setReverseLayout(true);
            linearLayoutManager.setStackFromEnd(true);

        } else {
            linearLayoutManager = new LinearLayoutManager(getActivity());

            // Newest Posts on top:
            linearLayoutManager.setReverseLayout(true);
            linearLayoutManager.setStackFromEnd(true);

            recyclerView.setLayoutManager(linearLayoutManager);
        }

        return recyclerView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        listState = linearLayoutManager.onSaveInstanceState();
        outState.putParcelable(LIST_STATE, listState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable(LIST_STATE);
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView name;
        public TextView time;
        TextView description;
        ImageView imageView;
        TextView date;
        private String postId;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item, parent, false));
            title = itemView.findViewById(R.id.list_item_title);
            name = itemView.findViewById(R.id.list_item_name);
            imageView = itemView.findViewById(R.id.list_item_image);
            date = itemView.findViewById(R.id.list_item_date);
            time = itemView.findViewById(R.id.list_item_time);


            description = itemView.findViewById(R.id.list_item_desc);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra(PostDetailActivity.POST_ID, getPostId());
                    context.startActivity(intent);
                }
            });
        }

        String getPostId() {
            return postId;
        }

        void setPostId(String postId) {
            this.postId = postId;
        }
    }
}
