package application.Message;

/**
 * Created by puya3 on 2018-02-02.
 */

public class VerifyLoginMessage extends Message{
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String email;

    public VerifyLoginMessage(){

    }

    public VerifyLoginMessage(String email){
        this.email = email;
    }


}
