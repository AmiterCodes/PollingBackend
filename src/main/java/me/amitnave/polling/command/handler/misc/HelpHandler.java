package me.amitnave.polling.command.handler.misc;

import lombok.AllArgsConstructor;
import me.amitnave.polling.command.handler.CommandHandler;
import me.amitnave.polling.command.handler.CommandHandlerChecker;
import me.amitnave.polling.command.handler.ValidationResponse;
import me.amitnave.polling.command.handler.annotations.CommandInfo;
import me.amitnave.polling.command.handler.annotations.Private;
import me.amitnave.polling.command.handler.annotations.TextMatch;
import me.amitnave.polling.whatsapp.Message;
import me.amitnave.polling.whatsapp.Response;

import java.util.List;

@AllArgsConstructor
@Private
@TextMatch(regex = "^#עזרה$")
@CommandInfo(
        name = "עזרה",
        description = "פקודה שמציגה את ההודעה שאתם כרגע רואים",
        example = "#עזרה",
        usage = "#עזרה"
)
public class HelpHandler implements CommandHandler {

    private CommandHandlerChecker commandHandlerChecker;

    @Override
    public ValidationResponse validate(Message message) {
        return ValidationResponse.VALID_RESPONSE;
    }

    private static String commandInfoToString(CommandInfo info) {
        StringBuilder response = new StringBuilder();
        response.append("*").append(info.name()).append("*");
        response.append("\n");
        response.append("תיאור: ");
        response.append(info.description());
        response.append("\n");
        response.append("שימוש: ");
        response.append("\n  ```");
        response.append(info.usage());
        response.append("```\n");
        response.append("דוגמא: ");
        response.append("\n");
        response.append(info.example());

        return response.toString();
    }

    @Override
    public List<Response> process(Message message) {

        StringBuilder response = new StringBuilder("עזרה כללית");
        response.append("\n\n");


        for (CommandHandler handler : commandHandlerChecker.getCommandHandlers()) {
            Class<? extends CommandHandler> handlerClass = handler.getClass();
            if(handlerClass.isAnnotationPresent(CommandInfo.class)) {
                CommandInfo info = handlerClass.getAnnotation(CommandInfo.class);
                response.append(commandInfoToString(info)).append("\n\n");
            }
        }
        return message.createSingletonResponse(response.toString());
    }
}
