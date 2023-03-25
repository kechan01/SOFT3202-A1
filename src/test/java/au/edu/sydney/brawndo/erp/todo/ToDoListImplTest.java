package au.edu.sydney.brawndo.erp.todo;

import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.NotBlank;
import net.jqwik.api.constraints.StringLength;
import net.jqwik.api.lifecycle.BeforeTry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;


public class ToDoListImplTest {

    private ToDoListImpl toDoList;
    private final LocalDateTime time = LocalDateTime.of(2023,3,23,12,30);
    int initialSize;
    @BeforeEach
    public void setup() {
        toDoList = new ToDoListImpl();
        initialSize = toDoList.findAll().size();
    }

    @Test
    public void addTaskManualId() {
        Task task = toDoList.add(6, LocalDateTime.now(), "old", "old");

        assertEquals(task.getID(), 6);
        assertEquals(initialSize + 1, toDoList.findAll().size());

        Task newTask = toDoList.add(8, LocalDateTime.now(), "new", "new");

        assertEquals(newTask.getID(), 8);
        assertEquals(initialSize + 2, toDoList.findAll().size());
    }

    @Test
    public void addTaskManualIdRepeat() {
        Task task = toDoList.add(1, LocalDateTime.now(), "old", "old");

        assertEquals(task.getID(), 1);

        assertThrows(IllegalArgumentException.class, () ->
                toDoList.add(1, LocalDateTime.now(), "new", "new"));

        assertEquals(initialSize + 1, toDoList.findAll().size());
    }

    @Test
    public void addTaskNullId() {
        Task task1 = toDoList.add(null, LocalDateTime.now(), "x", "x");

        assertEquals(1, task1.getID());
        assertEquals(initialSize + 1, toDoList.findAll().size());

        Task task2 = toDoList.add(null, LocalDateTime.now(), "x", "x");

        assertEquals(2, task2.getID());
        assertEquals(initialSize + 2, toDoList.findAll().size());

        Task task3 = toDoList.add(null, LocalDateTime.now(), "x", "x");

        assertEquals(3, task3.getID());
        assertEquals(initialSize + 3, toDoList.findAll().size());
    }

    @Test
    public void addTaskNullToManualToNullId() {
        Task task1 = toDoList.add(null, LocalDateTime.now(), "x", "x");

        assertEquals(1, task1.getID());
        assertEquals(initialSize + 1, toDoList.findAll().size());

        Task task2 = toDoList.add(8, LocalDateTime.now(), "x", "x");

        assertEquals(8, task2.getID());
        assertEquals(initialSize + 2, toDoList.findAll().size());

        assertThrows(IllegalStateException.class, () ->
                toDoList.add(null, LocalDateTime.now(), "x", "x"));

        assertEquals(initialSize + 2, toDoList.findAll().size());

        Task task3 = toDoList.add(3, LocalDateTime.now(), "x", "x");

        assertEquals(3, task3.getID());
        assertEquals(initialSize + 3, toDoList.findAll().size());
    }

    @Test
    public void addTaskTimeTest() {
        LocalDateTime dateTime = LocalDateTime.now();
        Task task = toDoList.add(1, dateTime, "f", "f");
        assertEquals(dateTime, task.getDateTime());
        assertEquals(initialSize + 1, toDoList.findAll().size());
    }


    @Test
    public void addTaskTimeNullTest() {
        assertThrows(IllegalArgumentException.class, () ->
                toDoList.add(1, null, "f", "f"));
        assertEquals(initialSize, toDoList.findAll().size());
    }

    @Test
    public void addTaskLocationNotEmpty() {
        Task task = toDoList.add(1, LocalDateTime.now(), "Home", "f");
        assertEquals("Home", task.getLocation());
        assertEquals(initialSize + 1, toDoList.findAll().size());
    }

    @Test
    public void addTaskLocationEmptyNull() {
        assertThrows(IllegalArgumentException.class, () ->
                toDoList.add(1, LocalDateTime.now(), null, "f"));
        assertEquals(0, toDoList.findAll().size());

        assertThrows(IllegalArgumentException.class, () ->
                toDoList.add(1, LocalDateTime.now(), "", "f"));
        assertEquals(0, toDoList.findAll().size());

        assertThrows(IllegalArgumentException.class, () ->
                toDoList.add(1, LocalDateTime.now(), " ", "f"));
        assertEquals(0, toDoList.findAll().size());
    }

    @Test
    public void addTaskLocationTooLong() {
        String location = new String(new char[257]).replace('\0', 'C');

        assertThrows(IllegalArgumentException.class, () ->
                toDoList.add(1, LocalDateTime.now(), location, "f"));
        assertEquals(initialSize, toDoList.findAll().size());
    }


    @Test
    public void addTaskLocationMaxLength() {
        String location = new String(new char[256]).replace('\0', 'C');
        Task t = toDoList.add(1, LocalDateTime.now(), location, "Cat");

        assertEquals(initialSize + 1, toDoList.findAll().size());
        assertEquals(t, toDoList.findOne(1) );
    }

    public int addingSimpleTasks() {
        toDoList.add(1, LocalDateTime.now(), "f", "f");
        toDoList.add(2, LocalDateTime.now(), "f", "f");
        toDoList.add(3, LocalDateTime.now(), "f", "f");
        return toDoList.findAll().size();
    }

    @Test
    public void removeIdExist() {
        int size = addingSimpleTasks();
        boolean success = toDoList.remove(1);
        assertTrue(success);
        assertEquals(size-1, toDoList.findAll().size());

        success = toDoList.remove(2);
        assertTrue(success);
        assertEquals(size-2, toDoList.findAll().size());
    }

    @Test
    public void removeIdNotExist() {
        int size = addingSimpleTasks();
        boolean success = toDoList.remove(10);
        assertFalse(success);
        assertEquals(size, toDoList.findAll().size());
    }

    @Test
    public void findOneExist() {
        addingSimpleTasks();
        Task task = toDoList.findOne(1);
        assertEquals(1, task.getID());
    }

    @Test
    public void findOneNotExist() {
        addingSimpleTasks();
        Task task = toDoList.findOne(10);
        assertNull(task);
    }

    public int addingRealisticTasks() {
        toDoList.add(1, time, "Jbhifi", "fa").complete();
        toDoList.add(2, time.plusWeeks(2), "Maccas", "f");
        toDoList.add(3, time.plusWeeks(4), "KFC", "fa");
        toDoList.add(4, time.plusWeeks(2), "Uni", "f").complete();
        toDoList.add(5, time.plusWeeks(4), "Home", "fa").complete();
        toDoList.add(6, time.plusWeeks(1), "Station", "f");
        return 6;
    }

    @Test
    public void findAllTasks() {
        int size = addingRealisticTasks();
        List<Task> tasks = toDoList.findAll();
        assertEquals(size, tasks.size());
    }

    @Test
    public void findAllCompletedAndNot() {
        addingRealisticTasks();
        List<Task> completedTasks = toDoList.findAll(true);
        assertEquals(3, completedTasks.size());

        for (Task t : completedTasks) {
            assertTrue(t.getLocation().matches("Jbhifi|Uni|Home"));
        }

        List<Task> notCompletedTasks = toDoList.findAll(false);
        assertEquals(3, notCompletedTasks.size());

        for (Task t : notCompletedTasks) {
            assertTrue(t.getLocation().matches("Maccas|KFC|Station"));
        }

    }


    @Test
    public void findAllValidTime1() {
        LocalDateTime from = LocalDateTime.of(
                2023, 3, 1, 12,30);
        LocalDateTime to = from.plusDays(20);
        addingRealisticTasks();
        List<Task> tasks = toDoList.findAll(from, to, false);
        for (Task task : tasks) {
            assertTrue(checkTimeRange(from, to, task));
            assertFalse(task.isCompleted());
        }

    }

    @Test
    public void findAllValidTime2() {
        LocalDateTime from = LocalDateTime.of(
                2023, 3, 1, 12,30);
        LocalDateTime to = from.plusDays(50);
        addingRealisticTasks();
        List<Task> tasks = toDoList.findAll(from, to, true);
        for (Task task : tasks) {
            assertTrue(checkTimeRange(from, to, task));
            assertTrue(task.isCompleted());
        }
    }

    @Test
    public void findAllValidTime3() {
        LocalDateTime from = LocalDateTime.of(
                2023, 3, 1, 12,30);
        LocalDateTime to = from.plusDays(100);
        addingRealisticTasks();
        List<Task> tasks = toDoList.findAll(from, to, false);
        for (Task task : tasks) {
            assertTrue(checkTimeRange(from, to, task));
            assertFalse(task.isCompleted());
        }
    }

    public boolean checkTimeRange(LocalDateTime from, LocalDateTime to, Task task) {
        return task.getDateTime().isAfter(from) && task.getDateTime().isBefore(to);
    }

    @Test
    public void findAllInValidTime() {
        LocalDateTime from = time;
        LocalDateTime to = from.minusDays(1);
        addingRealisticTasks();
        assertThrows(IllegalArgumentException.class, () ->
                toDoList.findAll(from, to, true));
        assertThrows(IllegalArgumentException.class, () ->
                toDoList.findAll(from, from, false));
    }


    public Map<Task.Field, String> testParams1() {
        Map<Task.Field, String> map = new HashMap<>();
        map.put(Task.Field.LOCATION, "Jbihifi");
        map.put(Task.Field.DESCRIPTION, "f");
        map.put(Task.Field.LOCATION, "Uni");
        return map;
    }

    @Test
    public void findAllValidParamAndSearchFalse() {
        boolean andSearch = false;
        boolean completed = true;
        LocalDateTime from = LocalDateTime.of(
                2023, 3, 1, 12, 30);
        LocalDateTime to = from.plusWeeks(7);
        addingRealisticTasks();
        Map<Task.Field, String> testParams = testParams1();
        List<Task> tasks = toDoList.findAll(testParams, from, to, completed, andSearch);

        for (Task task : tasks) {
            assertTrue(checkTimeRange(from, to, task));
            if (andSearch) {
                assertTrue(checkAllParam(task, testParams));
            } else {
                assertTrue(checkParam(task, testParams));
            }
            assertEquals(task.isCompleted(), completed);
        }
    }

    @Test
    public void findAllValidParamAndSearchTrue() {
        boolean andSearch = true;
        boolean completed = false;
        LocalDateTime from = LocalDateTime.of(
                2023, 3, 1, 12,30);
        LocalDateTime to = from.plusWeeks(8);
        addingRealisticTasks();
        Map<Task.Field, String> testParams = testParams1();
        List<Task> tasks = toDoList.findAll(testParams, from, to, completed, andSearch);

        for (Task task : tasks) {
            assertTrue(checkTimeRange(from, to, task));
            if (andSearch) {
                assertTrue(checkAllParam(task, testParams));
            } else {
                assertTrue(checkParam(task, testParams));
            }
            assertEquals(task.isCompleted(), completed);
        }

    }

    public boolean checkParam(Task task, Map<Task.Field, String> testParams) {
        for (Map.Entry<Task.Field, String> param : testParams.entrySet()) {
            if (param.getKey().equals(Task.Field.LOCATION)) {
                if (task.getLocation().contains(param.getValue())){
                    return true;
                }
            } else if (param.getKey().equals(Task.Field.DESCRIPTION)){
                if (task.getDescription().contains(param.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkAllParam(Task task, Map<Task.Field, String> testParams) {
        for (Map.Entry<Task.Field, String> param : testParams.entrySet()) {
            if (param.getKey().equals(Task.Field.LOCATION)) {
                if (!task.getLocation().contains(param.getValue())){
                    return false;
                }
            } else {
                if (!task.getDescription().contains(param.getValue())) {
                    return false;
                }
            }
        }
        return true;
    }

    public Map<Task.Field, String> testNullParams(Task.Field field, String str) {
        Map<Task.Field, String> map = new HashMap<>();
        map.put(field, str);
        return map;
    }

    @Test
    public void findAllInValidParam() {
        boolean andSearch = true;
        boolean completed = false;

        LocalDateTime from = LocalDateTime.of(
                2023, 3, 20, 12,30);
        LocalDateTime to = from.plusDays(100);
        addingRealisticTasks();
        Map<Task.Field, String> testParams = testNullParams(Task.Field.LOCATION, null);
        assertThrows(IllegalArgumentException.class, () ->
                toDoList.findAll(testParams, from, to, completed, andSearch));

        Map<Task.Field, String> testParams2 = testNullParams(null, null);
        assertThrows(IllegalArgumentException.class, () ->
                toDoList.findAll(testParams2, from, to, completed, andSearch));

        Map<Task.Field, String> testParams3 = testNullParams(null, "f");
        assertThrows(IllegalArgumentException.class, () ->
                toDoList.findAll(testParams3, from, to, completed, andSearch));

    }

    @Test
    public void findAllParamInvalidTime() {
        boolean andSearch = false;
        boolean completed = false;

        LocalDateTime from = time;
        LocalDateTime to = from.minusDays(1);
        addingRealisticTasks();
        Map<Task.Field, String> testParams = testParams1();
        assertThrows(IllegalArgumentException.class, () ->
                toDoList.findAll(testParams, from, to, completed, andSearch));

    }

    @Test
    public void clearTest() {
        addingRealisticTasks();
        toDoList.clear();

        assertTrue(toDoList.findAll().isEmpty());
    }

}
