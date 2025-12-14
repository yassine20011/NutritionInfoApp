package com.example.productinfoapp.ui.productdetail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.productinfoapp.R;
import com.example.productinfoapp.data.model.Product;
import java.util.ArrayList;
import java.util.List;

public class AlternativeProductAdapter extends RecyclerView.Adapter<AlternativeProductAdapter.ViewHolder> {
    private List<Product> alternatives = new ArrayList<>();
    private Context context;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_alternative, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = alternatives.get(position);
        
        holder.name.setText(product.getName());
        
        // Score
        int score = product.getCalculatedScore();
        if (score == 0) score = (int) product.getScore();
        holder.score.setText(String.valueOf(score));
        
        int scoreColor = getScoreColor(score);
        holder.score.setTextColor(scoreColor);
        GradientDrawable indicator = (GradientDrawable) holder.scoreIndicator.getBackground();
        indicator.setColor(scoreColor);
        
        // NutriScore badge
        String grade = product.getNutriScoreGrade();
        if (grade == null) grade = product.getNutriScore();
        if (grade != null && !grade.isEmpty()) {
            holder.nutriScore.setText(grade);
            setNutriScoreColor(holder.nutriScore, grade);
        }
        
        // Image
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(context).load(product.getImageUrl()).into(holder.image);
        }
        
        // Click to view product
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return alternatives.size();
    }

    public void setAlternatives(List<Product> alternatives) {
        this.alternatives = alternatives != null ? alternatives : new ArrayList<>();
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
        TextView name, score, nutriScore;
        View scoreIndicator;

        ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.alternativeImage);
            name = view.findViewById(R.id.alternativeName);
            score = view.findViewById(R.id.alternativeScore);
            nutriScore = view.findViewById(R.id.alternativeNutriScore);
            scoreIndicator = view.findViewById(R.id.alternativeScoreIndicator);
        }
    }
}
