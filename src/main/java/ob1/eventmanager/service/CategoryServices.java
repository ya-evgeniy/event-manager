package ob1.eventmanager.service;

import ob1.eventmanager.entity.CategoryEntity;

import java.util.List;
import java.util.Optional;

public interface CategoryServices {

    List<CategoryEntity> getCategories();

    Optional<CategoryEntity> getCategoryByName(String name);

}
