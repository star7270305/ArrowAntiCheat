package me.arrow.commands.subcommands;

import me.arrow.Arrow;
import me.arrow.commands.SubCommand;
import me.arrow.enums.MsgType;
import me.arrow.enums.Permissions;
import me.arrow.managers.profile.Profile;
import me.arrow.playerdata.data.CheckHolder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static me.arrow.utils.customutils.OtherUtility.*;

public class VerboseCommand extends SubCommand {

    private final Arrow plugin;

    public VerboseCommand(Arrow plugin) {
        this.plugin = plugin;
    }

    @Override
    protected String getName() {
        return "verbose";
    }

    @Override
    protected String getDescription() {
        return "Toggle the verbose";
    }

    @Override
    protected String getSyntax() {
        return "verbose";
    }

    @Override
    protected String getPermission() {
        return Permissions.COMMAND_VERBOSE.getPermission();
    }

    @Override
    protected int maxArguments() {
        return 1;
    }

    @Override
    protected boolean canConsoleExecute() {
        return false;
    }

    @Override
    protected void perform(CommandSender sender, String[] args) {
        final Profile profile = plugin.getProfileManager().getProfile((Player) sender);

        if (args.length < 2) {
            sender.sendMessage(translate("&cUsage: /arrow verbose <check|All>"));
            return;
        }

        String input = args[1]; // case-sensitive
        CheckHolder holder = profile.getCheckHolder();

        // validate input
        boolean exists = Arrays.stream(holder.getChecks())
                .anyMatch(check -> check.getClass().getSimpleName().equals(input));

        if (!exists && !input.equals("All")) {
            sender.sendMessage(translate("&cUnknown check: " + input));
            return;
        }

        String previous = profile.getVerbosingClass();

        if (previous.equals(input)) {
            // Toggle off if it's the same
            profile.setVerbose(false);
            profile.setVerbosingClass("None");
            sender.sendMessage(translate(MsgType.PREFIX.getMessage() + "&cDisabled verbose for &f" + input));
        } else {
            // Enable and replace previous
            profile.setVerbose(true);
            profile.setVerbosingClass(input);

            if (previous.equals("None")) {
                sender.sendMessage(translate(MsgType.PREFIX.getMessage() + "&aEnabled verbose for &f" + input));
            } else {
                sender.sendMessage(translate(MsgType.PREFIX.getMessage() +
                        "&aEnabled verbose for &f" + input + " &7and disabled verbose for &f" + previous));
            }
        }
    }




//    @Override
//    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
//        if (args.length == 2 && sender instanceof Player player) {
//            Profile profile = plugin.getProfileManager().getProfile(player);
//            if (profile == null) return Collections.emptyList();
//
//            return Arrays.stream(profile.getCheckHolder().getChecks())
//                    .map(check -> check.getClass().getSimpleName()) // only class name, no package
//                    .filter(name -> name.startsWith(args[1]))
//                    .toList();
//        }
//        return Collections.emptyList();
//    }

}
