package utils;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Request implements Serializable {
    private String slaveId ;
    private char operator ;
    private int lng ;
    private int lrg ;
    private int[][] a ;
    private int[][] b ;

    public Request(String slaveId, char operator, int lng, int lrg, int[][] a, int[][] b) {
        this.slaveId = slaveId;
        this.operator = operator;
        this.lng = lng;
        this.lrg = lrg;
        this.a = a;
        this.b = b;
    }
    public String getSlaveId() {
        return slaveId;
    }

    public void setSlaveId(String slaveId) {
        this.slaveId = slaveId;
    }

    public char getOperator() {
        return operator;
    }

    public void setOperator(char operator) {
        this.operator = operator;
    }

    public int[][] getA() {
        return a;
    }

    public void setA(int[][] a) {
        this.a = a;
    }

    public int[][] getB() {
        return b;
    }

    public void setB(int[][] b) {
        this.b = b;
    }


    public static Request loadRequestFromFile(String filePath) {
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath)) ;) {
            Request request = null ;
            List<String> lines = bufferedReader.lines().collect(Collectors.toList()) ;

            int lng = Integer.parseInt( lines.get(0).split(" ")[0]) ;
            int lrg = Integer.parseInt( lines.get(0).split(" ")[1]) ;

            char operator = lines.get(1).charAt(0) ;

            int[][] a = new int[lng][lrg ] ;
            int[][] b = new int[lng][lrg] ;

            for(int i = 0 ; i < lng ; i++ )  {
                for(int j = 0 ; j < lrg ; j++ ) {
                    a[i][j] = Integer.parseInt(lines.get(2 + i ).split(" ")[j]) ;
                    b[i][j] = Integer.parseInt(lines.get(3 + lng + i ).split(" ")[j]);
                }
            }
            request = new Request(null , operator , lng , lrg , a , b ) ;

            return request ;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
