package com.kaygb.fly_game.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kaygb.fly_game.R;
public class GameOverActivity extends AppCompatActivity {
    Button mBtnRestart;
    Button mBtnRetHome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        mBtnRestart = findViewById(R.id.btn_restart);
        mBtnRetHome = findViewById(R.id.btn_returnhome);
        mBtnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_restart) {
                    startGame();
                }
            }
        });
        mBtnRetHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.btn_returnhome){
                    returnIndex();
                }
            }
        });
    }
    public void startGame() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        finish();
    }
    public void returnIndex() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}