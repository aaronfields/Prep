package ly.generalassemb.prep;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class CourseActivity extends AppCompatActivity {
    private RecyclerView courseRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private FirebaseRecyclerAdapter<String, CourseViewHolder> courseAdapter;

    private int position;
    DatabaseReference coursesRef;
    Query courseQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);


        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        Log.d("POSITION", "POSITION: " + position);

        FirebaseDatabase myFirebase = FirebaseDatabase.getInstance();
        coursesRef = myFirebase.getReference().child("courses");

        switch (position) {
            case 0:
                courseQuery = coursesRef.startAt("ACC310F").endAt("ACC312");
                break;
            case 1:
                courseQuery = coursesRef.startAt("BIO301D").endAt("BIO325");
                break;
            case 2:
                courseQuery = coursesRef.startAt("CH301").endAt("CH328N");
                break;
            case 3:
                courseQuery = coursesRef.startAt("CS302").endAt("CS314");
                //courseQuery = myFirebase.getReference().child("courses").startAt("CS").endAt("CS314");
                break;
            case 4:
                courseQuery = coursesRef.startAt("ECO301").endAt("ECO420K");
                break;
            case 5:
                courseQuery = coursesRef.startAt("FR601C").endAt("FR611C");
                break;
            case 6:
                courseQuery = coursesRef.startAt("M302").endAt("M408N");
                break;
            case 7:
                courseQuery = coursesRef.startAt("PHY301").endAt("PHY317L");
                break;
            case 8:
                courseQuery = coursesRef.startAt("SPN601D").endAt("SPN611D");
                break;
            case 9:
                courseQuery = coursesRef.startAt("SDS302").endAt("SDS328M");
                break;
        }

                courseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String course = dataSnapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




        courseRecyclerView = (RecyclerView) findViewById(R.id.course_recyclerView);
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        courseAdapter = new FirebaseRecyclerAdapter<String, CourseViewHolder>(String.class,
                android.R.layout.two_line_list_item,CourseViewHolder.class, coursesRef) {

            @Override
            protected void populateViewHolder(CourseViewHolder viewHolder, String model, final int position) {
                viewHolder.courseName.setText(model);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CourseActivity.this, MapsActivity.class);
                        startActivity(intent);

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



}