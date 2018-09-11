package msu.olive.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import msu.olive.R;
import msu.olive.Server.RequestHandler;
import msu.olive.Server.Server;
import msu.olive.Activity.MapsActivity;
import msu.olive.Server.RequestHandler;
import msu.olive.Server.Server;
public class UploadActivity extends AppCompatActivity {

    private static int CAMERA_REQUEST_PERMISSION = 100;
    private static final int MAP_REQUEST = 3;

    private static int OPEN_CAMERA_NOUGAT = 70;
    private static int OPEN_CAMERA_MARSHMALLOW = 60;
    private static int OPEN_LIBRARY = 50;


    ImageView imgPreview;
    ImageButton btnCamera, btnLibrary, btnLocation;
    TextView txtAddress;
    Spinner spnIssues;
    Button btnPost;

    Bitmap bitmap;
    String mCurrentPhotoPath;
    private Uri selectedImage;

    double currentLat, currentLong;
    private final int REQUEST_LOCATION_PERMISSION = 22;

    EditText edStatus;
    FusedLocationProviderClient mFusedLocationClient;
    Location mLastLocation;


    private String address= "null";
    private String road_name = " ", sub_admin_area = " ", admin_area = " ", country = " ";

    private String fullAddress;
    private String uploadImage;
    private String uploadStatus = " ";
    private String strRoadSafetyIssue;

    private EditText edRoadName, edSubAdminArea, edAdminArea, edCountry;

    public static final String UPLOAD_KEY_IMAGE = "image";
    public static final String UPLOAD_KEY_ID_USER = "id_user";
    public static final String UPLOAD_KEY_STATUS = "status";
    public static final String UPLOAD_KEY_ADDRESS = "address";
    public static final String UPLOAD_KEY_ROAD_NAME = "road_name";
    public static final String UPLOAD_KEY_SUB_ADMIN_AREA = "sub_admin_area";
    public static final String UPLOAD_KEY_ADMIN_AREA = "admin_area";
    public static final String UPLOAD_KEY_COUNTRY = "country";
    public static final String UPLOAD_KEY_ISSUE = "issue";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        requestCameraPermission();
        requestAccessPermission();


        addControls();
        addEvents();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();

    }

    private void addControls() {
        imgPreview = findViewById(R.id.imgPreview);

        btnCamera = findViewById(R.id.btnCamera);

        btnLibrary = findViewById(R.id.btnGallery);

        btnLocation = findViewById(R.id.btnLocation);

        edStatus = findViewById(R.id.edStatus);

        btnPost = findViewById(R.id.btnShare);

        //txtAddress = findViewById(R.id.txtAddress);

        edRoadName = findViewById(R.id.edRoadName);
        edSubAdminArea = findViewById(R.id.edSubAdminArea);
        edAdminArea = findViewById(R.id.edAdminArea);
        edCountry = findViewById(R.id.edCountry);

        spnIssues = findViewById(R.id.spnIssues);
        ArrayAdapter<CharSequence> roadSafetyAdapter = ArrayAdapter.createFromResource(this,
                R.array.road_safety_issues, android.R.layout.simple_spinner_item);
        roadSafetyAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnIssues.setAdapter(roadSafetyAdapter);



    }

    private void addEvents() {

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                //openCamera();
                {
                    openCameraNougat();
                }
                else{
                    openCamera();
                }
            }
        });

        btnLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLibrary();
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (bitmap!=null){
                upload();}
                else{
                    Toast.makeText(UploadActivity.this, "Choose image to upload", Toast.LENGTH_SHORT).show();
                }
            }
        });



        spnIssues.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    strRoadSafetyIssue = "";
                } else {
                    strRoadSafetyIssue = parent.getItemAtPosition(position).toString();
                }

                // Toast.makeText(UploadActivity.this, strRoadSafetyIssue, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                strRoadSafetyIssue = "";
            }
        });

        edRoadName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                road_name = s.toString();
                fullAddress = road_name + " " + sub_admin_area + " " + admin_area + " " + country;
                //txtAddress.setText(fullAddress);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edAdminArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                admin_area = s.toString();
                fullAddress = road_name + " " + sub_admin_area + " " + admin_area + " " + country;
                // txtAddress.setText(fullAddress);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edSubAdminArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sub_admin_area = s.toString();
                fullAddress = road_name + " " + sub_admin_area + " " + admin_area + " " + country;
                //txtAddress.setText(fullAddress);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edCountry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                country = s.toString();
                fullAddress = road_name + " " + sub_admin_area + " " + admin_area + " " + country;
                //txtAddress.setText(fullAddress);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

    private void upload() {
        class UploadImage extends AsyncTask<Bitmap, Void, String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(UploadActivity.this, "Uploading...", null, true, true);
                loading.setCanceledOnTouchOutside(false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                finish();

            }

            @Override
            protected String doInBackground(Bitmap... params) {
                String result = null;
                bitmap = params[0];
                uploadImage = getStringImage(bitmap);
                //if bitmapsize > ?? then upload image = resize(bitmap)

                String id_user = String.valueOf(getIntent().getIntExtra("id_user", -1));


                uploadStatus = edStatus.getText().toString();

                if (uploadStatus == null) {
                    uploadStatus = " ";
                }
                //String id_user = String.valueOf(getIntent().getIntExtra("id_user", -1));

                //get data from the box

                road_name = edRoadName.getText().toString();
                sub_admin_area = edSubAdminArea.getText().toString();
                admin_area = edAdminArea.getText().toString();
                country = edAdminArea.getText().toString();

                HashMap<String, String> data = new HashMap<>();

                //set Strings = blank to avoid app crashing.

                if (address == null){
                    address = " ";
                }

                if (road_name == null){
                    road_name = " ";
                }

                if (sub_admin_area == null){
                    sub_admin_area = " ";
                }

                if (admin_area == null){
                    admin_area = " ";
                }

                if (country == null){
                    country = " ";
                }

                data.put(UPLOAD_KEY_IMAGE, uploadImage);
                data.put(UPLOAD_KEY_ID_USER, id_user);
                data.put(UPLOAD_KEY_STATUS, uploadStatus);
                data.put(UPLOAD_KEY_ROAD_NAME, road_name);
                data.put(UPLOAD_KEY_ADDRESS, address);
                data.put(UPLOAD_KEY_SUB_ADMIN_AREA, sub_admin_area);
                data.put(UPLOAD_KEY_ADMIN_AREA, admin_area);
                data.put(UPLOAD_KEY_COUNTRY, country);
                data.put(UPLOAD_KEY_ISSUE, strRoadSafetyIssue);

                result = rh.sendPostRequest(Server.UploadImage_URL, data);
                return result;
            }
        }


        UploadImage ui = new UploadImage();
        ui.execute(bitmap);


    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(final Location location) {
                    if (location != null) {
                        mLastLocation = location;
                        String result = location.getLatitude() + "\n" + location.getLongitude();
                        currentLat = location.getLatitude();
                        currentLong = location.getLongitude();
                        //Toast.makeText(UploadActivity.this, currentLat + "\n" + currentLong, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UploadActivity.this, "Cannot get current place", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void openMap() {
        Intent intent = new Intent(UploadActivity.this, MapsActivity.class);
        intent.putExtra("current_lat", currentLat);
        intent.putExtra("current_long", currentLong);
        startActivityForResult(intent, MAP_REQUEST);
    }

    private void openLibrary() {
        Intent inLibrary = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(inLibrary, OPEN_LIBRARY);

    }

    private void openCamera() {
        Intent inCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (inCamera.resolveActivity(getPackageManager()) != null) {


            File photoFile = null;
            try{
                photoFile = createImageFile();

            }catch (IOException e){
                e.printStackTrace();
                Log.d("loi",  e.getMessage());
            }

            if (photoFile != null){
                inCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(inCamera, OPEN_CAMERA_MARSHMALLOW);

            }


        } else {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        }
//        startActivityForResult(inCamera, 1);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OPEN_LIBRARY && resultCode == RESULT_OK){
            selectedImage = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                imgPreview.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (requestCode == OPEN_CAMERA_MARSHMALLOW && resultCode == RESULT_OK ){
            try{
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                imgPreview.setImageBitmap(bitmap);
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
                imgPreview.setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace();
                Log.e("error", e.toString());
            }
        }

        if (requestCode == MAP_REQUEST && resultCode == MapsActivity.RESULT_OK) {
            address = data.getStringExtra("address");
            road_name = data.getStringExtra("road_name");
            sub_admin_area = data.getStringExtra("sub_admin_area");
            admin_area = data.getStringExtra("admin_area");
            country = data.getStringExtra("country");
            //txtAddress.setText(address);

            edRoadName.setText(road_name);
            edSubAdminArea.setText(sub_admin_area);
            edAdminArea.setText(admin_area);
            edCountry.setText(country);


        }
    }


    private void requestAccessPermission() {

        //request write external permission to get full size image from camera
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 99);
        }
        //request write internal permission to get from library

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }


    private void requestCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_PERMISSION);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

}
