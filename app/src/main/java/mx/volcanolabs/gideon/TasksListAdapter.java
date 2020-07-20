package mx.volcanolabs.gideon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mx.volcanolabs.gideon.models.Task;

public class TasksListAdapter extends RecyclerView.Adapter<TasksListAdapter.ViewHolder> {
    private List<Task> tasks;
    private TasksActions listener;

    public TasksListAdapter(TasksActions listener) {
        this.listener = listener;
    }

    public void setData(List<Task> taskList) {
        tasks = taskList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks != null ? tasks.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription;
        TextView tvLocation;
        TextView tvGroup;
        CheckBox chxComplete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvGroup = itemView.findViewById(R.id.tv_group);
            chxComplete = itemView.findViewById(R.id.chx_complete);
        }

        public void bind(Task task) {
            tvDescription.setText(task.getDescription());
            tvGroup.setText(task.getGroup().getName());
            chxComplete.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
                listener.onTaskCompletedkClicked(task);
            });
            chxComplete.setChecked(task.isCompleted());

            if (task.getLocation() != null) {
                tvLocation.setText(task.getLocation().getAddress());
            } else {
                tvLocation.setText(R.string.no_location);
            }
        }
    }

    public interface TasksActions {
        void onTaskCompletedkClicked(Task task);
    }
}
