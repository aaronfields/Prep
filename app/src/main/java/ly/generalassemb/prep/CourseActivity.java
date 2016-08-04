package ly.generalassemb.prep;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.Subject;

public class CourseActivity extends AppCompatActivity {
    CourseAdapter mCourseAdapter;
    SubjectAdapter mSubjectAdapter;
    RecyclerView courseRecyclerView;
    RecyclerView subjectRecyclerView;
    RecyclerView.LayoutManager courseLayoutManager;
    RecyclerView.LayoutManager subjectLayoutManager;
    List<String> courses;
    List<String> subjects;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference courseRef = database.getReference("courses");
    DatabaseReference subjectRef = database.getReference("subjects");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        // Use Firebase UI to populate RecyclerViews

        subjectRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        subjectLayoutManager = new LinearLayoutManager(this);
        subjectRecyclerView.setLayoutManager(subjectLayoutManager);
        subjects = new ArrayList<>();
        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String subject = dataSnapshot.getKey();
                subjects.add(subject);

                for(int i = 0; i < dataSnapshot.getChildrenCount(); i++){
                    String subjects = dataSnapshot.child("subjects").getValue().toString();
                    String m = new String(subjects);
                    // add m to array list;
                    mSubjectAdapter = new SubjectAdapter(subjects, CourseActivity.this);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        subjectRef.addChildEventListener(listener);



    }
}
