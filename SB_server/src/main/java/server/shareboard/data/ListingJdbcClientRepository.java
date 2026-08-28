package server.shareboard.data;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import server.shareboard.data.mappers.ListingMapper;
import server.shareboard.models.Listing;

import java.util.List;
import java.util.Optional;
@Repository
public class ListingJdbcClientRepository implements ListingRepository{

    private final JdbcClient jdbcClient;

    public ListingJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<Listing> findAll() {
        return jdbcClient.sql("select * from listing limit 100").query(new ListingMapper()).list();
    }

    @Override
    public Listing findById(int listingId) {
        return jdbcClient.sql("select * from listing where id = ?")
                .param(listingId)
                .query(new ListingMapper())
                .optional().orElse(null);
    }

    @Override
    public List<Listing> findByUserId(int userId) {
        return jdbcClient.sql("select * from listing where user_id = ?")
                .param(userId)
                .query(new ListingMapper())
                .list();
    }

    @Override
    public Listing createListing(Listing listing) {
        final String sql = """
                insert into listing ( user_id, category_id, type, quantity, quantity_remaining, urgency, needed_by, available_until, status)
                values( :user_id, :category_id, :type, :quantity, :quantity_remaining, :urgency, :needed_by, :available_until, :status);
                """;

        KeyHolder keyHolder= new GeneratedKeyHolder();
        int rowsAffectedo = jdbcClient.sql(sql)
                .param("user_id", listing.getUserId())
                .param("category_id", listing.getCategoryId())
                .param("type", listing.getType().name())
                .param("quantity", listing.getQuantity())
                .param("quantity_remaining", listing.getQuantityRemaining())
                .param("urgency", listing.getUrgency() != null ? listing.getUrgency().name() : null)
                .param("needed_by", listing.getNeededBy())
                .param("available_until", listing.getAvailableUntil())
                .param("status", listing.getStatus().name())
                .update(keyHolder, "id");

        if (rowsAffectedo == 0) {
            return null;
        }
     listing.setListingId(keyHolder.getKey().intValue());
        return listing;
    }

    @Override
    public boolean update(Listing listing) {
        return jdbcClient.sql("""
                update listing set
                user_id = :user_id,
                category_id = :category_id,
                type = :type,
                quantity = :quantity,
                quantity_remaining = :quantity_remaining,
                urgency = :urgency,
                needed_by = :needed_by,
                available_until = :available_until,
                status = :status
                where id = :id
                """)
                .param("user_id", listing.getUserId())
                .param("category_id", listing.getCategoryId())
                .param("type", listing.getType())
                .param("quantity", listing.getQuantity())
                .param("quantity_remaining", listing.getQuantityRemaining())
                .param("urgency", listing.getUrgency())
                .param("needed_by", listing.getNeededBy())
                .param("available_until", listing.getAvailableUntil())
                .param("status", listing.getStatus())
                .update() > 0;
    }

    @Override
    public boolean deleteById(int listingId) {
        return jdbcClient.sql("delete from listing where id = ?;")
                .param(listingId)
                .update() > 0;
    }
}
