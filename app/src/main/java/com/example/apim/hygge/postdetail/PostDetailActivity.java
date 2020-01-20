package com.example.apim.hygge.postdetail;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.apim.hygge.R;
import com.example.apim.hygge.data.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class PostDetailActivity extends AppCompatActivity {

    public static final String POST_ID = "postId";

    private TextView title;
    private TextView name;
    private ImageView imageView;
    private TextView description;

    private java.text.DateFormat dateFormat;
    private java.text.DateFormat timeFormat;
    private TextView date;
    private TextView time;

    private TextView placeName;
    private TextView placeAddress;
    private Button deleteButton;

    private Post post;
    private DatabaseReference rootReference;
    private DatabaseReference queryReference;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //ab.setDisplayShowHomeEnabled(true);


        title = findViewById(R.id.detail_title);

        description = findViewById(R.id.detail_description);

        name = findViewById(R.id.detail_name);
        imageView = findViewById(R.id.detail_image);

        dateFormat = DateFormat.getMediumDateFormat(this);
        timeFormat = DateFormat.getTimeFormat(this);
        date = findViewById(R.id.detail_date);
        time = findViewById(R.id.detail_time);

        placeName = findViewById(R.id.detail_place_name);
        placeAddress = findViewById(R.id.detail_place_address);

        String postId = getIntent().getStringExtra(POST_ID);

        rootReference = FirebaseDatabase.getInstance().getReference();

        queryReference = rootReference.child("posts").child(postId);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        deleteButton = findViewById(R.id.detail_delete_btn);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        queryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                post = dataSnapshot.getValue(Post.class);

                if (post != null) {
                    title.setText(post.getTitle());
                    description.setText(post.getDescription());

                    if (post.getName() != null) {
                        name.setText(post.getName());
                    }

                    if (post.getYear() != null && post.getMinute() != null) {
                        Log.d("YearAndMinute", Integer.parseInt(post.getYear())+", "+post.getMinute());
                        date.setText(dateFormat.format(
                                new Date(Integer.parseInt(post.getYear()) - 1900,
                                        Integer.parseInt(post.getMonth()),
                                        Integer.parseInt(post.getDay())
                                )));

                        time.setText(timeFormat.format(
                                new Date(Integer.parseInt(post.getYear()) - 1900,
                                        Integer.parseInt(post.getMonth()),
                                        Integer.parseInt(post.getDay()),
                                        Integer.parseInt(post.getHourOfDay()),
                                        Integer.parseInt(post.getMinute())
                                )));
                    }

                    if (post.getPlaceId() != null && post.getPlaceName() != null
                            && post.getPlaceAddress() != null) {
                        placeName.setText(post.getPlaceName());
                        placeAddress.setText(post.getPlaceAddress());
                    }

                    Picasso.with(getBaseContext()).load(post.getPhotoUrl())
                            .error(R.drawable.ic_person_black_24dp)
                            .placeholder(R.drawable.ic_person_black_24dp)
                            .into(imageView);

                    // Delete button only visible if Post was created by the user:
                    if (post.getUserId() != null && post.getUserId().equals(firebaseUser.getUid())) {
                        deleteButton.setVisibility(View.VISIBLE);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void deletePost(View view) {
        if (post != null) {
            // Delete confirmation dialog:
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.delete_post);
            builder.setMessage(R.string.delete_sure);

            builder.setPositiveButton(R.string.delete_yes,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (queryReference != null) {
                                queryReference.removeValue();
                            }
                            finish();
                        }
                    });

            builder.setNegativeButton(R.string.delete_no,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
