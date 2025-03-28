// package loadAnalyzedTextToDb.services;

// import java.sql.Connection;
// import java.sql.SQLException;

// import loadAnalyzedTextToDb.daos.S3UserObjectDAO;
// import loadAnalyzedTextToDb.daos.S3UserObjectDAOImpl;
// import loadAnalyzedTextToDb.dtos.S3UserObject;

// public class S3UserObjectService {

//     private final S3UserObjectDAO dao;
//     private final Connection connection;

//     public S3UserObjectService(Connection connection) {
//         this.connection = connection;
//         this.dao = new S3UserObjectDAOImpl(connection);
//     }

//     public void processAndStoreS3UserObject(S3UserObject s3UserObject) throws SQLException {
//         try {
//             // Start a transaction
//             connection.setAutoCommit(false);

//             // Save the S3 user object first
//             int s3UserObjectId =  dao.saveS3UserObject(s3UserObject);

//             // Then save its blocks
//             dao.saveBlocks(s3UserObject, s3UserObjectId);

//             // Commit the transaction
//             connection.commit();
//         } catch (SQLException e) {
//             // Rollback in case of any error
//             connection.rollback();
//             throw e;
//         } finally {
//             // Reset to default auto-commit behavior
//             connection.setAutoCommit(true);
//         }
//     }
// } 