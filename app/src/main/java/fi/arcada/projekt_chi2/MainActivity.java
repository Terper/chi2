package fi.arcada.projekt_chi2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import fi.arcada.projekt_chi2.R.id;

public class MainActivity extends AppCompatActivity {

    Button[] buttons = new Button[4];
    FloatingActionButton fabRevert;
    FloatingActionButton fabSettings;

    /*
    * värden mappat till knappar
    *
    * buttonValues[0]    buttonValues[1]
    * buttonValues[2]    buttonValues[3]
    *
     */
    int[] buttonValues = new int[buttons.length];

    TextView textViewCol1, textViewCol2, textViewRow1, textViewRow2, textViewSignificance, textViewChi, textViewP, textViewResult, textViewResultCol1, textViewResultCol2, textViewResultExplanation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences pref = this.getSharedPreferences("MyPref", 0);

        textViewCol1 = findViewById(R.id.textViewCol1);
        textViewCol2 = findViewById(R.id.textViewCol2);
        textViewRow1 = findViewById(R.id.textViewRow1);
        textViewRow2 = findViewById(R.id.textViewRow2);
        textViewSignificance = findViewById(R.id.textViewSignificance);
        textViewChi = findViewById(R.id.textViewChi);
        textViewP = findViewById(R.id.textViewP);
        textViewResult = findViewById(R.id.textViewResult);
        textViewResultCol1 = findViewById(R.id.textViewResultCol1);
        textViewResultCol2 = findViewById(R.id.textViewResultCol2);
        textViewResultExplanation = findViewById(R.id.textViewResultExplanation);

        // ifall det inte finns någon data sparad används samma som i exemplet
        textViewCol1.setText(pref.getString("col1", "Barn"));
        textViewCol2.setText(pref.getString("col2", "Vuxna"));
        textViewRow1.setText(pref.getString("row1", "Spelar Fortnite"));
        textViewRow2.setText(pref.getString("row2", "Spelar inte Fortnite"));
        textViewSignificance.setText(String.format("Signifikansnivå: %.2f",pref.getFloat("sig", 0.05f)));
        textViewChi.setText(("Chi-2 resultat:"));
        textViewP.setText("P-värde:");
        textViewResult.setText(textViewRow1.getText().toString());
        textViewResultCol1.setText(textViewCol1.getText().toString().concat(":"));
        textViewResultCol2.setText(textViewCol2.getText().toString().concat(":"));
        textViewResultExplanation.setText("");


        // sparar datan för framtiden
        if (!pref.contains("col1")) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("col1", textViewCol1.getText().toString());
            editor.putString("col2", textViewCol2.getText().toString());
            editor.putString("row1", textViewRow1.getText().toString());
            editor.putString("row2", textViewRow2.getText().toString());
            editor.putFloat("sig", 0.05f);
            editor.apply();
        }


        //  initialiserar  knapparna
        int[] buttonIds = {R.id.button1, R.id.button2, R.id.button3, R.id.button4};
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = findViewById(buttonIds[i]);
            buttons[i].setOnClickListener(this::buttonClick);
        }

        fabRevert = findViewById(R.id.fabRevert);
        fabRevert.setOnClickListener(view -> {
            for (int i = 0; i < buttons.length; i++) {
                buttonValues[i] = 0;
                buttons[i].setText("0");
            }
        });

        fabSettings = findViewById(id.fabSettings);
        fabSettings.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        });

    }

    // kallas när inställningsvyn stänger
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences pref = this.getSharedPreferences("MyPref", 0);

        textViewCol1 = findViewById(R.id.textViewCol1);
        textViewCol2 = findViewById(R.id.textViewCol2);
        textViewRow1 = findViewById(R.id.textViewRow1);
        textViewRow2 = findViewById(R.id.textViewRow2);
        textViewSignificance = findViewById(R.id.textViewSignificance);
        textViewResult = findViewById(R.id.textViewResult);

        // fyller textViews med den nya datan
        textViewCol1.setText(pref.getString("col1", ""));
        textViewCol2.setText(pref.getString("col2", ""));
        textViewRow1.setText(pref.getString("row1", ""));
        textViewRow2.setText(pref.getString("row2", ""));
        textViewSignificance.setText(String.format("Signifikans: %.2f",pref.getFloat("sig", 0.0f)));
        textViewResult.setText(textViewRow1.getText().toString());

        calculate(buttonValues[0], buttonValues[1], buttonValues[2], buttonValues[3]);
    }


    /**
     *  Klickhanterare för knapparna
     */
    public void buttonClick(View view) {

        Button button = (Button) view;

        // tar reda på vilken index värdet skall updateras
        int index = 0;
        for (int i = 0; i < buttons.length; i++) {
            if (button.getId() == buttons[i].getId()) {
                index = i;
                break;
            }
        }

        buttonValues[index] += 1;
        button.setText(String.format("%d", buttonValues[index]));

        // Slutligen, kör metoden som ska räkna ut allt!
        calculate(buttonValues[0], buttonValues[1], buttonValues[2], buttonValues[3]);
    }

    /**
     * Metod som uppdaterar layouten och räknar ut själva analysen.
     */
    public void calculate(int val1, int val2, int val3, int val4) {

        textViewChi = findViewById(R.id.textViewChi);
        textViewP = findViewById(R.id.textViewP);
        textViewResultCol1 = findViewById(R.id.textViewResultCol1);
        textViewResultCol2 = findViewById(R.id.textViewResultCol2);
        textViewResultExplanation = findViewById(R.id.textViewResultExplanation);

        SharedPreferences pref = this.getSharedPreferences("MyPref", 0);

        //totala mängden av svar:
        int totalaSvar = val1 + val2 + val3 + val4;

        // 1 och 2 är ja och naj för grupp 1, 3 och 4 för grupp 2
        int totalSvarGrupp1 = val1 + val2;
        int totalSvarGrupp2 = val3 + val4;
        int totalJa = val1 + val3;
        int totalNej = val2 + val4;

        // förvänttade prosent summan för valen:
        double val1Expected = (double) totalJa * ( (double) totalSvarGrupp1 / (double) totalaSvar);
        double val2Expected = (double) totalNej * ( (double) totalSvarGrupp1 / (double) totalaSvar);
        double val3Expected = (double) totalJa * ( (double) totalSvarGrupp2/ (double) totalaSvar);
        double val4Expected = (double) totalNej * ( (double) totalSvarGrupp2/ (double) totalaSvar);

        //räknar ut x**2 med hjälp av chi2 formel:
        double chi2Val1 = Math.pow(val1 - val1Expected, 2) / val1Expected;
        double chi2Val2 = Math.pow(val2 - val2Expected, 2) / val2Expected;
        double chi2Val3 = Math.pow(val3 - val3Expected, 2) / val3Expected;
        double chi2Val4 = Math.pow(val4 - val4Expected, 2) / val4Expected;

        double chi2 = chi2Val1 + chi2Val2 + chi2Val3 + chi2Val4;
        double p = Significance.getP(chi2);

        textViewChi.setText(String.format("Chi-2 resultat: %.2f", chi2));
        textViewP.setText(String.format("P-värde: %.3f", p));

        double prosent1 = ((double) totalSvarGrupp1 / totalaSvar) * 100;
        double prosent2 = ((double) totalSvarGrupp2 / totalaSvar) * 100;


        textViewResultCol1.setText(pref.getString("col1", "").concat(String.format(": %d %%", (int) prosent1)));
        textViewResultCol2.setText(pref.getString("col2", "").concat(String.format(": %d %%", (int) prosent2)));

        double sig = pref.getFloat("sig", 0f);

        if (sig > p) {
            double prosent3 = (1 - p) * 100;
            textViewResultExplanation.setText(String.format("Resultatet är med %.1f %% sannolikhet inte oberoende och kan betraktas som signifikant", prosent3));
        } else {
            textViewResultExplanation.setText("Resultatet är inte signifikant");
        }

    }


}