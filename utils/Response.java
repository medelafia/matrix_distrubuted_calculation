package utils;

import javax.crypto.SecretKey;
import java.io.Serializable;

public class Response implements Serializable {
    private String slaveId  ;
    private int[][] result ;
    public Response(String slaveId , int[][] result ) {
        this.slaveId = slaveId ;
        this.result = result ;
    }
    public String toStringResponse() {
        StringBuilder responseBuilder = new StringBuilder()  ;
        String matrix = MatrixUtils.matToString(this.result) ;

        responseBuilder.append(this.slaveId).append('\n') ;
        responseBuilder.append(matrix) ;
        return responseBuilder.toString() ;
    }

    public String getSlaveId() {
        return slaveId;
    }

    public int[][] getResult() {
        return result;
    }
}
