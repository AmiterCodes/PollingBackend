package me.amitnave.polling.whatsapp;

import lombok.Data;
import me.amitnave.polling.model.Member;
import me.amitnave.polling.repo.MemberRepository;

import javax.swing.text.StringContent;
import java.util.Collections;
import java.util.List;

@Data
public class Message {
    private String messageId;

    private String chatId;
    private String content;
    private String senderPhone;

    private boolean respondingToBot;
    private String respondingMessageText;

    public Member getSendingMember(MemberRepository repository) {
        return repository.findByPhone(senderPhone);
    }

    public boolean isPrivate() {
        return chatId.endsWith("@c.us");
    }

    public Response createDirectResponse(String content) {
        return new Response(chatId, content, messageId);
    }

    public Response createPrivateResponse(String content) {
        return new Response(senderPhone, content, messageId);
    }

    public List<Response> createSingletonPrivateResponse(String content) {
        return Collections.singletonList(createPrivateResponse(content));
    }

    public List<Response> createSingletonResponse(String content) {
        return Collections.singletonList(createDirectResponse(content));
    }


}
