package com.k_nakamura.horiojapan.webupdatechecker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity  {
    static final int MENUITEM_ID_DELETE = 1;
    static final int MENUITEM_ID_EDIT = 2;

    ListView itemListView;

    SwipeRefreshLayout mSwipeRefresh;

    static CheckListDBAdapter clDBAdapter;
    static CheckListAdapter listAdapter;
    static ArrayList<CheckListData> checkListArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        setListeners();

        clDBAdapter = new CheckListDBAdapter(this);
        listAdapter = new CheckListAdapter();
        itemListView.setAdapter(listAdapter);
        loadCheckList();


    }

    protected void findViews(){
        itemListView = (ListView)findViewById(R.id.itemListView);
        mSwipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setColorSchemeResources(R.color.green, R.color.red, R.color.blue, R.color.yellow);
    }

    protected void setListeners(){
        itemListView.setOnCreateContextMenuListener(
                new View.OnCreateContextMenuListener(){
                    @Override public void onCreateContextMenu( ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        menu.add(0,MENUITEM_ID_EDIT,0,"Edit");
                        menu.add(0, MENUITEM_ID_DELETE, 0, "Delete");
                    }
                }
        );
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckListData clData = checkListArray.get(position);
                clData.setIsUpdate(false);
                Uri uri = Uri.parse(clData.getUrl());
                Intent i = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(i);
            }
        });

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 引っ張って離した時に呼ばれます。
                allCheck();
            }
        });
    }

    protected void loadCheckList(){
        checkListArray.clear();
        // Read
        clDBAdapter.open();
        Cursor c = clDBAdapter.getAllCheckListDatas();
        startManagingCursor(c);
        if(c.moveToFirst()){
            do {
                CheckListData clData = new CheckListData(
                        c.getInt(c.getColumnIndex(CheckListDBAdapter.COL_ID)),
                        c.getString(c.getColumnIndex(CheckListDBAdapter.COL_TITLE)),
                        c.getString(c.getColumnIndex(CheckListDBAdapter.COL_URL)),
                        c.getString(c.getColumnIndex(CheckListDBAdapter.COL_LASTUPDATE)),
                        c.getString(c.getColumnIndex(CheckListDBAdapter.COL_LASTHTML)),
                        c.getString(c.getColumnIndex(CheckListDBAdapter.COL_LASTDIFFERENCE)),
                        c.getString(c.getColumnIndex(CheckListDBAdapter.COL_IGNOREWARDS)),
                        c.getInt(c.getColumnIndex(CheckListDBAdapter.COL_ISUPDATED))==1,
                        c.getInt(c.getColumnIndex(CheckListDBAdapter.COL_ISNOTIFICATION))==1
                );
                checkListArray.add(clData);
            } while(c.moveToNext());
        }
        stopManagingCursor(c);
        clDBAdapter.close();
        listAdapter.notifyDataSetChanged();
    }

    private class CheckListAdapter extends BaseAdapter {
        List<ViewContainer> containerArray;

        public CheckListAdapter()
        {
            containerArray = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return checkListArray.size();
        }

        @Override
        public Object getItem(int position) {
            return checkListArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView dataTitleTextView;
            final TextView lastupdateTextView;
            final TextView isUpdate;
            final Button checkButton;

            View v = convertView;

            if(v==null){
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.row, null);
            }

            CheckListData checkList = (CheckListData) getItem(position);
            if(checkList != null){
                dataTitleTextView = (TextView)v.findViewById(R.id.dataTitle);
                lastupdateTextView = (TextView)v.findViewById( R.id.dataLastupdate);
                checkButton = (Button)v.findViewById(R.id.check_button);
                isUpdate = (TextView)v.findViewById(R.id.dataIsUpdate);

                dataTitleTextView.setText(checkList.getTitle());
                lastupdateTextView.setText(checkList.getLastupdate());
                isUpdate.setText(checkList.getIsUpdateText());
                checkButton.setTag(position);
                checkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckListData clData = (CheckListData) getItem((int)v.getTag());
                        try {
                            new GetHtmlTask(MainActivity.this, new ViewContainer(clData, null,null,checkButton,isUpdate,lastupdateTextView, null))
                                    .execute(new URL(clData.getUrl()));
                        }catch (MalformedURLException e){
                            e.printStackTrace();
                        }

                    }
                });
                checkButton.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Intent intent = new Intent(getApplication(), GetHtmlActivity.class);
                        CheckListData checkList = (CheckListData) getItem((int)v.getTag());
                        intent.putExtra("CheckListData", checkList);

                        int requestCode = 2000;
                        startActivityForResult( intent, requestCode );

                        return false;
                    }
                });

                ViewContainer vc = new ViewContainer(checkList,null,null,checkButton,isUpdate,lastupdateTextView,null);
                if(containerArray.size() < position + 1 )
                    containerArray.add(vc);

            }

            return v;
        }

        public List<ViewContainer> getContainerArray()
        {
            return containerArray;
        }
    }

    @Override public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        final CheckListData clData = checkListArray.get(menuInfo.position);

        switch(item.getItemId()){
            case MENUITEM_ID_DELETE:
                new AlertDialog.Builder(this)
                        .setIcon(R.mipmap.icon)
                        .setTitle("Are you sure you want to delete this data?")
                        .setPositiveButton( "Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override public void onClick(DialogInterface dialog, int which) {
                                        clDBAdapter.open();
                                        if(clDBAdapter.deleteCheckListData(clData.getId())){
                                            Toast.makeText( getBaseContext(),
                                                    "The data was successfully deleted.",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                            loadCheckList();
                                        }
                                        clDBAdapter.close();
                                    }
                                })
                        .setNegativeButton( "Cancel", null)
                        .show();
                return true;

            case MENUITEM_ID_EDIT:
                editContent(clData);

        }
        return super.onContextItemSelected(item);
    }

    private void editContent(CheckListData clData)
    {
        Intent intent = new Intent(getApplication(), EditActivity.class);
        intent.putExtra("CheckListData", clData);
        int requestCode ;
        if(clData.getId() == 0) requestCode = 1000;
        else requestCode = 2000;
        startActivityForResult( intent, requestCode );
    }
    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(resultCode != RESULT_OK) return;

        clDBAdapter.open();

        if(requestCode == 1000) clDBAdapter.saveCheckListData((CheckListData) intent.getSerializableExtra("RESULT"));
        if(requestCode == 2000) clDBAdapter.update((CheckListData) intent.getSerializableExtra("RESULT"));

        clDBAdapter.close();

        if(requestCode == 3000)
        {
            checkListArray = (ArrayList<CheckListData>)intent.getSerializableExtra("checkDataArray");
            for(CheckListData clData:checkListArray) clData.updateDB(this);
        }

        loadCheckList();
    }

    private void allCheck()
    {
        ViewContainer vc;
        List<ViewContainer> vcArray = new ArrayList<>();
        for(int i = 0; i < checkListArray.size(); i++)
        {
            int vPos = itemListView.getFirstVisiblePosition();
            int cCount = itemListView.getChildCount();
            if(i >= vPos && i < vPos + cCount) {
                TextView isUpdateText = (TextView) itemListView.getChildAt(i - vPos).findViewById(R.id.dataIsUpdate);
                TextView lastUpdateText = (TextView) itemListView.getChildAt(i - vPos).findViewById(R.id.dataLastupdate);
                Button checkButton = (Button) itemListView.getChildAt(i - vPos).findViewById(R.id.check_button);
                vc = new ViewContainer(checkListArray.get(i), null, null, checkButton, isUpdateText, lastUpdateText, mSwipeRefresh);
            }
            else
            {
                vc = new ViewContainer(checkListArray.get(i), null, null, null, null, null, mSwipeRefresh);
            }
            try {
                new GetHtmlTask(this, vc)
                        .execute(new URL(vc.getCheckListData().getUrl()));
            }catch (MalformedURLException e){
                e.printStackTrace();
            }

            vcArray.add(vc);
        }

    }

    /*
     *  オプションメニュー作成
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            editContent(new CheckListData(0,"","","","","","",false,false));
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_setting) {
            Intent intent = new Intent(getApplication(), SettingActivity.class);
            intent.putExtra("checkDataArray", checkListArray);
            int requestCode = 3000;
            startActivityForResult( intent ,requestCode);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
