package business;

import java.util.List;
import data.User;

public interface UserService {
    User getUserById(Long id);
    List<User> getAllUsers();
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(Long id);
}