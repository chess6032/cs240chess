package dataaccess;

public class LoginFailException extends DataAccessException {
    public LoginFailException(String message) {
        super(message);
    }
}
