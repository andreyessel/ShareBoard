package server.shareboard.data;

import server.shareboard.models.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    List<Category> findAll();

    Optional<Category> findById(int categoryId);
}
