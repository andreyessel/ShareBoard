package server.shareboard.data;

import server.shareboard.models.User;

import java.util.Optional;

public interface UserRepository {

   public  Optional<User>  findById(int userId);

  public  Optional<User> findByUsername(String username);

   public User create(User user);

   //stretch goal
  public boolean update(User user);
//stretch goal
  public  boolean delete(int userId);

}
