package ob1.eventmanager.repository;

import ob1.eventmanager.entity.EventQuestionAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventQuestionAnswerRepository extends JpaRepository<EventQuestionAnswerEntity, Long> {
}
