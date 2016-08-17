package com.k_nakamura.horiojapan.webupdatechecker;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    TextView tv_html;
    TextView tv_hash;
    Button btn_gethtml;

    SharedPreferences shPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html_source_view);

        findViews();

        shPref = getSharedPreferences("shPref",MODE_PRIVATE);
        tv_html.setText(shPref.getString("html",""));

        btn_gethtml.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn_gethtml.setText("...");
                        btn_gethtml.setEnabled(false);
                        try {
                            new GetHtmlTask(tv_html,tv_hash,btn_gethtml,shPref)
                                    .execute(new URL("http://www.keyakizaka46.com/"));
                        }catch (MalformedURLException e){
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    protected void findViews(){
        tv_hash = (TextView)findViewById(R.id.hash);
        tv_html = (TextView)findViewById(R.id.html);
        btn_gethtml = (Button)findViewById(R.id.get_button);
    }

}
