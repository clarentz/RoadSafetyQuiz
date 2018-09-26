package msu.roadsafety.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import msu.roadsafety.Fragment.GamesFragment;
import msu.roadsafety.R;
import msu.roadsafety.Server.RequestHandler;
import msu.roadsafety.Server.Server;

public class GameOverActivity extends AppCompatActivity {

    private int score;
    private int from_game;
    private int answered;
    private int id_user;
    private String strScore, strAnswered, strFromGame;



    private TextView txtScore, txtAnswered, txtFromGame;
    private Button btnShareScore, btnBack, btnHiScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        txtScore = findViewById(R.id.txtScoreFinal);
        txtAnswered = findViewById(R.id.txtAnswered);
        txtFromGame = findViewById(R.id.txtFromGame);

        btnShareScore = findViewById(R.id.btnPostScore);
        btnShareScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadScoretoServer();
            }
        });

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnHiScore = findViewById(R.id.btnHiscore);
        btnHiScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (from_game == 1) {
                    Intent intent = new Intent(GameOverActivity.this, HighScoreMCActivity.class);
                    startActivity(intent);
                } else if (from_game == 2){
                    Intent intent = new Intent(GameOverActivity.this, HighScoreYNActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(GameOverActivity.this, R.string.leaderboard_error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        score = getIntent().getIntExtra("score", 0);
        from_game = getIntent().getIntExtra("id_game", 1);
        answered = getIntent().getIntExtra("question_answered", 0);
        id_user = getIntent().getIntExtra("id_user",0);

        Log.i("from game : ", Integer.toString(from_game));


        switch (from_game) {
            case 1:
                strFromGame = getString(R.string.multiple_choice_result);
                strScore =  getString(R.string.correct_answer)+ Integer.toString(score);
                strAnswered = getString(R.string.number_of_question)+ Integer.toString(answered);

                txtScore.setText(strScore);
                txtFromGame.setText(strFromGame);
                txtAnswered.setText(strAnswered);
                break;
            case 2:
                strFromGame = getString(R.string.yes_no_result);
                strScore = getString(R.string.correct_answer) + Integer.toString(score);
                strAnswered = getString(R.string.number_of_question)+ Integer.toString(answered);

                txtScore.setText(strScore);
                txtFromGame.setText(strFromGame);
                txtAnswered.setText(strAnswered);
                break;

        }

    }

    private void uploadScoretoServer() {
        class UploadScore extends AsyncTask<String, Void, String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(GameOverActivity.this,
                        getApplicationContext().getResources().getString(R.string.uploading),
                        null, true, true);
                loading.setCanceledOnTouchOutside(false);
            }

            @Override
            protected String doInBackground(String... strings) {
                HashMap<String, String> data = new HashMap<>();
                data.put("game_type", Integer.toString(from_game));
                data.put("user_id", Integer.toString(id_user));
                data.put("score", Integer.toString(score));
                data.put("answered", Integer.toString(answered));
                String result = rh.sendPostRequest(Server.CreateScoreURL, data);
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(GameOverActivity.this, s, Toast.LENGTH_SHORT).show();
                finish();
            }

        }

        UploadScore uploadScore = new UploadScore();
        uploadScore.execute();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
