package itp341.lew.timothy.finalproject.model;

import java.util.ArrayList;

/**
 * Created by timothylew on 4/8/17.
 */

public class User {

    private String name;
    private String email;
    private String uid;
    private String photoURL;
    private ArrayList<String> needHelp;
    private ArrayList<String> tutoringClasses;
    private double totalScore;
    private double numberOfRatings;

    // Constructor that takes in no parameters to prevent Firebase errors.
    public User() {
        this.name = "";
        this.email = "";
        this.uid = "";
        this.photoURL = "";
        this.needHelp = new ArrayList<>();
        this.tutoringClasses = new ArrayList<>();
        totalScore = 0;
        numberOfRatings = 0;
    }

    // Constructor with no initialized lists for classes needed for help and classes you are tutoring.
    public User(String name, String email, String uid, double totalScore, double numberOfRatings) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.photoURL = "";
        this.needHelp = new ArrayList<>();
        this.tutoringClasses = new ArrayList<>();
        this.totalScore = totalScore;
        this.numberOfRatings = numberOfRatings;
    }

    // Constructor initializing all 5 member variables to function input.
    public User(String name, String email, String uid, ArrayList<String> needHelp, ArrayList<String> tutoringClasses, double totalScore, double numberOfRatings) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.photoURL = "";
        this.needHelp = needHelp;
        this.tutoringClasses = tutoringClasses;
        this.totalScore = totalScore;
        this.numberOfRatings = numberOfRatings;
    }

    // To string method used to print out a User object for debugging purposes.
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", uid='" + uid + '\'' +
                ", photoURL='" + photoURL + '\'' +
                ", needHelp=" + needHelp +
                ", tutoringClasses=" + tutoringClasses +
                ", totalScore=" + totalScore +
                ", numberOfRatings=" + numberOfRatings +
                '}';
    }

    // Getters and setters for all class members

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public double getNumberOfRatings() {
        return numberOfRatings;
    }

    public void setNumberOfRatings(double numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<String> getNeedHelp() {
        return needHelp;
    }

    public void setNeedHelp(ArrayList<String> needHelp) {
        this.needHelp = needHelp;
    }

    public ArrayList<String> getTutoringClasses() {
        return tutoringClasses;
    }

    public void setTutoringClasses(ArrayList<String> tutoringClasses) {
        this.tutoringClasses = tutoringClasses;
    }

    public void addHelpClass(String id) {
        needHelp.add(id);
    }

    public void addTutorClass(String id) {
        tutoringClasses.add(id);
    }




}
