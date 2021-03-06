package com.nuance.speechkitsample;

import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nuance.speechkit.Audio;
import com.nuance.speechkit.AudioPlayer;
import com.nuance.speechkit.DetectionType;
import com.nuance.speechkit.Interpretation;
import com.nuance.speechkit.Language;
import com.nuance.speechkit.Recognition;
import com.nuance.speechkit.Session;
import com.nuance.speechkit.Transaction;
import com.nuance.speechkit.TransactionException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This Activity is built to demonstrate how to perform NLU (Natural Language Understanding).
 * <p>
 * This Activity is very similar to ASRActivity. Much of the code is duplicated for clarity.
 * <p>
 * NLU is the transformation of text into meaning.
 * <p>
 * When performing speech recognition with SpeechKit, you have a variety of options. Here we demonstrate
 * Context Tag and Language.
 * <p>
 * The Context Tag is assigned in the system configuration upon deployment of an NLU model.
 * Combined with the App ID, it will be used to find the correct NLU version to query.
 * <p>
 * Languages can also be configured. Supported languages can be found here:
 * http://developer.nuance.com/public/index.php?task=supportedLanguages
 * <p>
 * Copyright (c) 2015 Nuance Communications. All rights reserved.
 */
public class NLUActivity extends DetailActivity implements View.OnClickListener {

    private Audio startEarcon;
    private Audio stopEarcon;
    private Audio errorEarcon;

    //private EditText nluContextTag;
    private String nluContextTag;
    //private EditText language;
    private String language;

    private ImageButton toggleReco;
    private boolean toggleRecoEnabled;

    private ProgressBar volumeBar;

    private Session speechSession;
    private Transaction recoTransaction;
    private State state = State.IDLE;

    private TextView userDisplay;
    private TextView userInputTxt;

    private Button sendBtn;
    private Button cancelBtn;

    private TextView textStaticCount;
    private TextView textCountDown;

    private String stdoutput;
    private String restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nlu);

        userDisplay = (TextView) findViewById(R.id.text_box_nlu);
        userDisplay.setText("Waiting for input");
        userInputTxt = (TextView) findViewById(R.id.text_usrinput);

        textStaticCount = (TextView) findViewById(R.id.staticCount);
        textStaticCount.setVisibility(View.INVISIBLE);
        textCountDown = (TextView) findViewById(R.id.textCountDown);
        textCountDown.setVisibility(View.INVISIBLE);

        sendBtn = (Button) findViewById(R.id.btn_send);
        cancelBtn = (Button) findViewById(R.id.btn_cancel);

        sendBtn.setVisibility(View.INVISIBLE);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData();
            }
        });
        cancelBtn.setVisibility(View.INVISIBLE);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                userDisplay.setText("Waiting for input");
                sendBtn.setVisibility(View.INVISIBLE);
                cancelBtn.setVisibility(View.INVISIBLE);
                toggleReco.setBackgroundColor(Color.GRAY);
                toggleRecoEnabled = true;
                textStaticCount.setVisibility(View.INVISIBLE);
                textCountDown.setVisibility(View.INVISIBLE);
                userInputTxt.setText("");
            }
        });

        toggleRecoEnabled = true;

        //nluContextTag = "M1718_A913_V1";
        nluContextTag = "newapp";
        language = "eng-USA";

        toggleReco = (ImageButton) findViewById(R.id.toggle_reco);
        toggleReco.setOnClickListener(this);

        volumeBar = (ProgressBar) findViewById(R.id.volume_bar);

        //Create a session
        speechSession = Session.Factory.session(this, Configuration.SERVER_URI, Configuration.APP_KEY);

        loadEarcons();

        setState(State.IDLE);
    }

    @Override
    public void onClick(View v) {
        if (v == toggleReco && toggleRecoEnabled) {
            toggleReco();
        }
    }

    //Timer for 1 second
    private Runnable mMyRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            int timeLeft = Integer.parseInt(textCountDown.getText().toString());
            if (timeLeft > 1){
                timeLeft -= 1;
                textCountDown.setText(String.valueOf(timeLeft));
                Handler myHandler = new Handler();
                myHandler.postDelayed(mMyRunnable, 1000);
            }
            else {
                sendData();
            }
        }
    };

    /* Reco transactions */

    private void toggleReco() {
        switch (state) {
            case IDLE:
                recognize();
                break;
            case LISTENING:
                stopRecording();
                break;
            case PROCESSING:
                cancel();
                break;
        }
    }

    /**
     * Start listening to the user and streaming their voice to the server.
     */
    private void recognize() {
        //Setup our Reco transaction options.
        Transaction.Options options = new Transaction.Options();
        //options.setDetection(resourceIDToDetectionType(detectionType.getCheckedRadioButtonId()));  TODO: Remove
        options.setDetection(DetectionType.Long);
        //options.setLanguage(new Language(language.getText().toString()));
        options.setLanguage(new Language(language));
        options.setEarcons(startEarcon, stopEarcon, errorEarcon, null);

        //Add properties to appServerData for use with custom service. Leave empty for use with NLU.
        JSONObject appServerData = new JSONObject();
        //Start listening
        //recoTransaction = speechSession.recognizeWithService(nluContextTag.getText().toString(), appServerData, options, recoListener);
        recoTransaction = speechSession.recognizeWithService(nluContextTag, appServerData, options, recoListener);
    }

    private void sendData(){
        userDisplay.setText("Waiting for input");
        sendBtn.setVisibility(View.INVISIBLE);
        cancelBtn.setVisibility(View.INVISIBLE);
        toggleReco.setBackgroundColor(Color.GRAY);
        toggleRecoEnabled = true;
        textStaticCount.setVisibility(View.INVISIBLE);
        textCountDown.setVisibility(View.INVISIBLE);
        userInputTxt.setText("");

        if (restaurant.equals("Moxy's")){

        }
        else if (restaurant.equals("The Keg")){

        }
        else if (restaurant.equals("Montana's")){

        }

        //Send data if we could
        //send stdoutput
    }

    private Transaction.Listener recoListener = new Transaction.Listener() {
        @Override
        public void onStartedRecording(Transaction transaction) {
            //logs.append("\nonStartedRecording");

            Log.d("smh", "Started Recording");

            //We have started recording the users voice.
            //We should update our state and start polling their volume.
            setState(State.LISTENING);
            startAudioLevelPoll();
        }

        @Override
        public void onFinishedRecording(Transaction transaction) {
            //logs.append("\nonFinishedRecording");

            Log.d("smh", "Finished Recording");

            //We have finished recording the users voice.
            //We should update our state and stop polling their volume.
            setState(State.PROCESSING);
            stopAudioLevelPoll();
        }

        @Override
        public void onServiceResponse(Transaction transaction, org.json.JSONObject response) {
            try {
                // 2 spaces for tabulations.
                //logs.append("\nonServiceResponse: " + response.toString(2));
                Log.d("smh", "On Service Response: " + response.toString(2));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            // We have received a service response. In this case it is our NLU result.
            // Note: this will only happen if you are doing NLU (or using a service)
            setState(State.IDLE);
        }

        @Override
        public void onRecognition(Transaction transaction, Recognition recognition) {
            //logs.append("\nonRecognition: " + recognition.getText());

            Log.d("smh", "We recognized it: " + recognition.getText());

            userInputTxt.setText("\"" + recognition.getText() + "\"");

            //We have received a transcription of the users voice from the server.
            setState(State.IDLE);
        }

        @Override
        public void onInterpretation(Transaction transaction, Interpretation interpretation) {
            try {
                //logs.append("\nonInterpretation: " + interpretation.getResult().toString(2));

                Log.d("smh", "We interpreted it: " + interpretation.getResult().toString(2));


                JSONObject obj = interpretation.getResult();

                JSONObject best_interpretation = obj.getJSONArray("interpretations").getJSONObject(0);
                JSONObject concepts = best_interpretation.getJSONObject("concepts");
                String attendees = "";
                restaurant = "";
                String timerange = "";

                if (concepts.has("Attendees") && concepts.has("Restaurant") && concepts.has("DURATION_RANGE")) {
                    attendees = concepts.getJSONArray("Attendees").getJSONObject(0).getString("literal");
                    restaurant = concepts.getJSONArray("Restaurant").getJSONObject(0).getString("literal");
                    timerange = concepts.getJSONArray("DURATION_RANGE").getJSONObject(0).getString("literal");

                    stdoutput = "I will like to book a table for " + attendees + "  between " + timerange;

                    toggleRecoEnabled = false;
                    toggleReco.setBackgroundColor(Color.rgb(24, 35, 57));
                    sendBtn.setVisibility(View.VISIBLE);
                    cancelBtn.setVisibility(View.VISIBLE);
                    textStaticCount.setVisibility(View.VISIBLE);
                    textCountDown.setVisibility(View.VISIBLE);
                    textCountDown.setText("5");
                    Handler myHandler = new Handler();
                    myHandler.postDelayed(mMyRunnable, 1000);
                }

                else {
                    userDisplay.setText("Not enough information. Please give the name of the restaunt, the number of people you're booking the table for and the time range.");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // We have received a service response. In this case it is our NLU result.
            // Note: this will only happen if you are doing NLU (or using a service)
            setState(State.IDLE);
        }

        @Override
        public void onSuccess(Transaction transaction, String s) {
            //logs.append("\nonSuccess");

            Log.d("smh", "Non Success");
            //Notification of a successful transaction. Nothing to do here.
        }

        @Override
        public void onError(Transaction transaction, String s, TransactionException e) {
            //logs.append("\nonError: " + e.getMessage() + ". " + s);

            Log.d("smh", "On Error: " + e.getMessage() + ". " + s);

            userDisplay.setText("There was an error in communicating with our servers. Please check your connectivity.");

            //Something went wrong. Check Configuration.java to ensure that your settings are correct.
            //The user could also be offline, so be sure to handle this case appropriately.
            //We will simply reset to the idle state.
            setState(State.IDLE);
        }
    };

    /**
     * Stop recording the user
     */
    private void stopRecording() {
        recoTransaction.stopRecording();
    }

    /**
     * Cancel the Reco transaction.
     * This will only cancel if we have not received a response from the server yet.
     */
    private void cancel() {
        recoTransaction.cancel();
    }

    /* Audio Level Polling */

    private Handler handler = new Handler();

    /**
     * Every 50 milliseconds we should update the volume meter in our UI.
     */
    private Runnable audioPoller = new Runnable() {
        @Override
        public void run() {
            float level = recoTransaction.getAudioLevel();
            volumeBar.setProgress((int) level);
            handler.postDelayed(audioPoller, 50);
        }
    };

    /**
     * Start polling the users audio level.
     */
    private void startAudioLevelPoll() {
        audioPoller.run();
    }

    /**
     * Stop polling the users audio level.
     */
    private void stopAudioLevelPoll() {
        handler.removeCallbacks(audioPoller);
        volumeBar.setProgress(0);
    }


    /* State Logic: IDLE -> LISTENING -> PROCESSING -> repeat */

    private enum State {
        IDLE,
        LISTENING,
        PROCESSING
    }

    /**
     * Set the state and update the button text.
     */
    private void setState(State newState) {
        state = newState;
        switch (newState) {
            case IDLE:
                break;
            case LISTENING:
                userDisplay.setText("Listening...");
                userInputTxt.setText("");
                toggleReco.setBackgroundColor(Color.rgb(51, 181, 229));
                toggleRecoEnabled = false;
                break;
            case PROCESSING:
                userDisplay.setText("Processing...");
                toggleReco.setBackgroundColor(Color.rgb(24, 35, 57));
                toggleRecoEnabled = false;
                break;
        }
    }

    /* Earcons */

    private void loadEarcons() {
        //Load all of the earcons from disk
        startEarcon = new Audio(this, R.raw.sk_start, Configuration.PCM_FORMAT);
        stopEarcon = new Audio(this, R.raw.sk_stop, Configuration.PCM_FORMAT);
        errorEarcon = new Audio(this, R.raw.sk_error, Configuration.PCM_FORMAT);
    }

    /* Helpers */

/*    private DetectionType resourceIDToDetectionType(int id) {
*//*        if(id == R.id.long_endpoint) {
            return DetectionType.Long;
        }
        if(id == R.id.short_endpoint) {
            return DetectionType.Short;
        }
        if(id == R.id.none) {
            return DetectionType.None;
        }
        return null;*//*
        return DetectionType.Long;
    }*/
}
