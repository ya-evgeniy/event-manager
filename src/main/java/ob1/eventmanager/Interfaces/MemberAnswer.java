package ob1.eventmanager.Interfaces;

public interface MemberAnswer {
    long getID();
    Member getMember();
    EventQuestion getQuestion();
    String getAnswer();

    void setAnswer(String answer);
}
