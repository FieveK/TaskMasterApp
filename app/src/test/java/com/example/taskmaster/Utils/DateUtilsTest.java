package com.example.taskmaster.Utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class DateUtilsTest {

    @Test
    public void testGetTodayDate() {
        String actual = DateUtils.getTodayDate();

        // Format tanggal: yyyy-MM-dd
        assertNotNull(actual);
        assertTrue(actual.matches("\\d{4}-\\d{2}-\\d{2}"));
    }
}
