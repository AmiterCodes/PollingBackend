package me.amitnave.polling.repo;

import me.amitnave.polling.model.Poll;
import me.amitnave.polling.model.PollOption;
import org.springframework.data.repository.CrudRepository;

public interface PollOptionRepository extends CrudRepository<PollOption, Integer> {
     boolean existsByPollListIndexAndPoll(int index, Poll poll);
     PollOption findByPollListIndexAndPoll(int index, Poll poll);
}
