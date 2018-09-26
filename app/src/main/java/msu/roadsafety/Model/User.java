package msu.roadsafety.Model;

public class User {
    int id;
    String username_user, email_user, avatar_user;

    public User() {
    }

    public User(int id, String username_user, String email_user, String avatar_user) {
        this.id = id;
        this.username_user = username_user;
        this.email_user = email_user;
        this.avatar_user = avatar_user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername_user() {
        return username_user;
    }

    public void setUsername_user(String username_user) {
        this.username_user = username_user;
    }

    public String getEmail_user() {
        return email_user;
    }

    public void setEmail_user(String email_user) {
        this.email_user = email_user;
    }

    public String getAvatar_user() {
        return avatar_user;
    }

    public void setAvatar_user(String avatar_user) {
        this.avatar_user = avatar_user;
    }
}
