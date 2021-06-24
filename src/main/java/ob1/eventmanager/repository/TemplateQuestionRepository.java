package ob1.eventmanager.repository;

import ob1.eventmanager.entity.TemplateQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateQuestionRepository extends JpaRepository<TemplateQuestionEntity, Long> {
}
