package juuxel.advent2024;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

public final class Loader {
    public static InputStream stream(int day) {
        return Loader.class.getResourceAsStream("/day" + day + ".txt");
    }

    public static Stream<String> lines(int day) throws IOException {
        try (InputStream stream = stream(day)) {
            return new String(stream.readAllBytes()).lines();
        }
    }
}
