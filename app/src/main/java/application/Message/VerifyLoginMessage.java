package application.Message;

/**
 * Created by puya3 on 2018-02-02.
 */

public class VerifyLoginMessage extends Message{

    public VerifyLoginMessage(){

    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String email;


    public VerifyLoginMessage(String email){
        this.email = email;
    }


}
