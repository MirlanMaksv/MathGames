package com.example.acer.transitions_everywhere.taskgenerators;

import java.util.ArrayList;

/**
 * Created by Mirlan on 08.11.2016.
 */

public class GeneratorTrueFalse extends TaskGenerator {

    public GeneratorTrueFalse() {
    }

    public GeneratorTrueFalse(int from, int to, ArrayList<Integer> operators) {
        super.operators = operators;
        lowerRangeAS = from;
        upperRangeAS = to;
        lowerRangeMD = from;
        upperRangeMD = to;
        isIncrementable = false;
    }

    private String buildOperation(int res, int num1, int num2, String operator) {
        String operation;
        answer = "1";
        int num = getRandomNum(10) % 3;
        boolean showCorrectTask = num == 1;     // if true correct answer will be shown, asking if it's true
        boolean toAdd = num == 0;               // if true result will be changed
        if (!showCorrectTask) {
            answer = "0";
            if (res > 0 && res % 10 == 0) {
                if (!toAdd && res > 10)
                    res -= 10;
                else res += 10;
            }
            else if (res % 3 == 0)
                num1 = changeNum(toAdd, num1);
            else if (res % 3 == 1)
                num2 = changeNum(toAdd, num2);
            else
                res = changeNum(toAdd, res);

            operation = num1 + operator + num2 + " = " + res;
        }
        else operation = num1 + operator + num2 + " = " + res;
        return operation;
    }

    private int changeNum(boolean toAdd, int num) {
        if (!toAdd && num > 2)
            num = num - 1;
        else num = num + 1;
        return num;
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
        return buildOperation(res / num1, num1 * num2, num1, " / ");
    }

}
