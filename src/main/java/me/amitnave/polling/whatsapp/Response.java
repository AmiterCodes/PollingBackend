package me.amitnave.polling.whatsapp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Response {

    private String chatId;
    private String content;

    @Nullable
    private String respondingTo;
}
