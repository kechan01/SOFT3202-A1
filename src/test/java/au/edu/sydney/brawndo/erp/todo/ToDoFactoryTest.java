package au.edu.sydney.brawndo.erp.todo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ToDoFactoryTest {

    @Test
    public void makeToDOList() {
        assertNotNull(new ToDoFactory().makeToDoList());
    }
}