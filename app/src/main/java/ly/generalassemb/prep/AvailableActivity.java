package ly.generalassemb.prep;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AvailableActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener{

    private String availability;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private boolean mPermissionDenied = false;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    private TextView mLatitudeText;
    private TextView mLongitudeText;

    private double latitude;
    private double longitude;
    private Location location;

    private LatLng userLocation;
    private double userLatitude;
    private double userLongitude;

    private LatLng currentLocation;
    Marker currLocationMarker;
    Marker studentMarker;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> drawerAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private String mClass;
    private String userClass;
    private String userName;
    private String userActive;

    private ImageView pinButton;
    private LatLng myLatLng;
    private String name;
    private String email;
    private String UID;
    private String myLatitude;
    private String myLongitude;
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<LatLng> latlngs = new ArrayList<>();
    private Marker userMarker;

    private Switch mswitch;
    private String tutorCourse;
    private ArrayList<String> tCourse;
    private DataSnapshot snapshot;
    Map<String, String> map;
    private DatabaseReference studentRef;
    private DatabaseReference sessionsRef;
    private DatabaseReference tutorRef;
    private Geocoder geocoder;
    private String address;
    private List<Address> addresses;
    private String userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available);

//        Intent intent = getIntent();
//        mClass = intent.getStringExtra("class");

        tCourse = new ArrayList<>();
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses  = geocoder.getFromLocation(latitude,longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }


        mDrawerList = (ListView)findViewById(R.id.navList);mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Prep");


         //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.tutor_map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


    }

    protected void onStart() {

        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {

        super.onStop();
        mGoogleApiClient.disconnect();
    }

    public AvailableActivity() {
        super();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastLocation = getLocation(mGoogleApiClient);

        mMap.setMyLocationEnabled(true);
        mMap.setBuildingsEnabled(true);
        latitude = mLastLocation.getLatitude();
        longitude = mLastLocation.getLongitude();
        currentLocation = new LatLng(latitude, longitude);

//        mMap.addMarker(new MarkerOptions().position(currentLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17));


        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("UID", "onConnected: " +UID);
        tutorRef = FirebaseDatabase.getInstance().getReference().child("tutors").child(UID).child("courses");

        tutorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("TUTORS", "COURSES: ");
                    tCourse = (ArrayList<String>) snapshot.getValue();
                    //Map<String, String> tutorMap = new HashMap<>();
                    //tutorMap = (HashMap) postSnapshot.getValue();
//                for(String course : tCourse){
//                    mClass = course;
//                    Log.d("MCLASS", "onDataChange: " + mClass);
//                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        studentRef = FirebaseDatabase.getInstance().getReference().child("users");

        studentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    map = new HashMap<>();
                    map = (HashMap) postSnapshot.getValue();
                    userClass = map.get("course");
                    Log.d("AM I ACTIVE", "onDataChange: " + userClass);
                    userName = map.get("displayname");
                    userActive = map.get("active");
                    Log.d("AM I ACTIVE", "onDataChange: " + userActive);

                    for(String course : tCourse) {
                        mClass = course;
                        Log.d("MCLASS", "onDataChange: " + mClass);


                        if (mClass.equals(userClass)) {

                            userLatitude = Double.valueOf(map.get("latitude"));
                            userLongitude = Double.valueOf(map.get("longitude"));
                            Log.d("LATITUDE", "MY LATITUDE IS: " + map.get("latitude"));
                            userClass = map.get("course");
                            userPhone = map.get("phonenumber");

                            Log.d("CLASS", "onDataChange: " + mClass + "theirClass: " + userClass);

                            mMap.setOnMarkerClickListener(AvailableActivity.this);

                            //if (userClass.equals(mClass)) {
                            userLocation = new LatLng(userLatitude, userLongitude);
//                    mMap.addMarker(options.position(userLocation));
                            latlngs.add(userLocation);

                            for (LatLng point : latlngs) {
                                Log.d("LATLONG", "onDataChange: " + point);
                                options.position(point);
                                mMap.addMarker(options).setTitle(userClass);
//                                studentMarker = mMap.addMarker(options);
//                                studentMarker.setSnippet(userClass);
                            }

//                            userMarker = mMap.addMarker(options);
//                            userMarker.setSnippet(userClass);


                        }
                    }
                    }
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        userLocation = new LatLng(userLatitude, userLongitude);
        mMap.addMarker(new MarkerOptions().position(userLocation));


    }

    public Location getLocation(GoogleApiClient client) {
        location = null;
        location = LocationServices.FusedLocationApi.getLastLocation(client);

        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d("LATLONG", "lat = " + latitude + " long = " + longitude);
        }
        return location;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                latitude = cameraPosition.target.latitude;
                longitude = cameraPosition.target.longitude;

                Log.d("centerLat","here: "+cameraPosition.target.latitude);

                Log.d("centerLong","here: "+cameraPosition.target.longitude);

            }
        });


//        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
//                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                Log.i("TAG", "Place: " + place.getName());
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                Log.i("TAG", "An error occurred: " + status);
//            }
//        });

    }

    private void addDrawerItems() {
        String[] osArray = { "Back to Subjects", "Payment", "History", "Switch to Student", "Account", "Sign Out"};
        drawerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(drawerAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent courseIntent = new Intent(AvailableActivity.this, TutorActivity.class);
                        startActivity(courseIntent);
                        break;
                    case 1:
                        Intent paymentIntent = new Intent(AvailableActivity.this, PaymentActivity.class);
                        startActivity(paymentIntent);
                        break;
                    case 2:
                        Intent historyIntent = new Intent(AvailableActivity.this, HistoryActivity.class);
                        startActivity(historyIntent);
                        break;
                    case 3:
                        Intent tutorIntent = new Intent(AvailableActivity.this, SubjectsActivity.class);
                        startActivity(tutorIntent);
                        break;
                    case 4:
                        Intent accountIntent = new Intent(AvailableActivity.this, AccountActivity.class);
                        startActivity(accountIntent);
                        break;
                    case 5:
                        AuthUI.getInstance()
                                .signOut(AvailableActivity.this)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // user is now signed out
                                        Toast.makeText(AvailableActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(AvailableActivity.this, SubjectsActivity.class));
                                        finish();
                                    }
                                });
                        break;

                }
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.drawer, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        try {
            addresses  = geocoder.getFromLocation(userLatitude,userLongitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        address = addresses.get(0).getAddressLine(0);

        AlertDialog.Builder builder = new AlertDialog.Builder(AvailableActivity.this);
                builder.setTitle(userClass);
                builder.setMessage("Accept this request?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(AvailableActivity.this, "Accepted!", Toast.LENGTH_SHORT).show();

                        map.put("tutorMatched", "yes");
                        map.put("sessionID", UID);
                        studentRef = FirebaseDatabase.getInstance().getReference().child("users");
                        String studentID = map.get("UID");

                        //ref.child("tutors").child(UID).setValue(map);

                        sessionsRef = FirebaseDatabase.getInstance().getReference().child("sessions");


                        sessionsRef.child(UID).setValue(map);
                        studentRef.child(studentID).setValue(map);

                        AlertDialog.Builder myBuilder = new AlertDialog.Builder(AvailableActivity.this);
                        myBuilder.setTitle("Prep");
                        myBuilder.setMessage(map.get("displayname") + " is located at " + address
                                + ". Their phone number is: " + userPhone + ".");
                        myBuilder.show();

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                    }
                });
        builder.show();
        return false;
    }
}
