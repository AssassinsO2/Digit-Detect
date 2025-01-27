package com.app.digitdetect.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.digitdetect.ApiService;
import com.app.digitdetect.adapters.ChatAdapter;
import com.app.digitdetect.CustomRecyclerView;
import com.app.digitdetect.R;
import com.app.digitdetect.RetrofitClientInstance;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class GenerateFragment extends Fragment {
    private static final String TAG = "GenerateFragment";
    private ApiService apiService;
    private EditText etDigit;
    private List<ChatAdapter.ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;

    public GenerateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_generate, container, false);
        apiService = RetrofitClientInstance.getApiService();

        CustomRecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        etDigit = rootView.findViewById(R.id.etDigit);
        ImageButton btnSend = rootView.findViewById(R.id.btnSend);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(getActivity(), chatMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(chatAdapter);

        btnSend.setOnClickListener(v -> {
            String digitText = etDigit.getText().toString();
            if (!digitText.isEmpty()) {
                sendDigitToServer(digitText);
            }
        });

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect rect = new Rect();
            rootView.getWindowVisibleDisplayFrame(rect);
            int screenHeight = rootView.getRootView().getHeight();
            int keypadHeight = screenHeight - rect.bottom;

            LinearLayout inputLayout = rootView.findViewById(R.id.inputLayout);

            // Convert 200dp to pixels
            float density = getResources().getDisplayMetrics().density;
            int paddingBottomInPx = (int) (340 * density);

            // When the keyboard opens, move the input layout and scroll RecyclerView
            if (keypadHeight > screenHeight * 0.15) {
                // Translate input layout by the height of the keyboard
                inputLayout.setTranslationY(-keypadHeight);

                // Add padding to the bottom of the RecyclerView to prevent overlap with the keyboard
                recyclerView.setPadding(recyclerView.getPaddingLeft(), recyclerView.getPaddingTop(), recyclerView.getPaddingRight(), paddingBottomInPx);

                // Ensure RecyclerView is scrolled to the last item
                recyclerView.post(() -> {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        // Scroll to the last position with an offset to account for the keyboard
                        int lastPosition = chatMessages.size() - 1;  // Decrease by 1 as the list is 0-indexed
                        int offset = recyclerView.getHeight() - etDigit.getHeight() - keypadHeight - 1000;
                        Log.d(TAG, "Keyboard Height: "+ keypadHeight);
                        Log.d(TAG, "Offset: "+ offset);
                        layoutManager.scrollToPositionWithOffset(lastPosition, offset);
                    }
                });
            } else {
                // Reset the translation if the keyboard is not visible
                inputLayout.setTranslationY(0);

                // Reset RecyclerView's padding to its original state
                recyclerView.setPadding(recyclerView.getPaddingLeft(), recyclerView.getPaddingTop(), recyclerView.getPaddingRight(), 0);
            }
        });
        recyclerView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick(); // Call performClick for accessibility
            }

            // Prevent parent (e.g., ViewPager2) from intercepting touch events
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false; // Let RecyclerView handle the event
        });
        return rootView;
    }


    private void sendDigitToServer(String digit) {
        // Add user's message to the chat
        chatMessages.add(new ChatAdapter.ChatMessage(ChatAdapter.TYPE_USER, digit, null));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);

        // Create the request body to send to the server
        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("digit", digit);

        // Call the server to generate the image based on the digit
        Call<ResponseBody> call = apiService.generateHandwrittenDigit(requestBody);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Convert the server response body to an InputStream (image)
                    InputStream inputStream = response.body().byteStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    // Add server's response (image) to the chat
                    chatMessages.add(new ChatAdapter.ChatMessage(ChatAdapter.TYPE_SERVER, null, bitmap));
                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);
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
    }
}
