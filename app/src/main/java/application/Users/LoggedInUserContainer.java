package application.Users;

/**
 * Created by Avi on 2018-02-04.
 */

public class LoggedInUserContainer {
    private static User user;
    private static LoggedInUserContainer instance = null;

    protected LoggedInUserContainer(){

    }

    public static LoggedInUserContainer getInstance(){
        if(instance == null){
            return new LoggedInUserContainer();
        }else{
            return instance;
        }
    }

    public void setUser(User user){
        this.user = user;
    }

    public User getUser(){
        return user;
    }
}
