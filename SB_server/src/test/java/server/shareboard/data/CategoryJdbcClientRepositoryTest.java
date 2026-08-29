package server.shareboard.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import server.shareboard.models.Category;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CategoryJdbcClientRepositoryTest {

    @Autowired
    private CategoryJdbcClientRepository repository;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setup() {
        jdbcClient.sql("call set_known_good_state();").update();
    }

    @Test
    void shouldFindAll() {
        //Act
        List<Category> categories = repository.findAll();

        //Assert
        assertEquals(2, categories.size());
    }

    @Test
    void shouldFindById() {
        //Act
        Category category = repository.findById(1);

        //Assert
        assertNotNull(category);
        assertEquals(1, category.getCategoryId());
        assertEquals("Blankets", category.getName());
    }

    @Test
    void shouldReturnNullWhenCategoryDoesNotExist() {
        //Act
        Category category = repository.findById(999);

        //Assert
        assertNull(category);
    }
}