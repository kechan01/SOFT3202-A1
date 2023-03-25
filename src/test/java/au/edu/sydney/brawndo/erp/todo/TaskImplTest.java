package au.edu.sydney.brawndo.erp.todo;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class TaskImplTest {

    private TaskImpl task;

    @BeforeEach
    public void setUp() {
        task = new TaskImpl(0, null, null, null);
    }


    @Test
    public void testGetID() {
        assertEquals(0, task.getID());

        TaskImpl task1 = new TaskImpl(1, null, null, null);
        assertEquals(1, task1.getID());

        TaskImpl task2 = new TaskImpl(-1, null, null, null);
        assertEquals(-1, task2.getID());

        TaskImpl task3 = new TaskImpl(10000, null, null, null);
        assertEquals(10000, task3.getID());
    }

    @Test
    public void setGetDateTime() {
        LocalDateTime dateTime = LocalDateTime.now();
        task.setDateTime(dateTime);
        assertEquals(dateTime, task.getDateTime());
    }

    @Test
    public void dateTimeNull() {
        assertThrows(IllegalArgumentException.class, () -> task.setDateTime(null));
        assertNull(task.getDateTime());
    }


    @Test
    public void setGetLocationNotEmpty() {
        task.setLocation("Home");
        assertEquals("Home", task.getLocation());
    }

    @Test
    public void setGetLocationEmptyNull() {
        assertThrows(IllegalArgumentException.class, () -> task.setLocation(null));
        assertNull(task.getLocation());
        assertThrows(IllegalArgumentException.class, () -> task.setLocation(""));
        assertNull(task.getLocation());
        assertThrows(IllegalArgumentException.class, () -> task.setLocation(" "));
        assertNull(task.getLocation());
    }

    @Test
    public void setGetLocationTooLong() {
        String location = new String(new char[257]).replace('\0', 'C');

        assertThrows(IllegalArgumentException.class, () -> task.setLocation(location));
        assertNull(task.getLocation());
    }


    @Test
    public void setGetLocationMaxLength() {
        String location = new String(new char[256]).replace('\0', 'C');
        task.setLocation(location);

        assertEquals(location, task.getLocation());
    }


    @Test
    public void getDescriptionNUll() {
        assertNull(task.getDescription());
    }
    @Test
    public void setGetDescription() {
        String description = "I have three cats";
        task.setDescription(description);

        assertEquals(description, task.getDescription());
    }

    @Test
    public void isCompleted() {
        assertFalse(task.isCompleted());
        task.complete();
        assertTrue(task.isCompleted());
        assertThrows(IllegalStateException.class, () -> task.complete());
    }

    @Test
    public void getField() {
        String location = "Indoor";
        String description = "Cat";
        task.setLocation(location);
        task.setDescription(description);

        assertEquals(description, task.getField(Task.Field.DESCRIPTION));
        assertEquals(location, task.getField(Task.Field.LOCATION));
    }

    @Test
    public void getFieldNull() {
        assertThrows(IllegalArgumentException.class, () -> task.getField(null));
    }

}