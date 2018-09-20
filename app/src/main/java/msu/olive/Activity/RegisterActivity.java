package msu.olive.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class RegisterActivity extends AppCompatActivity {

    EditText register_username, register_password, register_retype, register_info;
    Button btnRegister;


    private String mCurrentPhotoPath;
    private Uri selectedImage;
    private String uploadImage;
    Bitmap bitmap;

    ImageView imgAvatarPreview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        bumbum();
    }

    private void register() {
        String username = register_username.getText().toString();
        String password = register_password.getText().toString();
        String retype = register_retype.getText().toString();
        String info = register_info.getText().toString();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, R.string.please_insert_username, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.please_insert_password, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(retype)) {
            Toast.makeText(this, R.string.please_retype_password, Toast.LENGTH_SHORT).show();
        } else if (!password.equals(retype)) {
            Toast.makeText(this, R.string.password_unmatched, Toast.LENGTH_SHORT).show();
        } else {
            //pushData(username, password, info);
            sendRegistration();
        }
    }

    private void sendRegistration() {
        class Registration extends AsyncTask<Bitmap, Void, String>{

            ProgressDialog loading;
            RequestHandler requestHandler = new RequestHandler();


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(RegisterActivity.this, getApplicationContext().getResources().getString(R.string.creating_account), null, true, true);

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                loading.dismiss();
                if (s.equals("0")) {
                    Toast.makeText(RegisterActivity.this, R.string.register_failed_used, Toast.LENGTH_SHORT).show();
                } else if (s.equals("1")) {
                    Toast.makeText(RegisterActivity.this, R.string.register_success, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, s, Toast.LENGTH_SHORT).show();
                    Log.i("error", s);
                    Toast.makeText(RegisterActivity.this, R.string.register_failed_fatal, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            protected String doInBackground(Bitmap... bitmaps) {
                bitmap = bitmaps[0];
                uploadImage = getStringImage(bitmap);

                HashMap<String, String> data = new HashMap<>();

                data.put("username", register_username.getText().toString());
                data.put("password", register_password.getText().toString());
                data.put("info", register_info.getText().toString());
                data.put("avatar", uploadImage);


                String result = requestHandler.sendPostRequest(Server.Register_AvatarURL, data);
                return result;
            }
        }

        Registration  registration= new Registration();
        registration.execute(bitmap);

    }

//    private void pushData(final String username, final String password, final String info) {
//        class Push extends AsyncTask<Void, Void, String> {
//            ProgressDialog loading;
//            RequestHandler requestHandler = new RequestHandler();
//
//
//            @Override
//            protected String doInBackground(Void... voids) {
//                HashMap<String, String> data = new HashMap<>();
//                data.put("username", username);
//                data.put("password", password);
//                data.put("info", info);
//                data.put("avatar", uploadImage);
//                String result = requestHandler.sendPostRequest(Server.RegisterURL, data);
//                return result;
//            }
//
//
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                loading = ProgressDialog.show(RegisterActivity.this, getApplicationContext().getResources().getString(R.string.creating_account), null, true, true);
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                loading.dismiss();
//                if (s.equals("0")) {
//                    Toast.makeText(RegisterActivity.this, R.string.register_failed_used, Toast.LENGTH_SHORT).show();
//                } else if (s.equals("1")) {
//                    Toast.makeText(RegisterActivity.this, R.string.register_success, Toast.LENGTH_SHORT).show();
//                    finish();
//                } else {
//                    Toast.makeText(RegisterActivity.this, s, Toast.LENGTH_SHORT).show();
//                    Log.i("error", s);
//                    Toast.makeText(RegisterActivity.this, R.string.register_failed_fatal, Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//        Push push = new Push();
//        push.execute();
//
//    }






    private void bumbum() {
        register_username = (EditText) findViewById(R.id.register_username);
        register_password = (EditText) findViewById(R.id.register_password);
        register_retype = (EditText) findViewById(R.id.register_retype);
        register_info = (EditText) findViewById(R.id.register_info);
        btnRegister = (Button) findViewById(R.id.register_button_sign_up);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        imgAvatarPreview = findViewById(R.id.imgAvatarPreview);
        imgAvatarPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseAvatar();
            }
        });


        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_avatar);
        imgAvatarPreview.setImageBitmap(bitmap);
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
            startActivityForResult(pictureIntent, 70);
        }
    }

    private void openCamera() {
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intentCamera.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(intentCamera, 60);
            }
        }
    }

    private void openLibrary() {
        Intent intentLibrary = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intentLibrary, 50);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 50:
                if (resultCode == RESULT_OK) {
                    selectedImage = data.getData();

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        imgAvatarPreview.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case 60:
                if (resultCode == RESULT_OK) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                        imgAvatarPreview.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;


            case 70:
                if (resultCode == RESULT_OK) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                        imgAvatarPreview.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("error", e.toString());
                    }
                }

        }
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);


        mCurrentPhotoPath = "file:" + image.getAbsolutePath();

        return image;
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


}
