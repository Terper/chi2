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

    TextView textViewCol1, textViewCol2, textViewRow1, textViewRow2, textViewSignificance;

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

        // ifall det inte finns någon data sparad används samma som i exemplet
        textViewCol1.setText(pref.getString("col1", "Barn"));
        textViewCol2.setText(pref.getString("col2", "Vuxna"));
        textViewRow1.setText(pref.getString("row1", "Spelar Fortnite"));
        textViewRow2.setText(pref.getString("row2", "Spelar inte Fortnite"));
        textViewSignificance.setText(String.format("Signifikans: %.2f",pref.getFloat("sig", 0.05f)));

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

        // fyller textViews med den nya datan
        textViewCol1.setText(pref.getString("col1", ""));
        textViewCol2.setText(pref.getString("col2", ""));
        textViewRow1.setText(pref.getString("row1", ""));
        textViewRow2.setText(pref.getString("row2", ""));
        textViewSignificance.setText(String.format("Signifikans: %.2f",pref.getFloat("sig", 0.0f)));
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
        calculate();
    }

    /**
     * Metod som uppdaterar layouten och räknar ut själva analysen.
     */
    public void calculate() {

        // Uppdatera knapparnas text med de nuvarande värdena,
        // du kan använda .setText() på ett button-objekt.

        // Mata in värdena i Chi-2-uträkningen och ta emot resultatet, t.ex:
        //  double chi2 = Significance.chiSquared(val1, val2, val3, val4);

        // Mata in chi2-resultatet i getP() och ta emot p-värdet, t.ex:
        // double pValue = Significance.getP(chi2);

        /**
         *  - Visa chi2 och pValue åt användaren på ett bra och tydligt sätt!
         *
         *  - Visa procentuella andelen jakande svar inom de olika grupperna.
         *    T.ex. (val1 / (val1+val3) * 100) och (val2 / (val2+val4) * 100
         *
         *  - Analysera signifikansen genom att jämföra p-värdet
         *    med signifikansnivån, visa reultatet åt användaren
         *
         */

    }


}