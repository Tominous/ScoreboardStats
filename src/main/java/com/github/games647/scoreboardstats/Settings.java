package com.github.games647.scoreboardstats;

import com.github.games647.scoreboardstats.scoreboard.SbManager;
import com.github.games647.scoreboardstats.scoreboard.VariableReplacer;
import com.github.games647.variables.ConfigurationPaths;
import com.github.games647.variables.Message;
import com.github.games647.variables.Other;
import com.github.games647.variables.Permissions;
import com.github.games647.variables.SpecialCharacter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import org.fusesource.jansi.Ansi;

public final class Settings {

    private static final ScoreboardStats PLUGIN = ScoreboardStats.getInstance();

    private static boolean             pvpStats;
    private static boolean             tempScoreboard;
    private static boolean             hideVanished;
    private static boolean             sound;
    private static boolean             updateInfo;
    private static boolean             packetsystem;

    private static String              title;
    private static String              tempTitle;
    private static String              tempColor;
    private static String              topType;

    private static int                 intervall;
    private static int                 topitems;
    private static int                 tempShow;
    private static int                 tempDisapper;

    private static final Map<String, String> ITEMS = new HashMap<String, String>(14);
    private static List<String> disabledWorlds;

    public static void loadConfig() {
        PLUGIN.reloadConfig();

        final FileConfiguration config = PLUGIN.getConfig();

        loaditems(config.getConfigurationSection(ConfigurationPaths.ITEMS));

        hideVanished    = config.getBoolean(ConfigurationPaths.HIDE_VANISHED);
        sound           = config.getBoolean(ConfigurationPaths.SOUNDS);
        pvpStats        = config.getBoolean(ConfigurationPaths.PVPSTATS);
        updateInfo      = config.getBoolean(ConfigurationPaths.UPDATE_INFO);
        packetsystem    = config.getBoolean(ConfigurationPaths.PACKET_SYSTEM);

        disabledWorlds  = config.getStringList(ConfigurationPaths.DISABLED_WORLDS);
        intervall       = config.getInt(ConfigurationPaths.UPDATE_DELAY);
        title           = ChatColor.translateAlternateColorCodes(Other.CHATCOLOR_CHAR,
                checkLength(replaceSpecialCharacters(config.getString(ConfigurationPaths.TITLE)), Other.OBJECTIVE_LIMIT));

        if (config.getBoolean(ConfigurationPaths.TEMP)
                && pvpStats) {
            tempScoreboard  = true;

            topitems        = checkItems(config.getInt(ConfigurationPaths.TEMP_ITEMS));

            tempShow        = config.getInt(ConfigurationPaths.TEMP_SHOW);
            tempDisapper    = config.getInt(ConfigurationPaths.TEMP_DISAPPER);

            topType         = config.getString(ConfigurationPaths.TEMP_TYPE);

            tempColor       = ChatColor.translateAlternateColorCodes(Other.CHATCOLOR_CHAR, config.getString(ConfigurationPaths.TEMP_COLOR));
            tempTitle       = ChatColor.translateAlternateColorCodes(Other.CHATCOLOR_CHAR,
                    checkLength(replaceSpecialCharacters(config.getString(ConfigurationPaths.TEMP_TITLE)), Other.OBJECTIVE_LIMIT));
        }
    }

    public static void sendUpdate(Player player, boolean complete) {
        final Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);

        if (!player.hasPermission(Permissions.USE_PERMISSION)
                || objective == null
                || !objective.getName().equals(Other.PLUGIN_NAME)
                || PLUGIN.getHidelist().contains(player.getName())) {
            return;
        }

        for (final Map.Entry<String, String> entry : ITEMS.entrySet()) {
            SbManager.sendScore(
                    objective, entry.getKey(), VariableReplacer.getReplacedInt(entry.getValue(), player), complete);
        }
    }

    private static String checkLength(String check, int limit) {
        if (check.length() > limit) {
            final String cut = check.substring(0, limit);
            Bukkit.getLogger().log(Level.WARNING, Ansi.ansi().fg(Ansi.Color.RED) + Message.LOG_NAME + "{0}" + Ansi.ansi().fg(Ansi.Color.DEFAULT), String.format(Message.LONGER_THAN_LIMIT, cut, limit));
            return cut;
        }

        return check;
    }

    private static int checkItems(int input) {
        if (input >= Other.MINECRAFT_LIMIT) {
            Bukkit.getLogger().log(Level.WARNING, "{0}" + Message.LOG_NAME + Message.TOO_LONG_LIST + Ansi.ansi().fg(Ansi.Color.DEFAULT), Ansi.ansi().fg(Ansi.Color.RED));
            return Other.MINECRAFT_LIMIT - 1;
        }

        return input;
    }

    private static void loaditems(ConfigurationSection config) {
        final Set<String> keys = config.getKeys(false);

        if (!ITEMS.isEmpty()) {
            ITEMS.clear();
        }

        for (final String key : keys) {
            if (ITEMS.size() >= Other.MINECRAFT_LIMIT) {
                Bukkit.getLogger().log(Level.WARNING, "{0}" + Message.LOG_NAME + Message.TOO_LONG_LIST + Ansi.ansi().fg(Ansi.Color.DEFAULT), Ansi.ansi().fg(Ansi.Color.RED));
                break;
            }

            ITEMS.put(ChatColor.translateAlternateColorCodes(ChatColor.COLOR_CHAR, checkLength(replaceSpecialCharacters(key), Other.MINECRAFT_LIMIT)), config.getString(key));
        }
    }

    private static String replaceSpecialCharacters(String input) {
        return input
                .replace(SpecialCharacter.VAR_HEART     , SpecialCharacter.HEART)
                .replace(SpecialCharacter.VAR_CHECK     , SpecialCharacter.CHECK)

                .replace(SpecialCharacter.VAR_LESS      , SpecialCharacter.LESS)
                .replace(SpecialCharacter.VAR_BIGGER    , SpecialCharacter.BIGGER)

                .replace(SpecialCharacter.VAR_STAR      , SpecialCharacter.STAR)
                .replace(SpecialCharacter.VAR_ROUND_STAR, SpecialCharacter.ROUND_STAR)
                .replace(SpecialCharacter.VAR_STARS     , SpecialCharacter.STARS)

                .replace(SpecialCharacter.VAR_CROWN     , SpecialCharacter.CROWN)
                .replace(SpecialCharacter.VAR_CHESS     , SpecialCharacter.CHESS)

                .replace(SpecialCharacter.VAR_TOP       , SpecialCharacter.TOP)
                .replace(SpecialCharacter.VAR_BUTTON    , SpecialCharacter.BUTTON)
                .replace(SpecialCharacter.VAR_SIDE      , SpecialCharacter.SIDE)
                .replace(SpecialCharacter.VAR_MID       , SpecialCharacter.MID)

                .replace(SpecialCharacter.VAR_ONE       , SpecialCharacter.ONE)
                .replace(SpecialCharacter.VAR_TWO       , SpecialCharacter.TWO)
                .replace(SpecialCharacter.VAR_THREE     , SpecialCharacter.THREE)
                .replace(SpecialCharacter.VAR_FOUR      , SpecialCharacter.FOUR)
                .replace(SpecialCharacter.VAR_FIVE      , SpecialCharacter.FIVE)
                .replace(SpecialCharacter.VAR_SIX       , SpecialCharacter.SIX)
                .replace(SpecialCharacter.VAR_SEVEN     , SpecialCharacter.SEVEN)
                .replace(SpecialCharacter.VAR_EIGHT     , SpecialCharacter.EIGHT)
                .replace(SpecialCharacter.VAR_NINE      , SpecialCharacter.NINE)
                .replace(SpecialCharacter.VAR_TEN       , SpecialCharacter.TEN)

                .replace(SpecialCharacter.VAR_RIGHT_UP  , SpecialCharacter.RIGHT_UP)
                .replace(SpecialCharacter.VAR_LEFT_UP   , SpecialCharacter.LEFT_UP)
                .replace(SpecialCharacter.VAR_PHONE     , SpecialCharacter.PHONE)
                .replace(SpecialCharacter.VAR_PLANE     , SpecialCharacter.PLANE)
                .replace(SpecialCharacter.VAR_FLOWER    , SpecialCharacter.FLOWER)
                .replace(SpecialCharacter.VAR_MAIL      , SpecialCharacter.MAIL)
                .replace(SpecialCharacter.VAR_HAND      , SpecialCharacter.HAND)
                .replace(SpecialCharacter.VAR_WRITE     , SpecialCharacter.WRITE)
                .replace(SpecialCharacter.VAR_PENCIL    , SpecialCharacter.PENCIL)
                .replace(SpecialCharacter.VAR_X         , SpecialCharacter.X)
                .replace(SpecialCharacter.VAR_T_STAR    , SpecialCharacter.T_STAR)

                .replace(SpecialCharacter.VAR_ARROW     , SpecialCharacter.ARROW)
                .replace(SpecialCharacter.VAR_ARROW1    , SpecialCharacter.ARROW1)
                .replace(SpecialCharacter.VAR_ARROW2    , SpecialCharacter.ARROW2)
                .replace(SpecialCharacter.VAR_ARROW3    , SpecialCharacter.ARROW3)
                .replace(SpecialCharacter.VAR_ARROW4    , SpecialCharacter.ARROW4)

                .replace(SpecialCharacter.VAR_ONE1      , SpecialCharacter.ONE1)
                .replace(SpecialCharacter.VAR_TWO1      , SpecialCharacter.TWO1)
                .replace(SpecialCharacter.VAR_THREE1    , SpecialCharacter.THREE1)
                .replace(SpecialCharacter.VAR_FOUR1     , SpecialCharacter.FOUR1)
                .replace(SpecialCharacter.VAR_FIVE1     , SpecialCharacter.FIVE1)
                .replace(SpecialCharacter.VAR_SIX1      , SpecialCharacter.SIX1)
                .replace(SpecialCharacter.VAR_SEVEN1    , SpecialCharacter.SEVEN1)
                .replace(SpecialCharacter.VAR_EIGHT1    , SpecialCharacter.EIGHT1)
                .replace(SpecialCharacter.VAR_NINE1     , SpecialCharacter.NINE1)
                .replace(SpecialCharacter.VAR_TEN1      , SpecialCharacter.TEN1);
    }

    public static boolean isPvpStats() {
        return pvpStats;
    }

    public static boolean isTempScoreboard() {
        return tempScoreboard;
    }

    public static boolean isHideVanished() {
        return hideVanished;
    }

    public static boolean isSound() {
        return sound;
    }

    public static boolean isUpdateInfo() {
        return updateInfo;
    }

    public static boolean isPacketsystem() {
        return packetsystem;
    }

    public static String getTitle() {
        return title;
    }

    public static String getTempTitle() {
        return tempTitle;
    }

    public static String getTempColor() {
        return tempColor;
    }

    public static String getTopType() {
        return topType;
    }

    public static int getIntervall() {
        return intervall;
    }

    public static int getTopitems() {
        return topitems;
    }

    public static int getTempShow() {
        return tempShow;
    }

    public static int getTempDisapper() {
        return tempDisapper;
    }

    public static int getItemsLenght() {
        return ITEMS.size();
    }

    public static boolean isDisabledWorld(String name) {
        return disabledWorlds.contains(name);
    }

    @Override
    public String toString() {
        return "SettingsHandler{pvpStats="  + pvpStats

                + ", tempScoreboard="       + tempScoreboard
                + ", hideVanished="         + hideVanished
                + ", sound="                + sound
                + ", title="                + title
                + ", tempTitle="            + tempTitle
                + ", tempColor="            + tempColor
                + ", topType="              + topType
                + ", intervall="            + intervall
                + ", topitems="             + topitems
                + ", tempShow="             + tempShow
                + ", tempDisapper="         + tempDisapper
                + ", items="                + ITEMS
                + ", disabledWorlds="       + disabledWorlds

                + '}';
    }
}