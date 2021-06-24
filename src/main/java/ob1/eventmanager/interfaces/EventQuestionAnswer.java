package ob1.eventmanager.interfaces;

public interface EventQuestionAnswer {
    long getId();
    EventQuestion getQuestion();
    String getAnswer();

    void setAnswer(String answer);
}
