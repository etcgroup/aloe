package etc.aloe;

import java.util.Random;

/**
 * Global singleton Random class.
 */
public abstract class RandomProvider {

    private static Random _instance;

    public static void setRandom(Random random) {
        _instance = random;
    }

    public static Random getRandom() {
        return _instance;
    }
}
