package com.example.lab_5_iot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.ImageView;

import java.util.Random;

public class PuzzleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        GridLayout gridLayout = findViewById(R.id.gridLayout);

        Intent intent = getIntent();
        Uri selectedImageUri = intent.getData();
        Random random = new Random();
        int tamanio = random.nextInt() + 3;
        if (selectedImageUri != null) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new GridLayout.LayoutParams());
            imageView.setImageURI(selectedImageUri);

            gridLayout.addView(imageView);
            gridLayout.setColumnCount(tamanio);
            gridLayout.setRowCount(tamanio);
        }
    }
}