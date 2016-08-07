package ly.generalassemb.prep;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SubjectsActivity extends AppCompatActivity {

    private RecyclerView subjectsRecyclerView;
    private ArrayList<String> mSubjects = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;

    private ListView listView;

    FirebaseRecyclerAdapter<String, SubjectViewHolder> subjectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);

        Log.d("FIREBASE", "onCreate: ");

        FirebaseDatabase myFirebase = FirebaseDatabase.getInstance();
        DatabaseReference subjectsRef = myFirebase.getReference().child("subjects");

        subjectsRecyclerView = (RecyclerView) findViewById(R.id.subjects_recyclerView);
        subjectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Log.d("FIREBASE", "I'm here");

        subjectAdapter = new FirebaseRecyclerAdapter<String, SubjectViewHolder>(String.class,
                        android.R.layout.two_line_list_item,SubjectViewHolder.class, subjectsRef) {

                    @Override
                    protected void populateViewHolder(SubjectViewHolder viewHolder, String model, int position) {
                        viewHolder.subjectName.setText(model);
                        Log.d("FIREBASE", "We made it this far: ");

                    }
                };


        Log.d("FIREBASE", "RIGHT BEFORE ADAPTER");
        subjectsRecyclerView.setAdapter(subjectAdapter);

    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView subjectName;

        public SubjectViewHolder(View itemView) {
            super(itemView);

            subjectName = (TextView) itemView.findViewById(android.R.id.text1);

        }
    }


}