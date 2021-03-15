package me.doggy.justguard.utils;


import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.consts.Texts;
import me.doggy.justguard.flag.FlagPath;
import me.doggy.justguard.region.Region;
import org.slf4j.Logger;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;


public class MessageUtils {

    public static final Logger logger = JustGuard.getInstance().getLogger();

    public static void sendList(MessageReceiver receiver, List<Object> list, int page, int pageLength) {
        sendList(receiver, list, page, pageLength, (key)->{ return Text.of(key.toString()); });
    }

    public static void sendList(MessageReceiver receiver, List<Object> list, int page, int pageLength, Function<Object, Text> func) {
        final int pagesNum = (list.size()-1)/pageLength + 1;

        page = Math.min(1, Math.max(pagesNum, page)) - 1;
        int startIndex = pageLength * page;
        int endIndex = Math.min(list.size(), startIndex + pageLength);

        list = list.subList(startIndex, endIndex);

        String endStr = "---<" + TextManager.getText("page") + " " + (page + 1) + "/" + pagesNum + ">---";
        send(receiver, Text.of(new String(new char[endStr.length()]).replace('\0', '-')));

        int i = startIndex;
        for (Object key : list) {
            send(receiver, Text.of(String.valueOf(i++), ". ", func.apply(key)));
        }

        send(receiver, Text.of(endStr));
    }
    public static void sendList(MessageReceiver receiver, List<Object> list, int page, int pageLength, Consumer<Object> func) {
        final int pagesNum = (list.size()-1)/pageLength + 1;

        page = Math.min(1, Math.max(pagesNum, page)) - 1;
        int startIndex = pageLength * page;
        int endIndex = Math.min(list.size(), startIndex + pageLength);

        list = list.subList(startIndex, endIndex);

        String endStr = "---<" + TextManager.getText("page") + " " + (page + 1) + "/" + pagesNum + ">---";
        send(receiver, Text.of(new String(new char[endStr.length()]).replace('\0', '-')));

        int i = startIndex;
        for (Object key : list) {
            send(receiver, Text.of(String.valueOf(i++), ": "));
            func.accept(key);
        }

        send(receiver, Text.of(endStr));
    }

    public static void send(MessageReceiver receiver, Text text) {
        if(!text.isEmpty())
            receiver.sendMessage(text);
    }

    public static void sendError(MessageReceiver receiver, Text text) {
        receiver.sendMessage(Text.of(TextColors.RED,"error: ").concat(text));
    }
    public static void sendErrorNoFlagAccess(MessageReceiver receiver, FlagPath flagPath) {
        sendError(receiver, Text.of(TextManager.getText(
                Texts.DONT_HAVE_FLAG_ACCESS,
                flagPath.getFullPath()
        )));
    }

    public static void sendRegionInfo(MessageReceiver receiver, Region region) {
        Text[] texts = new Text[]{
                Text.of( Texts.UUID + ": " + region.getUniqueId().toString()),
                Text.of(Texts.MIN_POSITION + ": " + region.getBounds().getMin().toString()),
                Text.of(Texts.MAX_POSITION + ": " + region.getBounds().getMax().toString())
        };

        for (Text text : texts) {
            send(receiver, text);
        }

    }

}
