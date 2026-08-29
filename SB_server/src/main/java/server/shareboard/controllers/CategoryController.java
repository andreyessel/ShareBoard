package server.shareboard.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.shareboard.data.CategoryRepository;
import server.shareboard.models.Category;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository repository;

    public CategoryController(CategoryRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        List<Category> categories = repository.findAll();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Object> findById(@PathVariable("categoryId") int categoryId) {
        Category category = repository.findById(categoryId);

        if (category == null) {
            return new ResponseEntity<>("Category not found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(category, HttpStatus.OK);
    }
}