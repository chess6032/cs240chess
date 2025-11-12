package dataaccess.exceptions;

// TODO: inherit from DataAccessException ?
public class SqlException extends Exception {
    public SqlException(String message) {
        super(message);
    }
    public SqlException(String message, Throwable ex) {
        super(message, ex);
    }
    public SqlException(Throwable ex) {
        super(ex);
    }
}
