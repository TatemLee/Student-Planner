package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentplanner.R;

import java.util.List;

import Activities.CourseActivity;

public class CourseCardAdapter extends RecyclerView.Adapter<CourseVH> {
    List<Integer> id;
    List<String> title;
    List<String> status;


    //constructor
    public CourseCardAdapter(List<Integer> id, List<String> title, List<String> status) {
        this.id = id;
        this.title = title;
        this.status = status;

    }

    @NonNull
    @Override
    public CourseVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_cardview, parent, false);

        return new CourseVH(view).linkAdapter(this);
    }


    @Override
    public void onBindViewHolder(@NonNull CourseVH holder, int position) {
        holder.courseID.setText(id.get(position).toString());
        holder.courseTitle.setText(title.get(position).toString());
        holder.courseStatus.setText(status.get(position).toString());
    }


    @Override
    public int getItemCount() {
        return id.size();
    }

}


    class CourseVH extends RecyclerView.ViewHolder {
        private CourseCardAdapter adapter;
        TextView courseID;
        TextView courseTitle;
        TextView courseStatus;



        public CourseVH(@NonNull View itemView) {
            super(itemView);
            courseID = itemView.findViewById(R.id.course_id);
            courseTitle = itemView.findViewById(R.id.course_title);
            courseStatus = itemView.findViewById(R.id.course_status);

            //add/remove items from list of user selected items
            itemView.findViewById(R.id.bt_checkbox).setOnClickListener(view -> {
                //instantiate ImageButton object to change icon
                ImageButton checkbox = itemView.findViewById(R.id.bt_checkbox);
                //check if item is selected already
                if(CourseActivity.selected != null) {
                    for (int i = 0; i < CourseActivity.selected.size(); i++) {
                        //if item is selected already
                        if (CourseActivity.selected.get(i) == adapter.id.get(getAdapterPosition())) {
                            //uncheck box icon
                            checkbox.setImageResource(R.drawable.ic_unchecked_icon);
                            //remove item from selected list
                            CourseActivity.selected.remove(i);
                            return;
                        }
                    }
                }

                //if item is being selected
                checkbox.setImageResource(R.drawable.ic_checked_icon);
                CourseActivity.selected.add(adapter.id.get(getAdapterPosition()));
            });

        }

        public CourseVH linkAdapter(CourseCardAdapter adapter) {
            this.adapter = adapter;
            return this;
        }
    }
























