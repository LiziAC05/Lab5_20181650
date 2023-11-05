package com.example.lab_5_iot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lab_5_iot.adapters.ImageAdapter;

import java.util.Arrays;
import java.util.Random;

public class MemoriaActivity extends AppCompatActivity {
    ImageView currImageView = null;
    private int countPair = 0;
    private int helpCount = 0;
    final int[] drawable = new int[]{R.drawable.abra, R.drawable.aerodactyl,
            R.drawable.alakazam, R.drawable.arbok, R.drawable.arcanine, R.drawable.articuno};
    int[] pos = {0, 1, 2, 3, 4, 5, 0, 1, 2, 3, 4, 5};
    int currentPos = -1;
    int firstCardForHelp = -1;
    boolean[] cardsVisible;
    GridView gridView;
    private boolean isProcessing = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memoria);
        cardsVisible = new boolean[pos.length];
        gridView = (GridView) findViewById(R.id.gridMemo);
        ImageAdapter imageAdapter = new ImageAdapter(this, pos);
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener((adapterView, view, position, l) -> {
            if (cardsVisible[position] || isProcessing) {
                return; // La carta ya está visible o se está procesando, no hagas nada.
            }
            if (currentPos < 0){
                currentPos = position;
                currImageView = (ImageView) view;
                ((ImageView)view).setImageResource(drawable[pos[position]]);
            } else {
                ((ImageView) view).setImageResource(drawable[pos[position]]);
                if (pos[currentPos] == pos[position]) {
                    // Las cartas son iguales, marca las dos como visibles.
                    cardsVisible[currentPos] = true;
                    cardsVisible[position] = true;
                    countPair++;
                    if (countPair == pos.length / 2) {
                        Toast.makeText(getApplicationContext(), "Juego Terminado", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Las cartas son diferentes, voltea las dos cartas después de un breve retraso.
                    isProcessing = true;
                    gridView.setEnabled(false); // Desactiva la interacción con el GridView.

                    // Utiliza un Handler para ocultar las cartas después de un breve retraso.
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        ((ImageView) view).setImageResource(R.drawable.question);
                        if (currentPos >= 0 && currentPos < gridView.getChildCount()) {
                            ((ImageView) gridView.getChildAt(currentPos)).setImageResource(R.drawable.question);
                        }
                        isProcessing = false;
                        gridView.setEnabled(true); // Reactiva la interacción con el GridView.
                    }, 1000); // Cambia este valor si deseas un período de tiempo diferente.
                }
                currentPos = -1;

            }
        });

        Button btnRandom = findViewById(R.id.btnRandom);
        btnRandom.setOnClickListener(view -> shuffleImages());
        Button btnAyuda = findViewById(R.id.btnAyuda);
        btnAyuda.setOnClickListener(view -> {
            if (helpCount < 2) {
                // Implementa la lógica para mostrar la pareja de la imagen seleccionada.
                helpCount++;
                if (currentPos >= 0 && firstCardForHelp < 0) {
                    // Mostrar temporalmente la pareja de la imagen de la carta seleccionada con el botón de ayuda.
                    firstCardForHelp = currentPos;
                    ((ImageView) gridView.getChildAt(currentPos)).setImageResource(drawable[pos[currentPos]]);
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        // Ocultar la carta nuevamente después de un breve retraso.
                        if (firstCardForHelp >= 0) {
                            ((ImageView) gridView.getChildAt(firstCardForHelp)).setImageResource(R.drawable.question);
                            firstCardForHelp = -1;
                        }
                    }, 2000); // Cambia este valor si deseas un período de tiempo diferente.
                }
            }

            // Deshabilita el botón de ayuda después de dos usos.
            if (helpCount >= 2) {
                btnAyuda.setEnabled(false);
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        String savedPos = sharedPreferences.getString("pos", null);
        if (savedPos != null) {
            pos = parseSavedPosition(savedPos);
            // Cargar otros datos guardados

            // Configurar el juego con el estado cargado
            setupGame();
        } else {
            // Si no se encontró información guardada, inicia un nuevo juego
            startNewGame();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Guardar el estado del juego en SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Guardar posiciones de cartas, visibilidad de cartas y otros datos
        editor.putString("pos", Arrays.toString(pos));
        editor.putBoolean("helpCount", helpCount > 0);
        // Agregar otros datos aquí
        saveCardsVisibleToSharedPreferences(cardsVisible);
        editor.apply();
    }
    // Implementa el método shuffleImages
    private void shuffleImages() {
        Random random = new Random();
        for (int i = pos.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = pos[i];
            pos[i] = pos[index];
            pos[index] = temp;
        }

        // Actualiza la vista del tablero
        GridView gridView = (GridView) findViewById(R.id.gridMemo);
        ((ImageAdapter) gridView.getAdapter()).notifyDataSetChanged();
    }
    private int[] parseSavedPosition(String savedPosition) {
        String[] positionsArray = savedPosition.replace("[", "").replace("]", "").split(", ");
        int[] positions = new int[positionsArray.length];
        for (int i = 0; i < positionsArray.length; i++) {
            positions[i] = Integer.parseInt(positionsArray[i]);
        }
        return positions;
    }
    private void setupGame() {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        int savedCountPair = sharedPreferences.getInt("countPair", 0);
        countPair = savedCountPair;

        // Restaura cardsVisible si está guardado en SharedPreferences
        boolean[] savedCardsVisible = loadCardsVisibleFromSharedPreferences();
        if (savedCardsVisible != null) {
            cardsVisible = savedCardsVisible;
        }
        // Configura el GridView con las posiciones de las cartas
        ImageAdapter imageAdapter = new ImageAdapter(this, pos);
        gridView.setAdapter(imageAdapter);

        // Configura los listeners y otros aspectos del juego
        setupClickListeners();

        // Actualiza la vista del tablero
        ((ImageAdapter) gridView.getAdapter()).notifyDataSetChanged();
    }
    private boolean[] loadCardsVisibleFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        String cardsVisibleString = sharedPreferences.getString("cardsVisible", null);
        if (cardsVisibleString != null) {
            String[] cardsVisibleArray = cardsVisibleString.split(",");
            boolean[] loadedCardsVisible = new boolean[cardsVisibleArray.length];
            for (int i = 0; i < cardsVisibleArray.length; i++) {
                loadedCardsVisible[i] = Boolean.parseBoolean(cardsVisibleArray[i].trim());
            }
            return loadedCardsVisible;
        }
        return null;
    }

    private void saveCardsVisibleToSharedPreferences(boolean[] cardsVisible) {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String cardsVisibleString = Arrays.toString(cardsVisible);
        editor.putString("cardsVisible", cardsVisibleString);
        editor.apply();
    }

    private void setupClickListeners() {
        gridView.setOnItemClickListener((adapterView, view, position, l) -> {
            if (cardsVisible[position] || isProcessing) {
                return; // La carta ya está visible o se está procesando, no hagas nada.
            }
            if (currentPos < 0){
                currentPos = position;
                currImageView = (ImageView) view;
                ((ImageView) view).setImageResource(drawable[pos[position]]);
            } else {
                ((ImageView) view).setImageResource(drawable[pos[position]]);
                if (pos[currentPos] == pos[position]) {
                    // Las cartas son iguales, marca las dos como visibles.
                    cardsVisible[currentPos] = true;
                    cardsVisible[position] = true;
                    countPair++;
                    if (countPair == pos.length / 2) {
                        Toast.makeText(getApplicationContext(), "Juego Terminado", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Las cartas son diferentes, voltea las dos cartas después de un breve retraso.
                    isProcessing = true;
                    gridView.setEnabled(false); // Desactiva la interacción con el GridView.

                    // Utiliza un Handler para ocultar las cartas después de un breve retraso.
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        ((ImageView) view).setImageResource(R.drawable.question);
                        if (currentPos >= 0 && currentPos < gridView.getChildCount()) {
                            ((ImageView) gridView.getChildAt(currentPos)).setImageResource(R.drawable.question);
                        }
                        isProcessing = false;
                        gridView.setEnabled(true); // Reactiva la interacción con el GridView.
                    }, 1000); // Cambia este valor si deseas un período de tiempo diferente.
                }
                currentPos = -1;
            }
        });
    }
    private void startNewGame() {
        // Reinicia todas las variables y datos del juego a sus valores iniciales.
        // Por ejemplo, podrías restablecer las posiciones de las cartas, reiniciar la visibilidad de las cartas, etc.
        countPair = 0;
        helpCount = 0;
        currentPos = -1;
        firstCardForHelp = -1;

        // Restablece la visibilidad de las cartas
        for (int i = 0; i < cardsVisible.length; i++) {
            cardsVisible[i] = false;
        }

        // Vuelve a barajar las posiciones de las cartas para un nuevo juego.
        shuffleImages();

        // Configura el GridView con las nuevas posiciones de las cartas
        ImageAdapter imageAdapter = new ImageAdapter(this, pos);
        gridView.setAdapter(imageAdapter);

        // Configura los listeners y otros aspectos del juego.
        setupClickListeners();

        // Actualiza la vista del tablero.
        ((ImageAdapter) gridView.getAdapter()).notifyDataSetChanged();
    }
}