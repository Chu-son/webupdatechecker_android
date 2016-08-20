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

    static int _wordsId;

    EditText editTitleEditText;
    EditText editURLEditText;
    LinearLayout editIgnoreWordsLinearLayout;
    Button addIgnoreWordsButton;

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
        _wordsId = 0;
        loadIgnoreWords();

        addIgnoreWordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editIgnoreWordsLinearLayout.addView(addIgnoreWords(""));
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

    private void loadIgnoreWords()
    {
        String[] iws = clData.getIgnoreWords().split(",");
        for(String s:iws)
        {
            if(s.isEmpty())continue;
            editIgnoreWordsLinearLayout.addView(addIgnoreWords(s));
        }
    }

    private LinearLayout addIgnoreWords(String word)
    {
        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams params;

        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        EditText editView = new EditText(this);
        editView.setText(word);
        editView.setHint("Ignore Word");
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        layout.addView(editView,params);

        Button deleteButton = new Button(this);
        deleteButton.setText("-");
        deleteButton.setTag(_wordsId++);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = (int)v.getTag();
                String[] iws = clData.getIgnoreWords().split(",");
                StringBuilder sb = new StringBuilder();
                for(int i = 0 ; i < iws.length ; i++)
                {
                    if(i == index)continue;
                    sb.append(iws[i] + "\n");
                }
                clData.setIgnoreWords(sb.toString());

                editIgnoreWordsLinearLayout.removeViewAt(index);
            }
        });
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(deleteButton, params);

        return layout;
    }
    private void saveIgnoreWords()
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i < editIgnoreWordsLinearLayout.getChildCount() ; i++)
        {
            LinearLayout ll = (LinearLayout) editIgnoreWordsLinearLayout.getChildAt(i);
            sb.append(((EditText)ll.getChildAt(0)).getText().toString());
            sb.append(",");
        }
        clData.setIgnoreWords(sb.toString());
    }

    protected void findViews(){
        editTitleEditText = (EditText)findViewById(R.id.editTitleEditText);
        editURLEditText = (EditText)findViewById(R.id.editURLEditText);
        editURLEditText.addTextChangedListener(this);
        editIgnoreWordsLinearLayout = (LinearLayout)findViewById(R.id.editIgnoreWordsLinearLayout);
        addIgnoreWordsButton = (Button)findViewById(R.id.addIgnoreWords);
    }


    protected void saveItem(){
        clData.setTitle(editTitleEditText.getText().toString());
        clData.setUrl(editURLEditText.getText().toString());
        Date dateNow = new Date();
        clData.setLastupdate(dateNow.toLocaleString());
        saveIgnoreWords();
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
