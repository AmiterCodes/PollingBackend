package me.amitnave.polling.command.handler.poll;

import lombok.AllArgsConstructor;
import me.amitnave.polling.command.handler.CommandHandler;
import me.amitnave.polling.command.handler.ValidationResponse;
import me.amitnave.polling.command.handler.annotations.CommandInfo;
import me.amitnave.polling.command.handler.annotations.Private;
import me.amitnave.polling.command.handler.annotations.TextMatch;
import me.amitnave.polling.model.Poll;
import me.amitnave.polling.model.PollOption;
import me.amitnave.polling.model.PollVote;
import me.amitnave.polling.repo.MemberRepository;
import me.amitnave.polling.repo.PollOptionRepository;
import me.amitnave.polling.repo.PollRepository;
import me.amitnave.polling.whatsapp.Message;
import me.amitnave.polling.whatsapp.Response;

import java.util.Arrays;
import java.util.List;

@CommandInfo(
        usage = "#[מספר סקר]",
        example = "#3",
        description = "פקודה להצגת מצב סקר עדכני",
        name = "מצב סקר"
)
@Private
@TextMatch(regex = "^#\\d+$")
@AllArgsConstructor
public class PollStatusHandler implements CommandHandler {

    private PollRepository pollRepository;

    @Override
    public ValidationResponse validate(Message message) {

        String numString = message.getContent().substring(1);

        Integer num = Integer.parseInt(numString);

        if(!pollRepository.existsById(num)) return new ValidationResponse("לא קיים סקר כזה");


        return ValidationResponse.VALID_RESPONSE;
    }

    @Override
    public List<Response> process(Message message) {

        String numString = message.getContent().substring(1);

        Integer num = Integer.parseInt(numString);

        Poll poll = pollRepository.findById(num).orElseThrow(NullPointerException::new);

        return message.createSingletonResponse(poll.display(true, false));
    }
}
