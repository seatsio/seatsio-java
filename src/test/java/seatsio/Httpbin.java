package seatsio;

public class Httpbin {

    private static final String DEFAULT_URL = "https://httpbingo.org";

    public static String httpbinUrl() {
        return System.getenv().getOrDefault("HTTPBIN_URL", DEFAULT_URL);
    }

    public static String httpbinUrl(String path) {
        return httpbinUrl() + path;
    }
}

