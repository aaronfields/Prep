package ly.generalassemb.prep;

import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AvailableActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

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

    private LatLng currentLocation;
    Marker currLocationMarker;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> drawerAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private String mClass;

    private ImageView pinButton;
    private LatLng myLatLng;
    private String name;
    private String email;
    private String UID;
    private String myLatitude;
    private String myLongitude;

    private Switch mswitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available);

        Intent intent = getIntent();
        mClass = intent.getStringExtra("class");


        mDrawerList = (ListView)findViewById(R.id.navList);mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Availabe");


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.tutor_map);
//        mapFragment.getMapAsync(this);

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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastLocation = getLocation(mGoogleApiClient);

//        mMap.setMyLocationEnabled(true);
//        mMap.setBuildingsEnabled(true);
        latitude = mLastLocation.getLatitude();
        longitude = mLastLocation.getLongitude();
        currentLocation = new LatLng(latitude, longitude);
//        mMap.addMarker(new MarkerOptions().position(currentLocation));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        mswitch = (Switch) findViewById(R.id.availability_switch);
        mswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    availability = "yes";

                    myLatLng = new LatLng(latitude, longitude);
                    myLatitude = Double.toString(latitude);
                    myLongitude = Double.toString(longitude);

                    FirebaseUser tutor = FirebaseAuth.getInstance().getCurrentUser();
                    if (tutor != null) {
                        // Name, email address, and profile photo Url
                        name = tutor.getDisplayName();
                        email = tutor.getEmail();
                        UID = tutor.getUid();

                        Map<String, String> map = new HashMap<>();
                        //map.put("UID", UID);
                        map.put("displayname", name);
                        map.put("email", email);
                        map.put("available", availability);
                        map.put("courses", mClass);
                        map.put("latitude", myLatitude);
                        map.put("longitude", myLongitude);

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child("tutors").child(UID).setValue(map);

                    }
                } else {
                    // The toggle is disabled

                    availability = "no";

                    FirebaseUser tutor = FirebaseAuth.getInstance().getCurrentUser();
                    if (tutor != null) {
                        // Name, email address, and profile photo Url
                        name = tutor.getDisplayName();
                        email = tutor.getEmail();
                        UID = tutor.getUid();

                        Map<String, String> map = new HashMap<>();
                        //map.put("UID", UID);
                        map.put("displayname", name);
                        map.put("email", email);
                        map.put("available", availability);
                        map.put("courses", mClass);


                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child("tutors").child(UID).setValue(map);

                    }
                }
            }

        });

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


        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("TAG", "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });

    }

    private void addDrawerItems() {
        String[] osArray = { "Back to Courses", "Payment", "History", "Tutor with Prep", "Account", "Sign Out"};
        drawerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(drawerAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent courseIntent = new Intent(AvailableActivity.this, CourseActivity.class);
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
                        Intent tutorIntent = new Intent(AvailableActivity.this, TutorActivity.class);
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




}
