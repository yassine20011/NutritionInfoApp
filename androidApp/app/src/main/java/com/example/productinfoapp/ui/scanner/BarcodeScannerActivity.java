package com.example.productinfoapp.ui.scanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.example.productinfoapp.R;
import com.example.productinfoapp.data.api.ApiService;
import com.example.productinfoapp.data.api.RetrofitClient;
import com.example.productinfoapp.data.local.AppDatabase;
import com.example.productinfoapp.data.local.ScanHistoryDao;
import com.example.productinfoapp.data.local.ScanHistoryEntity;
import com.example.productinfoapp.data.model.Product;
import com.example.productinfoapp.ui.productdetail.ProductDetailActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BarcodeScannerActivity extends AppCompatActivity {
    private static final String TAG = "BarcodeScannerActivity";
    private static final int CAMERA_PERMISSION_REQUEST = 100;

    private PreviewView previewView;
    private ProgressBar progressBar;
    private TextView instructionText;
    private ImageButton closeButton;
    
    private ExecutorService cameraExecutor;
    private BarcodeScanner scanner;
    private boolean isProcessing = false;
    private ApiService apiService;
    private ScanHistoryDao historyDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        previewView = findViewById(R.id.previewView);
        progressBar = findViewById(R.id.progressBar);
        instructionText = findViewById(R.id.instructionText);
        closeButton = findViewById(R.id.closeButton);

        closeButton.setOnClickListener(v -> finish());

        apiService = RetrofitClient.getApiService();
        cameraExecutor = Executors.newSingleThreadExecutor();

        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E,
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_QR_CODE)
            .build();
        scanner = BarcodeScanning.getClient(options);

        if (checkCameraPermission()) {
            startCamera();
        } else {
            requestCameraPermission();
        }
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
            == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, 
            new String[]{Manifest.permission.CAMERA}, 
            CAMERA_PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission is required for scanning", 
                    Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = 
            ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases(cameraProvider);
            } catch (Exception e) {
                Log.e(TAG, "Error starting camera", e);
                Toast.makeText(this, "Failed to start camera", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build();

        imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);

        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
    }

    @androidx.camera.core.ExperimentalGetImage
    private void analyzeImage(ImageProxy imageProxy) {
        if (isProcessing) {
            imageProxy.close();
            return;
        }

        if (imageProxy.getImage() == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(
            imageProxy.getImage(), 
            imageProxy.getImageInfo().getRotationDegrees());

        scanner.process(image)
            .addOnSuccessListener(barcodes -> {
                if (!barcodes.isEmpty() && !isProcessing) {
                    String barcodeValue = barcodes.get(0).getRawValue();
                    if (barcodeValue != null) {
                        isProcessing = true;
                        runOnUiThread(() -> onBarcodeDetected(barcodeValue));
                    }
                }
            })
            .addOnFailureListener(e -> Log.e(TAG, "Barcode analysis failed", e))
            .addOnCompleteListener(task -> imageProxy.close());
    }

    private void onBarcodeDetected(String barcode) {
        Log.d(TAG, "Barcode detected: " + barcode);
        
        progressBar.setVisibility(View.VISIBLE);
        instructionText.setText("Looking up product...");

        apiService.getProductByBarcode(barcode).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Product product = response.body();
                    saveToHistory(barcode, product);
                    Intent intent = new Intent(BarcodeScannerActivity.this, ProductDetailActivity.class);
                    intent.putExtra("product_id", product.getId());
                    startActivity(intent);
                    finish();
                } else {
                    instructionText.setText("Product not found. Try again.");
                    isProcessing = false;
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                instructionText.setText("Error looking up product. Try again.");
                Log.e(TAG, "API call failed", t);
                isProcessing = false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        scanner.close();
    }

    private void saveToHistory(String barcode, Product product) {
        if (historyDao == null) {
            historyDao = AppDatabase.getInstance(this).scanHistoryDao();
        }
        Executors.newSingleThreadExecutor().execute(() -> {
            int score = product.getCalculatedScore();
            if (score == 0) score = (int) product.getScore();
            String nutri = product.getNutriScoreGrade();
            if (nutri == null) nutri = product.getNutriScore();
            ScanHistoryEntity entity = ScanHistoryEntity.fromProduct(
                barcode,
                product.getName(),
                product.getBrand(),
                product.getImageUrl(),
                score,
                nutri,
                product.isOrganic(),
                "Local"
            );
            historyDao.insert(entity);
        });
    }
}
