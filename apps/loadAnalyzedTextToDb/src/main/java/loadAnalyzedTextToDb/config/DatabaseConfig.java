package loadAnalyzedTextToDb.config;

import java.util.Optional;

public class DatabaseConfig {
    private final String url;
    private final String username;
    private final String password;


    // Private constructor to enforce creation through builder/factory method
    private DatabaseConfig(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;

    }

    // Fluent configuration loading method
    public static DatabaseConfig load() {
        return new Builder().build();
    }

    // Nested Builder class for flexible configuration
    public static class Builder {
        private Optional<String> url = Optional.ofNullable(System.getenv("DB_URL"));
        private Optional<String> username = Optional.ofNullable(System.getenv("DB_USERNAME"));
        private Optional<String> password = Optional.ofNullable(System.getenv("DB_PASSWORD"));


        public Builder validateRequired() {
            // Validate critical environment variables
            url.orElseThrow(() -> new IllegalStateException("Missing DB_URL environment variable"));
            username.orElseThrow(() -> new IllegalStateException("Missing DB_USERNAME environment variable"));
            password.orElseThrow(() -> new IllegalStateException("Missing DB_PASSWORD environment variable"));
            return this;
        }

        public DatabaseConfig build() {
            // Validate and provide defaults
            validateRequired();

            return new DatabaseConfig(
                url.get(),
                username.get(),
                password.get()
            );
        }

        // Optional methods for manual override if needed
        public Builder withUrl(String url) {
            this.url = Optional.ofNullable(url);
            return this;
        }

        public Builder withUsername(String username) {
            this.username = Optional.ofNullable(username);
            return this;
        }
    }

    // Getters for accessing configuration
    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}