package com.yio.trade.bean;

public class LoginBean {

    /**
     * token1 : anN4aG9leHpheWlndWJuY3RocG9udHFoa2tjMg==
     * token2 : NTkxMjRkM2ZiYmFhMmMwNjYyZjg0MDI0NjNjYzMyMWU=
     * url : https://bb.skr.today/zh_cn/?sign=11
     */

    private String token1;
    private String token2;
    private String url;

    public String getToken1() {
        return token1;
    }

    public void setToken1(String token1) {
        this.token1 = token1;
    }

    public String getToken2() {
        return token2;
    }

    public void setToken2(String token2) {
        this.token2 = token2;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
