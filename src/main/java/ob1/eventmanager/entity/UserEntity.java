package ob1.eventmanager.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor
@Table(name = "user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = true)
    private int telegramId;

    @Column(nullable = true)
    private String name;

    @Column(nullable = true)
    private Long chatId;

    @ManyToOne
    @JoinColumn(name = "selected_event", nullable = true)
    private EventEntity selectedEvent;

    @Column(nullable = true)
    private String previousState;

    @Column(nullable = true)
    private String currentState;

}
