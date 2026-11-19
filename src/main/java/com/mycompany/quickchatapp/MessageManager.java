package com.mycompany.quickchatapp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONObject;

public class MessageManager {

    private static final int MAX_CAPACITY = 100;
    private static final String JSON_FILE_PATH = "stored_messages.json";

    // Required Part 3 Arrays
    public String[] sentMessages = new String[MAX_CAPACITY];
    public String[] disregardedMessages = new String[MAX_CAPACITY];
    public String[] storedMessages = new String[MAX_CAPACITY];
    public String[] messageHashes = new String[MAX_CAPACITY];
    public String[] messageIDs = new String[MAX_CAPACITY];

    // Supporting parallel arrays for sender/recipient & unified lookup
    public String[] allSenders = new String[MAX_CAPACITY];
    public String[] allRecipients = new String[MAX_CAPACITY];
    public String[] allContents = new String[MAX_CAPACITY];
    public String[] allFlags = new String[MAX_CAPACITY];

    public String[] storedSenders = new String[MAX_CAPACITY];
    public String[] storedRecipients = new String[MAX_CAPACITY];

    public int sentCount = 0;
    public int disregardCount = 0;
    public int storedCount = 0;
    public int totalCount = 0;

    /**
     * Dynamically populates arrays based on message flag (no hard-coding).
     */
    public void addMessage(Message msg) {
        if (totalCount >= MAX_CAPACITY) return;

        // Populate master tracking arrays
        messageIDs[totalCount] = msg.getMessageID();
        messageHashes[totalCount] = msg.getMessageHash();
        allSenders[totalCount] = msg.getSender();
        allRecipients[totalCount] = msg.getRecipient();
        allContents[totalCount] = msg.getContent();
        allFlags[totalCount] = msg.getFlag();
        totalCount++;

        // Route into specific flag array
        if ("Sent".equalsIgnoreCase(msg.getFlag())) {
            sentMessages[sentCount++] = msg.getContent();
        } else if ("Disregard".equalsIgnoreCase(msg.getFlag()) || "Disregarded".equalsIgnoreCase(msg.getFlag())) {
            disregardedMessages[disregardCount++] = msg.getContent();
        } else if ("Stored".equalsIgnoreCase(msg.getFlag())) {
            storedMessages[storedCount] = msg.getContent();
            storedSenders[storedCount] = msg.getSender();
            storedRecipients[storedCount] = msg.getRecipient();
            storedCount++;
            saveStoredMessagesToJSON();
        }
    }

    /**
     * Writes all stored messages to stored_messages.json using org.json.
     */
    public void saveStoredMessagesToJSON() {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < totalCount; i++) {
            if ("Stored".equalsIgnoreCase(allFlags[i])) {
                JSONObject obj = new JSONObject();
                obj.put("messageID", messageIDs[i]);
                obj.put("messageHash", messageHashes[i]);
                obj.put("sender", allSenders[i]);
                obj.put("recipient", allRecipients[i]);
                obj.put("message", allContents[i]);
                obj.put("flag", allFlags[i]);
                jsonArray.put(obj);
            }
        }
        try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
            writer.write(jsonArray.toString(4));
        } catch (IOException e) {
            System.out.println("Error saving JSON: " + e.getMessage());
        }
    }

    /**
     * Reads stored_messages.json into the storedMessages array.
     */
    public void loadStoredMessagesFromJSON() {
        File file = new File(JSON_FILE_PATH);
        if (!file.exists()) return;

        try {
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            JSONArray jsonArray = new JSONArray(content);

            // Reset stored arrays before loading from JSON
            storedMessages = new String[MAX_CAPACITY];
            storedSenders = new String[MAX_CAPACITY];
            storedRecipients = new String[MAX_CAPACITY];
            storedCount = 0;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                storedMessages[storedCount] = obj.getString("message");
                storedSenders[storedCount] = obj.optString("sender", "Developer");
                storedRecipients[storedCount] = obj.getString("recipient");
                storedCount++;
            }
        } catch (Exception e) {
            System.out.println("Error reading JSON: " + e.getMessage());
        }
    }

    // 2a. Display the sender and recipient of all stored messages
    public String displayStoredSendersAndRecipients() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < storedCount; i++) {
            sb.append("Sender: ").append(storedSenders[i])
              .append(" | Recipient: ").append(storedRecipients[i])
              .append("\n");
        }
        return sb.length() > 0 ? sb.toString().trim() : "No stored messages found.";
    }

    // 2b. Display the longest message (checks across active messages)
    public String getLongestMessage() {
        String longest = "";
        for (int i = 0; i < totalCount; i++) {
            if (allContents[i] != null && allContents[i].length() > longest.length()) {
                longest = allContents[i];
            }
        }
        return longest;
    }

    // 2c. Search for a message ID and display the corresponding recipient and message
    public String searchByMessageID(String searchID) {
        for (int i = 0; i < totalCount; i++) {
            if (messageIDs[i] != null && messageIDs[i].equals(searchID)) {
                return "Recipient: " + allRecipients[i] + "\nMessage: " + allContents[i];
            }
        }
        return "Message ID not found.";
    }

    // 2d. Search for all messages sent or stored for a particular recipient
    public String searchByRecipient(String recipientNumber) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < totalCount; i++) {
            if (allRecipients[i] != null && allRecipients[i].equals(recipientNumber)) {
                if ("Sent".equalsIgnoreCase(allFlags[i]) || "Stored".equalsIgnoreCase(allFlags[i])) {
                    if (sb.length() > 0) sb.append("\n");
                    sb.append(allContents[i]);
                }
            }
        }
        return sb.length() > 0 ? sb.toString() : "No messages found for recipient: " + recipientNumber;
    }

    // 2e. Delete a message using the message hash
    public String deleteByMessageHash(String hash) {
        for (int i = 0; i < totalCount; i++) {
            if (messageHashes[i] != null && messageHashes[i].equalsIgnoreCase(hash)) {
                String deletedContent = allContents[i];
                String deletedFlag = allFlags[i];

                // Shift master arrays left
                for (int j = i; j < totalCount - 1; j++) {
                    messageIDs[j] = messageIDs[j + 1];
                    messageHashes[j] = messageHashes[j + 1];
                    allSenders[j] = allSenders[j + 1];
                    allRecipients[j] = allRecipients[j + 1];
                    allContents[j] = allContents[j + 1];
                    allFlags[j] = allFlags[j + 1];
                }
                totalCount--;
                messageIDs[totalCount] = null;
                messageHashes[totalCount] = null;
                allContents[totalCount] = null;

                // Rebuild specific flag arrays & update JSON
                rebuildFlagArrays();
                if ("Stored".equalsIgnoreCase(deletedFlag)) {
                    saveStoredMessagesToJSON();
                }

                return "Message \"" + deletedContent + "\" successfully deleted.";
            }
        }
        return "Message hash not found.";
    }

    private void rebuildFlagArrays() {
        sentCount = 0;
        disregardCount = 0;
        storedCount = 0;
        for (int i = 0; i < totalCount; i++) {
            if ("Sent".equalsIgnoreCase(allFlags[i])) {
                sentMessages[sentCount++] = allContents[i];
            } else if ("Disregard".equalsIgnoreCase(allFlags[i]) || "Disregarded".equalsIgnoreCase(allFlags[i])) {
                disregardedMessages[disregardCount++] = allContents[i];
            } else if ("Stored".equalsIgnoreCase(allFlags[i])) {
                storedMessages[storedCount] = allContents[i];
                storedSenders[storedCount] = allSenders[i];
                storedRecipients[storedCount] = allRecipients[i];
                storedCount++;
            }
        }
    }

    // 2f. Display a report that lists the full details of all sent messages
    public String displaySentMessagesReport() {
        StringBuilder sb = new StringBuilder("=== SENT MESSAGES REPORT ===\n");
        boolean found = false;
        for (int i = 0; i < totalCount; i++) {
            if ("Sent".equalsIgnoreCase(allFlags[i])) {
                found = true;
                sb.append("Message Hash: ").append(messageHashes[i]).append("\n")
                  .append("Recipient: ").append(allRecipients[i]).append("\n")
                  .append("Message: ").append(allContents[i]).append("\n")
                  .append("----------------------------\n");
            }
        }
        return found ? sb.toString().trim() : "No sent messages to display.";
    }
}