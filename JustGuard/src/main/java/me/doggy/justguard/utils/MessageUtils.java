package me.doggy.justguard.utils;


import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.TextManager;
import org.slf4j.Logger;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


public class MessageUtils {

    public static final Logger logger = JustGuard.getInstance().getLogger();

    public static void SendList(MessageReceiver receiver, List<String> list, int page, int pageLength)
    {
        SendList(receiver, list, page, pageLength, (key)->Text.of(key));
    }

    public static void SendList(MessageReceiver receiver, List<String> list, int page, int pageLength, Function<String, Text> func)
    {
        final int pagesNum = (list.size()-1)/pageLength + 1;

        page = Math.min(1, Math.max(pagesNum, page)) - 1;
        int startIndex = pageLength * page;
        int endIndex = Math.min(list.size(), startIndex + pageLength);

        list = list.subList(startIndex, endIndex);

        String endStr = "---<" + TextManager.getText("page") + " " + (page + 1) + "/" + pagesNum + ">---";
        Send(receiver, Text.of(new String(new char[endStr.length()]).replace('\0', '-')));

        int i = startIndex;
        for (String key : list)
        {
            Send(receiver, Text.of(String.valueOf(i++), ". ", func.apply(key)));
        }

        Send(receiver, Text.of(endStr));
    }

    public static void Send(MessageReceiver receiver, Text text)
    {
        receiver.sendMessage(text);
    }

    public static void SendError(MessageReceiver receiver, Text text)
    {
        receiver.sendMessage(Text.of(TextColors.RED,"error: ").concat(text));
    }

}
