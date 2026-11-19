package com.mycompany.quickchatapp;

import javax.swing.JOptionPane;

public class QuickChatApp {

    public static void main(String[] args) {
        MessageManager manager = new MessageManager();
        manager.loadStoredMessagesFromJSON(); // Load previously stored JSON messages on startup

        String currentSender = "Developer"; // Replace with logged-in user's username/number from Part 1
        int messageCounter = 0;

        boolean running = true;
        while (running) {
            String choice = JOptionPane.showInputDialog(null,
                    "Welcome to QuickChat.\n\n"
                    + "1) Send Messages\n"
                    + "2) Show Recently Sent Messages\n"
                    + "3) Quit\n"
                    + "4) Stored Messages",
                    "QuickChat Main Menu", JOptionPane.QUESTION_MESSAGE);

            if (choice == null || choice.equals("3")) {
                running = false;
                break;
            }

            switch (choice.trim()) {
                case "1":
                    String numStr = JOptionPane.showInputDialog("How many messages do you wish to enter?");
                    if (numStr == null) break;
                    int count = Integer.parseInt(numStr.trim());

                    for (int i = 0; i < count; i++) {
                        messageCounter++;
                        String recipient = JOptionPane.showInputDialog("Enter recipient cell number (e.g., +27834557896):");
                        String content = JOptionPane.showInputDialog("Enter message (max 250 characters):");

                        if (content != null && content.length() > 250) {
                            JOptionPane.showMessageDialog(null, "Message exceeds 250 characters by " + (content.length() - 250) + ", please reduce size.");
                            i--;
                            messageCounter--;
                            continue;
                        }

                        String[] options = {"Send Message", "Disregard Message", "Store Message to send later"};
                        int action = JOptionPane.showOptionDialog(null, "Choose an action for this message:",
                                "Message Options", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                                null, options, options[0]);

                        String flag = (action == 0) ? "Sent" : (action == 1) ? "Disregard" : "Stored";
                        Message msg = new Message(currentSender, recipient, content, flag, messageCounter);
                        manager.addMessage(msg);

                        JOptionPane.showMessageDialog(null,
                                "MessageID: " + msg.getMessageID()
                                + "\nMessage Hash: " + msg.getMessageHash()
                                + "\nRecipient: " + msg.getRecipient()
                                + "\nMessage: " + msg.getContent());
                    }
                    break;

                case "2":
                    JOptionPane.showMessageDialog(null, manager.displaySentMessagesReport());
                    break;

                case "4":
                    handleStoredMessagesMenu(manager);
                    break;

                default:
                    JOptionPane.showMessageDialog(null, "Invalid option. Please select 1 - 4.");
            }
        }
    }

    private static void handleStoredMessagesMenu(MessageManager manager) {
        boolean inSubMenu = true;
        while (inSubMenu) {
            String subChoice = JOptionPane.showInputDialog(null,
                    "Stored Messages & Reports Menu:\n\n"
                    + "a) Display sender and recipient of all stored messages\n"
                    + "b) Display the longest stored/sent message\n"
                    + "c) Search for a message ID\n"
                    + "d) Search all messages for a particular recipient\n"
                    + "e) Delete a message using the message hash\n"
                    + "f) Display full Sent Messages report\n"
                    + "x) Back to Main Menu",
                    "Stored Messages", JOptionPane.QUESTION_MESSAGE);

            if (subChoice == null || subChoice.equalsIgnoreCase("x")) {
                inSubMenu = false;
                break;
            }

            switch (subChoice.trim().toLowerCase()) {
                case "a":
                    JOptionPane.showMessageDialog(null, manager.displayStoredSendersAndRecipients());
                    break;
                case "b":
                    JOptionPane.showMessageDialog(null, "Longest Message:\n" + manager.getLongestMessage());
                    break;
                case "c":
                    String searchId = JOptionPane.showInputDialog("Enter Message ID to search:");
                    if (searchId != null) {
                        JOptionPane.showMessageDialog(null, manager.searchByMessageID(searchId.trim()));
                    }
                    break;
                case "d":
                    String searchRec = JOptionPane.showInputDialog("Enter Recipient number (e.g., +27838884567):");
                    if (searchRec != null) {
                        JOptionPane.showMessageDialog(null, manager.searchByRecipient(searchRec.trim()));
                    }
                    break;
                case "e":
                    String hashToDelete = JOptionPane.showInputDialog("Enter Message Hash to delete:");
                    if (hashToDelete != null) {
                        JOptionPane.showMessageDialog(null, manager.deleteByMessageHash(hashToDelete.trim()));
                    }
                    break;
                case "f":
                    JOptionPane.showMessageDialog(null, manager.displaySentMessagesReport());
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid selection. Enter a - f or x.");
            }
        }
    }
}