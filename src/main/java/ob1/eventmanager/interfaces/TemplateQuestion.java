package ob1.eventmanager.interfaces;

import java.util.List;

public interface TemplateQuestion {
    long getId();
    Template getTemplate();
    String getQuestion();
    List<TemplateQuestionAnswer> getAllAnswers();
}
