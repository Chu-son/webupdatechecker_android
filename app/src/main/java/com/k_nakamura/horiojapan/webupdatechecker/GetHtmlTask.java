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
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * Created by KOSUKE on 2016/08/14.
 */
public class GetHtmlTask
        extends AsyncTask<URL, Void, String>
{
    private TextView tv_html;
    private TextView tv_hash;
    private Button btn_get;

    private String preHtml;

    SharedPreferences shPref;

    public GetHtmlTask(TextView tv_html, TextView tv_hash, Button getButton, SharedPreferences shPref)
    {
        super();
        this.tv_html = tv_html;
        this.tv_hash = tv_hash;
        this.btn_get = getButton;

        this.shPref = shPref;
        preHtml = shPref.getString("html","");
    }

    @Override
    protected String doInBackground(URL... urls) {
        // 取得したテキストを格納する変数
        final StringBuilder result = new StringBuilder();
        // アクセス先URL
        final URL url = urls[0];

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

    @Override
    protected void onPostExecute(String result) {
        String diff;
        result = getCombinedStr(splitHtmlTags(result));
        preHtml = getCombinedStr(splitHtmlTags(preHtml));

        if(!result.equals(preHtml)) {
            diff = getUpdatedLines(preHtml, result);
        }
        else diff = "";
        tv_hash.setText(diff);
        tv_html.setText(result);

        SharedPreferences.Editor e = shPref.edit();
        e.putString("html", result);
        e.apply();

        btn_get.setText("GET");
        btn_get.setEnabled(true);
    }

    protected String getUpdatedLines(String str_A, String str_B) {
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
        while(it.hasNext())
        {
            sb.append(it.next()+"\n");
        }

        return sb.toString();
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

}