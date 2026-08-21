package com.daqem.tinymobfarm.util;

import net.minecraft.world.entity.player.Player;

public final class XpUtils {

    private XpUtils() {
    }

    public static int getPlayerXp(Player player) {
        return player.totalExperience;
    }

    /**
     * XP points needed to advance from {@code level} to {@code level + 1} (vanilla formula):
     * 2L+7 for L 0-15, 5L-38 for L 16-30, 9L-158 for L 31+.
     */
    public static int xpBarCap(int level) {
        if (level >= 31) return 9 * level - 158;
        if (level >= 16) return 5 * level - 38;
        return 2 * level + 7;
    }

    /**
     * Total XP points required to reach {@code level}.
     */
    public static int getExperienceForLevel(int level) {
        int total = 0;
        for (int l = 0; l < level; l++) {
            total += xpBarCap(l);
        }
        return total;
    }

    public static int getLevelForExperience(int xp) {
        int level = 0;
        while (level < 1000 && getExperienceForLevel(level + 1) <= xp) {
            level++;
        }
        return level;
    }

    /**
     * Adds (or subtracts, if {@code amount} is negative) raw XP points and reconciles
     * the player's level and progress bar. Negative amounts are clamped at 0 total XP.
     */
    public static void addPlayerXp(Player player, int amount) {
        player.totalExperience = Math.max(0, player.totalExperience + amount);
        int level = getLevelForExperience(player.totalExperience);
        player.experienceLevel = level;
        int xpForLevel = getExperienceForLevel(level);
        int bar = xpBarCap(level);
        player.experienceProgress = bar <= 0 ? 0.0F : (float) (player.totalExperience - xpForLevel) / (float) bar;
    }
}
