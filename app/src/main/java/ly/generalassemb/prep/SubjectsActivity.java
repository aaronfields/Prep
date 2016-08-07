package ly.generalassemb.prep;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SubjectsActivity extends AppCompatActivity {

    private RecyclerView subjectsRecyclerView;
    private ArrayList<String> mSubjects = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;

    private FirebaseRecyclerAdapter<String, SubjectViewHolder> subjectAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);

        FirebaseDatabase myFirebase = FirebaseDatabase.getInstance();
        DatabaseReference subjectsRef = myFirebase.getReference().child("subjects");

        subjectsRecyclerView = (RecyclerView) findViewById(R.id.subjects_recyclerView);
        subjectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        subjectAdapter = new FirebaseRecyclerAdapter<String, SubjectViewHolder>(String.class,
                        android.R.layout.two_line_list_item,SubjectViewHolder.class, subjectsRef) {

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

    public static class SubjectViewHolder extends RecyclerView.ViewHolder{
        TextView subjectName;
        View mView;

        public SubjectViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            subjectName = (TextView) mView.findViewById(android.R.id.text1);

        }

    }

}