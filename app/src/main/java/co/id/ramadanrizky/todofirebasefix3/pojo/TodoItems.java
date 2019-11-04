package co.id.ramadanrizky.todofirebasefix3.pojo;

import java.io.Serializable;
import java.util.HashMap;

public class TodoItems implements Serializable {

    private int viewType;
    private String task;
    private String date;
    private String category;
    private String key;

    public TodoItems(int viewType, String task, String date, String category) {
        this.viewType = viewType;
        this.task = task;
        this.date = date;
        this.category = category;
    }

    public TodoItems() {
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public HashMap<String, String> toFirebaseObj() {
        HashMap<String, String> todo = new HashMap<String, String>();
        todo.put("task", task);
        todo.put("date", date);
        todo.put("category", category);

        return todo;
    }
}
