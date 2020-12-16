package me.amitnave.polling.command.handler;

public class HandlerHelper {

    public static boolean StartsWithAndNotEmpty(String prefix, String content) {
        if(!content.startsWith(prefix)) return false;
        return !content.replaceFirst(prefix, "").equals("");
    }

}
