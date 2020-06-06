package net.runelite.api.spells;

public enum StandardSpell {
    HOME_TELEPORT (0, 0, 1),
    WIND_STRIKE (1, 5.5, 2),
    CONFUSE (3, 13, 3),
    ENCHANT_CROSSBOW_BOLT (4, 9, 4),
    WATER_STRIKE (5, 7.5, 5),
    ENCHANT_LVL_1 (7, 17.5, 6),
    EARTH_STRIKE (9, 9.5, 7),
    WEAKEN (11, 21, 8),
    FIRE_STRIKE (13, 11.5, 9),
    BONES_TO_BANANAS (15, 25, 10),
    WIND_BOLT (17, 13.5, 11),
    CURSE (19, 29, 12),
    BIND (20, 30, 13),
    LOW_LEVEL_ALCHEMY (21, 31, 14),
    WATER_BOLT (23, 16.5, 15),
    VARROCK_TELEPORT (25, 35, 16),
    ENCHANT_LVL_2 (27, 37, 17),
    EARTH_BOLT (29, 19.5, 18),
    LUMBRIDGE_TELEPORT (31, 41, 19),
    TELEKINETIC_GRAB (33, 43, 20),
    FIRE_BOLT (35, 22.5, 21),
    FALADOR_TELEPORT (37, 48, 22),
    CRUMBLE_UNDEAD (39, 24.5, 23),
    TELEPORT_TO_HOUSE (40, 30, 24),
    WIND_BLAST (41, 25.5, 25),
    SUPERHEAT_ITEM (43, 53, 26),
    CAMELOT_TELEPORT (45, 55.5, 27),
    WATER_BLAST (47, 28.5, 28),
    ENCHANT_LVL_3 (49, 59, 29),
    IBAN_BLAST (50, 30, 30),
    SNARE (50, 60, 31),
    MAGIC_DART (50, 30, 32),
    ARDOUGNE_TELEPORT (51, 61, 33),
    EARTH_BLAST (53, 31.5, 34),
    HIGH_LEVEL_ALCHEMY (55, 65, 35),
    CHARGE_WATER_ORB (56, 66, 36),
    ENCHANT_LVL_4 (57, 67, 37),
    WATCHTOWER_TELEPORT (58, 68, 38),
    FIRE_BLAST (59, 34.5, 39),
    CHARGE_EARTH_ORB (60, 70, 40),
    BONES_TO_PEACHES (60, 35.5, 41),
    SARADOMIN_STRIKE (60, 35, 42),
    CLAWS_OF_GUTHIX (60, 35, 43),
    FLAMES_OF_ZAMORAK (60, 35, 44),
    TROLLHEIM_TELEPORT (61, 68, 45),
    WIND_WAVE (62, 36, 46),
    CHARGE_FIRE_ORB (63, 73, 47),
    TELEPORT_APE_ATOLL (64, 74, 48),
    WATER_WAVE (65, 37.5, 49),
    CHARGE_AIR_ORB (66, 76, 50),
    VULNERABILITY (66, 76, 51),
    ENCHANT_LVL_5 (68, 78, 52),
    TELEPORT_KOUREND_CASTLE (69, 82, 53),
    EARTH_WAVE (70, 40, 54),
    ENFEEBLE (73, 83, 55),
    TELEOTHER_LUMBRIDGE (74, 84, 56),
    FIRE_WAVE (75, 42.5, 57),
    ENTANGLE (79, 70, 58),
    STUN (80, 90, 59),
    CHARGE (80, 180, 60),
    WIND_SURGE (81, 44, 61),
    TELEOTHER_FALADOR (82, 92, 62),
    WATER_SURGE (85, 46, 63),
    TELE_BLOCK (85, 80, 64),
    TELEPORT_TO_BOUNTY_TARGET (85, 45, 65),
    ENCHANT_LVL_6 (87, 97, 66),
    TELEOTHER_CAMELOT (90, 100, 67),
    EARTH_SURGE (90, 48, 68),
    ENCHANT_LVL_7 (93, 110, 69),
    FIRE_SURGE (95, 51, 70);

    private final int level;
    private final double xp;
    private final int position;

    private StandardSpell(int level, double xp, int position) {
        this.level = level;
        this.xp = xp;
        this.position = position;
    }

    public int getLevel() {
        return this.level;
    }

    public double getXP() {
        return this.xp;
    }

    public int getPosition() {
        return this.position;
    }
}
