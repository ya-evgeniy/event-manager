package ob1.eventmanager.interfaces;

public interface Event {
    long getId();
    String getName();
    String getDate();
    String getPlace();
    boolean isVerified();

    void setName(String name);
    void setDate(String date);
    void setPlace(String place);
    void setVerified(boolean isVerified);
}
