package com.example.AatmnirbharKrishi.abot.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.AatmnirbharKrishi.abot.R;
import com.example.AatmnirbharKrishi.abot.adapter.MessageAdapter;
import com.example.AatmnirbharKrishi.abot.model.ResponseMessage;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static android.view.inputmethod.EditorInfo.IME_ACTION_SEND;

public class MainActivity extends AppCompatActivity {
    //crops initialization
    EditText InputFromKeyboard;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    List<ResponseMessage> responseMessageList;
    static String question,answer;
    public static TextToSpeech mTTS;
    Button Show_Crops;

    //Weather initialization
    String City="London";
    String temp,sTime;
    Bitmap bitmap;

    String Key ="7ba2d7afdf2bac7edc540126727c7ada";
    TextView txtCity,txtTime,txtTemp;
    ImageView imageView;
    Button WeatherBtn;
    RelativeLayout rlMain;

    TextView textView;
    FusedLocationProviderClient fusedLocationProviderClient;
    String locate;



    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //crops initialization
        InputFromKeyboard = findViewById(R.id.userInput);
        recyclerView = findViewById(R.id.conversation);
        responseMessageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(responseMessageList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(messageAdapter);
        Show_Crops= findViewById(R.id.ShowCrops);

        //weather initialization
        txtCity =findViewById(R.id.txtCity);
        WeatherBtn=findViewById(R.id.WeatherBtn);
        txtTime = findViewById(R.id.txtTime);
        txtTemp = findViewById(R.id.txtValue);
        imageView = findViewById(R.id.imgIcon);
        rlMain = findViewById(R.id.rlMain);
        textView=findViewById(R.id.text_viewLocation);

        //Initialize fused location provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        WeatherBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //GetInformation();
                    rlMain.setVisibility(View.VISIBLE);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            rlMain.setVisibility(View.GONE);
                        }
                    }, 1000);

                    GetInformation();
                    return true;
                }
                return false;
            }
        });
 //----------------------------------

        //For python initialization
        if(!Python.isStarted())
            Python.start(new AndroidPlatform(this));

        Python py = Python.getInstance();
        final PyObject pyobj = py.getModule("LocalDataset");    //give name of python file

        //Show Crops module
        Show_Crops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //function name with the arguments which are in python file
                PyObject obj=pyobj.callAttr("main");
                request("Show crops of Navsari");
                //save op of python file in text view
                answer="Showing crops of navsari:\n";
                answer+=obj.toString();
                reply(answer);

            }
        });

        //Initialization of TTS
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
                    question=InputFromKeyboard.getText().toString();
                    request(question);
                    reply(answer);
                    InputFromKeyboard.getText().clear();
                    //calling auto scrolling function

                }
                return false;
            }
        });


    }
    //questions, analyse and answers
    public void  request(String question){
        ResponseMessage responseMessage = new ResponseMessage(question, true);
        responseMessageList.add(responseMessage);
        messageAdapter.notifyDataSetChanged();
        if(question.equalsIgnoreCase("Hello") || question.equalsIgnoreCase("Hey")){
            answer="Hello! I'm your personal assistant. How may i help you?";
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
    }


    public void reply(final String answer){

        ResponseMessage responseMessage2 = new ResponseMessage(answer, false);
        responseMessageList.add(responseMessage2);
        messageAdapter.notifyDataSetChanged();
        if (!isLastVisible())
            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
        speak(answer);
    }

    //--------------------------------
    //For auto scrolling function
    boolean isLastVisible() {
        LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
        int pos = layoutManager.findLastCompletelyVisibleItemPosition();
        int numItems = recyclerView.getAdapter().getItemCount();
        return (pos >= numItems);
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
    
//for input from mic
public void micClick(View view) {

    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening");
    try {
        startActivityForResult(intent, 1);
    } catch (ActivityNotFoundException e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 &&resultCode == RESULT_OK && null != data) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            question=result.get(0).toString();
            request(question);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    reply(answer);
                }
            }, 1000);

        }
    }

    //For weather coding

    public class DownloadJSON extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            HttpURLConnection httpURLConnection;

            InputStream inputStream;

            InputStreamReader inputStreamReader;

            String result = "";

            try {

                URL url = new URL(strings[0]);

                httpURLConnection = (HttpURLConnection) url.openConnection();

                inputStream = httpURLConnection.getInputStream();

                inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();

                while (data != -1) {

                    result += (char) data;

                    data = inputStreamReader.read();
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }
    }

    public class DownloadIcon extends AsyncTask<String, Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(String... strings) {

            Bitmap bitmap = null;

            URL url;

            HttpURLConnection httpURLConnection;

            InputStream inputStream;


            try {

                url = new URL(strings[0]);

                httpURLConnection = (HttpURLConnection) url.openConnection();

                inputStream = httpURLConnection.getInputStream();

                bitmap = BitmapFactory.decodeStream(inputStream);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return bitmap;
        }
    }



    public void GetInformation(){

        City = locate;

        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + City +"&units=metric&appid="+ Key;

        DownloadJSON downloadJSON = new DownloadJSON();

        try {

            String result = downloadJSON.execute(url).get();

            JSONObject jsonObject = new JSONObject(result);

            temp = jsonObject.getJSONObject("main").getString("temp");


            Long time = jsonObject.getLong("dt");

            sTime = new SimpleDateFormat("dd-MM-yyyy \nhh:mm", Locale.ENGLISH)
                    .format(new Date());

            txtTime.setText(sTime);
            txtCity.setText(City);
            txtTemp.setText(temp + "°");

            String nameIcon = "10d";

            nameIcon = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");


            String urlIcon = "http://openweathermap.org/img/wn/"+ nameIcon +"@2x.png";

            DownloadIcon downloadIcon = new DownloadIcon();

            bitmap = downloadIcon.execute(urlIcon).get();

            //imageView.setImageBitmap(bitmap);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }





    //for location entire code--------------------------

    @Override
    protected void onStart()
    {
        // TODO Auto-generated method stub

        //Check permission
        if (ActivityCompat.checkSelfPermission(MainActivity.this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //When permission granted
            getLocation();
        } else {
            //when permisssion denied
            ActivityCompat.requestPermissions(MainActivity.this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        //GetInformation();

        super.onStart();
    }

    @Override
    protected void onResume() {
        GetInformation();
        super.onResume();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void  onRequestPermissionsResult(int requestCode, String[] permissions,
            //            //                                         int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //Initialize location
                Location location = task.getResult();
                if (location != null) {
                    try {
                        //Initialize geocoder
                        Geocoder geocoder = new Geocoder(MainActivity.this,
                                Locale.getDefault());
                        //Initialize address list
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );
                        locate=addresses.get(0).getLocality();
                        textView.setText("city name:"+locate);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    //--------------------------
}

