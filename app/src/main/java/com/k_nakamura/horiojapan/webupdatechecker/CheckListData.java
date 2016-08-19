package com.k_nakamura.horiojapan.webupdatechecker;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by user on 2016/08/18.
 */
public class CheckListData
        implements Serializable
{
    protected int id;
    protected String title;
    protected String url;
    protected String lastupdate;
    protected String lastHtml;
    protected String ignoreWards;

    public CheckListData(int id, String title, String url, String lastupdate, String lastHtml, String ignoreWards)
    {
        this.id = id;
        this.title = title;
        this.url = url;
        this.lastupdate = lastupdate;
        this.lastHtml = lastHtml;
        this.ignoreWards = ignoreWards;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastHtml() {
        return lastHtml;
    }

    public void setLastHtml(String lastHtml) {
        this.lastHtml = lastHtml;
    }

    public String getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(String lastupdate) {
        this.lastupdate = lastupdate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIgnoreWards() {
        return ignoreWards;
    }

    public void setIgnoreWards(String ignoreWards) {
        this.ignoreWards = ignoreWards;
    }
}
