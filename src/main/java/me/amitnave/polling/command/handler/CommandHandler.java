package me.amitnave.polling.command.handler;

import me.amitnave.polling.whatsapp.Message;
import me.amitnave.polling.whatsapp.Response;

import java.util.List;

public interface CommandHandler {

    public ValidationResponse validate(Message message);
    public List<Response> process(Message message);
}
