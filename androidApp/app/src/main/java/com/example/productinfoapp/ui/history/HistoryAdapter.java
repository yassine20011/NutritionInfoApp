package com.example.productinfoapp.ui.history;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.productinfoapp.R;
import com.example.productinfoapp.data.local.ScanHistoryEntity;
import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<ScanHistoryEntity> items = new ArrayList<>();
    private Context context;
    private OnHistoryItemClickListener clickListener;
    private OnFavoriteToggleListener favoriteListener;

    public interface OnHistoryItemClickListener {
        void onItemClick(ScanHistoryEntity item);
    }

    public interface OnFavoriteToggleListener {
        void onFavoriteToggle(ScanHistoryEntity item, boolean isFavorite);
    }

    public void setOnItemClickListener(OnHistoryItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnFavoriteToggleListener(OnFavoriteToggleListener listener) {
        this.favoriteListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScanHistoryEntity item = items.get(position);

        holder.productName.setText(item.productName);
        holder.brandName.setText(item.brandName);

        // Score
        holder.score.setText(String.valueOf(item.healthScore));
        int scoreColor = getScoreColor(item.healthScore);
        holder.score.setTextColor(scoreColor);
        GradientDrawable indicator = (GradientDrawable) holder.scoreIndicator.getBackground();
        indicator.setColor(scoreColor);

        // NutriScore
        if (item.nutriScoreGrade != null) {
            holder.nutriScore.setText(item.nutriScoreGrade);
            setNutriScoreColor(holder.nutriScore, item.nutriScoreGrade);
        }

        // Timestamp
        CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(
                item.scannedAt, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
        holder.timestamp.setText(relativeTime);

        // Image
        if (item.imageUrl != null && !item.imageUrl.isEmpty()) {
            Glide.with(context).load(item.imageUrl).into(holder.image);
        }

        // Favorite
        holder.favoriteButton.setImageResource(
                item.isFavorite ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        
        holder.favoriteButton.setOnClickListener(v -> {
            if (favoriteListener != null) {
                favoriteListener.onFavoriteToggle(item, !item.isFavorite);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<ScanHistoryEntity> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    private int getScoreColor(int score) {
        if (score >= 75) return Color.parseColor("#4CAF50");
        if (score >= 50) return Color.parseColor("#8BC34A");
        if (score >= 25) return Color.parseColor("#FF9800");
        return Color.parseColor("#F44336");
    }

    private void setNutriScoreColor(TextView badge, String grade) {
        int color;
        switch (grade.toUpperCase()) {
            case "A": color = Color.parseColor("#038141"); break;
            case "B": color = Color.parseColor("#85BB2F"); break;
            case "C": color = Color.parseColor("#FECB02"); break;
            case "D": color = Color.parseColor("#EE8100"); break;
            case "E": color = Color.parseColor("#E63E11"); break;
            default: color = Color.parseColor("#9E9E9E");
        }
        GradientDrawable bg = (GradientDrawable) badge.getBackground();
        bg.setColor(color);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView productName, brandName, score, nutriScore, timestamp;
        View scoreIndicator;
        ImageButton favoriteButton;

        ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.historyImage);
            productName = view.findViewById(R.id.historyProductName);
            brandName = view.findViewById(R.id.historyBrandName);
            score = view.findViewById(R.id.historyScore);
            nutriScore = view.findViewById(R.id.historyNutriScore);
            timestamp = view.findViewById(R.id.historyTimestamp);
            scoreIndicator = view.findViewById(R.id.historyScoreIndicator);
            favoriteButton = view.findViewById(R.id.favoriteButton);
        }
    }
}
