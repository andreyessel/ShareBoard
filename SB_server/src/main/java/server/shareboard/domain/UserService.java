package server.shareboard.domain;

import org.springframework.stereotype.Service;
import server.shareboard.data.UserRepository;
import server.shareboard.models.User;

@Service
public class UserService {

    private final UserRepository repository;


    public UserService(UserRepository repository) {
        this.repository = repository;
    }


    public Result<User> findById(int userId) {
        Result<User> result = new Result<>();

        if (userId <= 0) {
            result.addErrorMessage("Invalid user id", ResultType.INVALID);
            return result;
        }

        User found = repository.findById(userId);
        if (found == null){
            result.addErrorMessage("User not found",ResultType.NOT_FOUND);
        } else {
            result.setPayload(found);
        }
        return result;
    }


    public Result<User> findByUsername(String username) {
        User found = repository.findByUsername(username);
        Result<User> result = new Result<>();

        if (found == null) {
            result.addErrorMessage("User not found", ResultType.NOT_FOUND);
        } else {
            result.setPayload(found);
        }
        return result;
    }

    public Result<User> create(User user) {
        Result<User> result = new Result<>();

        //validate
        if (user == null) {
            result.addErrorMessage("User cannot be null", ResultType.INVALID);
            return result;
        }
        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank())  {
            result.addErrorMessage("Password cannot be null", ResultType.INVALID);
            return result;
        }
        User created = repository.create(user);
        if (created == null)  {
            result.addErrorMessage("Could not create user", ResultType.INVALID);
            return result;
        }
        result.setPayload(created);
        return result;
    }
}
