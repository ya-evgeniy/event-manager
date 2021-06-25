package ob1.eventmanager.statemachine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @AllArgsConstructor
public class MessageStateMachineContext<T> {

    private final Map<String, Object> headers;

    private final T previousState;

    private T nextState;

    @SuppressWarnings("unchecked")
    public <V> V get(String id) {
        if (headers == null) return null;
        return (V) headers.get(id);
    }

}
