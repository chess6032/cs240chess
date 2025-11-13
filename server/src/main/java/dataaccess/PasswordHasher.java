package dataaccess;

import org.mindrot.jbcrypt.BCrypt;

public interface PasswordHasher {
    static String hashPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    static boolean checkPassword(String clearTextPassword, String hashedPassword) {
        return BCrypt.checkpw(clearTextPassword, hashedPassword);
    }
}
