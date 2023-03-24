package au.edu.sydney.brawndo.erp.todo;

import java.time.LocalDateTime;

public class TaskImpl implements Task {

    LocalDateTime dateTime;
    String location;
    String description;
    boolean isCompleted;
    int id;

    public TaskImpl (int id, LocalDateTime dateTime, String location, String description) {
        this.id = id;
        this.dateTime = dateTime;
        this.location = location;
        this.description = description;
        this.isCompleted = false;
    }
    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public LocalDateTime getDateTime() {
        return this.dateTime;
    }

    @Override
    public String getLocation() {
        return this.location;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public boolean isCompleted() {
        return this.isCompleted;
    }

    @Override
    public void setDateTime(LocalDateTime dateTime) throws IllegalArgumentException {
        if (dateTime == null) throw new IllegalArgumentException("DateTime cannot be null");
        this.dateTime = dateTime;
    }

    @Override
    public void setLocation(String location) throws IllegalArgumentException {
        if (location == null || location.isBlank()) throw new IllegalArgumentException("Location cannot be null or empty");
        if (location.length() > 256 ) throw new IllegalArgumentException("Location name must be 256 characters or less");
        this.location = location;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void complete() throws IllegalStateException {
        if (isCompleted) throw new IllegalStateException("Task is already completed");
        this.isCompleted = true;
    }

    @Override
    public String getField(Field field) throws IllegalArgumentException {
        if (field == null) throw new IllegalArgumentException("Field cannot be null");
        if (field == Field.LOCATION) return getLocation();
        else return getDescription();
    }
}