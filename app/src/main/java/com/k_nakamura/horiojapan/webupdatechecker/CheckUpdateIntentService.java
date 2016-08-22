package com.k_nakamura.horiojapan.webupdatechecker;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by KOSUKE on 2016/08/22.
 */
public class CheckUpdateIntentService extends IntentService {
    final static String TAG = "CheckUpdateIntentService";
    final static int NOTIFICATION_CHECKUPDATE_ID = 1;

    private ArrayList<CheckListData> clDataArray;

    public CheckUpdateIntentService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        clDataArray = (ArrayList<CheckListData>)intent.getSerializableExtra("checkDataArray");
        StringBuilder resultTextBuilder = new StringBuilder();
        int isUpdateCount = 0;

        for(CheckListData clData:clDataArray)
        {
            String result;
            String preHtml;
            try {
                result = GetHtmlTask.getHTML(new URL(clData.getUrl()));
                result = GetHtmlTask.getTrimStr(result);
                preHtml = GetHtmlTask.getTrimStr(clData.getLastHtml());

                if(!result.equals(preHtml)) {
                    GetHtmlTask.getUpdatedLines(result, preHtml, clData);
                }
                if(clData.isUpdate()){
                    isUpdateCount++;
                    resultTextBuilder.append(clData.getTitle() + "\n");
                }
            }catch (MalformedURLException e){
                e.printStackTrace();
            }
        }

        sendNotification(resultTextBuilder.toString(), "", Integer.toString(isUpdateCount));
    }

    private void sendNotification(String text, String subText, String info)
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.icon)
                        .setContentTitle("Update check result")
                        .setContentText(text)
                        .setSubText(subText)
                        .setContentInfo(info);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATION_CHECKUPDATE_ID, mBuilder.build());
    }
}
