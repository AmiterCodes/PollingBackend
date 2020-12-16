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

import java.util.List;

@CommandInfo(
        usage = "#'[ביטוי או מילת חיפוש]' (מספר עמוד)",
        example = "#'קבב', #'מגניב' 5",
        description = "פקודה לחיפוש סקרים",
        name = "חיפוש סקרים"
)
@AllArgsConstructor
@Private
@TextMatch(regex = "^" + PollSearchHandler.SEARCH_FORMAT_REGEX + " ?\\d*" + "$")
public class PollSearchHandler implements CommandHandler {

    private PollRepository pollRepository;
    public static final int PAGE_SIZE = 10;
    public static final String SEARCH_FORMAT_REGEX = "#'.+'";

    @Override
    public ValidationResponse validate(Message message) {

        return ValidationResponse.VALID_RESPONSE;
    }

    @Override
    public List<Response> process(Message message) {
        String content = message.getContent();


        int page = 0;
        String searchQuery = content.substring(content.indexOf("'") + 1, content.lastIndexOf("'"));
        if(!content.matches("^" + SEARCH_FORMAT_REGEX + "$")) {
            page = Integer.parseInt(content.replaceFirst(SEARCH_FORMAT_REGEX + " ", "")) - 1;
        }

        Page<Poll> pollPage = pollRepository.findAllByDescriptionLike("%" + searchQuery + "%", PageRequest.of(page, PAGE_SIZE));

        String response = PollHelper.getPollPageView(page, pollPage, "#'" + searchQuery + "'");
        return message.createSingletonResponse(response);
    }
}
