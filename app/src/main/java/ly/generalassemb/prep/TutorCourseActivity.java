package ly.generalassemb.prep;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

public class TutorCourseActivity extends AppCompatActivity {

    private RecyclerView courseRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private FirebaseRecyclerAdapter<String, CourseViewHolder> courseAdapter;

    private int position;
    DatabaseReference coursesRef;
    Query courseQuery;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> drawerAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private String UID;
    private String name;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_course);

        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        Log.d("POSITION", "POSITION: " + position);

        FirebaseDatabase myFirebase = FirebaseDatabase.getInstance();
        coursesRef = myFirebase.getReference().child("courses");

        switch (position) {
            case 0:
                courseQuery = coursesRef.orderByKey().startAt("ACC310F").endAt("ACC312");
                break;
            case 1:
                courseQuery = coursesRef.orderByKey().startAt("BIO301D").endAt("BIO325");
                break;
            case 2:
                courseQuery = coursesRef.orderByKey().startAt("CH301").endAt("CH328N");
                break;
            case 3:
                courseQuery = coursesRef.orderByKey().startAt("CS302").endAt("CS314");
                break;
            case 4:
                courseQuery = coursesRef.orderByKey().startAt("ECO301").endAt("ECO420K");
                break;
            case 5:
                courseQuery = coursesRef.orderByKey().startAt("FR601C").endAt("FR611C");
                break;
            case 6:
                courseQuery = coursesRef.orderByKey().startAt("M302").endAt("M408N");
                break;
            case 7:
                courseQuery = coursesRef.orderByKey().startAt("PHY301").endAt("PHY317L");
                break;
            case 8:
                courseQuery = coursesRef.orderByKey().startAt("SPN601D").endAt("SPN611D");
                break;
            case 9:
                courseQuery = coursesRef.orderByKey().startAt("SDS302").endAt("SDS328M");
                break;
        }


        courseRecyclerView = (RecyclerView) findViewById(R.id.course_recyclerView);
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        courseAdapter = new FirebaseRecyclerAdapter<String, CourseViewHolder>(String.class,
                android.R.layout.two_line_list_item,CourseViewHolder.class, courseQuery) {

            @Override
            protected void populateViewHolder(final CourseViewHolder viewHolder, final String model, final int position) {
                viewHolder.courseName.setText(model);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(TutorCourseActivity.this, AvailableActivity.class);
                        intent.putExtra("class", model);
                        startActivity(intent);

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
                            map.put("courses", model);

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            ref.child("tutors").child(UID).setValue(map);

                        }

                    }
                });

            }
        };

        courseRecyclerView.setAdapter(courseAdapter);

    }



    public static class CourseViewHolder extends RecyclerView.ViewHolder{
        TextView courseName;
        View mView;

        public CourseViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            courseName = (TextView) mView.findViewById(android.R.id.text1);

        }

    }


    private void addDrawerItems() {
        final String[] osArray = {"Back to Subjects","Payment", "History", "Tutor with Prep", "Account", "Sign Out"};
        drawerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(drawerAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent subjectsIntent = new Intent(TutorCourseActivity.this, SubjectsActivity.class);
                        startActivity(subjectsIntent);
                        break;
                    case 1:
                        Intent paymentIntent = new Intent(TutorCourseActivity.this, PaymentActivity.class);
                        startActivity(paymentIntent);
                        break;
                    case 2:
                        Intent historyIntent = new Intent(TutorCourseActivity.this, HistoryActivity.class);
                        startActivity(historyIntent);
                        break;
                    case 3:
                        Intent tutorIntent = new Intent(TutorCourseActivity.this, TutorActivity.class);
                        startActivity(tutorIntent);
                        break;
                    case 4:
                        Intent accountIntent = new Intent(TutorCourseActivity.this, AccountActivity.class);
                        startActivity(accountIntent);
                        break;
                    case 5:
                        AuthUI.getInstance()
                                .signOut(TutorCourseActivity.this)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // user is now signed out
                                        Toast.makeText(TutorCourseActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(TutorCourseActivity.this, SubjectsActivity.class));
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
