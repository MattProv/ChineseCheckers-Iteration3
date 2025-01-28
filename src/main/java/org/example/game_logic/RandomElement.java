package org.example.game_logic;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomElement {
    public static <T> T getRandomElement(List<T> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }
}