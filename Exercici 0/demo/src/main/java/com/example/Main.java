package com.example;

import java.util.ArrayList;

public class Main {

    public static double DiagonalLearningRate = 0.008;
    public static double VerticalLearningRate = 0.0004;
    public static double HorizontalLearningRate = 0.0004;
    public static int DiagonalEpochs = 10;
    public static int VerticalEpochs = 31;
    public static int HorizontalEpochs = 33;

    public static int[][] generateAllMatrices() {
        ArrayList<int[]> matrices = new ArrayList<>();
        
        // Hi ha 2^9 combinacions possibles
        for (int i = 0; i < 512; i++) {
            int[] matrix = new int[9];
            
            // Convertir l'índex `i` a una combinació binària de 9 bits
            String binary = String.format("%9s", Integer.toBinaryString(i)).replace(' ', '0');
            
            // Omplir l'array amb els bits corresponents
            for (int j = 0; j < 9; j++) {
                matrix[j] = binary.charAt(j) - '0';
            }
            
            matrices.add(matrix);
        }
        int[][] result = new int[matrices.size()][];
        for (int i = 0; i < matrices.size(); i++) {
            result[i] = matrices.get(i); // Copy each int[] to the 2D array
        }
        return result;
    }

    public static boolean hasHorizontalLine(int[] matrix) {
        for (int i = 0; i < 3; i++) {
            boolean lineFound = true;
            for (int j = 0; j < 3; j++) {
                if (matrix[i*3+j] != 1) {
                    lineFound = false;
                    break;
                }
            }
            if (lineFound) return true;
        }
        return false;
    }

    public static boolean hasVerticalLine(int[] matrix) {
        for (int i = 0; i < 3; i++) {
            boolean lineFound = true;
            for (int j = 0; j < 3; j++) {
                if (matrix[j*3+i] != 1) {
                    lineFound = false;
                    break;
                }
            }
            if (lineFound) return true;
        }
        return false;
    }

    public static boolean hasDiagonalLine(int[] matrix) {
        return (matrix[0] == 1 && matrix[4] == 1 && matrix[8] == 1) ||
        (matrix[2] == 1 && matrix [4] == 1 && matrix[6] == 1);
    }

    public static void main(String[] args) {

        int inputSize = 9;
        int[][] inputs = generateAllMatrices();

        Perceptron perceptronDiagonal = new Perceptron(inputSize,DiagonalLearningRate,false);
        Perceptron perceptronVertical= new Perceptron(inputSize,VerticalLearningRate,false);
        Perceptron perceptronHorizontal = new Perceptron(inputSize,HorizontalLearningRate,false);

        int[] outputsDiagonal = new int[inputs.length];
        int[] outputsVertical = new int[inputs.length];
        int[] outputsHorizontal = new int[inputs.length];

        for (int i = 0; i < inputs.length; i++) {
            outputsDiagonal[i] = hasDiagonalLine(inputs[i]) ? 1:0;
            outputsVertical[i] = hasHorizontalLine(inputs[i]) ? 1:0;
            outputsHorizontal[i] = hasHorizontalLine(inputs[i]) ? 1:0;
        }

        // Entrenar asl perceptróns
        perceptronDiagonal.train(inputs, outputsDiagonal, DiagonalEpochs);
        perceptronVertical.train(inputs, outputsVertical, VerticalEpochs);
        perceptronHorizontal.train(inputs, outputsHorizontal, HorizontalEpochs);

        // Calcular el % d'encert amb totes les entrades
        double accuracyD = perceptronDiagonal.testAccuracy(inputs, outputsDiagonal);
        double accuracyV = perceptronVertical.testAccuracy(inputs, outputsDiagonal);
        double accuracyH = perceptronHorizontal.testAccuracy(inputs, outputsDiagonal);
        System.out.println("Percentatge d'encert diagonal: " + accuracyD + "%");
        System.out.println("Percentatge d'encert vertical: " + accuracyV + "%");
        System.out.println("Percentatge d'encert horizontal: " + accuracyH + "%");
    }
}