package com.k_nakamura.horiojapan.webupdatechecker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by KOSUKE on 2016/08/19.
 */
public class GetHtmlActivity extends AppCompatActivity {
    static final int MENU_ID_SAVE = 1;

    TextView tv_html;
    TextView tv_hash;
    Button btn_gethtml;

    CheckListData clData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html_source_view);

        Intent intent = getIntent();
        clData = (CheckListData) intent.getSerializableExtra("CheckListData");

        findViews();

        tv_html.setText(clData.getLastHtml());

        btn_gethtml.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            new GetHtmlTask(new ViewContainer(tv_html,tv_hash,btn_gethtml,null,null),clData)
                                    .execute(new URL(clData.getUrl()));
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

    /*
     *  オプションメニュー作成
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //return super.onCreateOptionsMenu(menu);
        menu.add(0,MENU_ID_SAVE,0,"SAVE").setIcon(android.R.drawable.ic_menu_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == MENU_ID_SAVE) {
            Intent intent = new Intent();
            intent.putExtra("RESULT", clData);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
