package de.ostfalia.jee.ss16.api.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;

/**
 * Created by Henrik on 6/7/2016.
 */
@Entity
public class Lecture {
    @Id
    @GeneratedValue
    private int id;
    private String time;
    private String day;
    private String subject;
    private String tutor;
    private String room;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTutor() {
        return tutor;
    }

    public void setTutor(String tutor) {
        this.tutor = tutor;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    @Override
    public String toString() {
        return "Lecture [day=" + day + ", time=" + time + ", subject=" + subject + ", tutor=" + tutor + ", room=" + room + "]";
    }
}
