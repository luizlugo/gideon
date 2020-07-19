package mx.volcanolabs.gideon.locations;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mx.volcanolabs.gideon.R;
import mx.volcanolabs.gideon.models.Location;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {
    private List<Location> locationList;
    private LocationsActions listener;

    public LocationsAdapter(LocationsActions listener) {
        this.listener = listener;
    }

    public void setData(List<Location> locationList) {
        this.locationList = locationList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(locationList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.locationList != null ? locationList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvName;
        private TextView tvAddress;
        private TextView tvNote;
        private ImageButton btnRemove;
        private Location location;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvNote = itemView.findViewById(R.id.tv_note);
            btnRemove = itemView.findViewById(R.id.btn_remove);
            itemView.getRootView().setOnClickListener(this::onClick);
        }

        public void bind(Location location) {
            this.location = location;
            tvName.setText(location.getName());
            tvAddress.setText(location.getAddress());
            tvNote.setText(location.getNote());
            btnRemove.setOnClickListener(v -> listener.onRemoveClicked(location));
        }

        @Override
        public void onClick(View v) {
            listener.onLocationClicked(location);
        }
    }

    public interface LocationsActions {
        void onLocationClicked(Location location);
        void onRemoveClicked(Location location);
    }
}
