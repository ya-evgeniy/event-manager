package ob1.eventmanager.Interfaces;

import java.util.List;

public interface Category {
    long getID();
    String getName();
    List<Template> getAllTemplates();
}
