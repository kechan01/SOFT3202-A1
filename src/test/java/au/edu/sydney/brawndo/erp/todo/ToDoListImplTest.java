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

    @BeforeEach
    @BeforeTry
    public void setup() {
        toDoList = new ToDoListImpl();
    }

    @Property
    public void addTaskManualId(@ForAll int id) {
        Task task = toDoList.add(id, LocalDateTime.now(), "old", "old");
        assertEquals(task.getID(), id);
        assertEquals(1, toDoList.findAll().size());
        Task newTask = toDoList.add(id + 1, LocalDateTime.now(), "new", "new");
        assertEquals(newTask.getID(), id + 1);
        assertEquals(2, toDoList.findAll().size());
    }

    @Property
    public void addTaskManualIdRepeat(@ForAll int id) {
        Task task = toDoList.add(id, LocalDateTime.now(), "old", "old");
        assertEquals(task.getID(), id);
        assertThrows(IllegalArgumentException.class, () ->
                toDoList.add(id, LocalDateTime.now(), "new", "new"));
        assertEquals(1, toDoList.findAll().size());
    }

    @Test
    public void addTaskNullId() {
        Task task1 = toDoList.add(null, LocalDateTime.now(), "x", "x");
        assertEquals(1, task1.getID());
        assertEquals(1, toDoList.findAll().size());
        Task task2 = toDoList.add(null, LocalDateTime.now(), "x", "x");
        assertEquals(2, task2.getID());
        assertEquals(2, toDoList.findAll().size());
        Task task3 = toDoList.add(null, LocalDateTime.now(), "x", "x");
        assertEquals(3, task3.getID());
        assertEquals(3, toDoList.findAll().size());
    }

    @Test
    public void addTaskNullToManualToNullId() {
        Task task1 = toDoList.add(null, LocalDateTime.now(), "x", "x");
        assertEquals(1, task1.getID());
        assertEquals(1, toDoList.findAll().size());

        Task task2 = toDoList.add(8, LocalDateTime.now(), "x", "x");
        assertEquals(8, task2.getID());
        assertEquals(2, toDoList.findAll().size());

        assertThrows(IllegalStateException.class, () ->
                toDoList.add(null, LocalDateTime.now(), "x", "x"));
        assertEquals(2, toDoList.findAll().size());

        Task task3 = toDoList.add(3, LocalDateTime.now(), "x", "x");
        assertEquals(3, task3.getID());
        assertEquals(3, toDoList.findAll().size());
    }

    @Property
    public void addTaskTimeTest(@ForAll LocalDateTime dateTime) {
        Task task = toDoList.add(1, dateTime, "f", "f");
        assertEquals(dateTime, task.getDateTime());
        assertEquals(1, toDoList.findAll().size());
    }


    @Property
    public void addTaskTimeNullTest() {
        assertThrows(IllegalArgumentException.class, () ->
                toDoList.add(1, null, "f", "f"));
        assertEquals(0, toDoList.findAll().size());
    }

    @Property
    public void addTaskLocationNotEmpty(@ForAll @NotBlank String location) {
        Task task = toDoList.add(1, LocalDateTime.now(), location, "f");
        assertEquals(location, task.getLocation());
        assertEquals(1, toDoList.findAll().size());
    }

    @Property
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

    @Property
    public void addTaskLocationTooLong(@ForAll @NotBlank @StringLength(min=257) String location) {
        assertThrows(IllegalArgumentException.class, () ->
                toDoList.add(1, LocalDateTime.now(), location, "f"));
        assertEquals(0, toDoList.findAll().size());
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

    @Property
    public void findAllCompletedAndNot(@ForAll boolean completed) {
        addingRealisticTasks();
        List<Task> tasks = toDoList.findAll(completed);
        assertEquals(3, tasks.size());

        if (completed) {
            for (Task t : tasks) {
                assertTrue(t.getLocation().matches("Jbhifi|Uni|Home"));
            }
        } else {
            for (Task t : tasks) {
                assertTrue(t.getLocation().matches("Maccas|KFC|Station"));
            }
        }
    }


    @Property
    public void findAllValidTime(@ForAll boolean completed,
                                 @ForAll @IntRange(min=1, max=30) int day,
                                 @ForAll @IntRange(min=1, max=90) int num) {
        LocalDateTime from = LocalDateTime.of(
                2023, 3, day, 12,30);
        LocalDateTime to = from.plusDays(num);
        addingRealisticTasks();
        List<Task> tasks = toDoList.findAll(from, to, completed);
        for (Task task : tasks) {
            assertTrue(checkTimeRange(from, to, task));
            assertEquals(task.isCompleted(), completed);
        }
    }

    public boolean checkTimeRange(LocalDateTime from, LocalDateTime to, Task task) {
        return task.getDateTime().isAfter(from) && task.getDateTime().isBefore(to);
    }

    @Property
    public void findAllInValidTime(@ForAll boolean completed,
                                   @ForAll @IntRange(min=1, max=30) int day,
                                   @ForAll @IntRange(min=1, max=60) int num) {
        LocalDateTime from = LocalDateTime.of(
                2023, 3, day, 12,30);
        LocalDateTime to = from.minusDays(num);
        addingRealisticTasks();
        assertThrows(IllegalArgumentException.class, () ->
                toDoList.findAll(from, to, completed));
        assertThrows(IllegalArgumentException.class, () ->
                toDoList.findAll(from, from, completed));
    }

    @Provide
    Arbitrary<String> strings() {
        return Arbitraries.of("Maccas", "Jbhifi", "Uni", "KFC", "Station",
                "Home", "f", "fa");
    }

    @Provide
    Arbitrary<Task.Field> fields() {
        return Arbitraries.of(Task.Field.LOCATION, Task.Field.DESCRIPTION);
    }

    public Map<Task.Field, String> testParams() {
        Map<Task.Field, String> map = new HashMap<>();
        map.put(fields().sample(), strings().sample());
        map.put(fields().sample(), strings().sample());
        map.put(fields().sample(), strings().sample());
        map.put(fields().sample(), strings().sample());
        map.put(fields().sample(), strings().sample());
        map.put(fields().sample(), strings().sample());
        return map;
    }

    @Property
    public void findAllValidParam(@ForAll boolean completed,
                                  @ForAll @IntRange(min=1, max=30) int day,
                                  @ForAll @IntRange(min=1, max=60) int num,
                                  @ForAll boolean andSearch) {
        LocalDateTime from = LocalDateTime.of(
                2023, 3, day, 12,30);
        LocalDateTime to = from.plusDays(num);
        addingRealisticTasks();
        Map<Task.Field, String> testParams = testParams();
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

    @Property
    public void findAllInValidParam(@ForAll boolean completed,
                                  @ForAll @IntRange(min=1, max=30) int day,
                                  @ForAll @IntRange(min=1, max=60) int num,
                                  @ForAll boolean andSearch) {
        LocalDateTime from = LocalDateTime.of(
                2023, 3, day, 12,30);
        LocalDateTime to = from.plusDays(num);
        addingRealisticTasks();
        Map<Task.Field, String> testParams = testNullParams(fields().sample(), null);
        assertThrows(IllegalArgumentException.class, () ->
                toDoList.findAll(testParams, from, to, completed, andSearch));

        Map<Task.Field, String> testParams2 = testNullParams(null, null);
        assertThrows(IllegalArgumentException.class, () ->
                toDoList.findAll(testParams2, from, to, completed, andSearch));

        Map<Task.Field, String> testParams3 = testNullParams(null, strings().sample());
        assertThrows(IllegalArgumentException.class, () ->
                toDoList.findAll(testParams3, from, to, completed, andSearch));

    }

    @Property
    public void findAllParamInvalidTime(@ForAll boolean completed,
                                  @ForAll @IntRange(min=1, max=30) int day,
                                  @ForAll @IntRange(min=1, max=60) int num,
                                  @ForAll boolean andSearch) {
        LocalDateTime from = LocalDateTime.of(
                2023, 3, day, 12,30);
        LocalDateTime to = from.minusDays(num);
        addingRealisticTasks();
        Map<Task.Field, String> testParams = testParams();
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
