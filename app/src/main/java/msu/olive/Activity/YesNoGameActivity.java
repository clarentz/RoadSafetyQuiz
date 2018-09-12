package msu.olive.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Random;

import msu.olive.Model.Image;
import msu.olive.R;
import msu.olive.Server.Server;

public class YesNoGameActivity extends AppCompatActivity {


    TextView txtScoreYN, txtQuestionYN;
    private Button btnYes, btnNo, btnStop;
    ArrayList<Image> imagesArrayListYN_1,imagesArrayListYN;
    private int YNScore = 0;
    private int answered_question = 0;
    ImageView imgQuestionYN;
    String host = "http://"+  Server.HOST;
    String show_correct_answer;
    View correct_answer_toast, wrong_answer_toast;
    int id_user;

    TextView txtCorrectLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yes_no_game);

        addControls();

        id_user = getIntent().getIntExtra("id_user", 0);
        imagesArrayListYN_1 = (ArrayList<Image>) getIntent().getBundleExtra("data").getSerializable("list question");
        imagesArrayListYN = new ArrayList<>();


        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmStop();
            }
        });

        for (int i =0; i < imagesArrayListYN_1.size(); i++){
            if (imagesArrayListYN_1.get(i).getRoad_name().length() > 2
                    && imagesArrayListYN_1.get(i).getSub_admin_area().length() > 2
                    && imagesArrayListYN_1.get(i).getAdmin_area().length() >2
                    && imagesArrayListYN_1.get(i).getCountry().length() > 2 ){
                imagesArrayListYN.add(imagesArrayListYN_1.get(i));
            }
        }



        play();
    }

    private void play() {

        answered_question ++;

        txtScoreYN.setText("Score: " + YNScore);

        Random random = new Random();
        int random_number_question = random.nextInt(2);

        int random_number_answer = random.nextInt(imagesArrayListYN.size());
        Image question_material = imagesArrayListYN.get(random_number_answer);


        show_correct_answer = question_material.getRoad_name() + " is in " + question_material.getSub_admin_area();

        switch (random_number_question){
            case 1:
                //String image_url = "http://cdn.hoahoctro.vn/uploads/2018/04/5ae29976bc282-unnamed.jpg";
                String image_url = host+"/"+question_material.getUrl();
//                Toast.makeText(ActivityGame2.this, image_url, Toast.LENGTH_SHORT).show();
                Glide.with(this)
                        .load(image_url)
                        .into(imgQuestionYN);
                txtQuestionYN.setText(question_material.getRoad_name() + " is in " + question_material.getSub_admin_area());
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        correctAnswer();
                    }
                });
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wrongAnswer();
                    }
                });
            case 0:
                Random random1 = new Random();
                int random_wrong_answer = random1.nextInt(imagesArrayListYN.size());
                Image wrong_question_material = imagesArrayListYN.get(random_wrong_answer);
                txtQuestionYN.setText(question_material.getRoad_name() + " is in " + wrong_question_material.getSub_admin_area());
                String image_url1 =//"http://cdn.hoahoctro.vn/uploads/2018/04/5ae29976bc282-unnamed.jpg";
                        host+"/"+question_material.getUrl();
                // Toast.makeText(YesNoGameActivity.this, image_url1, Toast.LENGTH_SHORT).show();
                Glide.with(this)
                        .load(image_url1)
                        .into(imgQuestionYN);
                if (question_material.getSub_admin_area().equals(wrong_question_material.getSub_admin_area())){
                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            correctAnswer();
                        }
                    });
                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            wrongAnswer();
                        }
                    });
                }else {
                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            correctAnswer();
                        }
                    });
                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            wrongAnswer();
                        }
                    });
                }
        }

    }

    private void confirmStop() {
        new AlertDialog.Builder(this)
                .setTitle(" ")
                .setMessage("Do you want to stop?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent stopGame2 = new Intent(YesNoGameActivity.this, GameOverActivity.class);
                        stopGame2.putExtra("score", YNScore);
                        stopGame2.putExtra("question_answered", answered_question);
                        stopGame2.putExtra("id_game", 2);
                        stopGame2.putExtra("id_user", id_user);
                        YesNoGameActivity.this.finish();
                        startActivity(stopGame2);

                    }
                }).setNegativeButton("No", null).show();

    }

    private void addControls() {
        txtQuestionYN = findViewById(R.id.txtQuestionYN);
        txtScoreYN = findViewById(R.id.txtScoreYN);
        btnYes = findViewById(R.id.btnYes);
        btnNo = findViewById(R.id.btnNo);
        btnStop = findViewById(R.id.btnStop);

        imgQuestionYN = findViewById(R.id.imgQuestionYN);


        LayoutInflater inflater  = getLayoutInflater();
        correct_answer_toast = inflater.inflate(R.layout.toast_layout_correct,
                (ViewGroup) findViewById(R.id.toast_container_correct));
        wrong_answer_toast = inflater.inflate(R.layout.toast_layout_wrong,
                (ViewGroup) findViewById(R.id.toast_container_wrong));
        txtCorrectLocation = wrong_answer_toast.findViewById(R.id.txtCorrectLocation);
    }


    private void correctAnswer() {
        YNScore++;
        //Toast.makeText(this, "Correct Answer !", Toast.LENGTH_SHORT).show();

        Toast correct_toast = new Toast(getApplicationContext());
        correct_toast.setGravity(Gravity.CENTER_VERTICAL, 0 ,0);
        correct_toast.setDuration(Toast.LENGTH_SHORT);
        correct_toast.setView(correct_answer_toast);
        correct_toast.show();

        txtScoreYN.setText("Your score: " + YNScore);
        play();
    }

    private void wrongAnswer() {

        Toast wrong_toast = new Toast(getApplicationContext());
        wrong_toast.setGravity(Gravity.CENTER_VERTICAL, 0 ,0);
        wrong_toast.setDuration(Toast.LENGTH_SHORT);
        wrong_toast.setView(wrong_answer_toast);
        txtCorrectLocation.setText(show_correct_answer);
        wrong_toast.show();


        txtScoreYN.setText("Score: " + YNScore);
        play();
    }
}
