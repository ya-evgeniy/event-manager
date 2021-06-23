package ob1.eventmanager.Interfaces;

public interface Event {
    long getID();
    String getName();
    String getDate();
    String getPlace();
    boolean isVerified();

    void setName(String name);
    void setDate(String date);
    void setPlace(String place);
    void setVerified(boolean isVerified);
}
