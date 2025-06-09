package com.example.matrix;

import android.util.Log;

public class Utils {

    public static int[][] createMatrix(int row, int column) {

        int num = 1;
        int k = 0, l = 0;
        int[][] a = new int[row][column];
        while (k < row && l < column) {
            for (int i = l; i < column; i++) {
                a[k][i] = num++;
              //  Log.e("TAGRR2", k + " " + i + " = " + num);
            }
            k++;
            for (int j = k; j < row; j++) {
                a[j][column - 1] = num++;
              //  Log.e("TAGRR2", j + " " + (column - 1) + " = " + num);
            }
            column--;
            if (k < row) {
                for (int i = column - 1; i >= l; i--) {
                    a[row - 1][i] = num++;
               //     Log.e("TAGRR3", (row - 1) + " " + (i) + " = " + num);
                }
                row--;
            }
            if (l < column) {
                for (int j = row - 1; j >= k; j--) {
                    a[j][l] = num++;
                 //   Log.e("TAGRR4", (j) + " " + (l) + " = " + num);
                }
                l++;
            }
        }
        return a;
    }
}
