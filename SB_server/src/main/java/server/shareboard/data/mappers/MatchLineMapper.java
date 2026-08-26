package server.shareboard.data.mappers;

import org.springframework.jdbc.core.RowMapper;
import server.shareboard.models.MatchLine;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MatchLineMapper implements RowMapper<MatchLine> {
    @Override
    public MatchLine mapRow(ResultSet rs, int rowNum) throws SQLException {
        return null;
    }
}
