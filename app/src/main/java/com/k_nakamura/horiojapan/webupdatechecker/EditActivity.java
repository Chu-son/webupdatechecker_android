package com.k_nakamura.horiojapan.webupdatechecker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by KOSUKE on 2016/08/19.
 */
public class EditActivity
        extends AppCompatActivity
        implements TextWatcher
{

    static final int MENU_ID_SAVE = 1;

    EditText editTitleEditText;
    EditText editURLEditText;
    LinearLayout editIgnoreWardsLinearLayout;
    Button addIgnoreWardsButton;

    CheckListData clData;

    int count = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        findViews();

        Intent intent = getIntent();
        clData = (CheckListData) intent.getSerializableExtra("CheckListData");
        loadCheckListData();
        loadIgnoreWards();

        addIgnoreWardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editIgnoreWardsLinearLayout.addView(addIgnoreWards(""));
                count++;
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void loadCheckListData()
    {
        if(clData.getId() == 0) return;

        editTitleEditText.setText(clData.getTitle());
        editURLEditText.setText(clData.getUrl());
    }

    private void loadIgnoreWards()
    {
        String[] iws = clData.getIgnoreWards().split(",");
        for(String s:iws)
        {
            if(s.isEmpty())continue;
            editIgnoreWardsLinearLayout.addView(addIgnoreWards(s));
        }
    }
    private LinearLayout addIgnoreWards(String ward)
    {
        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams params;

        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        EditText editView = new EditText(this);
        editView.setText(ward);
        editView.setHint("Ignore Ward");
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(editView,params);

        return layout;
    }
    private void saveIgnoreWards()
    {
        StringBuilder sb = new StringBuilder();
        for(int i = editIgnoreWardsLinearLayout.getChildCount() - count ; i < editIgnoreWardsLinearLayout.getChildCount() ; i++)
        {
            LinearLayout ll = (LinearLayout) editIgnoreWardsLinearLayout.getChildAt(i);
            sb.append(((EditText)ll.getChildAt(0)).getText().toString());
            sb.append(",");
        }
        clData.setIgnoreWards(sb.toString());
    }

    protected void findViews(){
        editTitleEditText = (EditText)findViewById(R.id.editTitleEditText);
        editURLEditText = (EditText)findViewById(R.id.editURLEditText);
        editURLEditText.addTextChangedListener(this);
        editIgnoreWardsLinearLayout = (LinearLayout)findViewById(R.id.editIgnoreWardsLinearLayout);
        addIgnoreWardsButton = (Button)findViewById(R.id.addIgnoreWards);
    }


    protected void saveItem(){
        clData.setTitle(editTitleEditText.getText().toString());
        clData.setUrl(editURLEditText.getText().toString());
        Date dateNow = new Date();
        clData.setLastupdate(dateNow.toLocaleString());
        saveIgnoreWards();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
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
            saveItem();

            Intent intent = new Intent();
            intent.putExtra("RESULT", clData);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
