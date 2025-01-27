package com.app.digitdetect.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.digitdetect.ApiService;
import com.app.digitdetect.R;
import com.app.digitdetect.RetrofitClientInstance;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class PredictFragment extends Fragment {
    private static final String TAG = "PredictFragment";
    private ImageView selectedImageView;
    private TextView predictionTextView;
    private Uri imageUri;
    private ApiService apiService;
    private ActivityResultLauncher<Intent> filePickerLauncher;
    private ProgressBar progressBar;

    public PredictFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_predict, container, false);
        apiService = RetrofitClientInstance.getApiService();

        selectedImageView = rootView.findViewById(R.id.selectedImageView);
        Button chooseImageButton = rootView.findViewById(R.id.btn_select);
        Button sendImageButton = rootView.findViewById(R.id.btn_send);
        predictionTextView = rootView.findViewById(R.id.predictedLabel);
        progressBar = rootView.findViewById(R.id.progressBar);

        // Open gallery to pick an image
        chooseImageButton.setOnClickListener(v -> openGallery());

        // Send image to the server
        sendImageButton.setOnClickListener(v -> {
            if (imageUri != null) {
                predictionTextView.setText(null);
                progressBar.setVisibility(View.VISIBLE);
                sendImageToServer(imageUri);
            } else {
                Log.e(TAG, "Please select an image first");
                Toast.makeText(getContext(), "Please select an image first", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize file picker launcher
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        imageUri = data.getData();
                        if (imageUri != null) {
                            int cornerRadius = 20; // Adjust the corner radius as needed (in pixels)
                            Glide.with(this)
                                    .load(imageUri)
                                    .transform(new com.bumptech.glide.load.resource.bitmap.RoundedCorners(cornerRadius))
                                    .into(selectedImageView); // Load selected image into ImageView with rounded corners
                        }

                    }
                }
        );

        return rootView;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        filePickerLauncher.launch(intent);  // Launch the image picker
    }

    private void sendImageToServer(Uri imageUri) {
        try {
            // Convert the image URI to a File object
            File file = new File(Objects.requireNonNull(getRealPathFromURI(imageUri)));

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
            Log.e(TAG,"Error processing the image: ", Objects.requireNonNull(e));
            Toast.makeText(getContext(), "Error processing the image", Toast.LENGTH_SHORT).show();
        }
    }

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
            default:
                return "image/*";  // Fallback to general image type
        }
    }

    // Helper function to get the real file path from the URI
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {android.provider.MediaStore.Images.Media.DATA};
        try (android.database.Cursor cursor = requireContext().getContentResolver().query(contentUri, proj, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(proj[0]);
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            Log.e(TAG,"getRealPathFromURI", Objects.requireNonNull(e));
        }
        return null;
    }
}