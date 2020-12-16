package seatsio;

public enum Region {

    EU("eu"), US("us");

    private static final String BASE_URL = "https://api-{region}.seatsio.net";

    private final String id;

    Region(String id) {
        this.id = id;
    }

    public String getUrl() {
        return BASE_URL.replace("{region}", id);
    }
}
