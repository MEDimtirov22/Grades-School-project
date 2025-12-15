package business;

import java.util.List;
import java.util.Optional;
import data.User;
import data.UserRepository;

public class UserServiceImpl implements UserService {
    private final UserRepository repo;

    public UserServiceImpl(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public User getUserById(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    @Override
    public User createUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("username is required");
        }
        return repo.save(user);
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("id is required for update");
        }
        Optional<User> existing = repo.findById(user.getId());
        if (!existing.isPresent()) {
            throw new IllegalStateException("user not found");
        }
        return repo.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        repo.deleteById(id);
    }
}