package au.edu.sydney.brawndo.erp.todo;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ToDoListImpl implements ToDoList {
    Map<Integer, Task> ids = new HashMap<>();
    int currentId = 1;
    boolean manual = false;

    @Override
    public Task add(Integer id, LocalDateTime dateTime, String location, String description) throws IllegalArgumentException, IllegalStateException {
        if (id == null && !manual) {
            id = currentId;
            currentId++;
        } else if (id == null){
            throw new IllegalStateException("Manual id activated. Cannot auto generate id.");
        } else if (!manual){
            manual = true;
        }

        if (ids.containsKey(id)) throw new IllegalArgumentException("Id already exists");

        if (dateTime == null) throw new IllegalArgumentException("DateTime cannot be null");

        if (location == null || location.isBlank() || location.length() > 256)
            throw new IllegalArgumentException("Invalid location name");

        Task task = new TaskImpl(id, null, null, null);
        task.setDescription(description);
        task.setLocation(location);
        task.setDateTime(dateTime);
        ids.put(id, task);
        return task;
    }


    @Override
    public boolean remove(int id) {
        if (ids.containsKey(id)) {
            ids.remove(id);
            if(!manual) {
                currentId--;
            }
            return true;
        }

        return false;
    }

    @Override
    public Task findOne(int id) {
        if (ids.containsKey(id)) {
            return ids.get(id);
        }
        return null;
    }

    @Override
    public List<Task> findAll() {
        return ids.values().stream().toList();
    }

    @Override
    public List<Task> findAll(boolean completed) {
        return ids.values().stream().filter(e -> e.isCompleted() == completed).collect(Collectors.toList());
    }

    @Override
    public List<Task> findAll(LocalDateTime from, LocalDateTime to, Boolean completed) throws IllegalArgumentException {
        if (from.isEqual(to) || from.isAfter(to)) throw new IllegalArgumentException("From has to be before to");

        return ids.values().stream().filter(e -> {
            LocalDateTime eTime = e.getDateTime();
            boolean timeRange = eTime.isBefore(to) && eTime.isAfter(from);
            return timeRange && e.isCompleted() == completed;
        }).collect(Collectors.toList());
    }

    public boolean filterFields(Task e, Map<Task.Field, String> params, boolean andSearch) throws IllegalArgumentException{
        boolean contain = false;
        for (Map.Entry<Task.Field, String> entries : params.entrySet()) {
            if (entries.getValue() == null || entries.getKey() == null) throw new IllegalArgumentException("Filter values cannot be null");
            if (entries.getKey() == Task.Field.DESCRIPTION) {
                if (!e.getDescription().contains(entries.getValue()) && andSearch) {
                    return false;
                } else if (e.getDescription().contains(entries.getValue()) && !andSearch) {
                    System.out.println("Description contains!");
                    return true;
                } else if (e.getDescription().contains(entries.getValue())) {
                    contain = true;
                }
            } else if (entries.getKey() == Task.Field.LOCATION) {
                if (!e.getLocation().contains(entries.getValue()) && andSearch) {
                    return false;
                } else if (e.getLocation().contains(entries.getValue()) && !andSearch) {
                    return true;
                } else if (e.getLocation().contains(entries.getValue())) {
                    contain = true;
                }
            }
        }
        return contain;
    }
    @Override
    public List<Task> findAll(Map<Task.Field, String> params, LocalDateTime from, LocalDateTime to, Boolean completed, boolean andSearch) throws IllegalArgumentException {
        if (from.isEqual(to) || from.isAfter(to)) throw new IllegalArgumentException("From has to be before to");
        return ids.values().stream().filter(e -> {
            boolean filter = filterFields(e, params, andSearch);
            LocalDateTime eTime = e.getDateTime();
            boolean timeRange = eTime.isBefore(to) && eTime.isAfter(from);
            return timeRange && (e.isCompleted() == completed) && filter;
        }).collect(Collectors.toList());
    }

    @Override
    public void clear() {
        ids.clear();
        currentId = 1;
    }
}