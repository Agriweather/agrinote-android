package tw.com.agrinote.model;

import android.util.*;

/**
 * Created by orc59 on 2017/11/3.
 */

public class User {

    private int id;
    private String name;
    private String picPath;
    private String account;
    private String token;
    private boolean login;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public boolean validate(String token) {
        android.util.Log.i("User", "|" + this.token + "| = |" + token + "|");
        android.util.Log.i("User","this.token == token " + (this.token.trim().equals(token.trim())));
        return this.token.trim().equals(token.trim());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", picPath='" + picPath + '\'' +
                ", account='" + account + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
