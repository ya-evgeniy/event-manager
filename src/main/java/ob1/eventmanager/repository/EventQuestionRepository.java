package ob1.eventmanager.repository;

import ob1.eventmanager.entity.EventQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventQuestionRepository extends JpaRepository<EventQuestionEntity, Long> {
}
