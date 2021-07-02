package ob1.eventmanager.service;

import ob1.eventmanager.entity.CategoryEntity;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<CategoryEntity> getCategories();

    Optional<CategoryEntity> getById(long id);

    Optional<CategoryEntity> getCategoryByName(String name);

}
