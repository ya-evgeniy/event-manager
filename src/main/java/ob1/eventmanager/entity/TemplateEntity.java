package ob1.eventmanager.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor
@Table(name = "template")
public class TemplateEntity {

    @Id
    @Generated
    private long id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @Column(nullable = true)
    private String name;

    @OneToMany(mappedBy = "template", fetch = FetchType.EAGER)
    private List<TemplateQuestionEntity> questions;


    public List<TemplateQuestionEntity> getQuestions() {
        Hibernate.initialize(questions);
        return questions;
    }
}
