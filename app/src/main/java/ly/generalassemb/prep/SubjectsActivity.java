package ly.generalassemb.prep;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubjectsActivity extends AppCompatActivity {

    private RecyclerView subjectsRecyclerView;
    private ArrayList<String> mSubjects = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;

    private FirebaseRecyclerAdapter<String, SubjectViewHolder> subjectAdapter;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> drawerAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    private static int RC_SIGN_IN = 7;
    static final int REQUEST_LOCATION = 0;
    boolean isConnected;

    private String name;
    private String email;
    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if( user == null) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    RC_SIGN_IN);

        }

//        Intent intent = new Intent(SubjectsActivity.this, TutorSessionActivity.class);
//        startActivity(intent);

        checkConnection();
        checkPermissions();

        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        FirebaseDatabase myFirebase = FirebaseDatabase.getInstance();
        DatabaseReference subjectsRef = myFirebase.getReference().child("subjects");

        subjectsRecyclerView = (RecyclerView) findViewById(R.id.subjects_recyclerView);
        subjectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        subjectAdapter = new FirebaseRecyclerAdapter<String, SubjectViewHolder>(String.class,
                android.R.layout.two_line_list_item, SubjectViewHolder.class, subjectsRef) {

            @Override
            protected void populateViewHolder(SubjectViewHolder viewHolder, String model, final int position) {
                viewHolder.subjectName.setText(model);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //subjectAdapter.getRef(position);
                        Intent intent = new Intent(SubjectsActivity.this, CourseActivity.class);
                        intent.putExtra("position", position);
                        startActivity(intent);

                    }
                });

            }
        };

        subjectsRecyclerView.setAdapter(subjectAdapter);



    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Log.d("EventsActivity", "This is the current email: " +
                        FirebaseAuth.getInstance().getCurrentUser().getEmail());
                Log.d("EventsActivity", "This is the current uid: " +
                        FirebaseAuth.getInstance().getCurrentUser().getUid());

            }
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                // Name, email address, and profile photo Url
                name = user.getDisplayName();
                email = user.getEmail();
                UID = user.getUid();

                Map<String, String> map = new HashMap<>();
                //map.put("UID", UID);
                map.put("displayname", name);
                map.put("email", email);

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.child("users").child(UID).setValue(map);

            }
        }


    }


        public static class SubjectViewHolder extends RecyclerView.ViewHolder {
            TextView subjectName;
            View mView;

            public SubjectViewHolder(View itemView) {
                super(itemView);
                mView = itemView;

                subjectName = (TextView) mView.findViewById(android.R.id.text1);

            }

        }

    private void addDrawerItems() {
        final String[] osArray = {"Payment", "History", "Tutor with Prep", "Account", "Sign Out"};
        drawerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(drawerAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent paymentIntent = new Intent(SubjectsActivity.this, PaymentActivity.class);
                        startActivity(paymentIntent);
                        break;
                    case 1:
                        Intent historyIntent = new Intent(SubjectsActivity.this, HistoryActivity.class);
                        startActivity(historyIntent);
                        break;
                    case 2:
                        Intent tutorIntent = new Intent(SubjectsActivity.this, TutorActivity.class);
                        startActivity(tutorIntent);
                        break;
                    case 3:
                        Intent accountIntent = new Intent(SubjectsActivity.this, AccountActivity.class);
                        startActivity(accountIntent);
                        break;
                    case 4:
                        AuthUI.getInstance()
                                .signOut(SubjectsActivity.this)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // user is now signed out
                                        Toast.makeText(SubjectsActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SubjectsActivity.this, SubjectsActivity.class));
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
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                return;
            }
        }
    }

    public void checkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.d("SEARCH", "onCreate: You are connected");
            isConnected = true;

        } else {
            Log.d("SEARCH", "onCreate: You are not connected");
            isConnected = false;
        }
    }

    public void checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }


    }

}