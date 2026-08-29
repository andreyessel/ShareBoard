package server.shareboard.data;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import server.shareboard.data.mappers.MatchLineMapper;
import server.shareboard.models.Listing;
import server.shareboard.models.MatchLine;

import java.util.List;
@Repository
public class MatchLineJdbcClientRepository implements MatchLineRepository{

    private final JdbcClient jdbcClient;

    public MatchLineJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<MatchLine> findAll() {
        return jdbcClient.sql("select * from match_line limit 100").query(new MatchLineMapper()).list();
    }

    @Override
    public List<MatchLine> findByListingId(int listingId) {
        return jdbcClient.sql("""
                select * from match_line
                where need_listing_id = ? or surplus_listing_id = ?
                """)
                .param(listingId)
                .param(listingId)
                .query(new MatchLineMapper())
                .list();
    }

    @Override
    public MatchLine create(MatchLine matchLine) {
        final String sql = """
                insert into match_line ( need_listing_id, surplus_listing_id, quantity_matched)
                values (:need_listing_id, :surplus_listing_id, :quantity_matched);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcClient.sql(sql)
                .param("need_listing_id", matchLine.getNeedListingId())
                .param("surplus_listing_id", matchLine.getSurplusListingId())
                .param("quantity_matched", matchLine.getQuantityMatched())
                .update(keyHolder, "id");

        if (rowsAffected == 0) {
            return null;
        }
        matchLine.setMatchLineId(keyHolder.getKey().intValue());
        return matchLine;
    }
}
