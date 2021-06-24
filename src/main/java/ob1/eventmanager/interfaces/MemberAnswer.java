package ob1.eventmanager.interfaces;

public interface MemberAnswer {
    long getId();
    Member getMember();
    EventQuestion getQuestion();
    String getAnswer();

    void setAnswer(String answer);
}
