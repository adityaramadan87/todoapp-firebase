package co.id.ramadanrizky.todofirebasefix3.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserTodo implements Serializable {

    public String uid;
    public String username;
    public String email;
    public String address;
    public String imageUrl;
    public int typeLogin;


    public UserTodo(String uid,String username, String email, String address, String imageUrl, int typeLogin) {
        this.username = username;
        this.email = email;
        this.address = address;
        this.uid = uid;
        this.imageUrl = imageUrl;
        this.typeLogin = typeLogin;
    }

    public UserTodo() {
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTypeLogin() {
        return typeLogin;
    }

    public void setTypeLogin(int typeLogin) {
        this.typeLogin = typeLogin;
    }
}
