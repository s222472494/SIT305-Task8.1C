package com.example.a81cchatbot;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private Button goButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.usernameInput);
        goButton = findViewById(R.id.goButton);

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameInput.getText().toString().trim();

                if (!username.isEmpty()) {
                    Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
                    intent.putExtra("USERNAME", username);
                    startActivity(intent);
                } else {
                    usernameInput.setError("Please enter a username");
                }
            }
        });
    }
}