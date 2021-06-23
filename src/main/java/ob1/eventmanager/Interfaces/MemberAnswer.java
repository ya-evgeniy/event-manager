package ob1.eventmanager.Interfaces;

public interface MemberAnswer {
    int getID();
    int getMemberID();
    int getQuestionID();
    String getAnswer();

    void setAnswer(String answer);
}
