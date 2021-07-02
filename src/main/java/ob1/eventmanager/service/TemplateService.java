package ob1.eventmanager.service;

import ob1.eventmanager.entity.CategoryEntity;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.TemplateEntity;

import java.util.List;
import java.util.Optional;

public interface TemplateService {

    List<TemplateEntity> getTemplatesByCategory(CategoryEntity category);

    EventEntity copyTemplateToEvent(TemplateEntity template, EventEntity event);

    Optional<TemplateEntity> getTemplateById(long id);

    Optional<TemplateEntity> getTemplateByName(String name);

}
