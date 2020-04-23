package com.papa.flexibleedittext;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import com.papa.library.FlexibleEditText;

public class MainActivity extends AppCompatActivity {

    private FlexibleEditText fet_1;
    private FlexibleEditText fet_2;
    private FlexibleEditText fet_3;
    private FlexibleEditText fet_4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fet_1 = findViewById(R.id.fet_1);
        fet_2 = findViewById(R.id.fet_2);
        fet_3 = findViewById(R.id.fet_3);
        fet_4 = findViewById(R.id.fet_4);

        fet_1.setListener(new FlexibleEditText.OnIconClickListener() {
            @Override
            public void clickEvent(EditText view) {
                Toast.makeText(MainActivity.this, "you click fet_1, "+ view.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        fet_2.setListener(new FlexibleEditText.OnIconClickListener() {
            @Override
            public void clickEvent(EditText view) {
                Toast.makeText(MainActivity.this, "you click fet_2, "+ view.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        fet_3.setListener(new FlexibleEditText.OnIconClickListener() {
            @Override
            public void clickEvent(EditText view) {
                Toast.makeText(MainActivity.this, "you click fet_3, "+ view.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        fet_4.setListener(new FlexibleEditText.OnIconClickListener() {
            @Override
            public void clickEvent(EditText view) {
                Toast.makeText(MainActivity.this, "you click fet_4, "+ view.getText(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
