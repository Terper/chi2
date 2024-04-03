package fi.arcada.projekt_chi2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    TextView textCol1, textCol2, textRow1, textRow2, textSignificance;
    Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences pref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        textCol1 = findViewById(R.id.textCol1);
        textCol2 = findViewById(R.id.textCol2);
        textRow1 = findViewById(R.id.textRow1);
        textRow2 = findViewById(R.id.textRow2);
        textSignificance = findViewById(R.id.textSignificance);
        buttonSave = findViewById(R.id.buttonSave);

        textCol1.setText(pref.getString("col1", ""));
        textCol2.setText(pref.getString("col2", ""));
        textRow1.setText(pref.getString("row1", ""));
        textRow2.setText(pref.getString("row2", ""));
        textSignificance.setText(String.format("%.2f",pref.getFloat("sig", 0.05f)));

        buttonSave.setOnClickListener(view -> {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("col1", textCol1.getText().toString());
            editor.putString("col2", textCol2.getText().toString());
            editor.putString("row1", textRow1.getText().toString());
            editor.putString("row2", textRow2.getText().toString());
            editor.putFloat("sig", Float.parseFloat(textSignificance.getText().toString()));
            editor.apply();
            finish();
        });
    }
}
