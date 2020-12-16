package me.amitnave.polling.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class PollVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Member member;

    @ManyToOne
    private Poll poll;

    @ManyToOne
    private PollOption option;
}
