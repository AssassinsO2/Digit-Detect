package com.app.digitdetect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.app.digitdetect.R;

public class AppInfoFragment extends Fragment {

    public AppInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_app_info, container, false);

        // Set the detailed app description in the TextView
        TextView appInfoTextView = view.findViewById(R.id.appInfoTextView);
        appInfoTextView.setText(getAppDescription());

        return view;
    }

    private String getAppDescription() {
        return "This application, 'Digit Detect,' allows users to draw or upload images of handwritten digits and get accurate predictions powered by a sophisticated server-side machine learning model." +
                "\n\n**About the Model:**\n" +
                "- The backend server is equipped with **5 Convolutional Neural Network (CNN) models** trained on the MNIST dataset.\n" +
                "- Each model has been trained for **30 epochs**, achieving an accuracy of approximately **95%** on the test dataset." +
                "\n\n**How it Works:**\n" +
                "1. **Data Transmission:**\n" +
                "   - The application captures user-drawn digit data or an uploaded image.\n" +
                "   - Alternatively, users can input a text digit (0–9) to request a corresponding handwritten digit.\n" +
                "\n\n2. **Server-Side Processing:**\n" +
                "   - For digit recognition:\n" +
                "       - The server receives the digit data or uploaded image, preprocesses the image (e.g., resizing to 28x28 pixels, converting to grayscale), and feeds it into the **ensemble of CNN models**.\n" +
                "       - Each of the 5 models predicts the digit independently.\n" +
                "       - The predictions are combined using a **majority voting ensemble technique** to ensure robust and accurate results.\n" +
                "    - For handwritten digit generation:\n" +
                "       - The server uses the trained  model to create handwritten digits based on the user-provided text digit (0–9).\n" +
                "       - The same dataset (MNIST) ensures the generated digits are stylistically consistent with the recognition models.\n" +
                "       - The generated handwritten digit is encoded as an image and sent back to the app." +
                "\n\n3. **Prediction and Response:**\n" +
                "   - For recognition:\n" +
                "       - The server generates the predicted digit and sends the result back to the app.\n" +
                "   - For generation:\n" +
                "       - The server generates a visually handwritten digit and sends the image back to the app." +
                "\n\n**Features:**\n" +
                "- Real-time digit prediction with high accuracy.\n" +
                "- Smooth user interface for drawing, uploading digit images, or entering text digits.\n" +
                "- Handwritten digit generation for any text input digit (0–9).\n" +
                "- Robust server-side processing ensures fast and reliable results." +
                "\n\nThis app leverages the power of deep learning to deliver a seamless and highly accurate handwritten digit recognition and generation experience.";
    }
}
