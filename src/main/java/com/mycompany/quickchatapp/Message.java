package com.mycompany.quickchatapp;

import java.util.Random;

public class Message {
    private String messageID;
    private int messageNumber;
    private String sender;
    private String recipient;
    private String content;
    private String flag; // "Sent", "Stored", or "Disregard"
    private String messageHash;

    public Message(String sender, String recipient, String content, String flag, int messageNumber) {
        this.messageID = generateMessageID();
        this.messageNumber = messageNumber;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.flag = flag;
        this.messageHash = createMessageHash();
    }

    // Constructor for loading existing/test data with a known ID
    public Message(String messageID, String sender, String recipient, String content, String flag, int messageNumber) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.flag = flag;
        this.messageHash = createMessageHash();
    }

    private String generateMessageID() {
        Random rand = new Random();
        long num = 1000000000L + (long) (rand.nextDouble() * 9000000000L);
        return String.valueOf(num);
    }

    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    public int checkRecipientCell() {
        if (recipient != null && recipient.startsWith("+") && recipient.length() <= 13 && recipient.length() >= 11) {
            return 1;
        }
        return 0;
    }

    public String createMessageHash() {
        String idPrefix = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;
        String cleanText = content.replaceAll("[^a-zA-Z0-9\\s]", "").trim();
        String[] words = cleanText.split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord;

        return (idPrefix + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
    }

    // Getters & Setters
    public String getMessageID() { return messageID; }
    public int getMessageNumber() { return messageNumber; }
    public String getSender() { return sender; }
    public String getRecipient() { return recipient; }
    public String getContent() { return content; }
    public String getFlag() { return flag; }
    public String getMessageHash() { return messageHash; }
}