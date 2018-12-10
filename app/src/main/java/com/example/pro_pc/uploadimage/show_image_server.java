package com.example.pro_pc.uploadimage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class show_image_server extends AppCompatActivity {

    ImageView imageView;
    Button bLoad,bBsck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_server);
        imageView=findViewById(R.id.imageView);
        bLoad=findViewById(R.id.buttonLoad);
        bBsck=findViewById(R.id.button2);
        bLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(imageView);
            }
        });
    }
}
