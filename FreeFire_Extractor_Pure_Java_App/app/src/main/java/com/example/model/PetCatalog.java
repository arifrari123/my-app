package com.example.model;

import java.util.HashMap;
import java.util.Map;

public final class PetCatalog {
    private static final Map<Long, String> PETS = new HashMap<>();

    static {
        PETS.put(130000001L, "Kitty");
        PETS.put(130000002L, "Mechanical Puppy");
        PETS.put(130000003L, "Night Panther");
        PETS.put(130000004L, "Detective Panda");
        PETS.put(130000005L, "Shiba");
        PETS.put(130000006L, "Spirit Fox");
        PETS.put(130000007L, "Robo");
        PETS.put(130000008L, "Poring");
        PETS.put(130000009L, "Falco");
        PETS.put(130000010L, "Ottero");
        PETS.put(130000011L, "Mr. Waggor");
        PETS.put(130000012L, "Rockie");
        PETS.put(130000013L, "Beaston");
        PETS.put(130000014L, "Dreki");
        PETS.put(130000015L, "Moony");
        PETS.put(130000016L, "Dr. Beanie");
        PETS.put(130000017L, "Sensei Tig");
        PETS.put(130000018L, "Agent Hop");
        PETS.put(130000019L, "Yeti");
        PETS.put(130000020L, "Flash");
        PETS.put(130000021L, "Zasil");
        PETS.put(130000022L, "Finn");
        PETS.put(130000023L, "Hoot");
        PETS.put(130000024L, "Fang");
        PETS.put(130000025L, "Arvon");
        PETS.put(130000026L, "Kactus");
        PETS.put(130000027L, "Pug");
    }

    private PetCatalog() {}

    public static String getPetSpecies(long petId) {
        String name = PETS.get(petId);
        return name != null ? name : (petId > 0 ? "Pet #" + petId : "None");
    }
}
