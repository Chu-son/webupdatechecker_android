package com.k_nakamura.horiojapan.webupdatechecker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by KOSUKE on 2016/08/22.
 */
public class SettingActivity extends AppCompatActivity
{
    static final int MENU_ID_SAVE = 1;

    Switch isNotificationSwitch;
    Spinner intervalSpinner;
    LinearLayout checkListContainerLinearLayout;

    private ArrayList<CheckListData> clDataArray;
    private int _dataId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Intent intent = getIntent();
        clDataArray = (ArrayList<CheckListData>)intent.getSerializableExtra("checkDataArray");

        findViews();
        loadSettingCheckListData();
        setListeners();

    }

    protected void findViews(){
        isNotificationSwitch = (Switch)findViewById(R.id.isNotificationSwitch);
        intervalSpinner = (Spinner)findViewById(R.id.intervalSpinner);
        checkListContainerLinearLayout = (LinearLayout)findViewById(R.id.settingCheckListContainer);
    }

    protected void setListeners(){
        isNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    intervalSpinner.setEnabled(true);
                    checkListContainerLinearLayout.setEnabled(true);
                }
                else
                {
                    intervalSpinner.setEnabled(false);
                    checkListContainerLinearLayout.setEnabled(false);
                }
            }
        });

        String spinnerItems[] = {"1h", "3h", "6h", "12h"};
        // ArrayAdapter
        ArrayAdapter<String> adapter
                = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // spinner に adapter をセット
        intervalSpinner.setAdapter(adapter);

        // リスナーを登録
        intervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //　アイテムが選択された時
            public void onItemSelected(AdapterView<?> parent, View viw, int arg2, long arg3) {
                Spinner spinner = (Spinner) parent;
                String item = (String) spinner.getSelectedItem();

                if (item.equals("1h")) {
                }
            }

            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadSettingCheckListData()
    {
        for(CheckListData clData : clDataArray) {
            checkListContainerLinearLayout.addView(addCheckListData(clData));
        }
    }
    private LinearLayout addCheckListData(final CheckListData clData)
    {
        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams params;

        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView dataTitle = new TextView(this);
        dataTitle.setText(clData.getTitle());
        dataTitle.setTextSize(16);
        dataTitle.setTextColor(Color.BLACK);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        layout.addView(dataTitle, params);

        Switch onoffSwitch = new Switch(this);
        onoffSwitch.setTag(_dataId++);
        onoffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    clData.setIsNotification(true);
                }
                else
                {
                    clData.setIsNotification(false);
                }
            }
        });

        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(onoffSwitch, params);

        return layout;
    }

    private void setNotification()
    {
        Intent i = new Intent(getApplicationContext(), CheckUpdateIntentService.class); // ReceivedActivityを呼び出すインテントを作成
        i.putExtra("checkDataArray",clDataArray);
        PendingIntent sender = PendingIntent.getService(SettingActivity.this, 0, i, 0); // ブロードキャストを投げるPendingIntentの作成

        Calendar calendar = Calendar.getInstance(); // Calendar取得
        calendar.setTimeInMillis(System.currentTimeMillis()); // 現在時刻を取得
        calendar.add(Calendar.SECOND, 15); // 現時刻より15秒後を設定

        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE); // AlramManager取得
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender); // AlramManagerにPendingIntentを登録
    }

    /*
     *  オプションメニュー作成
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //return super.onCreateOptionsMenu(menu);
        menu.add(0,MENU_ID_SAVE,0,"SAVE")
                .setIcon(android.R.drawable.ic_menu_save)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
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
            setNotification();
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
