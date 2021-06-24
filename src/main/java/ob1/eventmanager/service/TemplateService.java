package ob1.eventmanager.service;

import ob1.eventmanager.entity.CategoryEntity;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.TemplateEntity;

import java.util.List;

public interface TemplateService {

    List<TemplateEntity> getTemplatesByCategory(CategoryEntity category);

    void copyTemplateToEvent(TemplateEntity template, EventEntity event);

}
