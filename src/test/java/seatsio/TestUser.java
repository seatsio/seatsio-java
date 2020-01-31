package seatsio;

public class TestUser {

    public String email;
    public String secretKey;
    public String designerKey;
    public TestUserWorkspace mainWorkspace;

    public static class TestUserWorkspace {

        public TestTechnicalUser primaryUser;
    }

    public static class TestTechnicalUser {

        public long id;
    }
}
