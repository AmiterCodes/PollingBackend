package me.amitnave.polling.command.handler.member;

import lombok.AllArgsConstructor;
import me.amitnave.polling.command.handler.CommandHandler;
import me.amitnave.polling.command.handler.ValidationResponse;
import me.amitnave.polling.command.handler.annotations.CommandInfo;
import me.amitnave.polling.command.handler.annotations.Private;
import me.amitnave.polling.command.handler.annotations.TextMatch;
import me.amitnave.polling.model.Member;
import me.amitnave.polling.repo.MemberRepository;
import me.amitnave.polling.whatsapp.Message;
import me.amitnave.polling.whatsapp.Response;

import java.util.List;

@Private
@TextMatch(regex = "#רשומים")
@CommandInfo(
        name = "רשימת משתמשים",
        description = "מציג רשימה של כל המשתמשים",
        example = "#רשומים",
        usage = "#רשומים"
)
@AllArgsConstructor
public class ListMembersHandler implements CommandHandler {

    private MemberRepository memberRepository;

    @Override
    public ValidationResponse validate(Message message) {
        return ValidationResponse.VALID_RESPONSE;
    }

    @Override
    public List<Response> process(Message message) {

        StringBuilder builder = new StringBuilder("*רשימת רשומים* 👥");
        builder.append("\n\n");
        Iterable<Member> memberIterable = memberRepository.findAll();

        memberIterable.forEach(member -> builder.append(member.getName()).append("\n"));



        return message.createSingletonResponse(builder.toString());
    }
}
