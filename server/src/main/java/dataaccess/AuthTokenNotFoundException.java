package dataaccess;

public class AuthTokenNotFoundException extends DataAccessException {
    public AuthTokenNotFoundException(String message) {
        super(message);
    }
}
