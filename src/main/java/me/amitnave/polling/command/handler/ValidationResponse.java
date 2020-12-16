package me.amitnave.polling.command.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.amitnave.polling.whatsapp.Response;

@Data
@AllArgsConstructor
public class ValidationResponse {
    private boolean valid;
    private String errorMessage;

    public ValidationResponse(String error) {
        this(false, error);
    }

    public boolean isEmpty() {
        return errorMessage.equals("");
    }

    public Response toResponse(String chatId, String messageId) {
        return new Response(chatId,"[⚠] " + errorMessage, messageId);
    }

    public static final ValidationResponse NOT_PRIVATE_ERROR =
            new ValidationResponse( "הפקודה זמינה רק בצ'אט פרטי.");
    public static final ValidationResponse NOT_MEMBER_ERROR =
            new ValidationResponse("אינך משתמש רשום, להרשמה השתמש ב#הרשמה שם\nלדוגמא:\n#הרשמה שמוליק");
    public static final ValidationResponse NO_RESPONSE =
            new ValidationResponse("");
    public static final ValidationResponse VALID_RESPONSE =
            new ValidationResponse(true, "");

}
