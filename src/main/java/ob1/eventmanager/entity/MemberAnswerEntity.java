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
import javax.persistence.Table;

@Entity
@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
@Table(name = "memberAnswer")
public class MemberAnswerEntity {

    @Id
    @Generated
    private long id;

    @ManyToOne
    @JoinColumn(name = "memberId", nullable = false)
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "questionId", nullable = false)
    private EventQuestionEntity question;

    @Column(nullable = false)
    private String answer;

}
