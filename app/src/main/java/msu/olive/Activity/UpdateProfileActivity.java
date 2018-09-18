package msu.olive.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import msu.olive.R;
import msu.olive.Server.RequestHandler;
import msu.olive.Server.Server;

import static android.opengl.GLES20.GL_MAX_TEXTURE_SIZE;

public class UpdateProfileActivity extends AppCompatActivity {


    private int CAMERA_REQUEST_PERMISSION = 100;
    private int MAP_REQUEST = 3;

    private int OPEN_CAMERA_NOUGAT = 70;
    private int OPEN_CAMERA_MARSHMALLOW = 60;
    private int OPEN_LIBRARY = 50;


    private ImageView imgAvatarPreview;
    private Spinner spnGender;
    private Button btnAvatarCamera, btnAvatarLibrary;
    private EditText edBirthday,edInfo;

    private Bitmap bitmap, resizedBitmap;
    private String mCurrentPhotoPath;
    private Uri selectedImage;
    private Button btnUpdateInfo;
    private String uploadImage;

    private String birthday, info, gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        addControls();
        addEvents();

    }

    private void addEvents() {
        btnAvatarCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    openCameraNougat();
                } else {
                    openCamera();
                }
            }
        });


        btnAvatarLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLibrary();
            }
        });

        btnUpdateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo();
            }
        });

        spnGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gender = "Unknown";
            }
        });



    }

    private void updateUserInfo() {
        class UserInfo extends AsyncTask<Bitmap, Void, String>{

            ProgressDialog dialog;
            RequestHandler requestHandler = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = ProgressDialog.show(UpdateProfileActivity.this,
                                                     getApplicationContext().getResources().getString(R.string.updating_info),
                                                    null,
                                                    true,
                                                    true);

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                dialog.dismiss();
                Toast.makeText(UpdateProfileActivity.this, s, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            protected String doInBackground(Bitmap... bitmaps) {

                String result = null;

                bitmap = bitmaps[0];
                uploadImage = getStringImage(bitmap);

                String id_user = String.valueOf(getIntent().getIntExtra("id_user", -1));

                birthday = edBirthday.getText().toString();
                info = edInfo.getText().toString();

                HashMap<String, String> data = new HashMap<>();

                if (birthday == null){
                    birthday = " ";
                }

                if (info == null){
                    info = " ";
                }

                data.put("avatar", uploadImage);
                data.put("birthday", birthday);
                data.put("info", info);
                data.put("gender", gender);

                result = requestHandler.sendPostRequest(Server.Profile_UpdateURL, data);
                return result;


            }

        }

        UserInfo userInfo = new UserInfo();
        userInfo.execute(bitmap);
    }

    private String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void openLibrary() {
        Intent intentPick = new Intent(Intent.ACTION_PICK);
        startActivityForResult(intentPick, OPEN_LIBRARY);
    }


    private void openCamera() {
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intentCamera.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
            }catch (Exception e){
                e.printStackTrace();
            }

            if (photoFile !=null){
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(intentCamera, OPEN_CAMERA_MARSHMALLOW);
            }
        }
    }

    private File createImageFile() throws  IOException{

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;

    }

    private void openCameraNougat() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Uri photoUri = FileProvider.getUriForFile(this, getPackageName() +".provider", photoFile);
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(pictureIntent, OPEN_CAMERA_NOUGAT);
        }
    }

    private void addControls() {

        imgAvatarPreview = findViewById(R.id.imgAvatarPreview);

        spnGender = findViewById(R.id.spnGender);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender,
                android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnGender.setAdapter(genderAdapter);

        edBirthday = findViewById(R.id.edBirthday);
        edInfo = findViewById(R.id.edInfo);
        btnAvatarCamera = findViewById(R.id.btnAvatarCamera);
        btnAvatarLibrary = findViewById(R.id.btnAvatarLibrary);
        btnUpdateInfo = findViewById(R.id.btnUpdateInfo);
    }

    @Override


        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == OPEN_LIBRARY && resultCode == RESULT_OK){
                selectedImage = data.getData();

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    if (bitmap.getHeight() > GL_MAX_TEXTURE_SIZE || bitmap.getWidth() > GL_MAX_TEXTURE_SIZE){

                        int dstWidth = (int) (bitmap.getWidth() * 0.8);
                        int dstHeight = (int) (bitmap.getHeight() * 0.8);

                        resizedBitmap = Bitmap.createScaledBitmap(bitmap, dstWidth,dstHeight,true);
                        bitmap = resizedBitmap;

                    }
                    imgAvatarPreview.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            if (requestCode == OPEN_CAMERA_MARSHMALLOW && resultCode == RESULT_OK ){
                try{
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                    if (bitmap.getHeight() > GL_MAX_TEXTURE_SIZE || bitmap.getWidth() > GL_MAX_TEXTURE_SIZE){

                        int dstWidth = (int) (bitmap.getWidth() * 0.8);
                        int dstHeight = (int) (bitmap.getHeight() * 0.8);

                        resizedBitmap = Bitmap.createScaledBitmap(bitmap, dstWidth,dstHeight,true);
                        bitmap = resizedBitmap;

                    }
                    imgAvatarPreview.setImageBitmap(bitmap);
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                // this return a small thumbnail image

//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            imgPreview.setImageBitmap(imageBitmap);
            }


            if (requestCode == OPEN_CAMERA_NOUGAT && resultCode == RESULT_OK){
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));

                    if (bitmap.getHeight() > GL_MAX_TEXTURE_SIZE || bitmap.getWidth() > GL_MAX_TEXTURE_SIZE){

                        int dstWidth = (int) (bitmap.getWidth() * 0.8);
                        int dstHeight = (int) (bitmap.getHeight() * 0.8);

                        resizedBitmap = Bitmap.createScaledBitmap(bitmap, dstWidth,dstHeight,true);
                        bitmap = resizedBitmap;

                    }
                    imgAvatarPreview.setImageBitmap(bitmap);




                }catch (IOException e){
                    e.printStackTrace();
                    Log.e("error", e.toString());
                }
            }


        }
}
