package me.amitnave.polling.command.handler.vote;

import antlr.StringUtils;
import lombok.AllArgsConstructor;
import me.amitnave.polling.command.handler.CommandHandler;
import me.amitnave.polling.command.handler.ValidationResponse;
import me.amitnave.polling.command.handler.annotations.CommandInfo;
import me.amitnave.polling.model.Member;
import me.amitnave.polling.model.Poll;
import me.amitnave.polling.model.PollOption;
import me.amitnave.polling.model.PollVote;
import me.amitnave.polling.repo.MemberRepository;
import me.amitnave.polling.repo.PollOptionRepository;
import me.amitnave.polling.repo.PollRepository;
import me.amitnave.polling.repo.PollVoteRepository;
import me.amitnave.polling.whatsapp.Message;
import me.amitnave.polling.whatsapp.Response;
import org.springframework.util.NumberUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@CommandInfo(
        usage = "[מספר אופציה כתגובה לסקר]",
        example = "3",
        description = "פקודה להצבעה על סקר",
        name = "הצבעה על סקר"
)
@AllArgsConstructor
public class VoteOnPollHandler implements CommandHandler {

    private PollRepository pollRepository;
    private PollOptionRepository pollOptionRepository;
    private PollVoteRepository pollVoteRepository;
    private MemberRepository memberRepository;

    private Poll parsePoll(String pollString) {
        String lineOne = pollString.split("\n")[0];
        String numId = lineOne.subSequence(lineOne.indexOf("#") + 1, lineOne.length()).toString();
        if(!numId.chars().allMatch(Character::isDigit)) return null;

        int id = Integer.parseInt(numId);

        return pollRepository.findById(id).orElse(null);
    }

    @Override
    public ValidationResponse validate(Message message) {

        String content = message.getRespondingMessageText();

        if(!message.isRespondingToBot()) return ValidationResponse.NO_RESPONSE;
        if(!message.getRespondingMessageText().startsWith("סקר #")) return ValidationResponse.NO_RESPONSE;
        if(message.getContent().length() == 0) return ValidationResponse.NO_RESPONSE;
        char voteChar = message.getContent().charAt(0);
        if(!Character.isDigit(voteChar)) return ValidationResponse.NO_RESPONSE;

        if(message.getSendingMember(memberRepository) == null) return ValidationResponse.NOT_MEMBER_ERROR;

        Poll poll = parsePoll(content);
        if(poll == null) return ValidationResponse.NO_RESPONSE;
        int votePosition = Integer.parseInt(""+voteChar);

        if(!pollOptionRepository.existsByPollListIndexAndPoll(votePosition, poll))
            return new ValidationResponse("האופציה שבחרת לא קיימת, אנא בחר אופציה בין " + "1" + " ל-" + poll.getOptions().size());

        else return ValidationResponse.VALID_RESPONSE;
    }

    @Override
    public List<Response> process(Message message) {
        String content = message.getRespondingMessageText();
        Poll poll = parsePoll(content);
        char voteChar = message.getContent().charAt(0);
        int votePosition = Integer.parseInt(""+voteChar);

        PollOption option = pollOptionRepository.findByPollListIndexAndPoll(votePosition, poll);

        Member member = message.getSendingMember(memberRepository);

        boolean newVote = false;

        PollVote vote = pollVoteRepository.findByMemberAndPoll(member, poll);
        if(vote == null) {
            newVote = true;
            vote = new PollVote();
        }
        vote.setMember(member);
        vote.setPoll(poll);
        vote.setOption(option);

        pollVoteRepository.save(vote);

        assert poll != null;
        return message.createSingletonResponse(
                "הצבעתך על סקר #" +
                        poll.getId() +
                        " " +
                        (newVote ? "נקלטה" : "שונתה") +
                        " בהצלחה.");
    }
}
