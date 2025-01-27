package com.app.digitdetect;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart
    @POST("/predict")
    Call<ResponseBody> predictDigit(@Part MultipartBody.Part image);

    @POST("/generate-handwritten-digit")
    Call<ResponseBody> generateHandwrittenDigit(@Body HashMap<String, String> body);
}
