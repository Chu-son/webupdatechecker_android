package com.k_nakamura.horiojapan.webupdatechecker;

import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by KOSUKE on 2016/08/21.
 */
public class ViewContainer {
    private TextView tv_html;
    private TextView tv_diff;
    private Button btn_check;
    private TextView tv_isUpdate;
    private TextView lastUpdate;
    private CheckListData clData;

    private String checkButtonText;

    public ViewContainer(CheckListData clData, TextView tv_html, TextView tv_diff, Button btn_check, TextView tv_isUpdate, TextView lastUpdate)
    {
        this.tv_html = tv_html;
        this.tv_diff = tv_diff;
        this.btn_check = btn_check;
        this.tv_isUpdate = tv_isUpdate;
        this.lastUpdate = lastUpdate;
        this.clData = clData;

        if(btn_check != null) checkButtonText = btn_check.getText().toString();
    }

    public void setHtmlText(String str)
    {
        if(tv_html == null)return;
        tv_html.setText(str);
    }

    public void setDiffText(String str)
    {
        if(tv_diff == null)return;
        tv_diff.setText(str);
    }

    public void setIsUpdateText(String str)
    {
        if(tv_isUpdate == null)return;
        tv_isUpdate.setText(str);
    }

    public void flipCheckButtonText()
    {
        if(btn_check == null)return;
        if(btn_check.isEnabled())
        {
            btn_check.setText("...");
            btn_check.setEnabled(false);
        }
        else
        {
            btn_check.setText(checkButtonText);
            btn_check.setEnabled(true);
        }

    }

    public void setLastUpdateTextNow()
    {
        if(lastUpdate == null)return;
        Date dateNow = new Date();
        lastUpdate.setText(dateNow.toLocaleString());
    }

    public CheckListData getCheckListData()
    {
        return clData;
    }

}
