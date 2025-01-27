package utils;


import java.util.Arrays;

public class MatrixUtils {

    public static int[][] strToIntArray(String str) {
        return Arrays.stream(str.split("\n"))
                .filter(row -> !row.isEmpty())
                .map(row -> Arrays.stream(row.split("\t"))
                        .mapToInt(Integer::parseInt)
                        .toArray())
                .toArray(int[][]::new);
    }
    // this method is to merge the responses
    public static int[][] constructMatFromStringArray(String[] responses, int lng, int lrg) {
        int[][] result = new int[lng][lrg];

        // Convert string responses to submatrices
        int[][] slave00 = strToIntArray(responses[0]);
        int[][] slave01 = strToIntArray(responses[1]);
        int[][] slave10 = strToIntArray(responses[2]);
        int[][] slave11 = strToIntArray(responses[3]);

        for (int i = 0; i < lng; i++) {
            for (int j = 0; j < lrg; j++) {
                if (i < lng / 2 && j < lrg / 2) {
                    result[i][j] = slave00[i][j];
                } else if (i < lng / 2 && j >= lrg / 2) {
                    result[i][j] = slave01[i][j - lrg / 2];
                } else if (i >= lng / 2 && j < lrg / 2) {
                    result[i][j] = slave10[i - lng / 2][j];
                } else {
                    result[i][j] = slave11[i - lng / 2][j - lrg / 2];
                }
            }
        }

        return result;
    }
    // to slice the array
    public static int[][] slice(int[][] mat , int startLine , int startColon , int endLine , int endColon) {
        int[][] new_mat = new int[endLine - startLine ][endColon - startColon ] ;
        for(int i = startLine ; i < endLine ; i++) {
            for(int j = startColon ; j < endColon ; j++ ) {
                new_mat[i - startLine][j - startColon] = mat[i][j] ;
            }
        }
        return new_mat ;
    }
    // to convert an int matrix to a string for send it to server
    public static String matToString(int[][] mat ) {
        StringBuilder stringBuilder = new StringBuilder() ;
        for(int i = 0 ; i< mat.length ; i++)
        {
            for(int j= 0 ; j < mat[0].length ; j++ ) {
                stringBuilder.append(mat[i][j] + "\t") ;
            }
            stringBuilder.append("\n") ;
        }
        return stringBuilder.toString() ;
    }
    public static boolean verifyDimension(int[][] a , int[][] b ) {
        if(a.length == b.length ) {
            for(int i = 0 ; i < a.length ; i++ ) {
                if(a[i].length != b[i].length ) return false ;
            }
            return true ;
        }else {
            return false ;
        }
    }
    public static int[][] addTwoMatrix(int[][] a , int[][] b) {
        int[][] res = new int[a.length][a[0].length] ;
        for( int i = 0 ; i < a.length ; i++ ) {
            for(int j = 0 ; j < a[0].length ; j++ ) {
                res[i][j] = a[i][j] + b[i][j] ;
            }
        }
        return res ;
    }
    public static int[][] multiplyTwoMatrix(int[][] a, int[][] b) {
        if (a[0].length != b.length) {
            throw new IllegalArgumentException("Matrix dimensions do not allow multiplication: " +
                    "Columns of A (" + a[0].length + ") must match rows of B (" + b.length + ").");
        }

        int[][] res = new int[a.length][b[0].length];

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                for (int k = 0; k < b.length; k++) {
                    res[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return res;
    }

    public static int[][] subTwoMatrix(int[][] a , int[][] b) {
        int[][] res = new int[a.length][a[0].length] ;
        for( int i = 0 ; i < a.length ; i++ ) {
            for(int j = 0 ; j < a[0].length ; j++ ) {
                res[i][j] = a[i][j] - b[i][j] ;
            }
        }
        return res ;
    }
    public static int[][] merge(int[][] mat00 , int[][] mat01  , int[][] mat10 , int[][] mat11 , int outLng , int outLrg ) {
        int[][] result = new int[outLng][outLrg];

        for (int i = 0; i < outLng; i++) {
            for (int j = 0; j < outLrg; j++) {
                if (i < outLng / 2 && j < outLrg / 2) {
                    result[i][j] = mat00[i][j];
                } else if (i < outLng / 2 && j >= outLrg / 2) {
                    result[i][j] = mat01[i][j - outLrg / 2];
                } else if (i >= outLng / 2 && j < outLrg / 2) {
                    result[i][j] = mat10[i - outLng / 2][j];
                } else {
                    result[i][j] = mat11[i - outLng / 2][j - outLrg / 2];
                }
            }
        }
        return result ;
    }
    public static int[][] matrix_padding(int[][] mat) {
        int new_lng = mat.length % 2 == 0 ? mat.length : mat.length + 1 ;
        int new_lrg = mat[0].length % 2 == 0 ? mat[0].length : mat[0].length + 1 ;

        int[][] padded_matrix = new int[new_lng][new_lrg] ;

        for(int i = 0 ; i < new_lng ; i++  ) {
            for (int j = 0 ; j < new_lrg; j++) {
                if(i >= mat.length || j >= mat[0].length ) padded_matrix[i][j] = 0;
                else padded_matrix[i][j] = mat[i][j] ;
            }
        }
        return padded_matrix ;
    }

}
