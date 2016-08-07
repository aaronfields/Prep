package ly.generalassemb.prep;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aaronfields on 8/2/16.
 */
public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.courseViewHolder>{

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference courseRef = database.getReference().child("courses");
    LayoutInflater inflater;
    Context context;
    List<String> courseList;
    List<String> mKeys;

    public CourseAdapter(List<String> courseList, List<String> keys, Context context){
        this.courseList = courseList;
        this.context = context;
        this.mKeys = keys;
    }


    @Override
    public courseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.from(parent.getContext()).inflate(R.layout.course_item, parent, false);
        courseViewHolder holder = new courseViewHolder(v, context, courseList);
        return holder;
    }

    @Override
    public void onBindViewHolder(courseViewHolder holder, int position) {
        String course = courseList.get(position);
        holder.mCourse.setText(courseList.get(position));
    }

    @Override
    public int getItemCount() {
        Log.d("TAG", "courseList: " + courseList.size());
        return courseList.size();

    }

    public class courseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView mCourse;
        List<String> courseList = new ArrayList<>();
        Context context;

        public courseViewHolder(View itemView, Context context, List<String> course) {
            super(itemView);
            this.courseList = course;
            this.context = context;

            itemView.setOnClickListener(this);
            mCourse = (TextView) itemView.findViewById(R.id.course);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            String course = courseList.get(position);
            Intent intent = new Intent(this.context, MapsActivity.class);
            intent.putExtra("course", course);
            this.context.startActivity(intent);

        }
    }
}
