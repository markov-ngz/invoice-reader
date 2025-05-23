// package loadAnalyzedTextToDb;

// import loadAnalyzedTextToDb.daos.S3UserObjectDAO;
// import loadAnalyzedTextToDb.daos.S3UserObjectDAOImpl;
// import loadAnalyzedTextToDb.dtos.BlockDTO;
// import loadAnalyzedTextToDb.dtos.GeometryDTO;
// import loadAnalyzedTextToDb.dtos.S3UserObject;
// import loadAnalyzedTextToDb.services.S3UserObjectService;

// import org.junit.jupiter.api.*;

// import java.sql.*;
// import java.util.ArrayList;
// import java.util.List;

// import static org.junit.jupiter.api.Assertions.*;

// public class S3UserObjectDAOImplTest {
//     private static Connection connection;
//     private S3UserObjectDAO dao;
//     private S3UserObjectService service ;

//     @BeforeAll
//     public static void setupDatabase() throws SQLException {
//         // Establish connection to the test database
//         connection = DriverManager.getConnection(
//             "jdbc:postgresql://localhost:5432/DBZ", 
//             "kakarott", 
//             "raditz"
//         );

//         // Prepare test tables (assuming they don't exist)
//         try (Statement stmt = connection.createStatement()) {
//             // Drop existing tables if they exist
//             stmt.execute("DROP TABLE IF EXISTS blocks");
//             stmt.execute("DROP TABLE IF EXISTS s3_user_objects");

//             // Create s3_user_objects table
//             stmt.execute("""
//                 CREATE TABLE s3_user_objects (
//                     id SERIAL PRIMARY KEY,
//                     bucket_name VARCHAR(255),
//                     object_key VARCHAR(255),
//                     user_id INTEGER
//                 )
//             """);

//             // Create blocks table with foreign key
//             stmt.execute("""
//                 CREATE TABLE blocks (
//                     id SERIAL PRIMARY KEY,
//                     s3_object_id INTEGER,
//                     block_type VARCHAR(50),
//                     block_id VARCHAR(100),
//                     text_type VARCHAR(50),
//                     text TEXT,
//                     geometry_width FLOAT,
//                     geometry_height FLOAT,
//                     geometry_left FLOAT,
//                     geometry_top FLOAT,
//                     FOREIGN KEY (s3_object_id) REFERENCES s3_user_objects(id)
//                 )
//             """);
//         }
//     }

//     @BeforeEach
//     public void setup() {

//         // Clear existing data before each test
//         try {
//             try (Statement stmt = connection.createStatement()) {
//                 stmt.execute("DELETE FROM blocks");
//                 stmt.execute("DELETE FROM s3_user_objects");
//             }
            
//             // Initialize DAO
//             dao = new S3UserObjectDAOImpl(connection);

//             service = new S3UserObjectService(connection) ; 

//         } catch (SQLException e) {
//             fail("Setup failed: " + e.getMessage());
//         }
//     }

//     @Test
//     public void testExample(){
//         assertTrue(true);
//     }

//     @Test
//     public void testSaveS3UserObject() throws SQLException {
//         // Prepare test object
//         S3UserObject s3UserObject = new S3UserObject("test-bucket", "test-key", 123);

//         int s3UserObjectId =  dao.saveS3UserObject(s3UserObject);

//         // Verify data was actually inserted
//         try (PreparedStatement pstmt = connection.prepareStatement(
//             "SELECT * FROM s3_user_objects WHERE bucket_name = ? AND object_key = ? AND user_id = ? and id = ? ")) {
            
//             pstmt.setString(1, "test-bucket");
//             pstmt.setString(2, "test-key");
//             pstmt.setInt(3, 123);
//             pstmt.setInt(4, s3UserObjectId);

//             try (ResultSet rs = pstmt.executeQuery()) {
//                 assertTrue(rs.next(), "Record should exist in database");
//             }
//         }
//     }

//     @Test
//     public void testSaveBlocks() throws SQLException {
//         // First, insert a S3 user object to get an ID
//         S3UserObject s3UserObject = new S3UserObject("test-bucket", "test-key", 123);
//         int s3UserObjectId =  dao.saveS3UserObject(s3UserObject);

//         // Prepare blocks
//         List<BlockDTO> blocks = new ArrayList<>();
        
//         BlockDTO block1 = new BlockDTO(null);
//         block1.blockType = "LINE";
//         block1.id = "block1";
//         block1.textType = "HANDWRITING";
//         block1.text = "Test Block 1";
        
//         GeometryDTO geometry1 = new GeometryDTO(null);
//         geometry1.width = 1.0f;
//         geometry1.height = 2.0f;
//         geometry1.left = 0.5f;
//         geometry1.top = 0.3f;
//         block1.geometry = geometry1;
        
//         blocks.add(block1);

//         // Add blocks to the S3 user object
//         s3UserObject.setBlocks(blocks);

//         // Save blocks
//         int insertedCount = dao.saveBlocks(s3UserObject, s3UserObjectId);

//         // Verify insertion
//         assertEquals(1, insertedCount);

//         // Verify data was actually inserted
//         try (PreparedStatement pstmt = connection.prepareStatement(
//             "SELECT * FROM blocks WHERE block_id = ? AND text = ? AND s3_object_id = ? ")) {
            
//             pstmt.setString(1, "block1");
//             pstmt.setString(2, "Test Block 1");
//             pstmt.setInt(3, s3UserObjectId);

//             try (ResultSet rs = pstmt.executeQuery()) {
//                 assertTrue(rs.next(), "Block record should exist in database");
                
//                 // Additional assertions to verify block details
//                 assertEquals("LINE", rs.getString("block_type"));
//                 assertEquals("HANDWRITING", rs.getString("text_type"));
//                 assertEquals(1.0f, rs.getFloat("geometry_width"), 0.001);
//                 assertEquals(2.0f, rs.getFloat("geometry_height"), 0.001);
//             }
//         }
//     }


//     @Test
//     public void testService() throws Exception {

//         S3UserObject s3UserObject = new S3UserObject("Example", "hello/world", 142) ; 
        
//         List<BlockDTO> blocks = new ArrayList<>();
        
//         BlockDTO block1 = new BlockDTO(null);
//         block1.blockType = "serviceLINE";
//         block1.id = "serviceBlockid";
//         block1.textType = "HANDWRITING";
//         block1.text = "Test Block 1";
        
//         GeometryDTO geometry1 = new GeometryDTO(null);
//         geometry1.width = 1.0f;
//         geometry1.height = 2.0f;
//         geometry1.left = 0.5f;
//         geometry1.top = 0.3f;
//         block1.geometry = geometry1;
        
//         blocks.add(block1);

//         s3UserObject.setBlocks(blocks);

//         assertDoesNotThrow(() -> service.processAndStoreS3UserObject(s3UserObject));

//         assertThrows(Exception.class, () -> service.processAndStoreS3UserObject(null)) ; 


//         try (PreparedStatement pstmt = connection.prepareStatement(
//             "SELECT * FROM blocks WHERE block_id = ? AND text = ? ")) {
            
//             pstmt.setString(1, "serviceBlockid");
//             pstmt.setString(2, "Test Block 1");

            
//             try (ResultSet rs = pstmt.executeQuery()) {
//                 assertTrue(rs.next(), "Block record should exist in database");
            
//             }
//         }

//     }

//     @AfterAll
//     public static void tearDown() throws SQLException {
//         if (connection != null) {
//             connection.close();
//         }
//     }
// }