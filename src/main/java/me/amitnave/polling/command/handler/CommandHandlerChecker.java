package me.amitnave.polling.command.handler;

import lombok.AllArgsConstructor;
import me.amitnave.polling.command.handler.annotations.MemberOnly;
import me.amitnave.polling.command.handler.annotations.Private;
import me.amitnave.polling.command.handler.annotations.TextMatch;
import me.amitnave.polling.command.handler.member.ChangeNameHandler;
import me.amitnave.polling.command.handler.member.ListMembersHandler;
import me.amitnave.polling.command.handler.member.SignUpHandler;
import me.amitnave.polling.command.handler.misc.HelpHandler;
import me.amitnave.polling.command.handler.poll.*;
import me.amitnave.polling.command.handler.vote.VoteOnPollHandler;
import me.amitnave.polling.repo.MemberRepository;
import me.amitnave.polling.repo.PollOptionRepository;
import me.amitnave.polling.repo.PollRepository;
import me.amitnave.polling.repo.PollVoteRepository;
import me.amitnave.polling.whatsapp.Message;
import me.amitnave.polling.whatsapp.Response;

import java.util.*;

@AllArgsConstructor
public class CommandHandlerChecker {

    private MemberRepository memberRepository;
    private PollRepository pollRepository;
    private PollOptionRepository pollOptionRepository;
    private PollVoteRepository pollVoteRepository;

    public List<Response> processMessage(Message message) {
        CommandHandler[] commandHandlers = getCommandHandlers();

        for (CommandHandler handler : commandHandlers) {
            Class<? extends CommandHandler> handlerClass = handler.getClass();

            String messageContent = message.getContent();

            boolean valid = true;
            boolean certain = false;

            if(handlerClass.isAnnotationPresent(TextMatch.class)) {
                TextMatch match = handlerClass.getAnnotation(TextMatch.class);
                if(!messageContent.matches(match.regex())) valid = false;
                if(valid) certain = true;
            }

            // if we arrived here, the right command has been detected.

            if(handlerClass.isAnnotationPresent(MemberOnly.class) && valid) {
                if(message.getSendingMember(memberRepository) == null) {
                    if(certain)
                        return message.createSingletonPrivateResponse(ValidationResponse.NOT_MEMBER_ERROR.getErrorMessage());
                    valid = false;
                }
            }
            if(handlerClass.isAnnotationPresent(Private.class) && valid) {
                if(!message.isPrivate()) {
                    if(certain)
                        return message.createSingletonPrivateResponse(ValidationResponse.NOT_PRIVATE_ERROR.getErrorMessage());
                    valid = false;
                }
            }

            if(valid) {
                ValidationResponse validationResponse = handler.validate(message);
                if (!validationResponse.isValid()) {
                    if (!validationResponse.isEmpty())
                        return Collections.singletonList(new Response(message.getSenderPhone(), "⚠ " + validationResponse.getErrorMessage(), message.getMessageId()));
                } else {
                    return handler.process(message);
                }
            }
        }

        return Collections.emptyList();
    }

    public CommandHandler[] getCommandHandlers() {
        return new CommandHandler[]{
                    new SignUpHandler(memberRepository),
                    new CreatePollHandler(memberRepository, pollRepository, pollOptionRepository),
                    new VoteOnPollHandler(pollRepository,pollOptionRepository,pollVoteRepository,memberRepository),
                    new PollStatusHandler(pollRepository),
                    new ChangeNameHandler(memberRepository),
                    new PollListHandler(pollRepository),
                    new PollSearchHandler(pollRepository),
                    new ReminderHandler(pollRepository),
                    new HelpHandler(this),
                    new ListMembersHandler(memberRepository)
            };
    }
}
