package com.example.deathcode.abot.activity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deathcode.abot.LocaleHelper;
import com.example.deathcode.abot.R;

public class WelcomePage extends AppCompatActivity {

    private Button Abutton;
    TextView language_dialog,text1;
    boolean lang_selected=true;
    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        language_dialog = (TextView)findViewById(R.id.dialog_language);
        text1=(TextView)findViewById(R.id.text1);
        Abutton=(Button) findViewById(R.id.AssistantButton);

        Abutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPage2();
            }
        });

        language_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] Language = {"ENGLISH", "हिन्दी"};
                final int checkedItem;

                if(lang_selected)
                {
                    checkedItem=0;
                }else
                {
                    checkedItem=1;
                }

                final AlertDialog.Builder builder = new AlertDialog.Builder(WelcomePage.this);
                builder.setTitle("Select a Language...")
                        .setSingleChoiceItems(Language, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(WelcomePage.this,"Language changed",Toast.LENGTH_SHORT).show();
                                language_dialog.setText(Language[which]);
                                lang_selected= Language[which].equals("ENGLISH");

                                //if user select prefered language as English then
                                if(Language[which].equals("ENGLISH"))
                                {
                                    context = LocaleHelper.setLocale(WelcomePage.this, "en");
                                    resources = context.getResources();

                                    text1.setText(resources.getString(R.string.language));
                                    Abutton.setText(resources.getString(R.string.Button));
                                }
                                //if user select prefered language as Hindi then
                                if(Language[which].equals("हिन्दी"))
                                {
                                    context = LocaleHelper.setLocale(WelcomePage.this, "hi");
                                    resources = context.getResources();

                                    text1.setText(resources.getString(R.string.language));
                                    Abutton.setText(resources.getString(R.string.Button));
                                }
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            }

        });
    }
    public  void openPage2(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}