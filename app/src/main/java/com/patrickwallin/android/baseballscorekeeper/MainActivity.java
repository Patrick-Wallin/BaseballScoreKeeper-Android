package com.patrickwallin.android.baseballscorekeeper;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    char currentTeam = 'A';
    int scoreTeamA = 0;
    int scoreTeamB = 0;
    int out = 0;
    int inning = 1;
    int run = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /*
        Method Name: activeTeam
        Parameter: None
        Purpose: Activate the team with run/out based on playing (taking turn).
     */
    public void activeTeam() {
        boolean bSetAllToFalse = false;
        if(inning == 9 && currentTeam == 'B' && scoreTeamB > scoreTeamA) {
            gameOver();
        }else {
            if (currentTeam == 'A') {
                setEnabledRunButton(currentTeam,true);
                setEnabledRunButton('B',false);
                setEnabledOutButton(currentTeam,true);
                setEnabledOutButton('B',false);
            } else {
                setEnabledRunButton(currentTeam,true);
                setEnabledRunButton('A',false);
                setEnabledOutButton(currentTeam,true);
                setEnabledOutButton('A',false);
            }
        }
    }

    /*
        Method Name: setEnabledRunButton
        Parameter: Team Letter, Enable button
        Purpose: Enable Run button based on team letter.
     */
    public void setEnabledRunButton(char team, boolean enabled) {
        int idButton = (team == 'A' ? R.id.run_team_a_button : R.id.run_team_b_button);
        Button btnRunForTeamA = (Button) findViewById(idButton);
        btnRunForTeamA.setEnabled(enabled);
    }

    /*
        Method Name: setEnabledOutButton
        Parameter: Team Letter, Enable button
        Purpose: Enable Out button based on team letter.
     */
    public void setEnabledOutButton(char team, boolean enabled) {
        int idButton = (team == 'A' ? R.id.out_team_a_button : R.id.out_team_b_button);
        Button btnOutForTeamA = (Button) findViewById(idButton);
        btnOutForTeamA.setEnabled(enabled);
    }

    /*
        Method Name: addRun
        Parameter: view
        Purpose: Add run to the current team who is at bat.
     */
    public void addRun(View view) {
        run++;
        int score = (currentTeam == 'A' ? ++scoreTeamA : ++scoreTeamB);
        displayScore(currentTeam,score);
    }

    public void addOut(View view) {
        out++;
        if(out > 2) {
            // switch to different team
            updateScoreboard();
            if(currentTeam == 'B')
                inning++;
            currentTeam = (currentTeam == 'A' ? 'B' : 'A');
            out = 0;
            run = 0;
            activeTeam();
            if(inning > 9 || (inning == 9 && currentTeam == 'B' && scoreTeamB > scoreTeamA)) {
                gameOver();
            }
        }
        updateOutView();
    }

    /*
        Method Name: updateOutView
        Parameter: none
        Purpose: Update number of out in view.
     */
    public void updateOutView() {
        TextView outView = (TextView) findViewById(R.id.out_view);
        outView.setText("Out: " + String.valueOf(out).trim());
    }

    public void gameOver() {
        Context context = getApplicationContext();
        CharSequence text = "";
        int duration = Toast.LENGTH_LONG;
        if(scoreTeamA == scoreTeamB) {
            text = "It is a tied!";
        }else if(scoreTeamA > scoreTeamB) {
            text = "Team A is a winner!";
        }else {
            text = "Team B is a winner!";
        }
        setEnabledRunButton('A',false);
        setEnabledRunButton('B',false);
        setEnabledOutButton('A',false);
        setEnabledOutButton('B',false);
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }

    public void resetGame(View view) {
        currentTeam = 'A';
        scoreTeamA = 0;
        scoreTeamB = 0;
        out = 0;
        inning = 1;
        run = 0;

        displayScore('A',0);
        displayScore('B',0);
        displayTotal('A',0);
        displayTotal('B',0);

        StringBuilder idTeamA = new StringBuilder();
        StringBuilder idTeamB = new StringBuilder();
        int idInningForTeamA = -1;
        int idInningForTeamB = -1;


        for(int iInning = 1; iInning <= 9; iInning++) {
            idTeamA.setLength(0);
            idTeamA.append("team_a_");
            idTeamA.append(String.valueOf(iInning));
            idTeamA.append("_inning");
            idTeamB.setLength(0);
            idTeamB.append("team_b_");
            idTeamB.append(String.valueOf(iInning));
            idTeamB.append("_inning");

            idInningForTeamA = -1;
            idInningForTeamB = -1;

            try {
                idInningForTeamA = R.id.class.getField(idTeamA.toString()).getInt(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

            try {
                idInningForTeamB = R.id.class.getField(idTeamB.toString()).getInt(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

            if(idInningForTeamA > -1) {
                TextView scoreView = (TextView) findViewById(idInningForTeamA);
                scoreView.setText("");
            }

            if(idInningForTeamB > -1) {
                TextView scoreView = (TextView) findViewById(idInningForTeamB);
                scoreView.setText("");
            }



        }

        activeTeam();

    }



    public void displayScore(char team, int score) {
        int idTextView = (team == 'A' ? R.id.team_a_score : R.id.team_b_score);
        TextView scoreView = (TextView) findViewById(idTextView);
        scoreView.setText(String.valueOf(score));
        updateScoreboard();
    }

    public void displayTotal(char team, int total) {
        int idTextView = (team == 'A' ? R.id.team_a_total : R.id.team_b_total);
        TextView totalView = (TextView) findViewById(idTextView);
        totalView.setText(String.valueOf(total));

    }

    public void updateScoreboard() {
        StringBuilder id = new StringBuilder();
        id.append("team_");
        id.append(String.valueOf(currentTeam).toLowerCase());
        id.append("_");
        id.append(String.valueOf(inning));
        id.append("_inning");

        StringBuilder idTotal = new StringBuilder();
        idTotal.append("team_");
        idTotal.append(String.valueOf(currentTeam).toLowerCase());
        idTotal.append("_total");

        int idInning = -1;
        int total = -1;

        try {
            idInning = R.id.class.getField(id.toString()).getInt(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        try {
            total = R.id.class.getField(idTotal.toString()).getInt(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        if(idInning > -1) {
            TextView scoreView = (TextView) findViewById(idInning);
            String sScore = scoreView.getText().toString().trim();
            scoreView.setText(String.valueOf(Integer.valueOf(run)));
        }else {
            // unable to get R.id.team_x_x_inning ID value

        }

        if(total > -1) {
            TextView scoreTotalView = (TextView) findViewById(total);
            String sScoreTotal = scoreTotalView.getText().toString().trim();
            sScoreTotal = (sScoreTotal.isEmpty() ? "0" : sScoreTotal);
            sScoreTotal = String.valueOf(currentTeam == 'A' ? scoreTeamA : scoreTeamB);
            scoreTotalView.setText(sScoreTotal);
        }else {
            // unable to get R.id.team_x_total ID value
        }

    }

}
