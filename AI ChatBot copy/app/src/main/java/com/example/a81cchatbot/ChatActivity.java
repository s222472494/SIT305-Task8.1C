package com.example.a81cchatbot;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Gravity;


import androidx.appcompat.app.AppCompatActivity;

import com.example.a81cchatbot.AI.OpenAIClient;
import com.example.a81cchatbot.AI.OpenAIService;
import com.example.a81cchatbot.AI.ChatRequest;
import com.example.a81cchatbot.AI.ChatResponse;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private LinearLayout messageContainer;
    private EditText messageInput;
    private ImageButton sendButton;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageContainer = findViewById(R.id.messageContainer);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        // Get username from intent
        username = getIntent().getStringExtra("USERNAME");

        // Show welcome message
        addMessage("Welcome " + username + "!", false);

        // Send button click
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = messageInput.getText().toString().trim();
                if (!msg.isEmpty()) {
                    addMessage(msg, true);
                    messageInput.setText("");

                    // Send message to OpenAI API and get response
                    getBotResponse(msg);
                }
            }
        });
    }


    private void addMessage(String text, boolean isUser) {
        // Create horizontal container for icon and message bubble
        LinearLayout messageLayout = new LinearLayout(this);
        messageLayout.setOrientation(LinearLayout.HORIZONTAL);
        messageLayout.setPadding(8, 8, 8, 8);

        // Align based on sender
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 8, 8, 8);
        layoutParams.gravity = isUser ? Gravity.END : Gravity.START;
        messageLayout.setLayoutParams(layoutParams);

        // Create the initials icon
        TextView iconView = new TextView(this);
        iconView.setText(isUser ? getInitials(username) : "AI");
        iconView.setTextColor(0xFFFFFFFF); // white text
        iconView.setBackgroundResource(R.drawable.circle_icon_bg);
        iconView.setGravity(Gravity.CENTER);
        iconView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        iconView.setTextSize(14);
        iconView.setPadding(16, 16, 16, 16);

        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        iconParams.setMargins(0, 0, 16, 0);
        iconView.setLayoutParams(iconParams);

        // Create the message TextView
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(24, 16, 24, 16);
        textView.setTextSize(16);
        textView.setMaxWidth(800);
        textView.setTextColor(0xFF000000); // black text
        textView.setBackgroundResource(isUser ? R.drawable.user_message : R.drawable.bot_message);

        // Add views in correct order
        if (isUser) {
            messageLayout.addView(textView);
            messageLayout.addView(iconView);
        } else {
            messageLayout.addView(iconView);
            messageLayout.addView(textView);
        }

        // Add to container
        messageContainer.addView(messageLayout);
    }


    // Helper method to get initials
    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "U";
        String[] parts = name.trim().split(" ");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
    }



    private void getBotResponse(String userMessage) {
        OpenAIService service = OpenAIClient.getRetrofitInstance().create(OpenAIService.class);

        // Prepare the request
        List<ChatRequest.Message> messages = new ArrayList<>();
        messages.add(new ChatRequest.Message("user", userMessage));

        ChatRequest request = new ChatRequest("gpt-3.5-turbo", messages);

        // Call the API
        service.getChatResponse(request).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String botMessage = response.body().getChoices().get(0).getMessage().getContent();
                    addMessage(botMessage, false); // Display the bot's reply
                } else {
                    addMessage("Error: " + response.message(), false);
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                addMessage("Failed to connect: " + t.getMessage(), false);
            }
        });
    }
}
