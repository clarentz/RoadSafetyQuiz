package msu.olive.Model;

public class Notification {
    String username_notification;
    int id_newsfeed;
    int type;

    public Notification() {

    }

    public Notification(String username_notification, int id_newfeed, int type) {
        this.username_notification = username_notification;
        this.id_newsfeed = id_newfeed;
        this.type = type;
    }

    public String getUsername_notification() {
        return username_notification;
    }

    public void setUsername_notification(String username_notification) {
        this.username_notification = username_notification;
    }

    public int getId_newfeed() {
        return id_newsfeed;
    }

    public void setId_newfeed(int id_newfeed) {
        this.id_newsfeed = id_newfeed;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


}
