package msu.olive.Model;

public class Comment {
    int id_comment;
    int id_commentator_comment;
    String username_comment;
    String content_comment;

    public Comment() {

    }

    public Comment(int id_comment, int id_commentator_comment, String username_comment, String content_comment) {
        this.id_comment = id_comment;
        this.id_commentator_comment = id_commentator_comment;
        this.username_comment = username_comment;
        this.content_comment = content_comment;
    }

    public int getId_comment() {
        return id_comment;
    }

    public int getId_commentator_comment() {
        return id_commentator_comment;
    }

    public String getUsername_comment() {
        return username_comment;
    }

    public String getContent_comment() {
        return content_comment;
    }

    public void setId_comment(int id_comment) {
        this.id_comment = id_comment;
    }

    public void setId_commentator_comment(int id_commentator_comment) {
        this.id_commentator_comment = id_commentator_comment;
    }

    public void setUsername_comment(String username_comment) {
        this.username_comment = username_comment;
    }

    public void setContent_comment(String content_comment) {
        this.content_comment = content_comment;
    }
}
