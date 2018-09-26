package msu.roadsafety.Model;

import java.io.Serializable;

public class Image implements Serializable {
    private String address, road_name, sub_admin_area, admin_area, country, issue, url;

    public Image() {
    }

    public Image(String address, String road_name, String sub_admin_area, String admin_area, String country, String issue, String url) {
        this.address = address;
        this.road_name = road_name;
        this.sub_admin_area = sub_admin_area;
        this.admin_area = admin_area;
        this.country = country;
        this.issue = issue;
        this.url = url;
    }

    public Image(String imageURL) {
        this.url = imageURL;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRoad_name() {
        return road_name;
    }

    public void setRoad_name(String road_name) {
        this.road_name = road_name;
    }

    public String getSub_admin_area() {
        return sub_admin_area;
    }

    public void setSub_admin_area(String sub_admin_area) {
        this.sub_admin_area = sub_admin_area;
    }

    public String getAdmin_area() {
        return admin_area;
    }

    public void setAdmin_area(String admin_area) {
        this.admin_area = admin_area;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
