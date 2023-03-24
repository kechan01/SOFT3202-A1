package au.edu.sydney.brawndo.erp.todo;

public class ToDoFactory {

    /**
     * <b>Preconditions:</b><br>
     * <br>
     * <b>Postconditions:</b><br>
     * <br>
     */
    public ToDoFactory() {
    }

    /**
     * <b>Preconditions:</b><br>
     * <br>
     * <b>Postconditions:</b><br>
     * <br>
     *
     * @return  A concrete instance of a ToDoList
     */
    public ToDoList makeToDoList() {
        return new ToDoListImpl();
    }
}
