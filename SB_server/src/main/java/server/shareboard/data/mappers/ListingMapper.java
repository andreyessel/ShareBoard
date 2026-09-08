package server.shareboard.data.mappers;

import org.springframework.jdbc.core.RowMapper;
import server.shareboard.models.*;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ListingMapper implements RowMapper<Listing> {
    @Override
    public Listing mapRow(ResultSet rs, int rowNum) throws SQLException {

        Date neededByDate = rs.getDate("needed_by");
        LocalDate neededBy = neededByDate == null ? null : neededByDate.toLocalDate();

        Date availableUntilDate = rs.getDate("available_until");
        LocalDate availableUntil = availableUntilDate == null ? null : availableUntilDate.toLocalDate();

        String urgencyStr = rs.getString("urgency");
        Urgency urgency = urgencyStr == null ? null : Urgency.valueOf(urgencyStr);

        Listing listing = new Listing(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("category_id"),
                ListingType.valueOf(rs.getString("type")),
                rs.getInt("quantity"),
                rs.getInt("quantity_remaining"),
                urgency,
                neededBy,
                availableUntil,
                Status.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at").toLocalDateTime()
        );

        listing.setOwnerUsername(rs.getString("owner_username"));
        listing.setOwnerIsOrganization(rs.getBoolean("owner_is_organization"));
        listing.setCategoryName(rs.getString("category_name"));
        listing.setAnonymous(rs.getBoolean("anonymous"));

        // Keeping the anonymous giver private
        if (listing.isAnonymous() && listing.getType() == ListingType.SURPLUS) {
            listing.setOwnerUsername("Anonymous Donor");
            listing.setOwnerIsOrganization(false);
        }
        return listing;
    }
}