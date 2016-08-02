package ly.generalassemb.prep;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by aaronfields on 8/2/16.
 */
public class CourseAdapter extends RecyclerView.Adapter{

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference courseRef = database.getReference().child("subjects");

    
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
