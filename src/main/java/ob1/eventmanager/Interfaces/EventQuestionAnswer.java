package ob1.eventmanager.Interfaces;

public interface EventQuestionAnswer {
    long getID();
    EventQuestion getQuestion();
    String getAnswer();

    void setAnswer(String answer);
}
