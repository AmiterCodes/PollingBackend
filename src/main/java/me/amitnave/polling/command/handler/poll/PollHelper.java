package me.amitnave.polling.command.handler.poll;

import me.amitnave.polling.command.handler.annotations.CommandInfo;
import me.amitnave.polling.model.Poll;
import org.springframework.data.domain.Page;


public class PollHelper {
    public static String getPollPageView(int page, Page<Poll> polls, String command) {
        StringBuilder response = new StringBuilder("*");
        response.append("רשימת הסקרים* עמוד ");
        response.append(page + 1);
        response.append("/");
        response.append(polls.getTotalPages()).append("\n");
        response.append("מציג ")
                .append(polls.getNumberOfElements())
                .append(" מתוך ")
                .append(polls.getTotalElements())
                .append(" סקרים");

        polls.forEach(poll -> {
            response.append("\n").append(poll.display(false, true)).append("\n");
        });

        if(polls.isEmpty()) {
            response.append("\n\n").append("העמוד הזה ריק.").append("\n\n");
        }

        if(page + 1 < polls.getTotalPages()) {
            response.append("\n")
                    .append("בשביל לגשת לעמוד הבא, השתמשו ב")
                    .append("```");
            response.append(command).append(" ").append(page + 2)
                    .append("```");;
        }
        return response.toString();
    }
}
