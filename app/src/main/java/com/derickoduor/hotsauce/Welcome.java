package com.derickoduor.hotsauce;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Welcome extends AppCompatActivity {

    Button signUp,signIn;
    TextView label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        signIn=(Button)findViewById(R.id.signIn);
        signUp=(Button)findViewById(R.id.signUp);

        label=(TextView)findViewById(R.id.label);

        Typeface typeface=Typeface.createFromAsset(getAssets(),"fonts/Caviar_Dreams_Bold.ttf");
        signIn.setTypeface(typeface);
        signUp.setTypeface(typeface);

        Typeface typeface2=Typeface.createFromAsset(getAssets(),"fonts/CaviarDreams_BoldItalic.ttf");
        label.setTypeface(typeface2);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Welcome.this,SignIn.class));
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Welcome.this,SignUp.class));
            }
        });
    }
}
