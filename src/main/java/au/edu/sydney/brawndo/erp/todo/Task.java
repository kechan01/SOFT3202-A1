package au.edu.sydney.brawndo.erp.todo;

import java.time.LocalDateTime;

public interface Task {

    /**
     * <b>Preconditions:</b><br>
     * <br>
     * <b>Postconditions:</b><br>
     * <br>
     *
     * @return The id of this task
     */
    int getID();

    /**
     * <b>Preconditions:</b><br>
     * <br>
     * <b>Postconditions:</b><br>
     * <br>
     *
     * @return The datetime of this task (may not be null)
     */
    LocalDateTime getDateTime();

    /**
     * <b>Preconditions:</b><br>
     * <br>
     * <b>Postconditions:</b><br>
     * <br>
     *
     * @return The location of this task (may not be null, empty, and must be 256 characters or less)
     */
    String getLocation();

    /**
     * <b>Preconditions:</b><br>
     * <br>
     * <b>Postconditions:</b><br>
     * <br>
     *
     * @return The description of this task if present, otherwise null
     */
    String getDescription();

    /**
     * <b>Preconditions:</b><br>
     * <br>
     * <b>Postconditions:</b><br>
     * <br>
     *
     * @return The completion status of this task
     */
    boolean isCompleted();

    /**
     * <b>Preconditions:</b><br>
     * <br>
     * <b>Postconditions:</b><br>
     * The new datetime should be returned from any future calls to this Task’s getDateTime method<br>
     *
     * @param dateTime May not be null
     * @throws IllegalArgumentException If the preconditions are violated
     */
    void setDateTime(LocalDateTime dateTime) throws IllegalArgumentException;

    /**
     * <b>Preconditions:</b><br>
     * <br>
     * <b>Postconditions:</b><br>
     *  The new location should be returned from any future calls to this Task’s getLocation method<br>
     *
     * @param location May not be null or empty, must be 256 characters or less
     * @throws IllegalArgumentException  If the preconditions are violated
     */
    void setLocation(String location) throws IllegalArgumentException;

    /**
     * <b>Preconditions:</b><br>
     * <br>
     * <b>Postconditions:</b><br>
     * The new description (or null if set) should be returned from any future calls to this Task’s getDescription method<br>
     *
     * @param description May be null
     */
    void setDescription(String description);

    /**
     * <b>Preconditions:</b><br>
     * <br>
     * <b>Postconditions:</b><br>
     * The task will be set to completed and will reflect this in isComplete()<br>
     *
     * @throws IllegalStateException  If this task was already complete
     */
    void complete() throws IllegalStateException;

    /**
     * <b>Preconditions:</b><br>
     * <br>
     * <b>Postconditions:</b><br>
     * <br>
     * @param field A mapping to a field stored by this Task (may not be null)
     * @throws IllegalArgumentException If the preconditions are violated
     * @return The string contents of the field mapped by the parameter (with inherited postconditions)
     *
     */
    String getField(Field field) throws IllegalArgumentException;

    enum Field{
        LOCATION, DESCRIPTION
    }
}
