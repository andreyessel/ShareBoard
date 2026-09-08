package server.shareboard.data;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import server.shareboard.data.mappers.ListingMapper;
import server.shareboard.models.Listing;

import java.util.List;

@Repository
public class ListingJdbcClientRepository implements ListingRepository{

    private final JdbcClient jdbcClient;

    public ListingJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    private static final String SELECT_WITH_JOINS = """
            select l.*, u.username as owner_username, u.is_organization as owner_is_organization, c.name as category_name
            from listing l
            join `user` u on l.user_id = u.id
            join category c on l.category_id = c.id
            """;

    @Override
    public List<Listing> findAll() {
        return jdbcClient.sql(SELECT_WITH_JOINS + " limit 100").query(new ListingMapper()).list();
    }

    @Override
    public Listing findById(int listingId) {
        return jdbcClient.sql(SELECT_WITH_JOINS + " where l.id = ?")
                .param(listingId)
                .query(new ListingMapper())
                .optional().orElse(null);
    }

    @Override
    public List<Listing> findByUserId(int userId) {
        return jdbcClient.sql(SELECT_WITH_JOINS + " where l.user_id = ?")
                .param(userId)
                .query(new ListingMapper())
                .list();
    }

    @Override
    public Listing create(Listing listing) {
        final String sql = """
                insert into listing ( user_id, category_id, type, quantity, quantity_remaining, urgency, needed_by, available_until, status, anonymous)
                values( :user_id, :category_id, :type, :quantity, :quantity_remaining, :urgency, :needed_by, :available_until, :status, :anonymous);
                """;

        KeyHolder keyHolder= new GeneratedKeyHolder();
        int rowsAffected = jdbcClient.sql(sql)
                .param("user_id", listing.getUserId())
                .param("category_id", listing.getCategoryId())
                .param("type", listing.getType().name())
                .param("quantity", listing.getQuantity())
                .param("quantity_remaining", listing.getQuantityRemaining())
                .param("urgency", listing.getUrgency() != null ? listing.getUrgency().name() : null)
                .param("needed_by", listing.getNeededBy())
                .param("available_until", listing.getAvailableUntil())
                .param("status", listing.getStatus().name())
                .param("anonymous", listing.isAnonymous())
                .update(keyHolder, "id");

        if (rowsAffected == 0) {
            return null;
        }
        listing.setListingId(keyHolder.getKey().intValue());
        return listing;
    }

    @Override
    public boolean update(Listing listing) {
        return jdbcClient.sql("""
            update listing set
            category_id = :category_id,
            quantity = :quantity,
            quantity_remaining = :quantity_remaining,
            urgency = :urgency,
            needed_by = :needed_by,
            available_until = :available_until,
            status = :status
            where id = :id
            """)
                .param("id", listing.getListingId())
                .param("category_id", listing.getCategoryId())
                .param("quantity", listing.getQuantity())
                .param("quantity_remaining", listing.getQuantityRemaining())
                .param("urgency", listing.getUrgency() == null ? null : listing.getUrgency().name())
                .param("needed_by", listing.getNeededBy())
                .param("available_until", listing.getAvailableUntil())
                .param("status", listing.getStatus().name())
                .update() > 0;
    }
    //implented anonymous in jdbc
}