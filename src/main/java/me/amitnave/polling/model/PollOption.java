package me.amitnave.polling.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class PollOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String description;

    @ManyToOne
    private Poll poll;

    @OneToMany(mappedBy = "option")
    private List<PollVote> votes;

    private Integer pollListIndex;
}
