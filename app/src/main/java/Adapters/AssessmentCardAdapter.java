package Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentplanner.R;

import java.util.List;

import Activities.AssessmentActivity;
import Activities.TermActivity;
import Database.Assessment;


public class AssessmentCardAdapter extends RecyclerView.Adapter<AssessmentVH>{

    List<Integer> id;
    List<String> title;
    List<String> type;

    //constructor
    public AssessmentCardAdapter(List<Integer> id, List<String> title, List<String> type) {
        this.id = id;
        this.title = title;
        this.type = type;
    }



    @NonNull
    @Override
    public AssessmentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assessment_cardview, parent, false);

        return new AssessmentVH(view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull AssessmentVH holder, int position) {
        holder.assessmentID.setText(id.get(position).toString());
        holder.assessmentTitle.setText(title.get(position));
        holder.assessmentType.setText(type.get(position));
    }

    @Override
    public int getItemCount() {
        return id.size();
    }
}




class AssessmentVH extends RecyclerView.ViewHolder {
    private AssessmentCardAdapter adapter;
    TextView assessmentID;
    TextView assessmentTitle;
    TextView assessmentType;



    public AssessmentVH(@NonNull View itemView) {
        super(itemView);
        assessmentID = itemView.findViewById(R.id.assessment_id);
        assessmentTitle = itemView.findViewById(R.id.assessment_title);
        assessmentType = itemView.findViewById(R.id.assessment_type);


        //add/remove items from list of user selected items
        itemView.findViewById(R.id.bt_checkbox).setOnClickListener(view -> {
            //instantiate ImageButton object to change icon
            ImageButton checkbox = itemView.findViewById(R.id.bt_checkbox);
            //check if item is selected already
            if(AssessmentActivity.selected != null) {
                for (int i = 0; i < AssessmentActivity.selected.size(); i++) {
                    //if item is selected already
                    if (AssessmentActivity.selected.get(i) == adapter.id.get(getAdapterPosition())) {
                        //uncheck box icon
                        checkbox.setImageResource(R.drawable.ic_unchecked_icon);
                        //remove item from selected list
                        AssessmentActivity.selected.remove(i);
                        return;
                    }
                }
            }
            //if item is being selected
            checkbox.setImageResource(R.drawable.ic_checked_icon);
            AssessmentActivity.selected.add(adapter.id.get(getAdapterPosition()));
        });


    }

    public AssessmentVH linkAdapter(AssessmentCardAdapter adapter) {
        this.adapter = adapter;
        return this;
    }
}