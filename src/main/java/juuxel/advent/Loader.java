package juuxel.advent;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

public final class Loader {
    public static InputStream stream(int year, int day) {
        return Loader.class.getResourceAsStream("/" + year + "/day" + day + ".txt");
    }

    public static Stream<String> lines(int year, int day) throws IOException {
        try (InputStream stream = stream(year, day)) {
            return new String(stream.readAllBytes()).lines();
        }
    }
}
