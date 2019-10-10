package com.example.superlikedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.superlikedemo.superLike.SuperLikeManager;

public class MainActivity extends AppCompatActivity {

    private SuperLikeManager superLikeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tvSuperLike = findViewById(R.id.tvSuperLike);
        superLikeManager = new SuperLikeManager(this);
        tvSuperLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                superLikeManager.showSuperLike(v);
            }
        });
    }
}
