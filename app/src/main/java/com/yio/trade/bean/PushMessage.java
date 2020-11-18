package com.yio.trade.bean;

public class PushMessage {

    /**
     * createTime :
     * pushTopic :
     * pushContent :
     * url :
     */

    public long createTime;
    public String pushTopic;
    public String pushContent;
    public String url;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getPushTopic() {
        return pushTopic;
    }

    public void setPushTopic(String pushTopic) {
        this.pushTopic = pushTopic;
    }

    public String getPushContent() {
        return pushContent;
    }

    public void setPushContent(String pushContent) {
        this.pushContent = pushContent;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
