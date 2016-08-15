package com.k_nakamura.horiojapan.webupdatechecker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView tv_html;
    TextView tv_hash;
    Button btn_gethtml;

    String html_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_hash = (TextView)findViewById(R.id.hash);
        tv_html = (TextView)findViewById(R.id.html);
        btn_gethtml = (Button)findViewById(R.id.get_button);

        btn_gethtml.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }
        );
    }



}
