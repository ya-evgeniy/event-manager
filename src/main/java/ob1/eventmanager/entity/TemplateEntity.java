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
@Table(name = "template")
public class TemplateEntity {

    @Id
    @Generated
    private long id;

    @ManyToOne
    @JoinColumn(name = "categoryId", nullable = false)
    private CategoryEntity category;

    @Column(nullable = true)
    private String name;

    @OneToMany(mappedBy = "template")
    private List<TemplateQuestionEntity> questions;

}
