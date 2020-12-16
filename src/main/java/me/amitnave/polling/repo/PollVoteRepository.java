package me.amitnave.polling.repo;

import me.amitnave.polling.model.Member;
import me.amitnave.polling.model.Poll;
import me.amitnave.polling.model.PollVote;
import org.springframework.data.repository.CrudRepository;

public interface PollVoteRepository extends CrudRepository<PollVote, Integer> {
    public PollVote findByMemberAndPoll(Member member, Poll poll);
}
