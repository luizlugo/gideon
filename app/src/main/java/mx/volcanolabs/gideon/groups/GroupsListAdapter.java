package mx.volcanolabs.gideon.groups;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mx.volcanolabs.gideon.R;
import mx.volcanolabs.gideon.models.Group;

public class GroupsListAdapter extends RecyclerView.Adapter<GroupsListAdapter.ViewHolder> {
    private List<Group> groupList;
    private GroupActions listener;

    public GroupsListAdapter(GroupActions listener) {
        this.listener = listener;
    }

    public void setData(List<Group> groupList) {
        this.groupList = groupList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(groupList.get(position));
    }

    @Override
    public int getItemCount() {
        return groupList == null ? 0 : groupList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvName;
        TextView tvNote;
        ImageButton btnRemove;
        Group group;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvNote = itemView.findViewById(R.id.tv_note);
            btnRemove = itemView.findViewById(R.id.btn_remove);
            itemView.setOnClickListener(this);
        }

        public void bind(Group group) {
            this.group = group;
            tvName.setText(group.getName());
            tvNote.setText(group.getNote());
            btnRemove.setOnClickListener(v -> listener.onGroupRemoveClicked(group));
        }

        @Override
        public void onClick(View v) {
            listener.onGroupClicked(group);
        }
    }

    public interface GroupActions {
        void onGroupRemoveClicked(Group group);
        void onGroupClicked(Group group);
    }
}
