package com.chubbygirl.main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private int i=0;
    private int myId = 8;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EnterRoomTextView tvSomeoneIn = findViewById(R.id.tv_name_in);
        Button btn = findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i++;
                boolean isMe = i==myId;
                tvSomeoneIn.sendSomeoneInMsg(isMe, i+"我来了");
            }
        });
    }
}