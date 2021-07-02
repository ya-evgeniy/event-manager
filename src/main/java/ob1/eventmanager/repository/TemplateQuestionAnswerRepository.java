package ob1.eventmanager.repository;

import ob1.eventmanager.entity.TemplateQuestionAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateQuestionAnswerRepository extends JpaRepository<TemplateQuestionAnswerEntity, Long> {
}
