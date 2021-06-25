package ob1.eventmanager.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor
@Table(name = "event")
public class EventEntity {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private long chatId;

    @Column(nullable = true)
    private String name;

    @Column(nullable = true)
    private LocalDateTime date;

    @Column(nullable = true)
    private String place;

    @Column(nullable = false)
    private boolean verified = false;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    private CategoryEntity category;

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = true)
    private TemplateEntity template;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @OneToMany(mappedBy = "event")
    private List<MemberEntity> members;

    @OneToMany(mappedBy = "event")
    private List<EventQuestionEntity> questions;

}
