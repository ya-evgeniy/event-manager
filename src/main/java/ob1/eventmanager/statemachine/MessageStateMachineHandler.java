package ob1.eventmanager.statemachine;

public interface MessageStateMachineHandler<T> {

    void handle(MessageStateMachineContext<T> context);

}
