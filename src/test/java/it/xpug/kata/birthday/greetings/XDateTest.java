package it.xpug.kata.birthday.greetings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


public class XDateTest {

    @Test
    public void getters() throws Exception {
        XDate date = new XDate("1789/01/24");
        assertEquals(1, date.getMonth());
        assertEquals(24, date.getDay());
    }

    @Test
    public void isSameDate() throws Exception {
        XDate date = new XDate("1789/01/24");
        XDate sameDay = new XDate("2001/01/24");
        XDate notSameDay = new XDate("1789/01/25");
        XDate notSameMonth = new XDate("1789/02/25");

        assertTrue(date.isSameDay(sameDay), "same");
        assertFalse(date.isSameDay(notSameDay), "not same day");
        assertFalse(date.isSameDay(notSameMonth), "not same month");
    }

    @Test
    public void equality() throws Exception {
        XDate base = new XDate("2000/01/02");
        XDate same = new XDate("2000/01/02");
        XDate different = new XDate("2000/01/04");

        assertNotEquals(null, base);
        assertNotEquals("", base);
        assertEquals(base, base);
        assertEquals(base, same);
        assertNotEquals(base, different);
    }

}
