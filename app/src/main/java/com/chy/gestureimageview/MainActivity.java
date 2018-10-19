package com.chy.gestureimageview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.chy.imageview.GestureImageView;

public class MainActivity extends AppCompatActivity {
    private GestureImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.img);
        imageView.setMiniAlpha(0.2f);
        imageView.setChangeAlpha(false);
        imageView.setChangeScale(true);
    }
}
