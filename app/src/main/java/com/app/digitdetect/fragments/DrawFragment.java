package com.app.digitdetect.fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.app.digitdetect.ApiService;
import com.app.digitdetect.DrawingView;
import com.app.digitdetect.R;
import com.app.digitdetect.RetrofitClientInstance;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class DrawFragment extends Fragment {
    private ApiService apiService;
    private static final String TAG = "DrawFragment";
    private DrawingView drawingView;
    private TextView predictionTextView;
    private ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.fragment_draw, container, false);
        apiService = RetrofitClientInstance.getApiService();

        drawingView = rootView.findViewById(R.id.drawing_view);
        Button btnSend = rootView.findViewById(R.id.btn_send);
        Button btnClear = rootView.findViewById(R.id.btn_clear);
        predictionTextView = rootView.findViewById(R.id.predictedLabel);
        progressBar = rootView.findViewById(R.id.progressBar);

        btnSend.setOnClickListener(v -> {
            predictionTextView.setText(null);
            progressBar.setVisibility(View.VISIBLE);
            sendDrawingToServer();
        });

        btnClear.setOnClickListener(v -> {
            predictionTextView.setText(null);
            drawingView.clearCanvas();
        });

        return rootView;
    }

    private void sendDrawingToServer() {
        Bitmap bitmap = drawingView.getDrawingBitmap();
        Uri imageUri = saveBitmapToFile(bitmap);

        if (imageUri != null) {
            sendImageToServer(imageUri);
        } else {
            Toast.makeText(getActivity(), "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri saveBitmapToFile(Bitmap bitmap) {
        try {
            // Create a file in the app's cache directory
            File cacheDir = requireContext().getCacheDir();
            long timestamp = System.currentTimeMillis();
            String fileName = "drawing_" + timestamp + ".png";
            File file = new File(cacheDir, fileName); // Save as PNG, or use "drawing.jpg" for JPG format

            // Save the bitmap to the file
            FileOutputStream fos = new FileOutputStream(file);
            boolean success = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); // PNG format, 100% quality
            fos.close();

            if (success) {
                // Return the URI of the saved file
                return Uri.fromFile(file);
            } else {
                Toast.makeText(getActivity(), "Error compressing bitmap", Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving bitmap to file: ", e);
            Toast.makeText(getActivity(), "Error saving image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void sendImageToServer(Uri imageUri) {
        try {
            // Convert the image URI to a File object directly (no need for getRealPathFromURI)
            File file = new File(Objects.requireNonNull(imageUri.getPath()));

            if (!file.exists()) {
                Toast.makeText(getActivity(), "File does not exist", Toast.LENGTH_SHORT).show();
                return;
            }

            // Dynamically determine the MIME type based on the file extension
            String fileExtension = getFileExtension(file);
            String mimeType = getMimeType(fileExtension);

            // Create a RequestBody with the correct MIME type
            RequestBody requestBody = RequestBody.create(MediaType.parse(mimeType), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestBody);

            // Call the server to generate the image based on the digit
            Call<ResponseBody> call = apiService.predictDigit(body);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            assert response.body() != null;
                            String responseBody = response.body().string();
                            try {
                                // Parse the JSON response
                                JSONObject jsonResponse = new JSONObject(responseBody);

                                // Extract the predicted label
                                int predictedLabel = jsonResponse.getInt("predicted_label");

                                // Set the predicted label to the TextView
                                progressBar.setVisibility(View.GONE);
                                predictionTextView.setText(String.valueOf(predictedLabel));
                            } catch (JSONException e) {
                                Log.e(TAG, "Error parsing probabilities JSON response", e);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing server response: ", e);
                            Toast.makeText(getActivity(), "Failed to get prediction", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // If the response is unsuccessful, display an error
                        Toast.makeText(getContext(), "Failed to generate image. Try again!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    // Log error and show failure message
                    Log.e("GenerateFragment", "Error: " + t.getMessage());
                    Toast.makeText(getContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error processing the image: ", e);
            Toast.makeText(getActivity(), "Error processing the image", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper function to get the file extension
    private String getFileExtension(File file) {
        String extension = "";
        int i = file.getName().lastIndexOf('.');
        if (i > 0) {
            extension = file.getName().substring(i + 1);
        }
        return extension.toLowerCase();
    }

    // Helper function to determine MIME type based on the file extension
    private String getMimeType(String extension) {
        switch (extension) {
            case "png":
                return "image/png";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "gif":
                return "image/gif";
            case "bmp":
                return "image/bmp";
            default:
                return "image/*";  // Fallback to general image type
        }
    }
}
