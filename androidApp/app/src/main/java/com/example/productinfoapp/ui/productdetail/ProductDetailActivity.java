package com.example.productinfoapp.ui.productdetail;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.productinfoapp.R;
import com.example.productinfoapp.data.api.ApiService;
import com.example.productinfoapp.data.api.RetrofitClient;
import com.example.productinfoapp.data.model.Additive;
import com.example.productinfoapp.data.model.Nutrition;
import com.example.productinfoapp.data.model.Product;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {
    private ProductDetailViewModel viewModel;
    private String productId;

    private ImageView detailImage;
    private TextView detailName, detailBrand, nutriScoreBadge, organicBadge;
    private TextView scoreValue, scoreCategory, scoreExplanation;
    private View scoreCircle;
    private LinearLayout nutritionContainer, additivesContainer;
    private CardView additivesCard, alternativesCard;
    private TextView detailIngredients, servingSize;
    private RecyclerView alternativesRecyclerView;
    private AlternativeProductAdapter alternativesAdapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productId = getIntent().getStringExtra("product_id");

        initViews();
        setupAlternativesRecyclerView();

        apiService = RetrofitClient.getApiService();
        viewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);

        if (productId != null) {
            viewModel.getProduct(productId).observe(this, this::displayProduct);
            loadAlternatives();
        }
    }

    private void initViews() {
        detailImage = findViewById(R.id.detailImage);
        detailName = findViewById(R.id.detailName);
        detailBrand = findViewById(R.id.detailBrand);
        nutriScoreBadge = findViewById(R.id.nutriScoreBadge);
        organicBadge = findViewById(R.id.organicBadge);
        scoreValue = findViewById(R.id.scoreValue);
        scoreCategory = findViewById(R.id.scoreCategory);
        scoreExplanation = findViewById(R.id.scoreExplanation);
        scoreCircle = findViewById(R.id.scoreCircle);
        nutritionContainer = findViewById(R.id.nutritionContainer);
        additivesContainer = findViewById(R.id.additivesContainer);
        additivesCard = findViewById(R.id.additivesCard);
        alternativesCard = findViewById(R.id.alternativesCard);
        detailIngredients = findViewById(R.id.detailIngredients);
        servingSize = findViewById(R.id.servingSize);
        alternativesRecyclerView = findViewById(R.id.alternativesRecyclerView);
    }

    private void displayProduct(Product product) {
        if (product == null) return;

        // Header
        detailName.setText(product.getName());
        detailBrand.setText(product.getBrand());

        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(this).load(product.getImageUrl()).into(detailImage);
        }

        // NutriScore Badge
        String grade = product.getNutriScoreGrade();
        if (grade == null) grade = product.getNutriScore();
        if (grade != null && !grade.isEmpty()) {
            nutriScoreBadge.setText(grade);
            setNutriScoreColor(nutriScoreBadge, grade);
        }

        // Organic Badge
        organicBadge.setVisibility(product.isOrganic() ? View.VISIBLE : View.GONE);

        // Health Score
        int score = product.getCalculatedScore();
        if (score == 0) score = (int) product.getScore();
        displayScore(score, product.getScoreCategory());

        // Ingredients
        String ingredients = product.getIngredients();
        detailIngredients.setText(ingredients != null ? ingredients : "Not available");

        // Nutrition
        displayNutrition(product.getNutrition());

        // Additives
        displayAdditives(product.getAdditives());
    }

    private void setupAlternativesRecyclerView() {
        alternativesAdapter = new AlternativeProductAdapter();
        alternativesRecyclerView.setLayoutManager(
            new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        alternativesRecyclerView.setAdapter(alternativesAdapter);
    }

    private void loadAlternatives() {
        apiService.getAlternatives(productId).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    alternativesCard.setVisibility(View.VISIBLE);
                    alternativesAdapter.setAlternatives(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("ProductDetail", "Failed to load alternatives", t);
            }
        });
    }

    private void displayScore(int score, Product.ScoreCategory category) {
        scoreValue.setText(String.valueOf(score));

        String label;
        int color;
        String explanation;

        if (category != null && category.getLabel() != null) {
            label = category.getLabel();
            color = parseColor(category.getColor());
        } else {
            if (score >= 75) {
                label = "Excellent";
                color = Color.parseColor("#4CAF50");
            } else if (score >= 50) {
                label = "Good";
                color = Color.parseColor("#8BC34A");
            } else if (score >= 25) {
                label = "Poor";
                color = Color.parseColor("#FF9800");
            } else {
                label = "Bad";
                color = Color.parseColor("#F44336");
            }
        }

        switch (label.toLowerCase()) {
            case "excellent":
                explanation = "This product has an excellent nutritional profile";
                break;
            case "good":
                explanation = "This product has a good nutritional profile";
                break;
            case "poor":
                explanation = "This product has some nutritional concerns";
                break;
            default:
                explanation = "This product has significant nutritional concerns";
        }

        scoreCategory.setText(label);
        scoreCategory.setTextColor(color);
        scoreExplanation.setText(explanation);

        // Update circle background color
        GradientDrawable circle = (GradientDrawable) scoreCircle.getBackground();
        circle.setColor(color);
    }

    private int parseColor(String colorStr) {
        try {
            return Color.parseColor(colorStr);
        } catch (Exception e) {
            return Color.parseColor("#4CAF50");
        }
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

    private void displayNutrition(Nutrition n) {
        nutritionContainer.removeAllViews();

        if (n == null) {
            TextView noData = new TextView(this);
            noData.setText("No nutrition data available");
            noData.setTextColor(Color.parseColor("#757575"));
            nutritionContainer.addView(noData);
            return;
        }

        if (n.getServingSize() > 0) {
            servingSize.setText(String.format("Per %.0f%s", n.getServingSize(), 
                n.getServingUnit() != null ? n.getServingUnit() : "g"));
        }

        addNutritionRow("Calories", n.getCalories(), "kcal", 2000, false);
        addNutritionRow("Sugar", n.getSugar(), "g", 50, true);
        addNutritionRow("Fat", n.getFat(), "g", 70, true);
        addNutritionRow("Saturated Fat", n.getSaturatedFat(), "g", 20, true);
        addNutritionRow("Salt", n.getSalt(), "g", 6, true);
        addNutritionRow("Protein", n.getProtein(), "g", 50, false);
        addNutritionRow("Fiber", n.getFiber(), "g", 25, false);
    }

    private void addNutritionRow(String name, double value, String unit, double maxValue, boolean negativeIndicator) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 8, 0, 8);

        TextView nameView = new TextView(this);
        nameView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        nameView.setText(name);
        nameView.setTextColor(Color.parseColor("#616161"));
        nameView.setTextSize(14);

        TextView valueView = new TextView(this);
        valueView.setText(String.format("%.1f %s", value, unit));
        valueView.setTextColor(Color.parseColor("#212121"));
        valueView.setTextSize(14);

        // Color indicator
        double percentage = (value / maxValue) * 100;
        int indicatorColor;
        if (negativeIndicator) {
            if (percentage < 30) indicatorColor = Color.parseColor("#4CAF50");
            else if (percentage < 60) indicatorColor = Color.parseColor("#FF9800");
            else indicatorColor = Color.parseColor("#F44336");
        } else {
            if (percentage > 30) indicatorColor = Color.parseColor("#4CAF50");
            else if (percentage > 10) indicatorColor = Color.parseColor("#FF9800");
            else indicatorColor = Color.parseColor("#F44336");
        }

        View indicator = new View(this);
        indicator.setLayoutParams(new LinearLayout.LayoutParams(8, 8));
        indicator.setBackgroundColor(indicatorColor);
        ((LinearLayout.LayoutParams) indicator.getLayoutParams()).setMargins(0, 6, 8, 0);

        row.addView(indicator);
        row.addView(nameView);
        row.addView(valueView);
        nutritionContainer.addView(row);
    }

    private void displayAdditives(List<Additive> additives) {
        additivesContainer.removeAllViews();

        if (additives == null || additives.isEmpty()) {
            additivesCard.setVisibility(View.GONE);
            return;
        }

        additivesCard.setVisibility(View.VISIBLE);

        for (Additive additive : additives) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 8, 0, 8);

            // Risk indicator
            View indicator = new View(this);
            indicator.setLayoutParams(new LinearLayout.LayoutParams(8, 8));
            indicator.setBackgroundColor(additive.getRiskColor());
            ((LinearLayout.LayoutParams) indicator.getLayoutParams()).setMargins(0, 6, 12, 0);

            TextView codeView = new TextView(this);
            codeView.setText(additive.getCode());
            codeView.setTextColor(Color.parseColor("#212121"));
            codeView.setTextSize(14);
            codeView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            ((LinearLayout.LayoutParams) codeView.getLayoutParams()).setMargins(0, 0, 12, 0);

            TextView nameView = new TextView(this);
            nameView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            nameView.setText(additive.getName());
            nameView.setTextColor(Color.parseColor("#757575"));
            nameView.setTextSize(12);

            TextView riskView = new TextView(this);
            riskView.setText(additive.getRiskLevel());
            riskView.setTextColor(additive.getRiskColor());
            riskView.setTextSize(12);

            row.addView(indicator);
            row.addView(codeView);
            row.addView(nameView);
            row.addView(riskView);
            additivesContainer.addView(row);
        }
    }
}
