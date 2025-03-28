// package loadAnalyzedTextToDb;

// import org.junit.jupiter.api.Test;

// import java.util.HashMap;
// import java.util.Map;

// import static org.junit.jupiter.api.Assertions.*;


// import loadAnalyzedTextToDb.config.DatabaseConfig;

// public class DatabaseConfigTest {

//     // Custom Environment Variable Provider
//     private static class TestEnvironmentProvider {
//         private final Map<String, String> environmentVariables = new HashMap<>();

//         public TestEnvironmentProvider addVariable(String key, String value) {
//             environmentVariables.put(key, value);
//             return this;
//         }

//         public DatabaseConfig createConfig() {
//             return new DatabaseConfig.Builder() {
//                 @Override
//                 protected String getEnvVariable(String key) {
//                     return environmentVariables.get(key);
//                 }
//             }.build();
//         }
//     }

//     @Test
//     public void testSuccessfulConfigurationLoading() {
//         TestEnvironmentProvider provider = new TestEnvironmentProvider()
//             .addVariable("DB_URL", "jdbc:postgresql://localhost:5432/testdb")
//             .addVariable("DB_USERNAME", "testuser")
//             .addVariable("DB_PASSWORD", "testpass")
//             .addVariable("DB_MAX_POOL_CONNECTIONS", "15");

//         DatabaseConfig config = provider.createConfig();

//         assertEquals("jdbc:postgresql://localhost:5432/testdb", config.getUrl());
//         assertEquals("testuser", config.getUsername());
//         assertEquals("testpass", config.getPassword());

//     }

//     @Test
//     public void testMissingRequiredEnvironmentVariables() {
//         TestEnvironmentProvider provider = new TestEnvironmentProvider()
//             .addVariable("DB_URL", null)
//             .addVariable("DB_USERNAME", null)
//             .addVariable("DB_PASSWORD", null);

//         assertThrows(IllegalStateException.class, provider::createConfig);
//     }

// }

