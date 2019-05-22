package com.example.heroesapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import api.HeroesAPI;
import model.Heroes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddHeroActivity extends AppCompatActivity {

    private final static String BASE_URL = "http://10.0.2.2:3000/";
    private EditText etName, etDesc;
    private Button btnRegister, btnShow;
    private ImageView imgProfile;
    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hero);

        etName = findViewById(R.id.etName);
        etDesc = findViewById(R.id.etDesc);
        btnRegister = findViewById(R.id.btnRegister);
        btnShow = findViewById(R.id.btnShow);
        imgProfile = findViewById(R.id.imgPhoto);

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrowseImage();
            }
        });
//        loadFormURl();

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddHeroActivity.this, ShowHeroesActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });
    }

    private void BrowseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, "please select an image ", Toast.LENGTH_SHORT).show();
            }
        }
        Uri uri = data.getData();
        imagePath = getRealPathFromUri(uri);
        previewImage(imagePath);
    }


    private String getRealPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), uri, projection, null, null, null);
        Cursor cursor= loader.loadInBackground();
        int colIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(colIndex);
        cursor.close();
        return result;
    }



    private void previewImage(String imagePath) {
        File imgFile = new File(imagePath);
        if(imgFile.exists());
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        imgProfile.setImageBitmap(myBitmap);
    }

    //    private void StrictMode()
//    {
//        android.os.StrictMode.ThreadPolicy policy =
//                new android.os.StrictMode.ThreadPolicy.Builder().permitAll().build();
//        android.os.StrictMode.setThreadPolicy(policy);
//    }
//
//    private void loadFormURl() {
//        StrictMode();
//        try{
//            String imgURL = "https://vignette.wikia.nocookie.net/marvelcinematicuniverse/images/f/fb/Marvel-avengers-infinity-war-iron-man-sixth-scale-figure-hot-toys-silo-903421.png/revision/latest?cb=20180318221316";
//            URL url = new URL(imgURL);
//            imgProfile.setImageBitmap(BitmapFactory.decodeStream((InputStream)url.getContent()));
//
//        } catch (IOException e) {
//
//          Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
//        }
//    }



    private void Register(){
        String name = etName.getText().toString();
        String desc = etDesc.getText().toString();

        Map<String,String> map = new HashMap<>();
        map.put("name",name);
        map.put("desc",desc);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        HeroesAPI heroesAPI = retrofit.create(HeroesAPI.class);


        Call<Void> voidCall = heroesAPI.addHero(map);

        voidCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if(!response.isSuccessful())
                {
                    Toast.makeText(AddHeroActivity.this, "" + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(AddHeroActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AddHeroActivity.this, "Error: "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
