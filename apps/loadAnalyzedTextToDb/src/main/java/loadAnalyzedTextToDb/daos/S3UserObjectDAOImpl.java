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
        String sql = "INSERT INTO s3_user_objects (bucket_name, object_key, user_id) VALUES (?, ?, ?) RETURNING id";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, s3UserObject.getBucketName());
            pstmt.setString(2, s3UserObject.getObjectKey());
            pstmt.setInt(3, s3UserObject.getUserId());
            
            ResultSet resultSet =  pstmt.executeQuery();

            if(resultSet.next()){
                return resultSet.getInt("id") ; 
            }else{
                throw new SQLException("Query did not return id from inserted row") ; 
            }
        }
    }

    @Override
    public int saveBlocks(S3UserObject s3UserObject, int s3ObjectId) throws SQLException {
        String sql = "INSERT INTO blocks " +
                     "(s3_object_id, block_type, block_id, text_type, text, " +
                     "geometry_width, geometry_height, geometry_left, geometry_top) " +
                     "VALUES ((SELECT id FROM s3_user_objects WHERE id = ?), ?, ?, ?, ?, ?, ?, ?, ?)";
        
        
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

}