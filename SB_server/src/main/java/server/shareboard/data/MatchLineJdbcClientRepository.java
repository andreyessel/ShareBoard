package server.shareboard.data;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import server.shareboard.data.mappers.MatchLineMapper;
import server.shareboard.domain.MatchHistoryEntry;
import server.shareboard.domain.RecentMatchEntry;
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
    public List<MatchHistoryEntry> findHistoryForListing(int listingId) {
        return jdbcClient.sql("""
                select
                    ml.id as match_line_id,
                    ml.quantity_matched,
                    ml.matched_at,
                    case when ml.need_listing_id = :listingId then 'SURPLUS' else 'NEED' end as counterpart_role,
                    case when ml.need_listing_id = :listingId then ml.surplus_listing_id else ml.need_listing_id end as counterpart_listing_id,
                    u.username as counterpart_username,
                    u.is_organization as counterpart_is_organization
                from match_line ml
                join listing counterpart
                    on counterpart.id = case when ml.need_listing_id = :listingId then ml.surplus_listing_id else ml.need_listing_id end
                join `user` u on u.id = counterpart.user_id
                where ml.need_listing_id = :listingId or ml.surplus_listing_id = :listingId
                order by ml.matched_at desc
                """)
                .param("listingId", listingId)
                .query((rs, rowNum) -> new MatchHistoryEntry(
                        rs.getInt("match_line_id"),
                        rs.getInt("counterpart_listing_id"),
                        rs.getString("counterpart_username"),
                        rs.getBoolean("counterpart_is_organization"),
                        rs.getString("counterpart_role"),
                        rs.getInt("quantity_matched"),
                        rs.getTimestamp("matched_at").toLocalDateTime()
                ))
                .list();
    }

    @Override
    public List<RecentMatchEntry> findRecent(int limit) {
        List<RecentMatchEntry> results = jdbcClient.sql("""
                select
                    ml.id as match_line_id,
                    ml.quantity_matched,
                    ml.matched_at,
                    c.name as category_name,
                    nu.username as need_username,
                    nu.is_organization as need_is_org,
                    su.username as surplus_username,
                    su.is_organization as surplus_is_org,
                    sl.anonymous as surplus_anonymous
                from match_line ml
                join listing nl on nl.id = ml.need_listing_id
                join listing sl on sl.id = ml.surplus_listing_id
                join `user` nu on nu.id = nl.user_id
                join `user` su on su.id = sl.user_id
                join category c on c.id = nl.category_id
                order by ml.matched_at desc
                limit :limit
                """)
                .param("limit", limit)
                .query((rs, rowNum) -> new RecentMatchEntry(
                        rs.getInt("match_line_id"),
                        rs.getString("category_name"),
                        rs.getString("need_username"),
                        rs.getBoolean("need_is_org"),
                        rs.getBoolean("surplus_anonymous") ? "Anonymous Donor" : rs.getString("surplus_username"),
                        rs.getBoolean("surplus_anonymous") ? false : rs.getBoolean("surplus_is_org"),
                        rs.getInt("quantity_matched"),
                        rs.getTimestamp("matched_at").toLocalDateTime()
                ))
                .list();
        return results;
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