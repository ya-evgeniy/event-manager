package ob1.eventmanager.interfaces;

public interface TemplateQuestionAnswer {
    long getId();
    TemplateQuestion getQuestion();
    String getAnswer();

    void setAnswer(String answer);
}
