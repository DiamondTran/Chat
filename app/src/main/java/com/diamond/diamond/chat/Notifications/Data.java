package com.diamond.diamond.chat.Notifications;

public class Data {
    private String User;
    private int icon;
    private String body;
    private String title;
    private String sented;

    public Data(String user, int icon, String body, String title, String sented) {
        User = user;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.sented = sented;
    }
public Data(){

}

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sented) {
        this.sented = sented;
    }
}
