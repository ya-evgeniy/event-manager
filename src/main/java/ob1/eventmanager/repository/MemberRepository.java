package ob1.eventmanager.repository;

import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Set;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    Set<MemberEntity> findAllByUser(UserEntity user);

}
