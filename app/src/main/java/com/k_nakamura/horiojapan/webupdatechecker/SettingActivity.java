package com.k_nakamura.horiojapan.webupdatechecker;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by KOSUKE on 2016/08/22.
 */
public class SettingActivity extends AppCompatActivity
{
    static final int MENU_ID_SAVE = 1;

    Switch isNotificationSwitch;
    Spinner intervalSpinner;
    LinearLayout checkListContainerLinearLayout;
    TextView checkTimeText;

    private ArrayList<CheckListData> clDataArray;
    private int _dataId = 0;

    SharedPreferences shPref;
    private boolean preNotificationSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Intent intent = getIntent();
        clDataArray = (ArrayList<CheckListData>)intent.getSerializableExtra("checkDataArray");

        findViews();
        loadSettingCheckListData();
        setListeners();

        shPref = getSharedPreferences("shPref",MODE_PRIVATE);
        preNotificationSetting =  shPref.getBoolean("PreKey_IsNotification",false);
        isNotificationSwitch.setChecked( preNotificationSetting );
        //intervalSpinner.setSelection( shPref.getInt("PreKey_SpinnerSelection",0) );
        checkTimeText.setText(shPref.getString("PreKey_CheckTimeString","00:00"));
    }

    protected void findViews(){
        isNotificationSwitch = (Switch)findViewById(R.id.isNotificationSwitch);

        //intervalSpinner = (Spinner)findViewById(R.id.intervalSpinner);
        checkListContainerLinearLayout = (LinearLayout)findViewById(R.id.settingCheckListContainer);
        checkTimeText = (TextView)findViewById(R.id.checkTimeText);
    }

    protected void setListeners(){
        isNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    //intervalSpinner.setEnabled(true);
                    checkListContainerLinearLayout.setEnabled(true);
                }
                else
                {
                    //intervalSpinner.setEnabled(false);
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
        //intervalSpinner.setAdapter(adapter);

        // リスナーを登録
        /*intervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        });*/

        checkTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                final int minute = calendar.get(Calendar.MINUTE);

                final TimePickerDialog timePickerDialog = new TimePickerDialog(SettingActivity.this,
                        AlertDialog.THEME_HOLO_LIGHT,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                checkTimeText.setText(String.format("%02d:%02d", hourOfDay,minute));
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });
    }

    private void loadSettingCheckListData()
    {
        for(CheckListData clData : clDataArray) {
            checkListContainerLinearLayout.addView(addCheckListData(clData));
        }
    }
    private LinearLayout addCheckListData(CheckListData clData)
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
                    clDataArray.get((int)buttonView.getTag()).setIsNotification(true);
                }
                else
                {
                    clDataArray.get((int)buttonView.getTag()).setIsNotification(false);
                }
            }
        });
        onoffSwitch.setChecked(clData.isNotification());

        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(onoffSwitch, params);

        return layout;
    }

    private void setNotification()
    {
        if(!isNotificationSwitch.isChecked())
        {
            if(preNotificationSetting)
            {
                Intent intent = new Intent(getApplicationContext(), CheckUpdateIntentService.class); // ReceivedActivityを呼び出すインテントを作成
                intent.putExtra("checkDataArray",clDataArray);
                PendingIntent sender = PendingIntent.getService(SettingActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT); // ブロードキャストを投げるPendingIntentの作成

                AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE); // AlramManager取得
                am.cancel(sender); // AlramManagerにPendingIntentを登録
            }
            return;
        }

        Intent intent = new Intent(getApplicationContext(), CheckUpdateIntentService.class); // ReceivedActivityを呼び出すインテントを作成
        intent.putExtra("checkDataArray",clDataArray);
        PendingIntent sender = PendingIntent.getService(SettingActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT); // ブロードキャストを投げるPendingIntentの作成
/*
        Calendar calendar = Calendar.getInstance(); // Calendar取得
        calendar.setTimeInMillis(System.currentTimeMillis()); // 現在時刻を取得
        calendar.add(Calendar.SECOND, 10); // 現時刻より15秒後を設定
*/
        String[] timeStrs = checkTimeText.getText().toString().split(":");
        int hour = Integer.parseInt(timeStrs[0]);
        int minuite = Integer.parseInt(timeStrs[1]);

        // 日本(+9)以外のタイムゾーンを使う時はここを変える
        TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");

        //今日の目標時刻のカレンダーインスタンス作成
        Calendar cal_target = Calendar.getInstance();
        cal_target.setTimeZone(tz);
        cal_target.set(Calendar.HOUR_OF_DAY, hour);
        cal_target.set(Calendar.MINUTE, minuite);
        cal_target.set(Calendar.SECOND, 0);

        //現在時刻のカレンダーインスタンス作成
        Calendar cal_now = Calendar.getInstance();
        cal_now.setTimeZone(tz);

        //ミリ秒取得
        long target_ms = cal_target.getTimeInMillis();
        long now_ms = cal_now.getTimeInMillis();

        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE); // AlramManager取得

        //今日ならそのまま指定
        if (target_ms <= now_ms) {
            cal_target.add(Calendar.DAY_OF_MONTH, 1);
            target_ms = cal_target.getTimeInMillis();
        }
        am.setRepeating(AlarmManager.RTC_WAKEUP,target_ms,AlarmManager.INTERVAL_DAY,sender);

        //AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE); // AlramManager取得
        //am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender); // AlramManagerにPendingIntentを登録
    }

    private void saveData()
    {
        SharedPreferences.Editor e = shPref.edit();
        e.putBoolean("PreKey_IsNotification", isNotificationSwitch.isChecked());
        //e.putInt("PreKey_SpinnerSelection", intervalSpinner.getSelectedItemPosition());
        e.putString("PreKey_CheckTimeString", checkTimeText.getText().toString());
        e.apply();

        for(CheckListData clData:clDataArray)
        {
            clData.updateDB(this);
        }
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
            saveData();

            Intent intent = new Intent();
            intent.putExtra("checkDataArray", clDataArray);
            setResult(RESULT_OK, intent);

            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
