package msu.olive.Model;

import java.io.Serializable;

public class Newsfeed implements Serializable{
    int id_newsfeed;
    String avatar_newsfeed;
    String username_newsfeed;
    String status_newsfeed;
    String image_newsfeed;
    String roadname_newsfeed;
    String adminarea_newsfeed;
    String subadminarea_newsfeed;
    String issue_newsfeed;
    int like_newsfeed;

    public Newsfeed() {

    }

    public Newsfeed(int id_newsfeed, String avatar_newsfeed, String username_newsfeed, String status_newsfeed, String image_newsfeed, String roadname_newsfeed, String adminarea_newsfeed, String subadminarea_newsfeed, String issue_newsfeed, int like_newsfeed) {
        this.id_newsfeed = id_newsfeed;
        this.avatar_newsfeed = avatar_newsfeed;
        this.username_newsfeed = username_newsfeed;
        this.status_newsfeed = status_newsfeed;
        this.image_newsfeed = image_newsfeed;
        this.roadname_newsfeed = roadname_newsfeed;
        this.adminarea_newsfeed = adminarea_newsfeed;
        this.subadminarea_newsfeed = subadminarea_newsfeed;
        this.issue_newsfeed = issue_newsfeed;
        this.like_newsfeed = like_newsfeed;
    }

    public int getLike_newsfeed() {
        return like_newsfeed;
    }

    public void setLike_newsfeed(int like_newsfeed) {
        this.like_newsfeed = like_newsfeed;
    }

    public String getRoadname_newsfeed() {
        return roadname_newsfeed;
    }

    public void setRoadname_newsfeed(String roadname_newsfeed) {
        this.roadname_newsfeed = roadname_newsfeed;
    }

    public String getAdminarea_newsfeed() {
        return adminarea_newsfeed;
    }

    public void setAdminarea_newsfeed(String adminarea_newsfeed) {
        this.adminarea_newsfeed = adminarea_newsfeed;
    }

    public String getSubadminarea_newsfeed() {
        return subadminarea_newsfeed;
    }

    public void setSubadminarea_newsfeed(String subadminarea_newsfeed) {
        this.subadminarea_newsfeed = subadminarea_newsfeed;
    }

    public String getIssue_newsfeed() {
        return issue_newsfeed;
    }

    public void setIssue_newsfeed(String issue_newsfeed) {
        this.issue_newsfeed = issue_newsfeed;
    }

    public int getId_newsfeed() {
        return id_newsfeed;
    }

    public void setId_newsfeed(int id_newsfeed) {
        this.id_newsfeed = id_newsfeed;
    }

    public String getAvatar_newsfeed() {
        return avatar_newsfeed;
    }

    public void setAvatar_newsfeed(String avatar_newsfeed) {
        this.avatar_newsfeed = avatar_newsfeed;
    }

    public String getUsername_newsfeed() {
        return username_newsfeed;
    }

    public void setUsername_newsfeed(String username_newsfeed) {
        this.username_newsfeed = username_newsfeed;
    }

    public String getStatus_newsfeed() {
        return status_newsfeed;
    }

    public void setStatus_newsfeed(String status_newsfeed) {
        this.status_newsfeed = status_newsfeed;
    }

    public String getImage_newsfeed() {
        return image_newsfeed;
    }

    public void setImage_newsfeed(String image_newsfeed) {
        this.image_newsfeed = image_newsfeed;
    }
}
