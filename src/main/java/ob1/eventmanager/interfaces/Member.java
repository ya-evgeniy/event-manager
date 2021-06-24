package ob1.eventmanager.interfaces;

import java.util.List;

public interface Member {
    long getId();
    User getUser();
    Event getEvent();
    List<MemberAnswer> getAllAnswers();
}
