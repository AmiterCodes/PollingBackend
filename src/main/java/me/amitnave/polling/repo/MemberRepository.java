package me.amitnave.polling.repo;

import me.amitnave.polling.model.Member;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MemberRepository extends CrudRepository<Member, Integer> {
    public Member findByPhone(String phone);

}
