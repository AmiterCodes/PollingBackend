package me.amitnave.polling.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.*;

@Entity
@Data
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String description;

    @ManyToOne
    private Member creator;

    @OneToMany(mappedBy = "poll")
    private List<PollOption> options;

    @OneToMany(mappedBy = "poll")
    private List<PollVote> votes;

    @CreationTimestamp
    private Date timestamp;

    @Nullable
    @OneToOne
    private PollOption result;

    public String displayPassed() {
        StringBuilder builder = new StringBuilder();

        builder.append("סקר #").append(id)
                .append("\n").append("*")
                .append("הוגה הסקר:")
                .append("* ")
                .append(creator.getName())
                .append("\n")
                .append(description);

        builder.append("\n\n");
        builder.append("תוצאה:");
        builder.append("\n");
        builder.append("*").append(result).append("*");

        return builder.toString();
    }

    public PollOption getWinner() {
        return options.stream().max(Comparator.comparingInt(option -> option.getVotes().size())
        ).orElse(null);
    }

    public String display(boolean includeVotes, boolean minified) {
        StringBuilder builder = new StringBuilder();

        builder.append("סקר #").append(id)
            .append("\n").append("*")
            .append("הוגה הסקר:")
            .append("* ")
            .append(creator.getName())
            .append("\n")
            .append(description);

        if(minified) return builder.toString();

        builder.append("\n")
                .append("*")
            .append("התשובות האפשריות:").append("*").append("\n\n");

        HashMap<Integer, List<PollVote>> voteMap = new HashMap<>();

        if(includeVotes) {
            votes.forEach(vote -> {
                PollOption option = vote.getOption();
                int optionId = option.getId();
                if (voteMap.containsKey(optionId)) {
                    voteMap.get(optionId).add(vote);
                } else {
                    List<PollVote> list = new ArrayList<>();
                    list.add(vote);
                    voteMap.put(optionId, list);
                }
            });
        }

        options.forEach(option -> {
            List<PollVote> optionVotes = voteMap.get(option.getId());
            builder
                    .append(option.getPollListIndex())
                    .append(" - ")
                    .append(option.getDescription());
            if(includeVotes) {
                builder.append(" - ").append((optionVotes == null) ? 0 : optionVotes.size());
            }
            builder.append("\n");
            if(optionVotes != null && includeVotes) {
                optionVotes.forEach(vote -> builder
                        .append("  - ")
                        .append(vote.getMember().getName())
                        .append("\n"));
            }
        });

        builder.append("\n")
                .append("הגב להודעה זאת עם מספר האופציה שאתה רוצה לבחור בה בשביל לבחור");

        return builder.toString();
    }
}
