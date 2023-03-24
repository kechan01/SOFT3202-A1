package au.edu.sydney.brawndo.erp.spfea;

import au.edu.sydney.brawndo.erp.todo.Task;
import au.edu.sydney.brawndo.erp.todo.ToDoList;

import java.time.LocalDateTime;
import java.util.List;

public class SPFEAFacadeImpl implements SPFEAFacade{
    @Override
    public void setToDoProvider(ToDoList provider) {

    }

    @Override
    public Task addNewTask(LocalDateTime dateTime, String description, String location) throws IllegalArgumentException, IllegalStateException {
        return null;
    }

    @Override
    public void completeTask(int id) throws IllegalArgumentException, IllegalStateException {

    }

    @Override
    public List<Task> getAllTasks() throws IllegalStateException {
        return null;
    }

    @Override
    public int addCustomer(String fName, String lName, String phone, String email) throws IllegalArgumentException {
        return 0;
    }

    @Override
    public Integer getCustomerID(String fName, String lName) throws IllegalArgumentException {
        return null;
    }

    @Override
    public List<String> getAllCustomers() {
        return null;
    }

    @Override
    public String getCustomerPhone(int id) throws IllegalArgumentException {
        return null;
    }

    @Override
    public void setCustomerPhone(int id, String phone) throws IllegalArgumentException {

    }

    @Override
    public String getCustomerEmail(int id) throws IllegalArgumentException {
        return null;
    }

    @Override
    public void setCustomerEmail(int id, String email) throws IllegalArgumentException {

    }

    @Override
    public void removeCustomer(int id) throws IllegalArgumentException {

    }
}
