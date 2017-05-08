package itp341.lew.timothy.finalproject.model;

/**
 * Created by timothylew on 4/24/17.
 */

public class Chat {
    private String messageContents;
    private String timestamp;
    private String recipientName;
    private String senderName;
    private String recipientID;
    private String senderID;
    private boolean rated;

    public Chat() {
        // Empty constructor so Firebase doesn't crash.
    }

    // Constructor to initialize data members.
    public Chat(String messageContents, String timestamp, String recipientName, String senderName, String recipientID, String senderID) {
        this.messageContents = messageContents;
        this.recipientName = recipientName;
        this.timestamp = timestamp;
        this.senderName = senderName;
        this.recipientID = recipientID;
        this.senderID = senderID;
        rated = false;
    }

    // toString method to make the object into a String
    @Override
    public String toString() {
        return "Chat{" +
                "messageContents='" + messageContents + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", recipientName='" + recipientName + '\'' +
                ", senderName='" + senderName + '\'' +
                ", recipientID='" + recipientID + '\'' +
                ", senderID='" + senderID + '\'' +
                ", rated=" + rated +
                '}';
    }

    // Getters and setters for all class members:

    public String getMessageContents() {
        return messageContents;
    }

    public void setMessageContents(String messageContents) {
        this.messageContents = messageContents;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getRecipientID() {
        return recipientID;
    }

    public void setRecipientID(String recipientID) {
        this.recipientID = recipientID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public boolean isRated() {
        return rated;
    }

    public void setRated(boolean rated) {
        this.rated = rated;
    }
}
