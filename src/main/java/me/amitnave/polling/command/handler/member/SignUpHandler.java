package me.amitnave.polling.command.handler.member;

import lombok.AllArgsConstructor;
import me.amitnave.polling.command.handler.CommandHandler;
import me.amitnave.polling.command.handler.HandlerHelper;
import me.amitnave.polling.command.handler.ValidationResponse;
import me.amitnave.polling.command.handler.annotations.CommandInfo;
import me.amitnave.polling.command.handler.annotations.Private;
import me.amitnave.polling.command.handler.annotations.TextMatch;
import me.amitnave.polling.model.Member;
import me.amitnave.polling.repo.MemberRepository;
import me.amitnave.polling.whatsapp.Message;
import me.amitnave.polling.whatsapp.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Private
@TextMatch(regex = "^#הרשמה .+$")
@CommandInfo(
        name = "הרשמה",
        description = "רושם משתמש וואצאפ לבוט הסקרים",
        example = "#הרשמה שמוליק ישראל",
        usage = "#הרשמה [שם]"
)
public class SignUpHandler implements CommandHandler {

    private MemberRepository memberRepository;

    public static final String SIGNUP_COMMAND = "#הרשמה ";

    @Override
    public ValidationResponse validate(Message message) {

        String content = message.getContent();

        Member existingMember = message.getSendingMember(memberRepository);

        if(existingMember != null) {
            return new ValidationResponse(false, "אתה כבר משתמש קיים, " + existingMember.getName());
        }

        return ValidationResponse.VALID_RESPONSE;
    }



    @Override
    public List<Response> process(Message message) {

        String content = message.getContent();
        String name = content.replaceFirst(SIGNUP_COMMAND, "");

        Member member = new Member();
        member.setName(name);
        member.setPhone(message.getSenderPhone());
        memberRepository.save(member);

        return message.createSingletonResponse( "הרשמה התבצעה בהצלחה.");
    }
}
