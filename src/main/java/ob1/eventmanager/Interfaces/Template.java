package ob1.eventmanager.Interfaces;

import java.util.List;

public interface Template {
    long getID();
    Category getCategory();
    String getName();
    List<TemplateQuestion> getAllQuestions();
}
