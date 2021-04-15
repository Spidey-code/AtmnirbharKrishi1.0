package com.example.AatmnirbharKrishi.abot.activity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.AatmnirbharKrishi.abot.LocaleHelper;
import com.example.AatmnirbharKrishi.abot.R;

public class WelcomePage extends AppCompatActivity {

    private Button Abutton;
    TextView text1;
    boolean lang_selected=true;
    Context context;
    Resources resources;
    ImageView Translator;
    //Shared preference initialization
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String Msg1 = "Welcomemsg";
    private static final String Msg2 = "Button";
    private static final boolean Msg3=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        text1= findViewById(R.id.text1);
        Abutton= findViewById(R.id.AssistantButton);
        Translator=findViewById(R.id.translation);

        //shared preference declaration
        sharedPreferences=getSharedPreferences( SHARED_PREF_NAME,MODE_PRIVATE);
        String WelcomeMsg = sharedPreferences.getString(Msg1,null);
        String buttonMsg = sharedPreferences.getString(Msg2,null);
        boolean SelectLang=sharedPreferences.getBoolean(String.valueOf(Msg3),true);

        if(WelcomeMsg!=null || buttonMsg!=null){
            text1.setText(WelcomeMsg);
            Abutton.setText(buttonMsg);
            lang_selected=SelectLang;
        }

        Abutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPage2();
            }
        });

        Translator.setOnClickListener(new View.OnClickListener() {
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
                                lang_selected= Language[which].equals("ENGLISH");

                                //if user select preferred language as English then
                                if(Language[which].equals("ENGLISH"))
                                {
                                    context = LocaleHelper.setLocale(WelcomePage.this, "en");
                                    resources = context.getResources();

                                    text1.setText(resources.getString(R.string.language));
                                    Abutton.setText(resources.getString(R.string.Button));

                                    SaveData(resources.getString(R.string.language),resources.getString(R.string.Button),true);

                            }
                                //if user select preferred language as Hindi then
                                if(Language[which].equals("हिन्दी"))
                                {
                                    context = LocaleHelper.setLocale(WelcomePage.this, "hi");
                                    resources = context.getResources();

                                    text1.setText(resources.getString(R.string.language));
                                    Abutton.setText(resources.getString(R.string.Button));

                                    SaveData(resources.getString(R.string.language),resources.getString(R.string.Button),false);
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

    public void SaveData(String s1,String s2,boolean s3){
        SharedPreferences.Editor editor =sharedPreferences.edit();
        editor.putString(Msg1,s1);
        editor.putString(Msg2,s2);
        editor.putBoolean(String.valueOf(Msg3),s3);
        editor.apply();
    }
}