package dataaccess.sqldao;

@FunctionalInterface
public interface ResultSetHandler<T> {
    T handle(java.sql.ResultSet rs) throws java.sql.SQLException;
}