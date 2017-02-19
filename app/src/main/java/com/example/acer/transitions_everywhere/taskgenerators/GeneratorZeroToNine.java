package com.example.acer.transitions_everywhere.taskgenerators;

import java.util.ArrayList;

/**
 * Created by Mirlan on 09.09.2016.
 */

public class GeneratorZeroToNine extends TaskGenerator {

    public GeneratorZeroToNine() {
    }

    public GeneratorZeroToNine(int from, int to, ArrayList<Integer> operators) {
        super.operators = operators;
        lowerRangeAS = from;
        upperRangeAS = to;
        lowerRangeMD = from;
        upperRangeMD = to;
        isIncrementable = false;
    }

    private String buildOperation(int res, int num1, int num2, String operator) {
        String operation;
        int num = getRandomNum(10) % 3;
        if (num == 0) {             // first num will be hidden
            answer = num1 + "";
            operation = "?" + operator + num2 + " = " + (res);
        }
        else if (num == 1) {      // second num will be hidden
            answer = num2 + "";
            operation = num1 + operator + "? = " + res;
        }
        else {                        // third num will be hidden
            answer = res + "";
            operation = num1 + operator + num2 + " = ?";
        }
        return operation;
    }

    public String getAddition() {
        int num1 = getRandomNumAS();
        int num2 = getRandomNumAS();
        return buildOperation(num1 + num2, num1, num2, " + ");
    }

    public String getSubtraction() {
        int num1 = getRandomNumAS();
        int num2 = getRandomNumAS();
        if (num1 == num2 && upperRangeAS != lowerRangeAS) {
            return getSubtraction();
        }
        else if (num2 > num1) {
            int temp = num1;
            num1 = num2;
            num2 = temp;
        }
        return buildOperation(num1 - num2, num1, num2, " - ");
    }

    public String getMultiplication() {
        int num1 = getRandomNumMD();
        int num2 = getRandomNumMD();
        return buildOperation(num1 * num2, num1, num2, " x ");
    }

    public String getDivision() {
        int num1 = getRandomNumMD();
        int num2 = getRandomNumMD();
        int res = num1 * num2;
        if (res % 2 == 0)
            return buildOperation(res / num2, num1 * num2, num2, " / ");
        return buildOperation(res / num1, num1 * num2, num1, " / ");
    }

}
