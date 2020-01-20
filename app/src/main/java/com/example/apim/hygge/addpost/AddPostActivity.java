package com.example.apim.hygge.addpost;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.apim.hygge.R;
import com.example.apim.hygge.data.Post;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class AddPostActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    public static final int PLACE_PICKER_REQUEST = 101;

    Calendar c;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    private int year;
    private int month;
    private int day;
    private int hourOfDay;
    private int minute;
    private String placeId;
    private String placeName;
    private String placeAddress;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private Button pickDate;
    private Button pickTime;
    private java.text.DateFormat dateFormat;
    private java.text.DateFormat timeFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
        }

        if (databaseReference == null) {
            databaseReference = firebaseDatabase.getReference();
        }

        setContentView(R.layout.activity_add_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Date and time:

        // Date:

        pickDate = findViewById(R.id.pick_date);

        dateFormat = DateFormat.getMediumDateFormat(this);

        pickDate.setText(dateFormat.format(new Date()));

        // Get current date:
        c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        datePickerDialog =
                new DatePickerDialog(this, this, year, month, day);

        // Time:

        pickTime = findViewById(R.id.pick_time);

        timeFormat = DateFormat.getTimeFormat(this);

        pickTime.setText(timeFormat.format(new Date()));

        // Get current time:
        hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        timePickerDialog =
                new TimePickerDialog(this, this, hourOfDay, minute, true);

        // Title, description FAB and submit:

        final EditText editTitle = findViewById(R.id.edit_title);
        final EditText editDescription = findViewById(R.id.edit_description);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editTitle.getText().toString().length() >= 4
                        && editDescription.getText().toString().length() >= 4) {
                    Post newPost = new Post();

                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    newPost.setTitle(editTitle.getText().toString());
                    newPost.setDescription(editDescription.getText().toString());

                    newPost.setName(firebaseUser.getDisplayName());
                    newPost.setPhotoUrl(firebaseUser.getPhotoUrl().toString());
                    newPost.setUserId(firebaseUser.getUid().toString());

                    newPost.setYear(Integer.toString(year));
                    newPost.setMonth(Integer.toString(month));
                    newPost.setDay(Integer.toString(day));
                    newPost.setHourOfDay(Integer.toString(hourOfDay));
                    newPost.setMinute(Integer.toString(minute));

                    newPost.setPlaceId(placeId);
                    newPost.setPlaceName(placeName);
                    newPost.setPlaceAddress(placeAddress);

                    databaseReference.child("posts").push().setValue(newPost);

                    finish();
                } else {
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator_layout),
                            "You have to fill everything!", LENGTH_SHORT);
                    snackbar.show();
                }

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Google API client for the Place Picker:
        GoogleApiClient client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        this.year = i;
        this.month = i1;
        this.day = i2;



        pickDate.setText(dateFormat.format(new Date(year-1900, month, day)));
    }

    public void showDatePicker(View view) {
        datePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        this.hourOfDay = i;
        this.minute = i1;

        Date date = new Date(year, month, day, hourOfDay, minute);

        pickTime.setText(timeFormat.format(date));
    }

    public void showTimePicker(View view) {
        timePickerDialog.show();
    }

    /*
     * Show place picker:
     */
    public void showPlacePicker(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                placeId = place.getId();
                placeName = place.getName().toString();
                placeAddress = place.getAddress().toString();
            }
        }
    }
}
