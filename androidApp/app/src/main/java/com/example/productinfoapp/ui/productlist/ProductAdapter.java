package com.example.productinfoapp.ui.productlist;

import android.content.Context;
import android.content.Intent;
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
import com.example.productinfoapp.ui.productdetail.ProductDetailActivity;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products = new ArrayList<>();
    private Context context;

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.textName.setText(product.getName());
        holder.textBrand.setText(product.getBrand());
        holder.textScore.setText("Score: " + product.getScore());
        
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(context)
                .load(product.getImageUrl())
                //.placeholder(R.mipmap.ic_launcher) // Optional, using default
                .into(holder.imageView);
        } else {
             holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textName;
        TextView textBrand;
        TextView textScore;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textName = itemView.findViewById(R.id.textName);
            textBrand = itemView.findViewById(R.id.textBrand);
            textScore = itemView.findViewById(R.id.textScore);
        }
    }
}
