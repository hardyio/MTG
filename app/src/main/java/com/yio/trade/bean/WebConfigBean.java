package com.yio.trade.bean;

public class WebConfigBean {

    /**
     * title :
     * url :
     * hasTitleBar : false
     * rewriteTitle : true
     * stateBarTextColor : bl
     * titleTextColor : #FFFF
     * titleColor : #FFFFFF
     * postData :
     * html :
     * webBack : true
     */

    private String title;
    private String url;
    private boolean hasTitleBar;
    private boolean rewriteTitle;
    private String stateBarTextColor;
    private String titleTextColor;
    private String titleColor;
    private String postData;
    private String html;
    private boolean webBack;

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

    public boolean isHasTitleBar() {
        return hasTitleBar;
    }

    public void setHasTitleBar(boolean hasTitleBar) {
        this.hasTitleBar = hasTitleBar;
    }

    public boolean isRewriteTitle() {
        return rewriteTitle;
    }

    public void setRewriteTitle(boolean rewriteTitle) {
        this.rewriteTitle = rewriteTitle;
    }

    public String getStateBarTextColor() {
        return stateBarTextColor;
    }

    public void setStateBarTextColor(String stateBarTextColor) {
        this.stateBarTextColor = stateBarTextColor;
    }

    public String getTitleTextColor() {
        return titleTextColor;
    }

    public void setTitleTextColor(String titleTextColor) {
        this.titleTextColor = titleTextColor;
    }

    public String getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(String titleColor) {
        this.titleColor = titleColor;
    }

    public String getPostData() {
        return postData;
    }

    public void setPostData(String postData) {
        this.postData = postData;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public boolean isWebBack() {
        return webBack;
    }

    public void setWebBack(boolean webBack) {
        this.webBack = webBack;
    }
}
