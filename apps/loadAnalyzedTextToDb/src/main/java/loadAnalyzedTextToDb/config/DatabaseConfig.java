// package loadAnalyzedTextToDb.config;

// import java.util.Optional;

// public class DatabaseConfig {
//     private final String url;
//     private final String username;
//     private final String password;


//     // Private constructor to enforce creation through builder/factory method
//     private DatabaseConfig(String url, String username, String password) {
//         this.url = url;
//         this.username = username;
//         this.password = password;

//     }

//     // Fluent configuration loading method
//     public static DatabaseConfig load() {
//         return new Builder().build();
//     }

//     public static class Builder {
//         // Method to allow overriding in test
//         protected String getEnvVariable(String key) {
//             return System.getenv(key);
//         }

//         private Optional<String> getOptionalEnv(String key) {
//             return Optional.ofNullable(getEnvVariable(key));
//         }

//         public DatabaseConfig build() {
//             // Validate critical environment variables
//             String url = getOptionalEnv("DB_URL")
//                 .orElseThrow(() -> new IllegalStateException("Missing DB_URL environment variable"));
            
//             String username = getOptionalEnv("DB_USERNAME")
//                 .orElseThrow(() -> new IllegalStateException("Missing DB_USERNAME environment variable"));
            
//             String password = getOptionalEnv("DB_PASSWORD")
//                 .orElseThrow(() -> new IllegalStateException("Missing DB_PASSWORD environment variable"));


//             return new DatabaseConfig(url, username, password);
//         }
//     }
//     // Getters for accessing configuration
//     public String getUrl() {
//         return url;
//     }

//     public String getUsername() {
//         return username;
//     }

//     public String getPassword() {
//         return password;
//     }

// }