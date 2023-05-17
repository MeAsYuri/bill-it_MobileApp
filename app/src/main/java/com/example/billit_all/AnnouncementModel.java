package com.example.billit_all;

public class AnnouncementModel {

    String announcementTitle, announcementSubject;

    public AnnouncementModel(String announcementTitle, String announcementSubject) {
        this.announcementTitle = announcementTitle;
        this.announcementSubject = announcementSubject;
    }

    public String getAnnouncementTitle() {
        return announcementTitle;
    }

    public void setAnnouncementTitle(String announcementTitle) {
        this.announcementTitle = announcementTitle;
    }

    public String getAnnouncementSubject() {
        return announcementSubject;
    }

    public void setAnnouncementSubject(String announcementSubject) {
        this.announcementSubject = announcementSubject;
    }
}