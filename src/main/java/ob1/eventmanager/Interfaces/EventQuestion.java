package ob1.eventmanager.Interfaces;

import java.util.List;

public interface EventQuestion {
    long getID();
    Event getEvent();
    String getQuestion();
    List<MemberAnswer> getAllMemberAnswers();
    List<EventQuestionAnswer> getAllEventQuestionAnswers();
}
