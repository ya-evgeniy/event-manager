package ob1.eventmanager.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
@Table(name = "eventQuestion")
public class EventQuestionEntity {

    @Id
    @Generated
    private long id;

    @ManyToOne
    @JoinColumn(name = "eventId", nullable = false)
    private EventEntity event;

    @Column(nullable = false)
    private String question;

    @OneToMany(mappedBy = "question")
    private List<EventQuestionAnswerEntity> answers;

}
