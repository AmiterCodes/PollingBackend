package me.amitnave.polling.repo;

import me.amitnave.polling.model.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface PollRepository extends PagingAndSortingRepository<Poll, Integer> {
    Page<Poll> findAllByDescriptionLike(String query, Pageable pageable);
    List<Poll> findAllByTimestampBefore(Date time);
    List<Poll> findAllByResultIsNull();
}
