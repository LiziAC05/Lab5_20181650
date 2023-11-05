package com.example.lab_5_iot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lab_5_iot.adapters.ImageAdapter;

public class MemoriaActivity extends AppCompatActivity {
    ImageView currImageView = null;
    private int countPair = 0;
    final int[] drawable = new int[]{R.drawable.abra, R.drawable.aerodactyl,
            R.drawable.alakazam, R.drawable.arbok, R.drawable.arcanine, R.drawable.articuno};
    int[] pos = {0, 1, 2, 3, 4, 5, 0, 1, 2, 3, 4, 5};
    int currentPos = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memoria);
        GridView gridView = (GridView) findViewById(R.id.gridMemo);
        ImageAdapter imageAdapter = new ImageAdapter(this);
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (currentPos < 0){
                    currentPos = position;
                    currImageView = (ImageView) view;
                    ((ImageView)view).setImageResource(drawable[pos[position]]);
                } else {
                    if(currentPos == position){
                        ((ImageView)view).setImageResource(R.drawable.question);
                    } else if (pos[currentPos] == pos[position]) {
                        currImageView.setImageResource(R.drawable.question);
                        Toast.makeText(getApplicationContext(), "Incorrecto", Toast.LENGTH_SHORT).show();
                    } else {
                        ((ImageView)view).setImageResource(drawable[pos[position]]);
                        countPair++;
                        if(countPair==0){
                            Toast.makeText(getApplicationContext(), "Juego Terminado", Toast.LENGTH_SHORT).show();
                        }
                    }
                    currentPos = -1;

                }
            }
        });
    }
}