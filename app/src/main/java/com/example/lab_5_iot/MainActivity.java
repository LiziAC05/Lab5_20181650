package com.example.lab_5_iot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static  final int REQUEST_PICK_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnIrPuzzle = findViewById(R.id.btnIrPuzzle);
        Button btnIrMemo = findViewById(R.id.btnIrMemoria);
        btnIrPuzzle.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            startActivityForResult(intent, REQUEST_PICK_CODE);
        });
        btnIrMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(MainActivity.this, MemoriaActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_CODE && resultCode == RESULT_OK) {
            // Obtiene la URI de la imagen seleccionada
            Uri selectedImageUri = data.getData();

            // Envía la URI a la segunda actividad
            Intent intent = new Intent(this, PuzzleActivity.class);
            intent.setData(selectedImageUri);
            startActivity(intent);
        }
    }
}