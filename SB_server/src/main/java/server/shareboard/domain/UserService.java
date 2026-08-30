package server.shareboard.domain;

import org.springframework.stereotype.Service;
import server.shareboard.data.UserRepository;
import server.shareboard.models.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;

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

    public Result<User> create(User user)  {
        Result<User> result = new Result<>();

        //validate
        if (user == null) {
            result.addErrorMessage("User cannot be null", ResultType.INVALID);
            return result;
        }
        if (user.getUsername() == null || user.getUsername().isBlank())  {
            result.addErrorMessage("Username cannot be null", ResultType.INVALID);
        }
        if (user.getPasswordHash() == null || user.getPasswordHash().length() < 8){
            result.addErrorMessage("Password must be at least 8 characters", ResultType.INVALID);
        }
        if (!result.isSuccess()){
            return result;
        }

        if (repository.findByUsername(user.getUsername()) != null) {
            result.addErrorMessage("Username is already taken", ResultType.INVALID);
            return result;
        }

        // hashing password
        user.setPasswordHash(hash(user.getPasswordHash()));
        user.setCreatedAt(LocalDateTime.now());

        User created = repository.create(user);
        if (created == null)  {
            result.addErrorMessage("Could not create user", ResultType.INVALID);
            return result;
        }

        result.setPayload(created);
        return result;
    }

    //An existing user logins in
    public Result<User> login(String username, String password) {
        Result<User> result = new Result<>();

        User found = repository.findByUsername(username);;
        if (found == null) {
            result.addErrorMessage("Invalid username or password",ResultType.INVALID);
            return result;
        }

        if (found.getPasswordHash().equals(hash(password))) {
            result.addErrorMessage("Invalid username or password", ResultType.INVALID);
            return result;
        }
        result.setPayload(found);
        return result;
    }

    //Password hashing logic
    private String hash(String rawPassword) throws NoSuchAlgorithmException {
      MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

      byte[] passwordAsBytes = rawPassword.getBytes();
      byte[] digestBytes = sha256.digest(passwordAsBytes);

      String printableHash = Base64.getEncoder().encodeToString(digestBytes);
      return printableHash;
    }
}
