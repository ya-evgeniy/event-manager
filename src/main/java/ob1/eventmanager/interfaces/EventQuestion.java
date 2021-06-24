package ob1.eventmanager.interfaces;

import java.util.List;

public interface EventQuestion {
    long getId();
    Event getEvent();
    String getQuestion();
    List<MemberAnswer> getAllMemberAnswers();
    List<EventQuestionAnswer> getAllEventQuestionAnswers();
}
