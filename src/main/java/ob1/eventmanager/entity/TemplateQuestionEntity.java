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
@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor
@Table(name = "template_question")
public class TemplateQuestionEntity {

    @Id
    @Generated
    private long id;

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private TemplateEntity template;

    @Column(nullable = false)
    private String question;

    @OneToMany(mappedBy = "question")
    private List<TemplateQuestionAnswerEntity> answers;

}
