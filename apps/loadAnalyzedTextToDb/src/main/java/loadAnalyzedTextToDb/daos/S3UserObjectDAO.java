package loadAnalyzedTextToDb.daos;
import java.sql.SQLException;

import loadAnalyzedTextToDb.dtos.S3UserObject;

public interface S3UserObjectDAO {
    /**
     * Persist an S3UserObject to the database
     * @param s3UserObject The object to be stored
     * @return The number of records inserted
     * @throws SQLException If a database error occurs
     */
    int saveS3UserObject(S3UserObject s3UserObject) throws SQLException;
    /**
     * Batch insert blocks associated with an S3UserObject
     * @param s3UserObject The object containing blocks to be stored
     * @return The number of block records inserted
     * @throws SQLException If a database error occurs
     */
    int saveBlocks(S3UserObject s3UserObject, int s3ObjectId) throws SQLException;

}
