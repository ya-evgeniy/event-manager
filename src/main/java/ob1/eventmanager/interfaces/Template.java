package ob1.eventmanager.interfaces;

import java.util.List;

public interface Template {
    long getId();
    Category getCategory();
    String getName();
    List<TemplateQuestion> getAllQuestions();
}
