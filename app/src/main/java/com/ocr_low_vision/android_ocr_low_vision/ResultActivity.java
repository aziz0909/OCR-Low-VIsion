package com.ocr_low_vision.android_ocr_low_vision;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.Locale;

import static com.ocr_low_vision.android_ocr_low_vision.Camera2.mFile;

public class ResultActivity extends AppCompatActivity {
    ImageView imageView;
    TextView textView;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);

        TextToSpeech.OnInitListener listener =
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(final int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            Log.d("TTS", "Text to speech engine started successfully.");
                            tts.setLanguage(Locale.ENGLISH);
                        } else {
                            Log.d("TTS", "Error starting the text to speech engine.");
                        }
                    }
                };
        tts = new TextToSpeech(this.getApplicationContext(), listener);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts.speak(textView.getText(), TextToSpeech.QUEUE_ADD, null, "DEFAULT");
            }
        });

        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(ResultActivity.this, Camera2.class);
                startActivity(intent);
                return true;
            }
        });

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize=4;

        Bitmap textBitmap = BitmapFactory.decodeFile(mFile.getAbsolutePath(), options);
        imageView.setImageBitmap(textBitmap);


        if (mFile != null) {
            TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();

            if (!textRecognizer.isOperational()) {
            }

            Frame imageFrame = new Frame.Builder()
                    .setBitmap(textBitmap)
                    .build();

            final SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);

            if (textBlocks.size() != 0) {
                textView.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        StringBuilder stringBuilder = new StringBuilder();
                        //StringJoiner joiner = new StringJoiner(" ");
                        for (int i = 0; i < textBlocks.size(); i++) {
                            TextBlock item = textBlocks.valueAt(i);
                            //joiner.add(item.getValue());
                            stringBuilder.append(item.getValue()+" \n ");
                        }
                        textView.setText(stringBuilder.toString()+" \n ");
                    }
                });
            }
        }
    }

    @Override
    public  void onPause(){
        super.onPause();
        tts.stop();
    }

    @Override
    public void onStop(){
        super.onStop();
        tts.stop();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    public void onDestroy(){
        super.onDestroy();
        tts.stop();
    }
}
