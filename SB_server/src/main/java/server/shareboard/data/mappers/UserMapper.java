package server.shareboard.data.mappers;

import org.springframework.jdbc.core.RowMapper;
import server.shareboard.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class UserMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getBoolean("is_organization"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
