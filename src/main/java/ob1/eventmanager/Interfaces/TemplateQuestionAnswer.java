package ob1.eventmanager.Interfaces;

public interface TemplateQuestionAnswer {
    long getID();
    TemplateQuestion getQuestion();
    String getAnswer();

    void setAnswer(String answer);
}
