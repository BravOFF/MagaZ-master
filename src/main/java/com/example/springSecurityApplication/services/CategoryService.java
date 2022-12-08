package com.example.springSecurityApplication.services;

import com.example.springSecurityApplication.models.Category;
import com.example.springSecurityApplication.models.Product;
import com.example.springSecurityApplication.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository CategoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {this.CategoryRepository = categoryRepository;}

    //Данный метод позволяет вернуть все категории
    public List<Category> getAllCategory() {
        return CategoryRepository.findAll();
    }

    //Данный метод позволяет вернуть категорию по id
    public Category getCategoryId(int id) {
        Optional<Category> optionalCategory = CategoryRepository.findById(id);
        return optionalCategory.orElse(null);
    }

    //Данный метод позволяет сохранить категорию
    @Transactional
    public void saveCategory(Category category) {
        CategoryRepository.save(category);
    }

    //Данный метод позволяет обновить данные категории
    @Transactional
    public void updateCategory(int id, Category category) {
        category.setId(id);
        CategoryRepository.save(category);
    }

    //Данный метод позволяет удалить категорию по id
    @Transactional
    public void deleteCategory(int id) {
        CategoryRepository.deleteById(id);
    }
}
