package msu.olive.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class MultipleChoicesGameActivity extends AppCompatActivity {

    private int MCscore = 0;
    private int answered_question = 0;
    private Button btnA, btnB, btnC, btnD, btnStop;
    private ArrayList<Image> imagesArrayList;
    private ImageView imgQuestionMC;
    private TextView txtQuestionMC, txtScoreMC;
    int MC_RANDOM_QUESTION, MC_RANDOM_PLACE;
    private TextView txtIssueMC;

    String host = "http://" + Server.HOST;

    View correct_answer_toast, wrong_answer_toast;
    TextView txtCorrectLocation;
    String show_correct_answer;
    private int id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_choices_game);


        addControls();

        id_user = getIntent().getIntExtra("id_user", 0);
        imagesArrayList = (ArrayList<Image>) getIntent().getBundleExtra("data").getSerializable("list question");
        play();

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmStop();
            }
        });
    }

    private void confirmStop() {
        new AlertDialog.Builder(this)
                .setTitle(" ")
                .setMessage("Do you want to stop?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent stop = new Intent(MultipleChoicesGameActivity.this, GameOverActivity.class);
                        stop.putExtra("score", MCscore) ;
                        stop.putExtra("id_game", 1);
                        stop.putExtra("question_answered", answered_question);
                        stop.putExtra("id_user", id_user);
                        MultipleChoicesGameActivity.this.finish();
                        startActivity(stop);

                    }
                }).setNegativeButton("No", null).show();
    }

    private void play() {
        txtScoreMC.setText("Your score: " + MCscore);

        Random random = new Random();

        MC_RANDOM_PLACE = random.nextInt(4);

        MC_RANDOM_QUESTION = random.nextInt(imagesArrayList.size());
        final Image question_material_MC = imagesArrayList.get(MC_RANDOM_QUESTION);

        show_correct_answer = question_material_MC.getRoad_name() + " is in " + question_material_MC.getSub_admin_area();


        final ArrayList<Image> sub_list1 = new ArrayList<>();
        for (int i = 0; i < imagesArrayList.size(); i++){
            if (!imagesArrayList.get(i).getSub_admin_area().equals(question_material_MC.getSub_admin_area())){
                sub_list1.add(imagesArrayList.get(i));
                Log.i("image 1", imagesArrayList.get(i).getSub_admin_area());
            }
        }

        int VALUE1_RANDOM = random.nextInt(sub_list1.size());
        final Image random_answer1 = sub_list1.get(VALUE1_RANDOM);

        final ArrayList<Image> sub_list2 = new ArrayList<>();
        for (int i= 0; i<sub_list1.size(); i++){
            if (!sub_list1.get(i).getSub_admin_area().equals(random_answer1.getSub_admin_area())){
                sub_list2.add(sub_list1.get(i));
            }
        }

        int VALUE2_RANDOM = random.nextInt(sub_list2.size());
        final Image random_answer2 = sub_list2.get(VALUE2_RANDOM);

        final ArrayList<Image> sub_list3 = new ArrayList<>();
        for (int i=0;i<sub_list2.size(); i++){
            if (!sub_list2.get(i).getSub_admin_area().equals(random_answer2.getSub_admin_area())){
                sub_list3.add(sub_list2.get(i));
            }
        }

        int VALUE3_RANDOM = random.nextInt(sub_list3.size());
        final Image random_answer3 = sub_list3.get(VALUE3_RANDOM);

        String image_url = host+"/"+question_material_MC.getUrl();
        Glide.with(this).load(image_url).into(imgQuestionMC);


        switch (MC_RANDOM_PLACE){
            case 0:
                String strQuestion = "Select position of " + question_material_MC.getRoad_name() + " :";

                txtQuestionMC.setText(strQuestion);
                Toast.makeText(this, question_material_MC.getIssue(), Toast.LENGTH_SHORT).show();
                txtIssueMC.setText(question_material_MC.getIssue());

                btnA.setText(question_material_MC.getSub_admin_area());
                btnB.setText(random_answer1.getSub_admin_area());
                btnC.setText(random_answer2.getSub_admin_area());
                btnD.setText(random_answer3.getSub_admin_area());

                btnA.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        correctAnswer();
                    }
                });
                btnB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wrongAnswer();
                    }
                });
                btnC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wrongAnswer();
                    }
                });
                btnD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wrongAnswer();
                    }
                });


            case 1:
                strQuestion = "Select position of " + question_material_MC.getRoad_name() + " :";
                txtQuestionMC.setText(strQuestion);

                btnB.setText(question_material_MC.getSub_admin_area());
                btnA.setText(random_answer1.getSub_admin_area());
                btnC.setText(random_answer2.getSub_admin_area());
                btnD.setText(random_answer3.getSub_admin_area());

                btnB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        correctAnswer();
                    }
                });
                btnA.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wrongAnswer();
                    }
                });
                btnC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wrongAnswer();
                    }
                });
                btnD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wrongAnswer();
                    }
                });

            case 2:
                strQuestion = "Select position of " + question_material_MC.getRoad_name() + " :";
                txtQuestionMC.setText(strQuestion);

                btnC.setText(question_material_MC.getSub_admin_area());
                btnA.setText(random_answer1.getSub_admin_area());
                btnB.setText(random_answer2.getSub_admin_area());
                btnD.setText(random_answer3.getSub_admin_area());

                btnC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        correctAnswer();
                    }
                });
                btnA.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wrongAnswer();
                    }
                });
                btnB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wrongAnswer();
                    }
                });
                btnD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wrongAnswer();
                    }
                });


            case 3:
                strQuestion = "Select position of " + question_material_MC.getRoad_name() + " :";
                txtQuestionMC.setText(strQuestion);

                btnD.setText(question_material_MC.getSub_admin_area());
                btnA.setText(random_answer1.getSub_admin_area());
                btnB.setText(random_answer2.getSub_admin_area());
                btnC.setText(random_answer3.getSub_admin_area());

                btnD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        correctAnswer();
                    }
                });
                btnA.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wrongAnswer();
                    }
                });
                btnB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wrongAnswer();
                    }
                });
                btnC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wrongAnswer();
                    }
                });

                answered_question++;

        }




    }

    private void addControls() {
        btnA = findViewById(R.id.btnA);
        btnB = findViewById(R.id.btnB);
        btnC = findViewById(R.id.btnC);
        btnD = findViewById(R.id.btnD);
        btnStop = findViewById(R.id.btnStopMC);
        imagesArrayList = new ArrayList<>();
        txtQuestionMC = findViewById(R.id.txtQuestionMC);
        txtScoreMC = findViewById(R.id.txtScoreMC);
        imgQuestionMC = findViewById(R.id.imgQuestionMC);
        txtIssueMC = findViewById(R.id.txtIssueMC);

        LayoutInflater inflater  = getLayoutInflater();
        correct_answer_toast = inflater.inflate(R.layout.toast_layout_correct,
                (ViewGroup) findViewById(R.id.toast_container_correct));
        wrong_answer_toast = inflater.inflate(R.layout.toast_layout_wrong,
                (ViewGroup) findViewById(R.id.toast_container_wrong));
        txtCorrectLocation = wrong_answer_toast.findViewById(R.id.txtCorrectLocation);
    }

    private void correctAnswer() {
        MCscore++;
        //Toast.makeText(this, "Correct Answer !", Toast.LENGTH_SHORT).show();

        Toast correct_toast = new Toast(getApplicationContext());
        correct_toast.setGravity(Gravity.CENTER_VERTICAL, 0 ,0);
        correct_toast.setDuration(Toast.LENGTH_SHORT);
        correct_toast.setView(correct_answer_toast);
        correct_toast.show();

        txtScoreMC.setText("Score: " + MCscore);
        play();
    }

    private void wrongAnswer() {

        Toast wrong_toast = new Toast(getApplicationContext());
        wrong_toast.setGravity(Gravity.CENTER_VERTICAL, 0 ,0);
        wrong_toast.setDuration(Toast.LENGTH_SHORT);
        wrong_toast.setView(wrong_answer_toast);
        txtCorrectLocation.setText(show_correct_answer);
        wrong_toast.show();


        txtScoreMC.setText("Score: " + MCscore);
        play();
    }
}
