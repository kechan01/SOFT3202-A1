package au.edu.sydney.brawndo.erp.spfea;

import au.edu.sydney.brawndo.erp.todo.Task;
import au.edu.sydney.brawndo.erp.todo.TaskImpl;
import au.edu.sydney.brawndo.erp.todo.ToDoList;
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
    public void setup() {
        toDoProvider = mock(ToDoList.class);
        spfeaFacade = new SPFEAFacadeImpl();
        spfeaFacade.setToDoProvider(toDoProvider);

    }

    @Test
    public void addNewTaskSimpleValid1() {
        LocalDateTime dateTime = LocalDateTime.now();
        String description = "A1";

        Task expectedTask = new TaskImpl(1, dateTime, "HOME OFFICE", description);
        when(toDoProvider.add(anyInt(), eq(dateTime), eq("HOME OFFICE"), eq(description))).thenReturn(expectedTask);

        Task actualTask = spfeaFacade.addNewTask(dateTime, description, "HOME OFFICE");

        assertEquals(expectedTask, actualTask);
        assertFalse(actualTask.isCompleted());
    }

    @Test
    public void addNewTaskSimpleValid2() {
        LocalDateTime dateTime = LocalDateTime.now();
        String description = "A1";

        Task expectedTask = new TaskImpl(1, dateTime, "CUSTOMER SITE", description);
        when(toDoProvider.add(anyInt(), eq(dateTime), eq("CUSTOMER SITE"), eq(description))).thenReturn(expectedTask);

        Task actualTask = spfeaFacade.addNewTask(dateTime, description, "CUSTOMER SITE");

        assertEquals(expectedTask, actualTask);
        assertFalse(actualTask.isCompleted());
    }

    @Test
    public void addNewTaskSimpleValid3() {
        LocalDateTime dateTime = LocalDateTime.now();
        String description = "A1";

        Task expectedTask = new TaskImpl(1, dateTime, "MOBILE", description);
        when(toDoProvider.add(anyInt(), eq(dateTime), eq("MOBILE"), eq(description))).thenReturn(expectedTask);

        Task actualTask = spfeaFacade.addNewTask(dateTime, description, "MOBILE");

        assertEquals(expectedTask, actualTask);
        assertFalse(actualTask.isCompleted());
    }

    @Test
    public void addNewTaskEarlierTime() {
        String location = "HOME OFFICE";
        String description = "Cat";

        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addNewTask(LocalDateTime.now().minusDays(10), description,
                        location));

        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addNewTask(LocalDateTime.now().minusSeconds(10), description,
                        location));
    }

    @Test
    public void addNewTaskWrongLocation() {
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addNewTask(LocalDateTime.now(), "cat", "Bed"));

        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addNewTask(LocalDateTime.now(), "cat", "Home"));

        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addNewTask(LocalDateTime.now(), "cat", "Home office"));

        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addNewTask(LocalDateTime.now(), "cat", "customer site"));

        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addNewTask(LocalDateTime.now(), "cat", "mobile"));

    }

    @Test
    public void addNewTaskNullEmptyLocation() {
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addNewTask(LocalDateTime.now(), "cat", null));
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addNewTask(LocalDateTime.now(), "cat", " "));

    }

    @Test
    public void addNewTaskNullEmptyDescription() {
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addNewTask(LocalDateTime.now(), null, "Home Office"));
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addNewTask(LocalDateTime.now(), " ", "Home Office"));

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


    @Test
    public void completeTaskSuccess() {
        LocalDateTime dateTime = LocalDateTime.now();
        String location = "HOME OFFICE";
        String description = "Cat";
        Task incompleteTask = new TaskImpl(1, dateTime, location, description);
        when(toDoProvider.findOne(1)).thenReturn(incompleteTask);

        spfeaFacade.completeTask(incompleteTask.getID());
        assertTrue(incompleteTask.isCompleted());
        verify(toDoProvider).findOne(1).complete();
    }

    @Test
    public void completeTaskNoMatching() {
        when(toDoProvider.findOne(anyInt())).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> spfeaFacade.completeTask(1));
    }

    @Test
    public void completeTaskAlreadyCompleted() {
        LocalDateTime dateTime = LocalDateTime.now();
        String location = "HOME OFFICE";
        String description = "work";
        Task completedTask = new TaskImpl(1, dateTime, location, description);
        completedTask.complete();
        when(toDoProvider.findOne(anyInt())).thenReturn(completedTask);

        assertThrows(IllegalStateException.class, () ->
                spfeaFacade.completeTask(1));
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
    public void addCustomerSameNames() {
        spfeaFacade.addCustomer("Naru", "To", "1234", "is@mycat.com");

        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addCustomer("Naru", "To", "1234", "is@mycat.com"));
    }

    @Test
    public void addCustomerCaseSensitive() {
        spfeaFacade.addCustomer("Naru", "To", "1234", "is@mycat.com");
        spfeaFacade.addCustomer("naru", "To", "1234", "is@mycat.com");

        assertNotEquals(spfeaFacade.getCustomerID("naru", "to"),
                spfeaFacade.getCustomerID("Naru", "To"));

    }

    @Test
    public void addCustomerWithMissingFields() {
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

    @Test
    public void addCustomerValidPhoneNumber1() {
        String fName = "Apple";
        String lName = "Juice";
        String phone = "123456789+(23)";
        String email = "appleyum@example.com";

        int id = spfeaFacade.addCustomer(fName, lName, phone, email);
        int getId = spfeaFacade.getCustomerID(fName, lName);
        assertEquals(id, getId);
    }

    @Test
    public void addCustomerValidPhoneNumber2() {
        String fName = "Apple";
        String lName = "Juice";
        String phone = "+()123456789";
        String email = "appleyum@example.com";

        int id = spfeaFacade.addCustomer(fName, lName, phone, email);
        int getId = spfeaFacade.getCustomerID(fName, lName);
        assertEquals(id, getId);
    }

    @Test
    public void addCustomerValidPhoneNumber3() {
        String fName = "Apple";
        String lName = "Juice";
        String phone = "+(23)123456789";
        String email = "appleyum@example.com";

        int id = spfeaFacade.addCustomer(fName, lName, phone, email);
        int getId = spfeaFacade.getCustomerID(fName, lName);
        assertEquals(id, getId);
    }

    @Test
    public void addCustomerValidPhoneNumber4() {
        String fName = "Apple";
        String lName = "Juice";
        String phone = "123456789";
        String email = "appleyum@example.com";

        int id = spfeaFacade.addCustomer(fName, lName, phone, email);
        int getId = spfeaFacade.getCustomerID(fName, lName);
        assertEquals(id, getId);
    }

    @Test
    public void addCustomerValidPhoneNumber5() {
        String fName = "Apple";
        String lName = "Juice";
        String phone = "12345+()6789";
        String email = "appleyum@example.com";

        int id = spfeaFacade.addCustomer(fName, lName, phone, email);
        int getId = spfeaFacade.getCustomerID(fName, lName);
        assertEquals(id, getId);
    }

    @Test
    public void addCustomerValidPhoneNumber6() {
        String fName = "Apple";
        String lName = "Juice";
        String phone = "12345+(23)6789";
        String email = "appleyum@example.com";

        int id = spfeaFacade.addCustomer(fName, lName, phone, email);
        int getId = spfeaFacade.getCustomerID(fName, lName);
        assertEquals(id, getId);
    }

    @Test
    public void addCustomerValidPhoneNumber7() {
        String fName = "Apple";
        String lName = "Juice";
        String phone = "+()+()";
        String email = "appleyum@example.com";

        int id = spfeaFacade.addCustomer(fName, lName, phone, email);
        int getId = spfeaFacade.getCustomerID(fName, lName);
        assertEquals(id, getId);
    }

    @Test
    public void addCustomerValidPhoneNumber8() {
        String fName = "Apple";
        String lName = "Juice";
        String phone = "+()+(23)";
        String email = "appleyum@example.com";

        int id = spfeaFacade.addCustomer(fName, lName, phone, email);
        int getId = spfeaFacade.getCustomerID(fName, lName);
        assertEquals(id, getId);
    }

    @Test
    public void addCustomerInvalidPhoneNumber() {
        String fName = "Apple";
        String lName = "Juice";
        String email = "appleyum@example.com";

        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addCustomer(fName, lName, "23 2", email));
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addCustomer(fName, lName, "phone", email));
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addCustomer(fName, lName, "+23", email));
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addCustomer(fName, lName, "+(123", email));
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addCustomer(fName, lName, "+()+", email));
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addCustomer(fName, lName, "+())", email));
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addCustomer(fName, lName, "+()", email));
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addCustomer(fName, lName, ")(+", email));

    }

    @Test
    public void addCustomerValidEmail() {
        String fName = "Apple";
        String lName = "Juice";
        String phone = "12345678";
        String email = "appleyum@@@example.com";

        int id = spfeaFacade.addCustomer(fName, lName, phone, email);
        int getId = spfeaFacade.getCustomerID(fName, lName);
        assertEquals(id, getId);
    }


    @Test
    public void addCustomerNullEmailNullPhone() {
        String fName = "Apple";
        String lName = "Juice";
        String phone = null;
        String email = null;
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addCustomer(fName, lName, phone, email));
    }

    @Test
    public void addCustomerEmptyEmail() {
        String fName = "Apple";
        String lName = "Juice";
        String phone = "12345";
        String email = "   ";
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addCustomer(fName, lName, phone, email));
    }

    @Test
    public void addCustomerNoAddressCharacterInEmail() {
        String fName = "Apple";
        String lName = "Juice";
        String phone = "12345";
        String email = "yum.com.au";
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.addCustomer(fName, lName, phone, email));
    }

    @Test
    public void addCustomerValidNullEmailOrPhone1() {
        String fName = "Apple";
        String lName = "Juice";
        String phone = null;
        String email = "yum@cat.com";
        int id = spfeaFacade.addCustomer(fName, lName, phone, email);
        int getId = spfeaFacade.getCustomerID(fName, lName);
        assertEquals(id, getId);
    }

    @Test
    public void addCustomerValidNullEmailOrPhone2() {
        String fName = "Apple";
        String lName = "Juice";
        String phone = "12345";
        String email = null;
        int id = spfeaFacade.addCustomer(fName, lName, phone, email);
        int getId = spfeaFacade.getCustomerID(fName, lName);
        assertEquals(id, getId);
    }

    @Test
    public void getCustomerIDValid() {
        String fName = "Apple";
        String lName = "Juice";
        String phone = "12345";
        String email = "naru@cat.com";

        int id = spfeaFacade.addCustomer(fName, lName, phone, email);
        int getId = spfeaFacade.getCustomerID(fName, lName);
        assertEquals(id, getId);
    }

    @Test
    public void getCustomerIDNullFName() {
        String fName = null;
        String lName = "Juice";
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.getCustomerID(fName,lName));
    }

    @Test
    public void getCustomerIDNullLName() {
        String fName = "Apple";
        String lName = null;
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.getCustomerID(fName,lName));
    }

    @Test
    public void getCustomerIDEmptyFName() {
        String fName = " ";
        String lName = "Juice";
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.getCustomerID(fName,lName));
    }

    @Test
    public void getCustomerIDEmptyLName() {
        String fName = "Apple";
        String lName = "  ";
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.getCustomerID(fName,lName));
    }

    @Test
    public void getCustomerIDDoesNotExist() {
        String fName = "Apple";
        String lName = "Juice";

        assertNull(spfeaFacade.getCustomerID(fName, lName));
    }

    @Test
    public void getCustomerIDCaseSensitive() {
        String fName = "Apple";
        String lName = "Juice";
        String phone = "12345";
        String email = "naru@cat.com";

        spfeaFacade.addCustomer(fName, lName, phone, email);
        assertNull(spfeaFacade.getCustomerID("apple", "juice"));
    }

    @Test
    public void getCustomerIDWrongFName() {
        String fName = "Apple";
        String lName = "Juice";
        String phone = "12345";
        String email = "naru@cat.com";

        int id = spfeaFacade.addCustomer(fName, lName, phone, email);
        int getId = spfeaFacade.getCustomerID("Cat", lName);
        assertEquals(id, getId);
    }

    @Test
    public void getCustomerIDWrongLName() {
        String fName = "Apple";
        String lName = "Juice";
        String phone = "12345";
        String email = "naru@cat.com";

        int id = spfeaFacade.addCustomer(fName, lName, phone, email);
        int getId = spfeaFacade.getCustomerID(fName, "Honey");
        assertEquals(id, getId);
    }

    @Test
    public void getAllCustomersEmpty() {
        List<String> expected = new ArrayList<>();
        List<String> customers = spfeaFacade.getAllCustomers();

        assertEquals(expected, customers);
    }

    @Test
    public void getAllCustomersNull() {
        List<String> customers = spfeaFacade.getAllCustomers();

        assertNotNull(customers);
    }

    @Test
    public void getAllCustomersValid() {
        List<String> expected = new ArrayList<>();
        expected.add("To, Naru");
        expected.add("Chan, Meeko");
        spfeaFacade.addCustomer("Naru", "To", "1234", "is@mycat.com");
        spfeaFacade.addCustomer("Meeko", "Chan", "1234", "is@lost.com");

        List<String> customers = spfeaFacade.getAllCustomers();

        assertEquals(expected, customers);
    }

    @Test
    public void getCustomerEmailCustomerExists() {
        int id = spfeaFacade.addCustomer("Naru", "To", "1234", "is@mycat.com");
        assertEquals("is@mycat.com", spfeaFacade.getCustomerEmail(id));
    }

    @Test
    public void getCustomerEmailCustomerNotExists() {
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.getCustomerEmail(100));
    }

    @Test
    public void setCustomerEmailCustomerExists() {
        int id = spfeaFacade.addCustomer("Naru", "To", "1234", "is@mycat.com");
        spfeaFacade.setCustomerEmail(id, "is@naughty.com");

        assertEquals("is@naughty.com", spfeaFacade.getCustomerEmail(id));
    }

    @Test
    public void setCustomerEmailCustomerNotExists() {
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.setCustomerEmail(100, "cat@com"));
    }

    @Test
    public void setCustomerEmailInvalidEmail() {
        int id = spfeaFacade.addCustomer("Naru", "To", "1234", "is@mycat.com");

        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.setCustomerEmail(id, "email"));
    }

    @Test
    public void setCustomerEmailNullNumberValid() {
        int id = spfeaFacade.addCustomer("Naru", "To", "1234", "is@mycat.com");
        spfeaFacade.setCustomerEmail(id, null);

        assertNull(spfeaFacade.getCustomerEmail(id));
    }

    @Test
    public void setCustomerEmailNullNumberNullEmail() {
        int id = spfeaFacade.addCustomer("Naru", "To", null, "is@mycat.com");

        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.setCustomerEmail(id, null));
    }

    @Test
    public void getCustomersEmailCustomerExists() {
        int id = spfeaFacade.addCustomer("Naru", "To", "1234", "is@mycat.com");
        assertEquals("1234", spfeaFacade.getCustomerPhone(id));
    }

    @Test
    public void getCustomersPhoneCustomerNotExists() {
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.getCustomerPhone(100));
    }

    @Test
    public void setCustomerPhoneCustomerExists() {
        int id = spfeaFacade.addCustomer("Naru", "To", "1234", "is@mycat.com");
        spfeaFacade.setCustomerPhone(id, "123456");

        assertEquals("123456", spfeaFacade.getCustomerPhone(id));
    }

    @Test
    public void setCustomerPhoneCustomerNotExists() {
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.setCustomerPhone(100, "1235465875"));
    }

    @Test
    public void setCustomerPhoneInvalidNumber() {
        int id = spfeaFacade.addCustomer("Naru", "To", "1234", "is@mycat.com");

        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.setCustomerPhone(id, "123 22"));
    }

    @Test
    public void setCustomerPhoneNullNumberValid() {
        int id = spfeaFacade.addCustomer("Naru", "To", "1234", "is@mycat.com");
        spfeaFacade.setCustomerPhone(id, null);

        assertNull(spfeaFacade.getCustomerPhone(id));
    }

    @Test
    public void setCustomerPhoneNullNumberNullEmail() {
        int id = spfeaFacade.addCustomer("Naru", "To", "1234", null);

        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.setCustomerPhone(id, null));
    }

    @Test
    public void removeCustomerNotExist() {
        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.removeCustomer(100));
    }

    @Test
    public void removeCustomerExists() {
        int id = spfeaFacade.addCustomer("Naru", "To", "1234", null);
        spfeaFacade.removeCustomer(id);

        assertFalse(spfeaFacade.getAllCustomers().contains("Naru, To"));

        assertThrows(IllegalArgumentException.class, () ->
                spfeaFacade.removeCustomer(id));
    }



}










