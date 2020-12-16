package me.amitnave.polling.command.handler.poll;

import lombok.AllArgsConstructor;
import me.amitnave.polling.command.handler.CommandHandler;
import me.amitnave.polling.command.handler.ValidationResponse;
import me.amitnave.polling.command.handler.annotations.CommandInfo;
import me.amitnave.polling.command.handler.annotations.MemberOnly;
import me.amitnave.polling.command.handler.annotations.Private;
import me.amitnave.polling.command.handler.annotations.TextMatch;
import me.amitnave.polling.model.Poll;
import me.amitnave.polling.repo.PollRepository;
import me.amitnave.polling.whatsapp.Message;
import me.amitnave.polling.whatsapp.Response;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        usage = "#",
        example = "#",
        description = "פקודה להצגת כל הסקרים שהרשום עדיין לא ענה עליהם",
        name = "תזכורת"
)
@AllArgsConstructor
@TextMatch(regex = "^#$")
@Private
@MemberOnly
public class ReminderHandler implements CommandHandler {

    private PollRepository pollRepository;

    @Override
    public ValidationResponse validate(Message message) {
        return ValidationResponse.VALID_RESPONSE;
    }

    @Override
    public List<Response> process(Message message) {
        List<Response> responses = new ArrayList<>();

        List<Poll> polls = pollRepository.findAllByResultIsNull();
        polls
                .stream()
                .filter(poll -> poll
                        .getVotes()
                        .stream()
                        .noneMatch(vote -> vote
                                .getMember()
                                .getPhone()
                                .equals(message
                                        .getSenderPhone())))
        .forEach(poll -> responses.add(message.createDirectResponse(poll.display(false, false))));

        return responses;
    }
}
