package com.galaxy.ordering.controller.admin;

import com.galaxy.ordering.common.Result;
import com.galaxy.ordering.entity.Category;
import com.galaxy.ordering.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public Result<Category> create(@RequestBody Category category) {
        categoryService.save(category);
        return Result.ok(category);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.ok();
    }
}
