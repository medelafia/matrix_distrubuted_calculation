package utils;

import java.io.Serializable;

public class Response implements Serializable {
    private String slaveId  ;
    private int[][] result ;
    public Response(String slaveId , int[][] result ) {
        this.slaveId = slaveId ;
        this.result = result ;
    }
    public String getSlaveId() {
        return slaveId;
    }

    public int[][] getResult() {
        return result;
    }
}
