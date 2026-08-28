package server.shareboard.data;

import org.springframework.stereotype.Repository;
import server.shareboard.models.Category;

import java.util.List;
import java.util.Optional;
@Repository
public class CategoryJdbcClientRepository implements CategoryRepository{
    @Override
    public List<Category> findAll() {
        return List.of();
    }

    @Override
    public Optional<Category> findById(int categoryId) {
        return Optional.empty();
    }
}
