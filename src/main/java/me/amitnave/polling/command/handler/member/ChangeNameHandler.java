package me.amitnave.polling.command.handler.member;

import lombok.AllArgsConstructor;
import me.amitnave.polling.command.handler.CommandHandler;
import me.amitnave.polling.command.handler.HandlerHelper;
import me.amitnave.polling.command.handler.ValidationResponse;
import me.amitnave.polling.command.handler.annotations.CommandInfo;
import me.amitnave.polling.command.handler.annotations.MemberOnly;
import me.amitnave.polling.command.handler.annotations.Private;
import me.amitnave.polling.command.handler.annotations.TextMatch;
import me.amitnave.polling.model.Member;
import me.amitnave.polling.repo.MemberRepository;
import me.amitnave.polling.whatsapp.Message;
import me.amitnave.polling.whatsapp.Response;

import java.util.Collections;
import java.util.List;

@Private
@MemberOnly
@TextMatch(regex = "^" + ChangeNameHandler.CHANGE_NAME_COMMAND + ".+$")
@CommandInfo(
        name = "שינוי שם",
        description = "פקודה לשינוי שם",
        example = "#שנה שם עמיר",
        usage = "#שנה שם [שם חדש]"
)
@AllArgsConstructor
public class ChangeNameHandler implements CommandHandler {

    private MemberRepository memberRepository;

    public static final String CHANGE_NAME_COMMAND = "#שנה שם ";

    @Override
    public ValidationResponse validate(Message message) {

        String content = message.getContent();

        if(content.equals(CHANGE_NAME_COMMAND)) return new ValidationResponse("אתה חייב לציין שם אחרי הפקודה, ראה #עזרה");

        return ValidationResponse.VALID_RESPONSE;
    }

    @Override
    public List<Response> process(Message message) {
        Member member = message.getSendingMember(memberRepository);

        String newName = message.getContent().replaceFirst(CHANGE_NAME_COMMAND, "");

        member.setName(newName);

        memberRepository.save(member);
        return message.createSingletonResponse( "השם שלך עודכן בהצלחה, " + newName);
    }
}
