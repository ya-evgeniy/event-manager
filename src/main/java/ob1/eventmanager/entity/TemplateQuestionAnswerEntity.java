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
@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor
@Table(name = "template_question_answer")
public class TemplateQuestionAnswerEntity {

    @Id
    @Generated
    private long id;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private TemplateQuestionEntity question;

    @Column(nullable = false)
    private String answer;

}
