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
}
