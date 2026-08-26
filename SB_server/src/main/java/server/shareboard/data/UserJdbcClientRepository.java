package server.shareboard.data;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import server.shareboard.models.User;

import java.util.Optional;

@Repository
public class UserJdbcClientRepository implements UserRepository{

    private final JdbcClient jdbcClient;

    public UserJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<User> findById(int userId) {
        return jdbcClient.sql("select * from `user` where id = ?")
                .query();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public User create(User user) {
        return null;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public boolean delete(int userId) {
        return false;
    }
}
