package au.edu.sydney.brawndo.erp.spfea;

import au.edu.sydney.brawndo.erp.todo.Task;
import au.edu.sydney.brawndo.erp.todo.TaskImpl;
import au.edu.sydney.brawndo.erp.todo.ToDoList;
import net.jqwik.api.*;
import net.jqwik.api.constraints.NotBlank;
import net.jqwik.api.lifecycle.BeforeTry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SPFEAFacadeImplTest {
    private ToDoList toDoProvider;

    private SPFEAFacade spfeaFacade;

    @BeforeEach
    @BeforeTry
    public void setup() {
        toDoProvider = mock(ToDoList.class);
        spfeaFacade = new SPFEAFacadeImpl();
        spfeaFacade.setToDoProvider(toDoProvider);

    }

    @Provide
    Arbitrary<String> locationStrings() {
        return Arbitraries.of("HOME OFFICE", "CUSTOMER SITE", "MOBILE");
    }

    @Property
    public void addNewTaskSimpleValid(@ForAll("locationStrings") String location) {
        LocalDateTime dateTime = LocalDateTime.now();
        String description = "A1";

        Task expectedTask = new TaskImpl(1, dateTime, location, description);
        when(toDoProvider.add(anyInt(), eq(dateTime), eq(location), eq(description))).thenReturn(expectedTask);

        Task actualTask = spfeaFacade.addNewTask(dateTime, description, location);

        assertEquals(expectedTask.getID(), actualTask.getID());
        assertEquals(expectedTask.getDateTime(), actualTask.getDateTime());
        assertEquals(expectedTask.getLocation(), actualTask.getLocation());
        assertEquals(expectedTask.getDescription(), actualTask.getDescription());
        assertFalse(actualTask.isCompleted());
    }

    @Property
    public void addNewTaskNullTime(@ForAll("locationStrings") String location, @ForAll @NotBlank String description) {
        when(toDoProvider.add(anyInt(), any(LocalDateTime.class), eq(location), eq(description))).thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addNewTask(null, description, location));
    }

    @Property
    public void addNewTaskEarlierTime(@ForAll("locationStrings") String location, @ForAll @NotBlank String description) {
        when(toDoProvider.add(anyInt(), any(LocalDateTime.class), eq(location), eq(description))).
                thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addNewTask(LocalDateTime.now().minusDays(10), description,
                        location));

        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addNewTask(LocalDateTime.now().minusSeconds(10), description,
                        location));
    }

    @Provide
    Arbitrary<String> wrongLocationStrings() {
        return Arbitraries.strings().filter(e -> !e.matches("HOME OFFICE|CUSTOMER SITE|MOBILE"));
    }

    @Property
    public void addNewTaskWrongLocation(@ForAll("wrongLocationStrings") String location, @ForAll @NotBlank String description) {
        when(toDoProvider.add(anyInt(), any(LocalDateTime.class), eq(location), eq(description))).
                thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addNewTask(LocalDateTime.now(), description, location));
    }

    @Property
    public void addNewTaskNullEmptyLocation(@ForAll @NotBlank String description) {
        doThrow(IllegalArgumentException.class).when(
                toDoProvider).add(anyInt(), any(LocalDateTime.class), anyString(), eq(description));

        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addNewTask(LocalDateTime.now(), "a", null));
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addNewTask(LocalDateTime.now(), "a", " "));

    }

    @Property
    public void addNewTaskNullEmptyDescription(@ForAll("locationStrings") String location) {
        doThrow(IllegalArgumentException.class).when(
                toDoProvider).add(anyInt(), any(LocalDateTime.class), eq(location), anyString());

        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addNewTask(LocalDateTime.now(), null, location));
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addNewTask(LocalDateTime.now(), " ", location));

    }

    @Test
    public void addNewTaskNullToDoProvider() {
        spfeaFacade.setToDoProvider(null);
        assertThrows(IllegalStateException.class, () ->
                spfeaFacade.addNewTask(LocalDateTime.now(), "f", "HOME OFFICE"));
    }

    @Test
    public void completeTaskNullToDoProvider() {
        spfeaFacade.setToDoProvider(null);
        assertThrows(IllegalStateException.class, () ->
                spfeaFacade.completeTask(1));

    }


    @Property
    public void completeTaskSuccess(@ForAll int id) {
        LocalDateTime dateTime = LocalDateTime.now();
        String location = "HOME OFFICE";
        String description = "work";
        Task incompleteTask = new TaskImpl(id, dateTime, location, description);
        when(toDoProvider.findOne(id)).thenReturn(incompleteTask);

        spfeaFacade.completeTask(incompleteTask.getID());
        assertTrue(incompleteTask.isCompleted());
        verify(toDoProvider).findOne(id).complete();

    }

    @Property
    public void completeTaskNoMatching(@ForAll int id) {
        when(toDoProvider.findOne(id)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> spfeaFacade.completeTask(id));
    }

    @Property
    public void completeTaskAlreadyCompleted(@ForAll int id) {
        LocalDateTime dateTime = LocalDateTime.now();
        String location = "HOME OFFICE";
        String description = "work";
        Task completedTask = new TaskImpl(id, dateTime, location, description);
        completedTask.complete();
        when(toDoProvider.findOne(id)).thenReturn(completedTask);

        assertThrows(IllegalStateException.class, () ->
                spfeaFacade.completeTask(id));
    }

    @Test
    public void testGetAllTasksWithEmptyList() {
        List<Task> expectedTasks = new ArrayList<>();
        when(toDoProvider.findAll()).thenReturn(expectedTasks);

        List<Task> actualTasks = spfeaFacade.getAllTasks();

        assertEquals(expectedTasks, actualTasks);
    }

    @Test
    public void testGetAllTasksWithNonEmptyList() {
        List<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(new TaskImpl(1, LocalDateTime.now(), "HOME OFFICE", "A1"));
        expectedTasks.add(new TaskImpl(2, LocalDateTime.now().plusDays(1), "CUSTOMER SITE", "A2"));
        when(toDoProvider.findAll()).thenReturn(expectedTasks);

        List<Task> actualTasks = spfeaFacade.getAllTasks();

        assertEquals(expectedTasks, actualTasks);
    }

    @Test
    public void testGetAllTasksWithNullToDoProvider() {
        assertThrows(IllegalStateException.class, () -> {
            spfeaFacade.setToDoProvider(null);
            spfeaFacade.getAllTasks();
        });
    }

    @Test
    public void addCustomerValid() {
        String fName = "Apple";
        String lName = "Yum";
        String phone = "123456789";
        String email = "appleyum@example.com";

        int id = spfeaFacade.addCustomer(fName, lName, phone, email);
        int getId = spfeaFacade.getCustomerID(fName, lName);
        assertEquals(id, getId);

    }

    @Test
    public void addCustomerNullFName() {
        String fName = null;
        String lName = "Yum";
        String phone = "123456789";
        String email = "appleyum@example.com";

        assertThrows(IllegalArgumentException.class, () -> {
            spfeaFacade.addCustomer(fName, lName, phone, email);
        });

    }

    @Test
    public void addCustomerEmptyFName() {
        String fName = " ";
        String lName = "Yum";
        String phone = "123456789";
        String email = "appleyum@example.com";

        assertThrows(IllegalArgumentException.class, () -> {
            spfeaFacade.addCustomer(fName, lName, phone, email);
        });
    }

    @Test
    public void addCustomerNullLName() {
        String fName = "Apple";
        String lName = null;
        String phone = "123456789";
        String email = "appleyum@example.com";

        assertThrows(IllegalArgumentException.class, () -> {
            spfeaFacade.addCustomer(fName, lName, phone, email);
        });
    }

    @Test
    public void addCustomerEmptyLName() {
        String fName = "Apple";
        String lName = " ";
        String phone = "123456789";
        String email = "appleyum@example.com";

        assertThrows(IllegalArgumentException.class, () -> {
            spfeaFacade.addCustomer(fName, lName, phone, email);
        });
    }

    @Test
    public void testAddCustomerWithMissingFields() {
        String fName = "";
        String lName = "Yum";
        String phone = null;
        String email = "";
        assertThrows(IllegalArgumentException.class, () -> spfeaFacade.addCustomer(fName, lName, phone, email));
    }

    @Test
    public void addCustomerEmptyPhone() {
        String fName = "Apple";
        String lName = "Juice";
        String phone = " ";
        String email = "appleyum@example.com";
        assertThrows(IllegalArgumentException.class, () -> spfeaFacade.addCustomer(fName, lName, phone, email));
    }
    @Test
    public void addCustomerNullPhoneNumber() {
        String fName = "Apple";
        String lName = "Juice";
        String phone = null;
        String email = "appleyum@example.com";

        int id = spfeaFacade.addCustomer(fName, lName, phone, email);
        int getId = spfeaFacade.getCustomerID(fName, lName);
        assertEquals(id, getId);
    }

    @Provide
    Arbitrary<String> invalidPhoneNumbers() {
        return Arbitraries.strings().filter(e -> !e.matches("[0-9+()]+"));
    }
    @Property
    public void addCustomerValidPhoneNumber() {
        String fName = "Apple";
        String lName = "Juice";
        String phone = "123456789+(23)";
        String email = "appleyum@example.com";

        int id = spfeaFacade.addCustomer(fName, lName, phone, email);
        int getId = spfeaFacade.getCustomerID(fName, lName);
        assertEquals(id, getId);
    }

    @Property
    public void addCustomerInvalidPhoneNumber(@ForAll("invalidPhoneNumbers") @NotBlank String phone) {
        String fName = "Apple";
        String lName = "Juice";
        String email = "appleyum@example.com";

        int id = spfeaFacade.addCustomer(fName, lName, phone, email);
        int getId = spfeaFacade.getCustomerID(fName, lName);
        assertEquals(id, getId);
    }

}










