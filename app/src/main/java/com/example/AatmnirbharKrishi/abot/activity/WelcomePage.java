package com.example.AatmnirbharKrishi.abot.activity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.AatmnirbharKrishi.abot.LocaleHelper;
import com.example.AatmnirbharKrishi.abot.R;

import java.util.Locale;

public class WelcomePage extends AppCompatActivity {

    private Button Abutton;
    TextView text1;
    boolean lang_selected=true;
    Context context;
    Resources resources;
    public static TextToSpeech mTTS;
    ImageView Translator;
    public String Greet="Welcome to Aatmnirbhar Krishi android application";
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
        Greet=WelcomeMsg;
        if(WelcomeMsg!=null || buttonMsg!=null){
            text1.setText(WelcomeMsg);
            Abutton.setText(buttonMsg);
            lang_selected=SelectLang;
        }

        //Initialization of TTS
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int resultE =  mTTS.setLanguage(Locale.ENGLISH);
                    int resultH = mTTS.setLanguage(new Locale("hin"));
                    if (resultE == TextToSpeech.LANG_MISSING_DATA
                            || resultE == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not Supported");
                    }
                }else {
                    Log.e("TTS", "Intialization Failed");
                }
            }
        });


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
                                    speak(resources.getString(R.string.language));
                                    SaveData(resources.getString(R.string.language),resources.getString(R.string.Button),true);

                            }
                                //if user select preferred language as Hindi then
                                if(Language[which].equals("हिन्दी"))
                                {
                                    context = LocaleHelper.setLocale(WelcomePage.this, "hi");
                                    resources = context.getResources();

                                    text1.setText(resources.getString(R.string.language));
                                    Abutton.setText(resources.getString(R.string.Button));
                                    speak(resources.getString(R.string.language));
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
        intent.putExtra("langSelected",lang_selected);
        startActivity(intent);
        speak("Running");
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        speak(Greet);
    }
    public void SaveData(String s1,String s2,boolean s3){
        SharedPreferences.Editor editor =sharedPreferences.edit();
        editor.putString(Msg1,s1);
        editor.putString(Msg2,s2);
        editor.putBoolean(String.valueOf(Msg3),s3);
        editor.apply();
    }

    //for text to speech
    public static void speak(String answer) {
        String text = answer;
        mTTS.setPitch(1);
        mTTS.setSpeechRate(1);
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
    @Override
    protected void onDestroy() {
        if (mTTS != null ) {
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }

}