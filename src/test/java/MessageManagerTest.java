package com.mycompany.quickchatapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessageManagerTest {

    private MessageManager manager;
    private Message msg1, msg2, msg3, msg4, msg5;

    @BeforeEach
    public void setUp() {
        manager = new MessageManager();

        // Test Data Message 1 (Sent)[span_4](start_span)[span_4](end_span)
        msg1 = new Message("1000000001", "Developer", "+27834557896",
                "Did you get the cake?", "Sent", 1);

        // Test Data Message 2 (Stored)[span_5](start_span)[span_5](end_span)
        msg2 = new Message("1000000002", "Developer", "+27838884567",
                "Where are you? You are late! I have asked you to be on time.", "Stored", 2);

        // Test Data Message 3 (Disregard)[span_6](start_span)[span_6](end_span)
        msg3 = new Message("1000000003", "Developer", "+27834484567",
                "Yohoooo, I am at your gate.", "Disregard", 3);

        // Test Data Message 4 (Sent - Developer 0838884567)[span_7](start_span)[span_7](end_span)
        msg4 = new Message("0838884567", "Developer", "0838884567",
                "It is dinner time !", "Sent", 4);

        // Test Data Message 5 (Stored)[span_8](start_span)[span_8](end_span)
        msg5 = new Message("1000000005", "Developer", "+27838884567",
                "Ok, I am leaving without you.", "Stored", 5);

        manager.addMessage(msg1);
        manager.addMessage(msg2);
        manager.addMessage(msg3);
        manager.addMessage(msg4);
        manager.addMessage(msg5);
    }

    @Test
    public void testSentMessagesArrayPopulated() {
        assertEquals(2, manager.sentCount);
        assertEquals("Did you get the cake?", manager.sentMessages[0]);
        assertEquals("It is dinner time !", manager.sentMessages[1]);
    }

    @Test
    public void testDisplayLongestMessage() {
        String expected = "Where are you? You are late! I have asked you to be on time.";
        assertEquals(expected, manager.getLongestMessage());
    }

    @Test
    public void testSearchForMessageID() {
        String result = manager.searchByMessageID("0838884567");
        assertTrue(result.contains("It is dinner time !"));
    }

    @Test
    public void testSearchAllMessagesForRecipient() {
        String expected = "Where are you? You are late! I have asked you to be on time.\nOk, I am leaving without you.";
        assertEquals(expected, manager.searchByRecipient("+27838884567"));
    }

    @Test
    public void testDeleteMessageUsingHash() {
        String hashToDelete = msg2.getMessageHash();
        String expectedResponse = "Message \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.";
        assertEquals(expectedResponse, manager.deleteByMessageHash(hashToDelete));
    }

    @Test
    public void testDisplayReport() {
        String report = manager.displaySentMessagesReport();
        assertTrue(report.contains(msg1.getMessageHash()));
        assertTrue(report.contains("+27834557896"));
        assertTrue(report.contains("Did you get the cake?"));
        assertTrue(report.contains(msg4.getMessageHash()));
        assertTrue(report.contains("It is dinner time !"));
    }
}