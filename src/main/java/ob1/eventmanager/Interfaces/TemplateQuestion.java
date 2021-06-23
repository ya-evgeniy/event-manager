package ob1.eventmanager.Interfaces;

import java.util.List;

public interface TemplateQuestion {
    long getID();
    Template getTemplate();
    String getQuestion();
    List<TemplateQuestionAnswer> getAllAnswers();
}
