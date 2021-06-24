package ob1.eventmanager.interfaces;

import java.util.List;

public interface Category {
    long getId();
    String getName();
    List<Template> getAllTemplates();
}
