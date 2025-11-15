package dataaccess.exceptions;

public class SqlInterruptedException extends SqlException {
    public SqlInterruptedException(String message) {
        super(message);
    }
    public SqlInterruptedException(String message, Throwable ex) {
        super(message, ex);
    }
    public SqlInterruptedException(Throwable ex) {
        super(ex);
    }
}
