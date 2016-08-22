package com.k_nakamura.horiojapan.webupdatechecker;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Debug;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * Created by KOSUKE on 2016/08/14.
 */
public class GetHtmlTask
        extends AsyncTask<URL, Void, String>
{
    private String preHtml;
    private ViewContainer viewContainer;

    private CheckListData clData;

    public GetHtmlTask(ViewContainer viewContainer)
    {
        super();
        this.viewContainer = viewContainer;
        this.clData = viewContainer.getCheckListData();

        viewContainer.flipCheckButtonText();
        viewContainer.setIsUpdateText("Checking...");

        preHtml = clData.getLastHtml();

        clData.setIsUpdate(false);
    }

    @Override
    protected String doInBackground(URL... urls)
    {
        return getHTML(urls[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        String diff;
        //result = getCombinedStr(splitHtmlTags(result));
        //preHtml = getCombinedStr(splitHtmlTags(preHtml));

        result = getTrimStr(result);
        preHtml = getTrimStr(preHtml);

        if(!result.equals(preHtml)) {
            diff = getUpdatedLines(result, preHtml, clData);
        }
        else
        {
            diff = "";
        }
        viewContainer.setDiffText(diff);
        viewContainer.setHtmlText(result);
        viewContainer.setIsUpdateText(clData.getIsUpdateText());
        viewContainer.setLastUpdateTextNow();

        clData.setLastHtml(result);
        Date dateNow = new Date();
        clData.setLastupdate(dateNow.toLocaleString());

        viewContainer.flipCheckButtonText();
    }

    public static String getUpdatedLines(String str_A, String str_B, CheckListData clData) {
        String[] str_A_array = str_A.split("\n");
        String[] str_B_array = str_B.split("\n");

        StringBuilder sb = new StringBuilder();
        int count = 0;
        for(int i = 0; i < str_A_array.length ;i++)
        {
            if(i == str_A_array.length || i == str_B_array.length)break;
            if(!str_A_array[i].equals(str_B_array[i]))
            {
                if(isIgnoreStr(str_A_array[i], clData))
                {
                    count++;
                    continue;
                }
                clData.setIsUpdate(true);

                sb.append(str_B_array[i]);
                sb.append("\n");
                sb.append("=> ");
                sb.append(str_A_array[i]);
                sb.append("\n");
            }
        }
        sb.append("Ignore " + count + " rows\n");
        sb.append("Diff " + (str_B_array.length-str_A_array.length) + " rows\n");
        return sb.toString();
    }

    /*protected String getUpdatedLines(String str_A, String str_B) {
        HashSet<String> hash_A = null;
        HashSet<String> hash_B = null;

        if (!str_A.isEmpty()) {
            hash_A = getHashSet(str_A);
        }

        if (!str_B.isEmpty()) {
            hash_B = getHashSet(str_B);
        }

        if (hash_A != null && hash_B != null) {
            hash_A.removeAll(hash_B);
        }

        Iterator it = hash_A.iterator();
        StringBuilder sb = new StringBuilder();
        int count = 0;
        while(it.hasNext())
        {
            String s = (String)it.next();
            if(isIgnoreStr(s))
            {
                count++;
                continue;
            }

            sb.append(s+"\n");
        }
        sb.append("Ignore " + count + " rows\n");

        return sb.toString();
    }*/

    protected static boolean isIgnoreStr(String str, CheckListData clData)
    {
        String[] ignorestrs = clData.getIgnoreWords().split(",");
        for(String s:ignorestrs)
        {
            if(s.equals(""))continue;
            if(str.indexOf(s) > -1) return true;
        }
        return false;
    }

    protected HashSet<String> getHashSet(String str) {
        if (str.isEmpty()) {
            return null;
        } else {
            HashSet<String> hashSet = new HashSet<>();
            String[] str_list = str.split("\n");
            for(int i = 0; i < str_list.length;i++)
            {
                hashSet.add(str_list[i].trim());
            }
            return hashSet;
        }
    }

    protected String[] splitHtmlTags(String str)
    {
        Pattern p = Pattern.compile("(/.*?)?>\\s*<|[<>]");
        return p.split(str);
    }
    protected String getCombinedStr(String[] str_list)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < str_list.length ; i++)
        {
            if(str_list[i].trim().isEmpty()) continue;
            sb.append(str_list[i].trim()+"\n");
        }
        return sb.toString();
    }
    public static String getTrimStr(String str)
    {
        StringBuilder sb = new StringBuilder();
        String[] str_list = str.split("\n");
        for(int i = 0; i < str_list.length ; i++)
        {
            if(str_list[i].trim().isEmpty()) continue;
            sb.append(str_list[i].trim()+"\n");
        }
        return sb.toString();
    }

    public static String getHTML(URL url)
    {
        // 取得したテキストを格納する変数
        final StringBuilder result = new StringBuilder();

        HttpURLConnection con = null;
        try {
            // ローカル処理
            // コネクション取得
            con = (HttpURLConnection) url.openConnection();
            con.connect();

            // HTTPレスポンスコード
            final int status = con.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                // 通信に成功した
                // テキストを取得する
                final InputStream in = con.getInputStream();
                String encoding = con.getContentEncoding();
                if (encoding == null) encoding = "UTF8";
                final InputStreamReader inReader = new InputStreamReader(in, encoding);
                final BufferedReader bufReader = new BufferedReader(inReader);
                String line = null;
                // 1行ずつテキストを読み込む
                while((line = bufReader.readLine()) != null) {
                    result.append(line);
                    result.append("\n");
                }
                bufReader.close();
                inReader.close();
                in.close();
            }

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (ProtocolException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (con != null) {
                // コネクションを切断
                con.disconnect();
            }
        }

        return result.toString();
    }
}
