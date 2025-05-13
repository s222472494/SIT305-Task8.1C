package com.example.a81cchatbot.AI;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OpenAIService {
    @Headers({
            "Content-Type: application/json",
            "Authorization: // Input your own API Key here
    })
    @POST("v1/chat/completions")
    Call<ChatResponse> getChatResponse(@Body ChatRequest request);
}
