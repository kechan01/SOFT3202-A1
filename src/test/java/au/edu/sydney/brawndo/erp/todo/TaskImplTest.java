package au.edu.sydney.brawndo.erp.todo;

import net.jqwik.api.*;
import net.jqwik.api.constraints.NotBlank;
import net.jqwik.api.constraints.Positive;
import net.jqwik.api.constraints.StringLength;
import net.jqwik.api.lifecycle.BeforeTry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class TaskImplTest {

    private TaskImpl task;

    @BeforeEach
    @BeforeTry
    public void setUp() {
        task = new TaskImpl(0, null, null, null);
    }


    @Property
    public void testGetID(@ForAll @Positive int id) {
        TaskImpl newTask = new TaskImpl(id, null, null, null);
        assertEquals(id, newTask.getID());
    }

    @Property
    public void setGetDateTime(@ForAll LocalDateTime dateTimes) {
        task.setDateTime(dateTimes);
        assertEquals(dateTimes, task.getDateTime());
    }

    @Property
    public void dateTimeNull() {
        assertThrows(IllegalArgumentException.class, () -> task.setDateTime(null));
        assertNull(task.getDateTime());
    }


    @Property
    public void setGetLocationNotEmpty(@ForAll @NotBlank String location) {
        task.setLocation(location);
        assertEquals(location, task.getLocation());
    }

    @Property
    public void setGetLocationEmptyNull() {
        assertThrows(IllegalArgumentException.class, () -> task.setLocation(null));
        assertNull(task.getLocation());
        assertThrows(IllegalArgumentException.class, () -> task.setLocation(""));
        assertNull(task.getLocation());
        assertThrows(IllegalArgumentException.class, () -> task.setLocation(" "));
        assertNull(task.getLocation());
    }

    @Property
    public void setGetLocationTooLong(@ForAll @NotBlank @StringLength(min=257) String location) {
        assertThrows(IllegalArgumentException.class, () -> task.setLocation(location));
        assertNull(task.getLocation());
    }

    @Property
    public void setGetDescription(@ForAll String description) {
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

    @Property
    public void getField(@ForAll Task.Field field, @ForAll @NotBlank @StringLength(min=1, max=256) String location, @ForAll String description) {
        task.setLocation(location);
        task.setDescription(description);

        if (field == Task.Field.DESCRIPTION) {
            assertEquals(description, task.getField(field));
        }
        if (field == Task.Field.LOCATION) {
            assertEquals(location, task.getField(field));
        }
    }

    @Test
    public void getFieldNull() {
        assertThrows(IllegalArgumentException.class, () -> task.getField(null));
    }

}