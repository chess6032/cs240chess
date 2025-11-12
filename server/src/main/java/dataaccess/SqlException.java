package dataaccess;

public class SqlException extends DataAccessException {
    public SqlException(String message) {
        super(message);
    }
    public SqlException(String message, Throwable ex) { super(message, ex); }
}
