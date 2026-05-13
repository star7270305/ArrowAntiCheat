package me.arrow.enums;

import lombok.Getter;
import me.arrow.Arrow;
import me.arrow.utils.ChatUtils;

import java.util.Arrays;
import java.util.List;

/**
 * A message type enumerations class in order to cache our messages from our theme and easily grab them.
 */
@Getter
public enum MsgType {
    MAIN_THEME_COLOR(Arrow.getInstance().getThemeManager().getTheme().getString("main_theme_color")),
    SECOND_THEME_COLOR(Arrow.getInstance().getThemeManager().getTheme().getString("second_theme_color")),
    EXPERIMENTAL_SYMBOL(Arrow.getInstance().getThemeManager().getTheme().getString("experimental_symbol")),
    HOVER_SYMBOL(Arrow.getInstance().getThemeManager().getTheme().getString("hover_symbol")),
    PREFIX(ChatUtils.format(Arrow.getInstance().getThemeManager().getTheme().getString("prefix"))),
    NO_PERMISSION(PREFIX.getMessage() + ChatUtils.format(Arrow.getInstance().getThemeManager().getTheme().getString("no_perm"))),
    CONSOLE_COMMANDS(PREFIX.getMessage() + ChatUtils.format(Arrow.getInstance().getThemeManager().getTheme().getString("console_commands"))),
    ALERT_MESSAGE(PREFIX.getMessage() + ChatUtils.format(Arrow.getInstance().getThemeManager().getTheme().getString("alert_message"))),
    ALERT_HOVER(stringFromList(Arrays.asList(
            "%informationtitleformatted%",
            "%informationformatted%",
            "(Ping: %ping% TPS: %tps%) &7(Click to teleport)"
    ))),
    PUNISH_BROADCAST(stringFromList(Arrow.getInstance().getThemeManager().getTheme().getConfig().getStringList("punish_broadcast")));

    private final String message;

    MsgType(String message) {
        this.message = message;
    }

    private static String stringFromList(List<String> list) {

        StringBuilder sb = new StringBuilder();

        int size = list.size();

        for (int i = 0; i < size; i++) {

            sb.append(list.get(i));

            if (size - 1 != i) sb.append("\n");
        }

        return ChatUtils.format(sb.toString());
    }
}