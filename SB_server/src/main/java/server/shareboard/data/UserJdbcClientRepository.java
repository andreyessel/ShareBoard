package server.shareboard.data;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import server.shareboard.data.mappers.UserMapper;
import server.shareboard.models.User;

import java.util.Optional;

@Repository
public class UserJdbcClientRepository implements UserRepository{

    private final JdbcClient jdbcClient;

    public UserJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public User findById(int userId) {
        return jdbcClient.sql("select * from `user` where id = ?")
                .param(userId)
                .query(new UserMapper())
                .optional().orElse(null);
    }
// use rows
    @Override
    public User findByUsername(String username) {
        return jdbcClient.sql("select * from `user` where username = ?")
                .param(username)
                .query(new UserMapper())
                .optional().orElse(null);
    }

    @Override
    public User create(User user) {
        final String sql = """
                INSERT INTO `user` (username, password_hash, is_organization)
                VALUES(:username, :password_hash, :is_organization);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcClient.sql(sql)
                .param("username", user.getUsername())
                .param("password_hash", user.getPasswordHash())
                .param("is_organization", user.isOrganization())
                .update(keyHolder, "id");

        if (rowsAffected == 0) {
            return null;
        }

        user.setUserId(keyHolder.getKey().intValue());

        return user;
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
