package ob1.eventmanager.service.impl;

import ob1.eventmanager.entity.CategoryEntity;
import ob1.eventmanager.repository.CategoryRepository;
import ob1.eventmanager.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<CategoryEntity> getCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<CategoryEntity> getById(long id) {
        return categoryRepository.getFirstById(id);
    }

    @Override
    public Optional<CategoryEntity> getCategoryByName(String name) {
        return categoryRepository.getFirstByNameLike(name);
    }

}