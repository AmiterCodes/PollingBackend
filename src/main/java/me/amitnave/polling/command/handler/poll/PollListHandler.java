package me.amitnave.polling.command.handler.poll;

import lombok.AllArgsConstructor;
import me.amitnave.polling.command.handler.CommandHandler;
import me.amitnave.polling.command.handler.ValidationResponse;
import me.amitnave.polling.command.handler.annotations.CommandInfo;
import me.amitnave.polling.command.handler.annotations.Private;
import me.amitnave.polling.command.handler.annotations.TextMatch;
import me.amitnave.polling.model.Poll;
import me.amitnave.polling.repo.PollRepository;
import me.amitnave.polling.whatsapp.Message;
import me.amitnave.polling.whatsapp.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static me.amitnave.polling.command.handler.poll.PollHelper.getPollPageView;

@CommandInfo(
        usage = "#סקרים 3, #סקרים",
        example = "#סקרים (עמוד)",
        description = "מציג רשימה של סקרים",
        name = "רשימת סקרים"
)
@AllArgsConstructor
@Private
@TextMatch(regex = "^" + PollListHandler.POLLS_COMMAND + " ?" + "\\d*" + "$")
public class PollListHandler implements CommandHandler {
    private PollRepository pollRepository;

    public static final int PAGE_SIZE = 10;
    public static final String POLLS_COMMAND = "#סקרים";

    @Override
    public ValidationResponse validate(Message message) {
        return ValidationResponse.VALID_RESPONSE;
    }

    @Override
    public List<Response> process(Message message) {
        String content = message.getContent();

        int page = 0;


        if(!content.equals(POLLS_COMMAND)) {
            page = Integer.parseInt(content.replaceFirst(POLLS_COMMAND + " ", "")) - 1;
        }

        Page<Poll> polls = pollRepository.findAll(PageRequest.of(page, PAGE_SIZE, Sort.Direction.DESC, "id"));

        String response = getPollPageView(page, polls, POLLS_COMMAND);

        return message.createSingletonResponse(response);
    }


}
