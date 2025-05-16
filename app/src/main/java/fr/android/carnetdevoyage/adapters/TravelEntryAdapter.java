package fr.android.carnetdevoyage.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fr.android.carnetdevoyage.R;
import fr.android.carnetdevoyage.database.TravelEntry;

import java.io.File;
import java.util.List;

public class TravelEntryAdapter extends RecyclerView.Adapter<TravelEntryAdapter.TravelEntryViewHolder> {

    private Context context;
    private List<TravelEntry> entryList;

    public TravelEntryAdapter(Context context, List<TravelEntry> entryList) {
        this.context = context;
        this.entryList = entryList;
    }

    @NonNull
    @Override
    public TravelEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_travel_entry, parent, false);
        return new TravelEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelEntryViewHolder holder, int position) {
        TravelEntry entry = entryList.get(position);

        holder.textTitle.setText(entry.getTitle());
        holder.textLocation.setText(String.format("Lat: %.4f, Long: %.4f", entry.getLatitude(), entry.getLongitude()));
        holder.textDate.setText(entry.getDate());

        // Charger l'image si disponible
        if (entry.getImagePath() != null && !entry.getImagePath().isEmpty()) {
            File imgFile = new File(entry.getImagePath());
            if (imgFile.exists()) {
                holder.imageThumbnail.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return entryList.size();
    }

    public class TravelEntryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageThumbnail;
        TextView textTitle, textLocation, textDate;

        public TravelEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageThumbnail = itemView.findViewById(R.id.image_thumbnail);
            textTitle = itemView.findViewById(R.id.text_title);
            textLocation = itemView.findViewById(R.id.text_location);
            textDate = itemView.findViewById(R.id.text_date);

            // Ajouter un écouteur de clic si nécessaire
            itemView.setOnClickListener(v -> {
                // Code pour gérer le clic sur un élément
            });
        }
    }
}