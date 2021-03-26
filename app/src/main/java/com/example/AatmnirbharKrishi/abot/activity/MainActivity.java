package com.example.AatmnirbharKrishi.abot.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.AatmnirbharKrishi.abot.R;
import com.example.AatmnirbharKrishi.abot.adapter.MessageAdapter;
import com.example.AatmnirbharKrishi.abot.model.ResponseMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.view.inputmethod.EditorInfo.IME_ACTION_SEND;

public class MainActivity extends AppCompatActivity {

    EditText InputFromKeyboard;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    List<ResponseMessage> responseMessageList;
    EditText InputFromMic;
    String question,answer;
    private TextToSpeech mTTS;
    Button Show_Crops;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InputFromKeyboard = findViewById(R.id.userInput);
        recyclerView = findViewById(R.id.conversation);
        responseMessageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(responseMessageList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(messageAdapter);
        InputFromMic = (EditText) findViewById(R.id.userInput);
        Show_Crops=(Button)findViewById(R.id.ShowCrops);

        //For python intialization
        if(!Python.isStarted())
            Python.start(new AndroidPlatform(this));

        Python py = Python.getInstance();
        final PyObject pyobj = py.getModule("LocalDataset");                            //give name of python file
        Show_Crops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //function name with the arguments which are in python file
                PyObject obj=pyobj.callAttr("main");

                //save op of python file in text view
                answer=obj.toString();
            }
        });

        //Intialization of TTS
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result =  mTTS.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not Supported");
                    }
                }else {
                    Log.e("TTS", "Intialization Failed");
                }
            }
        });

    //For input from keyboard and bot reply
        InputFromKeyboard.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == IME_ACTION_SEND) {
                    ResponseMessage responseMessage = new ResponseMessage(InputFromKeyboard.getText().toString(), true);
                    responseMessageList.add(responseMessage);
                    question=InputFromKeyboard.getText().toString();
                    if(question.equalsIgnoreCase("Show crops of Navsari") || question.equalsIgnoreCase("Hey")){
                        //answer="Hello! I'm your personal assistant. How may i help you?";
                    }
                    else if(question.equalsIgnoreCase("What can you do?")){
                        answer="I can help you in solving questions related to farming. I can also give you information about crops and your soil.";
                    }
                    else if(question.equalsIgnoreCase("Good Morning")){
                        answer="A very good morning to you!";
                    }
                    else{
                        answer="Sorry! I can't help you with that!";
                    }
                    ResponseMessage responseMessage2 = new ResponseMessage(answer, false);
                    responseMessageList.add(responseMessage2);
                    messageAdapter.notifyDataSetChanged();
                    speak();
                    InputFromKeyboard.getText().clear();
                    //calling auto scrolling function
                    if (!isLastVisible())
                        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                }
                return false;
            }
        });


    }
    //For auto scrolling function
    boolean isLastVisible() {
        LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
        int pos = layoutManager.findLastCompletelyVisibleItemPosition();
        int numItems = recyclerView.getAdapter().getItemCount();
        return (pos >= numItems);
    }

    //for input from mic
    public void micClick(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now!");
        try {
            startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    //for text to speech
    private void speak() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    InputFromMic.setText(result.get(0));
                }
                break;
        }
    }
}

