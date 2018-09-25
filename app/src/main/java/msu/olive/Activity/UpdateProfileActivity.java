package msu.olive.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
    private EditText edAge, edInfo;

    private Bitmap bmpAvatar;
    private Bitmap resizedBitmap;
    private String mCurrentPhotoPath;
    private Uri selectedImage;
    private Button btnUpdateInfo;
    private String uploadImage;
    String avatar_url;

    private String username, age, info, gender;
    private int id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);


        addControls();
        addEvents();
        getAvatar();

    }

    private void getAvatar() {
        class Avatar extends AsyncTask<String,String,Bitmap>{


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                imgAvatarPreview.setImageBitmap(bitmap);
                bmpAvatar = bitmap;

            }

            @Override
            protected Bitmap doInBackground(String... strings) {
                Bitmap downloadedAvatar = null;
                try {
                    URL url = new URL(strings[0]);
                    downloadedAvatar = BitmapFactory.decodeStream((InputStream) url.getContent());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return downloadedAvatar;
            }
        }

        new Avatar().execute(avatar_url);
    }

    private void addEvents() {

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

        imgAvatarPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseAvatar();
            }
        });


    }

    private void chooseAvatar() {
        AlertDialog.Builder selectMethod = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.select_method))
                .setItems(R.array.select_method, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            openLibrary();
                        }
                        if (which == 1) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                openCameraNougat();
                            } else {
                                openCamera();
                            }

                        }
                    }
                });

        selectMethod.show();
    }


    private void updateUserInfo() {
        class UserInfo extends AsyncTask<Bitmap, Void, String> {

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
                Log.i("result", s);
                finish();
            }

            @Override
            protected String doInBackground(Bitmap... bitmaps) {

                String result = null;

                bmpAvatar = bitmaps[0];
                uploadImage = getStringImage(bmpAvatar);

                String id_user = String.valueOf(getIntent().getIntExtra("id_user", -1));


                age = edAge.getText().toString();
                info = edInfo.getText().toString();

                HashMap<String, String> data = new HashMap<>();

                if (age == null) {
                    age = " ";
                }

                if (info == null) {
                    info = " ";
                }

                data.put("id_user", id_user);
                data.put("username", username);
                data.put("avatar", uploadImage);
                data.put("age", age);
                data.put("info", info);
                data.put("gender", gender);

                result = requestHandler.sendPostRequest(Server.Profile_UpdateURL, data);
                return result;


            }

        }

        UserInfo userInfo = new UserInfo();
        userInfo.execute(bmpAvatar);
    }

    private String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void openLibrary() {
        Intent intentPick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intentPick, OPEN_LIBRARY);
    }


    private void openCamera() {
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intentCamera.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(intentCamera, OPEN_CAMERA_MARSHMALLOW);
            }
        }
    }

    private File createImageFile() throws IOException {

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
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Uri photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(pictureIntent, OPEN_CAMERA_NOUGAT);
        }
    }

    private void addControls() {

        id_user = getIntent().getIntExtra("id_user", -1);
        username = getIntent().getStringExtra("username");
        avatar_url = "http://128.199.127.190/roadsafety/oldman/avatar_folder/" + username + "_avatar.png";
        Log.i("userProfileActivity" , username);
        Log.i("avatar url", avatar_url);


        //bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_avatar);
        imgAvatarPreview = findViewById(R.id.imgAvatarPreview);


        //Glide.with(this).asBitmap().load(avatar_url).into(imgAvatarPreview);

        //imgAvatarPreview.setImageBitmap(bitmap);


        String gender[] = {getString(R.string.male), getString(R.string.female)};

        spnGender = findViewById(R.id.spnGender);
//        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender,
//                android.R.layout.simple_spinner_item);

        ArrayAdapter genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gender);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnGender.setAdapter(genderAdapter);

        edAge = findViewById(R.id.edAge);
        edInfo = findViewById(R.id.edInfo);
        btnUpdateInfo = findViewById(R.id.btnUpdateInfo);
    }

    @Override


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OPEN_LIBRARY && resultCode == RESULT_OK) {
            selectedImage = data.getData();

            try {
                bmpAvatar = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                if (bmpAvatar.getHeight() > GL_MAX_TEXTURE_SIZE || bmpAvatar.getWidth() > GL_MAX_TEXTURE_SIZE) {

                    int dstWidth = (int) (bmpAvatar.getWidth() * 0.8);
                    int dstHeight = (int) (bmpAvatar.getHeight() * 0.8);

                    resizedBitmap = Bitmap.createScaledBitmap(bmpAvatar, dstWidth, dstHeight, true);
                    bmpAvatar = resizedBitmap;

                }
                imgAvatarPreview.setImageBitmap(bmpAvatar);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (requestCode == OPEN_CAMERA_MARSHMALLOW && resultCode == RESULT_OK) {
            try {
                bmpAvatar = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                if (bmpAvatar.getHeight() > GL_MAX_TEXTURE_SIZE || bmpAvatar.getWidth() > GL_MAX_TEXTURE_SIZE) {

                    int dstWidth = (int) (bmpAvatar.getWidth() * 0.8);
                    int dstHeight = (int) (bmpAvatar.getHeight() * 0.8);

                    resizedBitmap = Bitmap.createScaledBitmap(bmpAvatar, dstWidth, dstHeight, true);
                    bmpAvatar = resizedBitmap;

                }
                imgAvatarPreview.setImageBitmap(bmpAvatar);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // this return a small thumbnail image

//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            imgPreview.setImageBitmap(imageBitmap);
        }


        if (requestCode == OPEN_CAMERA_NOUGAT && resultCode == RESULT_OK) {
            try {
                bmpAvatar = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));

                if (bmpAvatar.getHeight() > GL_MAX_TEXTURE_SIZE || bmpAvatar.getWidth() > GL_MAX_TEXTURE_SIZE) {

                    int dstWidth = (int) (bmpAvatar.getWidth() * 0.8);
                    int dstHeight = (int) (bmpAvatar.getHeight() * 0.8);

                    resizedBitmap = Bitmap.createScaledBitmap(bmpAvatar, dstWidth, dstHeight, true);
                    bmpAvatar = resizedBitmap;

                }
                imgAvatarPreview.setImageBitmap(bmpAvatar);


            } catch (IOException e) {
                e.printStackTrace();
                Log.e("error", e.toString());
            }
        }


    }

//    private void readData(String response){
//        try {
//            JSONArray
//        }
//    }

}
