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

import Activities.TermActivity;


public class TermCardAdapter extends RecyclerView.Adapter<TermVH> {
    List<Integer> id;
    List<String> title;

    //constructor
    public TermCardAdapter(List<Integer> id, List<String> title) {
        this.id = id;
        this.title = title;
    }

    @NonNull
    @Override
    public TermVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.term_cardview, parent, false);

        return new TermVH(view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull TermVH holder, int position) {
        holder.termID.setText(id.get(position).toString());
        holder.termTitle.setText(title.get(position).toString());

    }

    @Override
    public int getItemCount() {
        return id.size();
    }
}

class TermVH extends  RecyclerView.ViewHolder {
    private TermCardAdapter adapter;
    TextView termID;
    TextView termTitle;


    public TermVH(@NonNull View itemView) {
        super(itemView);

        termID = itemView.findViewById(R.id.term_id);
        termTitle = itemView.findViewById(R.id.term_title);

        //add/remove items from list of user selected items
        itemView.findViewById(R.id.bt_checkbox).setOnClickListener(view -> {
            //instantiate ImageButton object to change icon
            ImageButton checkbox = itemView.findViewById(R.id.bt_checkbox);
            //check if item is selected already
            if(TermActivity.selected != null) {
                for (int i = 0; i < TermActivity.selected.size(); i++) {
                    //if item is selected already
                    if (TermActivity.selected.get(i) == adapter.id.get(getAdapterPosition())) {
                        //uncheck box icon
                        checkbox.setImageResource(R.drawable.ic_unchecked_icon);
                        //remove item from selected list
                        TermActivity.selected.remove(i);
                        return;
                    }
                }
            }
            //if item is being selected
            checkbox.setImageResource(R.drawable.ic_checked_icon);
            TermActivity.selected.add(adapter.id.get(getAdapterPosition()));
        });
    }

    public TermVH linkAdapter(TermCardAdapter adapter) {
        this.adapter = adapter;
        return this;
    }
}










