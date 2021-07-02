package ob1.eventmanager.repository;

import ob1.eventmanager.entity.MemberAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberAnswerRepository extends JpaRepository<MemberAnswerEntity, Long> {

    MemberAnswerEntity getById(Long aLong);//fixme как это сделать optional?
}
