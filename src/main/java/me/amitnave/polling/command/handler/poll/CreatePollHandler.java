package me.amitnave.polling.command.handler.poll;

import lombok.AllArgsConstructor;
import me.amitnave.polling.command.handler.CommandHandler;
import me.amitnave.polling.command.handler.Settings;
import me.amitnave.polling.command.handler.ValidationResponse;
import me.amitnave.polling.command.handler.annotations.CommandInfo;
import me.amitnave.polling.command.handler.annotations.MemberOnly;
import me.amitnave.polling.command.handler.annotations.Private;
import me.amitnave.polling.command.handler.annotations.TextMatch;
import me.amitnave.polling.model.Member;
import me.amitnave.polling.model.Poll;
import me.amitnave.polling.model.PollOption;
import me.amitnave.polling.model.PollVote;
import me.amitnave.polling.repo.MemberRepository;
import me.amitnave.polling.repo.PollOptionRepository;
import me.amitnave.polling.repo.PollRepository;
import me.amitnave.polling.whatsapp.Message;
import me.amitnave.polling.whatsapp.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@TextMatch(regex = "^#צור סקר\\n[\\S\\s]+$")
@Private
@MemberOnly
@AllArgsConstructor
@CommandInfo(
        name = "יצירת סקר",
        description = "פקודה ליצירת סקר",
        example = "#צור סקר\n" +
                "מי הכי מגניב?\n" +
                "-גיל\n" +
                "-הרוש\n" +
                "-שגיא לוי",
        usage = "#צור סקר\n" +
                "[תוכן הסקר]\n" +
                "-[אופציה 1]\n" +
                "-[אופציה 2]\n" +
                "..."
)
public class CreatePollHandler implements CommandHandler {

    private MemberRepository memberRepository;
    private PollRepository pollRepository;
    private PollOptionRepository pollOptionRepository;

    public static final String CREATE_POLL_HEADER = "#צור סקר\n";

    @Override
    public ValidationResponse validate(Message message) {
        String content = message.getContent();

        content = content.replaceFirst(CREATE_POLL_HEADER, "");

        List<String> lines = Arrays.asList(content.split("\n"));

        if(lines.stream().noneMatch(line -> line.startsWith("-"))) return new ValidationResponse("לא סיפקת שום אופציות לסקר");
        if(lines.stream().allMatch(line -> line.startsWith("-"))) return new ValidationResponse("לא סיפקת שום תיאור לסקר");
        if(lines.stream().filter(line -> line.startsWith("-")).count() > 8) return new ValidationResponse("מותר עד 8 אופציות");

        return ValidationResponse.VALID_RESPONSE;
    }

    @Override
    public List<Response> process(Message message) {
        String content = message.getContent();

        content = content.replaceFirst(CREATE_POLL_HEADER, "");

        String[] lines = content.split("\n");

        List<PollOption> options = new ArrayList<>();
        Poll poll = new Poll();

        StringBuilder pollDescription = new StringBuilder();

        int optionIndex = 1;


        for (String line : lines) {
            if(line.startsWith("-")) {
                PollOption option = new PollOption();
                option.setPollListIndex(optionIndex++);
                option.setPoll(poll);
                option.setDescription(line.replaceFirst("-","").trim());
                options.add(option);
            } else {
                pollDescription.append(line).append("\n");
            }
        }

        PollOption invalidOption = new PollOption();
        invalidOption.setPollListIndex(optionIndex);
        invalidOption.setPoll(poll);
        invalidOption.setDescription("הסקר פגום");
        options.add(invalidOption);

        Member creator = message.getSendingMember(memberRepository);

        poll.setCreator(creator);
        poll.setDescription(pollDescription.toString().trim());
        poll.setOptions(options);

        pollRepository.save(poll);

        pollOptionRepository.saveAll(options);

        return Arrays.asList(
                message.createDirectResponse( "הסקר הוגש בהצלחה"),
                new Response(Settings.POLLS_CHAT, poll.display(false, false), "")
        );
    }
}
