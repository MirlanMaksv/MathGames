package com.example.acer.transitions_everywhere.taskgenerators;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Mirlan on 08.11.2016.
 */

public abstract class TaskGenerator {

    private Random random = new Random();
    int upperRangeAS = 24;
    int lowerRangeAS = 1;
    int upperRangeMD = 7;
    int lowerRangeMD = 1;
    ArrayList<Integer> operators = fillDefOperators();
    String answer;
    boolean isIncrementable = true;

    public String newTask(boolean isTrue) {
        if (isIncrementable && isTrue) {
            upperRangeAS += 2;
            lowerRangeAS += 1;
            if (upperRangeAS % 8 == 0) {    // increase range on each 4th correct answer
                upperRangeMD += 1;
                lowerRangeMD += 1;
            }
        }
        String operation;
        int operator = operators.get(getRandomNum(operators.size()));
        if (operator == 0) operation = getAddition();
        else if (operator == 1) operation = getSubtraction();
        else if (operator == 2) operation = getMultiplication();
        else operation = getDivision();
        return operation;
    }

    abstract String getAddition();

    abstract String getSubtraction();

    abstract String getMultiplication();

    abstract String getDivision();

    public String getRes() {
        return answer;
    }

    int getRandomNumAS() {
        if(upperRangeAS != lowerRangeAS)
            return random.nextInt(upperRangeAS - lowerRangeAS) + lowerRangeAS;
        return upperRangeAS;
    }

    int getRandomNumMD() {
        if(upperRangeMD != lowerRangeMD)
            return random.nextInt(upperRangeMD - lowerRangeMD) + lowerRangeMD;
        return upperRangeMD;
    }

    int getRandomNum(int to) {
        return random.nextInt(to);
    }

    private ArrayList<Integer> fillDefOperators() {
        ArrayList<Integer> ops = new ArrayList<>();
        ops.add(0);
        ops.add(1);
        ops.add(2);
        ops.add(3);
        return ops;
    }
}
