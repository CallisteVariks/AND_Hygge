package com.example.apim.hygge.data;

public class Post {
    private String id;
    private String name;
    private String title;
    private String description;

    private String year;
    private String month;
    private String day;
    private String hourOfDay;
    private String minute;

    private String placeId;
    private String placeName;
    private String placeAddress;

    private String photoUrl;
    private String userId;

    public Post() {} // for Firebase

    public Post(String id, String name, String title, String description, String year, String month,
                String day, String hourOfDay, String minute, String placeId, String placeName,
                String placeAddress, String photoUrl, String userId) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.description = description;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.photoUrl = photoUrl;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(String hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
