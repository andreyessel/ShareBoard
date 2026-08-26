package server.shareboard.data.mappers;

import org.springframework.jdbc.core.RowMapper;
import server.shareboard.models.Listing;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ListingMapper implements RowMapper<Listing> {
    @Override
    public Listing mapRow(ResultSet rs, int rowNum) throws SQLException {
        return null;
    }
}
