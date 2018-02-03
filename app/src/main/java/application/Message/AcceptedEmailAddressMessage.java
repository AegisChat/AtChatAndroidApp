package application.Message;

/**
 * Created by puya3 on 2018-02-02.
 */

public class AcceptedEmailAddressMessage extends Message{
    boolean isEmail;

    public AcceptedEmailAddressMessage(){

    }

    public AcceptedEmailAddressMessage(boolean isEmail){
        this.isEmail = isEmail;
    }

    public boolean isEmail() {
        return isEmail;
    }

    public void setEmail(boolean email) {
        isEmail = email;
    }
}
