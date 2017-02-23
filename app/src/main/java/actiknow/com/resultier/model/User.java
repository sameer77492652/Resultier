package actiknow.com.resultier.model;

/**
 * Created by actiknow on 2/3/17.
 */

public class User {
    int user_id;
    String user_name, user_email, user_mobile;

    public User(int user_id, String user_name, String user_email, String user_mobile){
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_email = user_email;
        this.user_mobile = user_mobile;

    }

    public User() {

    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }
}
