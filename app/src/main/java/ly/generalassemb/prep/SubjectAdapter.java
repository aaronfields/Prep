package ly.generalassemb.prep;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aaronfields on 8/4/16.
 */
public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.subjectViewHolder>{
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference subjectRef = database.getReference().child("subjects");
    LayoutInflater inflater;
    List<String> subjectList;
    Context context;

    public SubjectAdapter(List<String> subjects, Context context){
        this.subjectList = subjects;
        this.context = context;
    }

    @Override
    public SubjectAdapter.subjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.from(parent.getContext()).inflate(R.layout.subject_item, parent, false);
        subjectViewHolder holder = new subjectViewHolder(v, context, subjectList);
        return holder;
    }

    @Override
    public void onBindViewHolder(SubjectAdapter.subjectViewHolder holder, int position) {
        holder.mSubject.setText(subjectList.get(position));

    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public class subjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView mSubject;
        List<String> subjectList = new ArrayList<>();
        Context context;

        public subjectViewHolder(View itemView, Context context, List<String> subjects) {
            super(itemView);
            this.context = context;
            this.subjectList = subjects;

            itemView.setOnClickListener(this);
            mSubject = (TextView) itemView.findViewById(R.id.subject);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            String subject = subjectList.get(position);
            Intent intent = new Intent(this.context, CourseActivity.class);
            intent.putExtra("subject", subject);
            this.context.startActivity(intent);

        }
    }
}
