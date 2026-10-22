package com.example.model;

import java.util.HashMap;
import java.util.Map;

public final class CharacterCatalog {
    private static final Map<Long, String> CHARACTERS = new HashMap<>();
    private static final Map<Long, String> RANKS = new HashMap<>();

    static {
        CHARACTERS.put(102000001L, "Primis (Adam)");
        CHARACTERS.put(102000002L, "Nulla (Eve)");
        CHARACTERS.put(102000003L, "Ford");
        CHARACTERS.put(102000004L, "Olivia");
        CHARACTERS.put(102000005L, "Kelly");
        CHARACTERS.put(102000006L, "Nikita");
        CHARACTERS.put(102000007L, "Andrew");
        CHARACTERS.put(102000008L, "Misha");
        CHARACTERS.put(102000009L, "Kla");
        CHARACTERS.put(102000010L, "Paloma");
        CHARACTERS.put(102000011L, "Maxim");
        CHARACTERS.put(102000012L, "Antonio");
        CHARACTERS.put(102000013L, "Caroline");
        CHARACTERS.put(102000014L, "Miguel");
        CHARACTERS.put(102000015L, "Hayato");
        CHARACTERS.put(102000016L, "Moco");
        CHARACTERS.put(102000017L, "Wukong");
        CHARACTERS.put(102000018L, "Laura");
        CHARACTERS.put(102000019L, "Rafael");
        CHARACTERS.put(102000020L, "A124");
        CHARACTERS.put(102000021L, "Shani");
        CHARACTERS.put(102000022L, "Alok");
        CHARACTERS.put(102000023L, "Alvaro");
        CHARACTERS.put(102000024L, "Notora");
        CHARACTERS.put(102000025L, "Steffie");
        CHARACTERS.put(102000026L, "Jota");
        CHARACTERS.put(102000027L, "Kapella");
        CHARACTERS.put(102000028L, "Wolfrahh");
        CHARACTERS.put(102000029L, "Luqueta");
        CHARACTERS.put(102000030L, "Kelly The Swift");
        CHARACTERS.put(102000031L, "Hayato Firebrand");
        CHARACTERS.put(102000032L, "K (Captain Booyah)");
        CHARACTERS.put(102000033L, "Dasha");
        CHARACTERS.put(102000034L, "Chrono");
        CHARACTERS.put(102000035L, "Shirou");
        CHARACTERS.put(102000036L, "Skyler");
        CHARACTERS.put(102000037L, "Xayne");
        CHARACTERS.put(102000038L, "Maro");
        CHARACTERS.put(102000039L, "D-Bee");
        CHARACTERS.put(102000040L, "Dimitri");
        CHARACTERS.put(102000041L, "Thiva");
        CHARACTERS.put(102000042L, "Leon");
        CHARACTERS.put(102000043L, "Otho");
        CHARACTERS.put(102000044L, "Nairi");
        CHARACTERS.put(102000045L, "Kenta");
        CHARACTERS.put(102000046L, "J.Biebs");
        CHARACTERS.put(102000047L, "Iris");
        CHARACTERS.put(102000048L, "Tatsuya");
        CHARACTERS.put(102000049L, "Luna");
        CHARACTERS.put(102000050L, "Santino");
        CHARACTERS.put(102000051L, "Orion");
        CHARACTERS.put(102000052L, "Sonia");
        CHARACTERS.put(102000053L, "Suzy");
        CHARACTERS.put(102000054L, "Ignis");
        CHARACTERS.put(102000055L, "Ryden");
        CHARACTERS.put(102000056L, "Kairos");
        CHARACTERS.put(102000057L, "Kassie");

        // Ranks
        RANKS.put(301L, "Bronze I");
        RANKS.put(302L, "Bronze II");
        RANKS.put(303L, "Bronze III");
        RANKS.put(304L, "Silver I");
        RANKS.put(305L, "Silver II");
        RANKS.put(306L, "Silver III");
        RANKS.put(307L, "Gold I");
        RANKS.put(308L, "Gold II");
        RANKS.put(309L, "Gold III");
        RANKS.put(310L, "Gold IV");
        RANKS.put(311L, "Platinum I");
        RANKS.put(312L, "Platinum II");
        RANKS.put(313L, "Platinum III");
        RANKS.put(314L, "Platinum IV");
        RANKS.put(315L, "Diamond I");
        RANKS.put(316L, "Diamond II");
        RANKS.put(317L, "Diamond III");
        RANKS.put(318L, "Diamond IV");
        RANKS.put(319L, "Heroic");
        RANKS.put(320L, "Master");
        RANKS.put(321L, "Grandmaster");
    }

    private CharacterCatalog() {}

    public static String getCharacterName(long charId) {
        String name = CHARACTERS.get(charId);
        return name != null ? name : "Character #" + charId;
    }

    public static String getRankTitle(long rankId) {
        String rank = RANKS.get(rankId);
        return rank != null ? rank : (rankId > 0 ? "Rank " + rankId : "Unranked");
    }
}
