package ly.generalassemb.prep;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

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
    private double cameraLatitude;
    private double cameraLongitude;

    private LatLng currentLocation;
    Marker currLocationMarker;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> drawerAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private String mClass;

    private ImageView pinButton;
    private Button requestTutor;
    private LatLng myLatLng;
    private String name;
    private String email;
    private String UID;
    private String myLatitude;
    private String myLongitude;
    private Key sessionID;

    Map<String, String> map;
    DatabaseReference ref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        pinButton = (ImageView) findViewById(R.id.imageMarker);
        requestTutor = (Button) findViewById(R.id.request_button);

        Intent intent = getIntent();
        mClass = intent.getStringExtra("class");

        mDrawerList = (ListView)findViewById(R.id.navList);mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(mClass);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            name = user.getDisplayName();
            email = user.getEmail();
            UID = user.getUid();
        }

        ref = FirebaseDatabase.getInstance().getReference().child("users");

    if(ref.child(UID)!=null) {

        ref.child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //String sessionID = (String) dataSnapshot.getValue();
                if (dataSnapshot.hasChild("sessionID")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    builder.setTitle("A tutor is on the way!");
                    builder.show();
                    //builder.setMessage("Accept this request?");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            // Retrieve new posts as they are added to the database

        });
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

        mMap.setMyLocationEnabled(true);
        mMap.setBuildingsEnabled(true);
        latitude = mLastLocation.getLatitude();
        longitude = mLastLocation.getLongitude();
        currentLocation = new LatLng(latitude, longitude);
        //mMap.addMarker(new MarkerOptions().position(currentLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17));
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                latitude = cameraPosition.target.latitude;
                longitude = cameraPosition.target.longitude;

                Log.d("centerLat","here: "+latitude);

                Log.d("centerLong","here: "+longitude);

                requestTutor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myLatLng = new LatLng(latitude, longitude);
                        myLatitude = Double.toString(latitude);
                        myLongitude = Double.toString(longitude);

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            name = user.getDisplayName();
                            email = user.getEmail();
                            UID = user.getUid();

                            map = new HashMap<>();
                            map.put("UID", UID);
                            map.put("displayname", name);
                            map.put("email", email);
                            map.put("course", mClass);
                            map.put("latitude", myLatitude);
                            map.put("longitude", myLongitude);
                            map.put("active", "yes");

                            ref = FirebaseDatabase.getInstance().getReference().child("users");
                            ref.child(UID).setValue(map);

                            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                            builder.setMessage("Searching for Tutor...");
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    map.remove("active");
                                    ref.child("users").child(UID).setValue(map);
                                    dialogInterface.cancel();

                                }
                            });
                            builder.show();

                        }

                    }
                });

            }
        });





        //mLastLocation = getLocation(mGoogleApiClient);


        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        //Log.d("MAPLATLONG", "lat: " + mLastLocation.getLatitude() + " long: "+ mLastLocation.getLatitude());


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





    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void addDrawerItems() {
        String[] osArray = { "Back to Subjects", "Payment", "History", "Tutor with Prep", "Account", "Sign Out"};
        drawerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(drawerAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent courseIntent = new Intent(MapsActivity.this, SubjectsActivity.class);
                        startActivity(courseIntent);
                        break;
                    case 1:
                        Intent paymentIntent = new Intent(MapsActivity.this, PaymentActivity.class);
                        startActivity(paymentIntent);
                        break;
                    case 2:
                        Intent historyIntent = new Intent(MapsActivity.this, HistoryActivity.class);
                        startActivity(historyIntent);
                        break;
                    case 3:
                        Intent tutorIntent = new Intent(MapsActivity.this, TutorActivity.class);
                        startActivity(tutorIntent);
                        break;
                    case 4:
                        Intent accountIntent = new Intent(MapsActivity.this, AccountActivity.class);
                        startActivity(accountIntent);
                        break;
                    case 5:
                        AuthUI.getInstance()
                                .signOut(MapsActivity.this)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // user is now signed out
                                        Toast.makeText(MapsActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(MapsActivity.this, SubjectsActivity.class));
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