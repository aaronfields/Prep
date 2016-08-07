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

        switch (position) {
            case 3:
                coursesRef = myFirebase.getReference().child("courses");
                courseQuery = coursesRef.startAt("CS302").endAt("CS314");
                //courseQuery = myFirebase.getReference().child("courses").startAt("CS").endAt("CS314");

                courseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String course = dataSnapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        }


        courseRecyclerView = (RecyclerView) findViewById(R.id.course_recyclerView);
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        courseAdapter = new FirebaseRecyclerAdapter<String, CourseViewHolder>(String.class,
                android.R.layout.two_line_list_item,CourseViewHolder.class, courseQuery) {

            @Override
            protected void populateViewHolder(CourseViewHolder viewHolder, String model, final int position) {
                viewHolder.courseName.setText(model);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //subjectAdapter.getRef(position);
                        switch (position) {
                            case 0:



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



}