package server.shareboard.data;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import server.shareboard.data.mappers.CategoryMapper;
import server.shareboard.models.Category;

import java.util.List;
import java.util.Optional;
@Repository
public class CategoryJdbcClientRepository implements CategoryRepository{
    private final JdbcClient jdbcClient;

    public CategoryJdbcClientRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<Category> findAll() {
        return jdbcClient.sql("select  * from category limit 100").query(new CategoryMapper()).list();
    }

    @Override
    public Category findById(int categoryId) {
        return jdbcClient.sql("select * from category where id = ?")
                .param(categoryId)
                .query(new CategoryMapper())
                .optional().orElse(null);
    }


}
