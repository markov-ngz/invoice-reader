package invoiceReader;

import java.util.List;

import software.amazon.awssdk.services.textract.model.Block;

public class S3UserObjectwithText extends S3UserObject{
    private List<Block> blocks ; 

    public List<Block> getBlocks(){
        return this.blocks ; 
    }

    public void setBlocks(List<Block> blocks){
        this.blocks = blocks ; 
    }
}
