package com.example.lab_5_iot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PuzzleActivity extends AppCompatActivity {
    private static final float MIN_SLIDE_DISTANCE = 50;
    private List<ImageView> piezas;
    private int numRows, numCols;
    private int emptyIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        // Obtener la URI de la imagen de la primera actividad
        String imagenUriString = getIntent().getStringExtra("imagenUri");
        Uri imagenUri = Uri.parse(imagenUriString);
        try {
            Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagenUri);
            int width = originalBitmap.getWidth() / 3;
            int height = originalBitmap.getHeight() / 3;

            GridLayout gridLayout = findViewById(R.id.gridLayout);

            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    if (row == 2 && col == 2) {
                        // Casilla vacía
                        ImageView imageView = findViewById(R.id.casillaVacia);
                        imageView.setImageBitmap(null);
                        imageView.setVisibility(View.INVISIBLE);
                    } else {
                        // Crea las piezas a partir de la imagen original
                        int x = col * width;
                        int y = row * height;
                        Bitmap piece = Bitmap.createBitmap(originalBitmap, x, y, width, height);

                        // Crea un nuevo ImageView para la pieza
                        ImageView imageView = new ImageView(this);
                        imageView.setLayoutParams(new GridLayout.LayoutParams());
                        imageView.setImageBitmap(piece);

                        // Agrega el ImageView al GridLayout
                        gridLayout.addView(imageView);

                        // Obtener la URI de la imagen de la primera actividad y cargar las piezas


                    }
                }
            }
            piezas = new ArrayList<>();
            // Agregar las piezas a la lista
            for (int i = 0; i < gridLayout.getChildCount(); i++) {
                int resourceId = getResources().getIdentifier("pieza_" + i, "id", getPackageName());
                ImageView piece = findViewById(resourceId);
                piezas.add((ImageView) gridLayout.getChildAt(i));

            }
            emptyIndex = piezas.size() - 1;

        } catch (IOException e) {
            e.printStackTrace();
        }
        for (ImageView piece : piezas) {
            piece.setOnTouchListener(new View.OnTouchListener() {
                private float startX, startY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            // Guarda la posición de inicio del toque
                            startX = event.getX();
                            startY = event.getY();
                            return true;
                        case MotionEvent.ACTION_UP:
                            // Calcula la dirección del deslizamiento y realiza el movimiento si es válido
                            float endX = event.getX();
                            float endY = event.getY();
                            handlePieceMovement(v, startX, startY, endX, endY);
                            return true;
                    }
                    return false;
                }
            });
        }
        // Recuperar el estado del juego desde SharedPreferences
        SharedPreferences preferences = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        emptyIndex = preferences.getInt("emptyIndex", 0);
    }
    public void comenzarJuego(View view) {
        // Definir las secuencias de movimientos predeterminadas
        int[][] secuencias = {
                {0, 1, 2, 3, 5, 4, 7, 6, 8},
                {4, 3, 0, 1, 2, 5, 8, 7, 6},
                // Agrega más secuencias según sea necesario
        };

        // Realizar las secuencias de movimientos
        for (int[] secuencia : secuencias) {
            realizarSecuencia(secuencia);
        }
    }

    private void realizarSecuencia(int[] secuencia) {
        for (int i = 0; i < secuencia.length - 1; i++) {
            int from = secuencia[i];
            int to = secuencia[i + 1];

            // Intercambiar las piezas en las posiciones "from" y "to"
            Bitmap tmpBitmap = ((BitmapDrawable) piezas.get(from).getDrawable()).getBitmap();
            piezas.get(from).setImageBitmap(((BitmapDrawable) piezas.get(to).getDrawable()).getBitmap());
            piezas.get(to).setImageBitmap(tmpBitmap);
        }
    }
    private void handlePieceMovement(View piece, float startX, float startY, float endX, float endY) {
        int currentIndex = piezas.indexOf(piece);
        int numCols = 3;
        if (numCols != 0) {
        // Determina la fila y columna actual de la pieza
        int currentRow = currentIndex / numCols;
        int currentCol = currentIndex % numCols;

        // Calcula la diferencia en coordenadas X e Y
        float deltaX = endX - startX;
        float deltaY = endY - startY;

        // Calcula la distancia absoluta en píxeles del movimiento
        float absDeltaX = Math.abs(deltaX);
        float absDeltaY = Math.abs(deltaY);

        // Verifica si el movimiento es lo suficientemente largo para ser considerado un deslizamiento
        if (absDeltaX < MIN_SLIDE_DISTANCE && absDeltaY < MIN_SLIDE_DISTANCE) {
            return;
        }

        // Determina la dirección del movimiento (horizontal o vertical)
        if (absDeltaX > absDeltaY) {
            // Movimiento horizontal
            if (deltaX > 0 && currentCol > 0) {
                // Mueve la pieza hacia la izquierda
                movePiece(currentIndex, currentIndex - 1);
            } else if (deltaX < 0 && currentCol < numCols - 1) {
                // Mueve la pieza hacia la derecha
                movePiece(currentIndex, currentIndex + 1);
            }
        } else {
            // Movimiento vertical
            if (deltaY > 0 && currentRow > 0) {
                // Mueve la pieza hacia arriba
                movePiece(currentIndex, currentIndex - numCols);
            } else if (deltaY < 0 && currentRow < numRows - 1) {
                // Mueve la pieza hacia abajo
                movePiece(currentIndex, currentIndex + numCols);
            }
        }
    }else {
            // Manejo del error: Mostrar un mensaje de error al usuario
            Toast.makeText(this, "Error: División por cero", Toast.LENGTH_SHORT).show();
        }
    }
    private void movePiece(int fromIndex, int toIndex) {
        // Obtiene la ImageView de la pieza que se mueve y la casilla vacía
        ImageView fromPiece = piezas.get(fromIndex);
        ImageView toPiece = piezas.get(toIndex);

        // Intercambia las imágenes entre las piezas
        Drawable tempDrawable = fromPiece.getDrawable();
        fromPiece.setImageDrawable(toPiece.getDrawable());
        toPiece.setImageDrawable(tempDrawable);

        // Actualiza el índice de la casilla vacía
        emptyIndex = fromIndex;
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences preferences = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Guarda el estado del juego, incluyendo la posición de las piezas y otros datos relevantes
        editor.putInt("emptyIndex", emptyIndex);
        // Guarda otras variables de estado según sea necesario

        editor.apply();
    }
}