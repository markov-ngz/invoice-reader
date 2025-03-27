package loadAnalyzedTextToDb.daos ; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import loadAnalyzedTextToDb.dtos.BlockDTO;
import loadAnalyzedTextToDb.dtos.S3UserObject;

public class S3UserObjectDAOImpl implements S3UserObjectDAO {
    private final Connection connection;

    public S3UserObjectDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int saveS3UserObject(S3UserObject s3UserObject) throws SQLException {
        String sql = "INSERT INTO s3_user_objects (bucket_name, object_key, user_id) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, s3UserObject.getBucketName());
            pstmt.setString(2, s3UserObject.getObjectKey());
            pstmt.setInt(3, s3UserObject.getUserId());
            
            return pstmt.executeUpdate();
        }
    }

    @Override
    public int saveBlocks(S3UserObject s3UserObject) throws SQLException {
        String sql = "INSERT INTO blocks " +
                     "(s3_object_id, block_type, block_id, text_type, text, " +
                     "geometry_width, geometry_height, geometry_left, geometry_top) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        // Assuming you've just inserted the S3 user object and want to use its generated ID
        int s3ObjectId = getCurrentGeneratedId();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            int insertedCount = 0;
            List<BlockDTO> blocks = s3UserObject.getBlocks();
            
            if (blocks != null) {
                for (BlockDTO block : blocks) {
                    pstmt.setInt(1, s3ObjectId);
                    pstmt.setString(2, block.blockType);
                    pstmt.setString(3, block.id);
                    pstmt.setString(4, block.textType);
                    pstmt.setString(5, block.text);
                    
                    // Handle geometry details
                    if (block.geometry != null) {
                        pstmt.setFloat(6, block.geometry.width);
                        pstmt.setFloat(7, block.geometry.height);
                        pstmt.setFloat(8, block.geometry.left);
                        pstmt.setFloat(9, block.geometry.top);
                    } else {
                        // Set null or default values if geometry is not present
                        pstmt.setNull(6, java.sql.Types.FLOAT);
                        pstmt.setNull(7, java.sql.Types.FLOAT);
                        pstmt.setNull(8, java.sql.Types.FLOAT);
                        pstmt.setNull(9, java.sql.Types.FLOAT);
                    }
                    
                    pstmt.addBatch();
                }
                
                int[] results = pstmt.executeBatch();
                insertedCount = results.length;
            }
            
            return insertedCount;
        }
    }

    // Helper method to get the last inserted ID (implementation depends on your database)
    private int getCurrentGeneratedId() throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT LAST_INSERT_ID()")) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Unable to retrieve last inserted ID");
        }
    }
}