package itp341.lew.timothy.finalproject.model;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by timothylew on 4/8/17.
 */

// this shouldnt hold EVERY SINGLE USER.  This might just hold ALL THE TUTORS AVAILABLE FOR ONE USER!

public class RelevantUserSingleton {

    private static RelevantUserSingleton singleton;
    private User currentUser;
    private ArrayList<User> users;
    private ArrayList<User> messagingUsers;
    private ArrayList<String> relevantUserID;
    private ArrayList<String> messagingUserID;
    private Context context;

    // getInstance method will create a new Singleton for us if it doesn't exist.
    // otherwise, it will return the instance of the Singleton.
    public static RelevantUserSingleton getInstance(Context c) {
        if(singleton == null) {
            singleton = new RelevantUserSingleton(c.getApplicationContext());
            System.out.println("Creating new singleton");
        }
        return singleton;
    }

    private RelevantUserSingleton() {
        // Required empty constructor to prevent crashes.
    }

    // Constructor that initializes data members.
    private RelevantUserSingleton(Context c) {
        users = new ArrayList<>();
        relevantUserID = new ArrayList<>();
        messagingUserID = new ArrayList<>();
        messagingUsers = new ArrayList<>();
        currentUser = new User();
        context = c;
    }

    // Setter for current user
    public void setCurrentUser(User user) {
        currentUser = user;
    }

    // Getter for current user
    public User getCurrentUser() {
        return currentUser;
    }

    // Getter for relevant user array
    public ArrayList<User> getRelevantUserArray() {
        return users;
    }

    // Adds a relevant user to the relevant user array.
    public void addRelevantUser(User user) {
        users.add(user);
        relevantUserID.add(user.getUid());
    }

    // Checks if the user already exists in the singleton.
    public boolean relevantUserExists(User user) {
        return relevantUserID.contains(user.getUid());
}

    // Clears all data from the singleton.
    public void clear() {
        users.clear();
        messagingUsers.clear();
        currentUser = new User();
        relevantUserID.clear();
        messagingUserID.clear();
    }

    // Getter for messaging users
    public ArrayList<User> getMessagingUsers() {
        return messagingUsers;
    }

    // Adds a messaging user to the messagingUser array
    public void addMessagingUser(User user) {
        messagingUsers.add(user);
        messagingUserID.add(user.getUid());
    }

    // Checks if a messaging user exists.
    public boolean messagingUserExists(User user) {
        return messagingUserID.contains(user.getUid());
    }
}
