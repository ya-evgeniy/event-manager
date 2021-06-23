package ob1.eventmanager.Interfaces;

import java.util.List;

public interface Member {
    long getID();
    User getUser();
    Event getEvent();
    List<MemberAnswer> getAllAnswers();
}
