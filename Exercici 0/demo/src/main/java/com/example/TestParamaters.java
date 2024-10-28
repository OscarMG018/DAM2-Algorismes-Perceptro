package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

class Results {
    public double learningRate;
    public int epochs;
    public double acc;

    public Results(double learningRate, int epochs, double acc) {
        this.learningRate = learningRate;
        this.epochs = epochs;
        this.acc = acc;
    }

    public Results() {
        this.learningRate = 0;
        this.epochs = 0;
        this.acc = -1;
    }

    @Override
    public String toString() {
        return "Results{" + "learningRate=" + learningRate + ", epochs=" + epochs + ", acc=" + acc + '}';
    }
}

public class TestParamaters {
    
    public static double minLearningRate = 0.0001;
    public static double maxLearningRate = 0.01;
    public static double learningRateStep = 0.0001;

    public static int minEpochs = 5;
    public static int maxEpochs = 40;
    public static int EpochsStep = 1;

    public static int numberOfAttempts = 100;

    public static Perceptron perceptronDiagonal;
    public static Perceptron perceptronVertical;
    public static Perceptron perceptronHorizontal;

    public static int[][] inputs;
    public static int inputSize = 9;

    public static int[] outputDiagonal;
    public static int[] outputVertical;
    public static int[] outputHorizontal;

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

    public static void generateOutputs() {
        inputs = generateAllMatrices();
        outputDiagonal = new int[inputs.length];
        outputVertical = new int[inputs.length];
        outputHorizontal = new int[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            outputDiagonal[i] = hasDiagonalLine(inputs[i]) ? 1:0;
            outputVertical[i] = hasHorizontalLine(inputs[i]) ? 1:0;
            outputHorizontal[i] = hasHorizontalLine(inputs[i]) ? 1:0;
        } 
    }


    // Test accuracy for the diagonal perceptron
    public static double testDiagonalAcc(double learningRate,int epochs) {
        perceptronDiagonal.resetWeights();
        perceptronDiagonal.setLearningRate(learningRate);
        // Entrenar asl perceptróns
        perceptronDiagonal.train(inputs, outputDiagonal, epochs);

        // Calcular el % d'encert amb totes les entrades
        return perceptronDiagonal.testAccuracy(inputs, outputDiagonal);
    }

    public static double testVerticalAcc(double learningRate,int epochs) {
        perceptronVertical.resetWeights();
        perceptronVertical.setLearningRate(learningRate);

        // Entrenar asl perceptróns
        perceptronVertical.train(inputs, outputVertical, epochs);

        // Calcular el % d'encert amb totes les entrades
        return perceptronVertical.testAccuracy(inputs, outputVertical);
    }

    public static double testHorizontalAcc(double learningRate,int epochs) {
        perceptronHorizontal.resetWeights();
        perceptronHorizontal.setLearningRate(learningRate);

        // Entrenar asl perceptróns
        perceptronHorizontal.train(inputs, outputHorizontal, epochs);

        // Calcular el % d'encert amb totes les entrades
        return perceptronHorizontal.testAccuracy(inputs, outputHorizontal);
    }


    public static void main(String[] args) {
        // Use ConcurrentHashMap for thread safety
        generateOutputs();
        Map<String, Results> results = new ConcurrentHashMap<>();
        results.put("diagonal", new Results());
        results.put("vertical", new Results());
        results.put("horizontal", new Results());

        perceptronDiagonal = new Perceptron(inputSize,0,false);
        perceptronVertical = new Perceptron(inputSize,0,false);
        perceptronHorizontal = new Perceptron(inputSize,0,false);

        int testNumber = (int) ((maxEpochs - minEpochs) / EpochsStep * (maxLearningRate - minLearningRate) / learningRateStep);
        System.out.println("Total number of tests to run: " + testNumber);

        // Get available processors for optimal thread pool size
        int processors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(processors);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int epochs = minEpochs; epochs < maxEpochs; epochs += EpochsStep) {
            for (double epsilon = minLearningRate; epsilon < maxLearningRate; epsilon += learningRateStep) {
                final int finalEpochs = epochs;
                final double finalEpsilon = epsilon;

                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    System.out.println("Running test with epochs=" + finalEpochs + ", learning rate=" + finalEpsilon);
                    
                    double accDiagonal = 0;
                    double accVertical = 0;
                    double accHorizontal = 0;
                    
                    for (int i = 0; i < numberOfAttempts; i++) {
                        accDiagonal += testDiagonalAcc(finalEpsilon, finalEpochs);
                        accVertical += testVerticalAcc(finalEpsilon, finalEpochs);
                        accHorizontal += testHorizontalAcc(finalEpsilon, finalEpochs);
                    }

                    // Calculate averages
                    final double avgAccDiagonal = accDiagonal / numberOfAttempts;
                    final double avgAccVertical = accVertical / numberOfAttempts;
                    final double avgAccHorizontal = accHorizontal / numberOfAttempts;
                    

                    // Thread-safe updates using compute
                    results.compute("diagonal", (k, v) -> v.acc < avgAccDiagonal ? 
                        new Results(finalEpsilon, finalEpochs, avgAccDiagonal) : v);
                    results.compute("vertical", (k, v) -> v.acc < avgAccVertical ? 
                        new Results(finalEpsilon, finalEpochs, avgAccVertical) : v);
                    results.compute("horizontal", (k, v) -> v.acc < avgAccHorizontal ? 
                        new Results(finalEpsilon, finalEpochs, avgAccHorizontal) : v);
                }, executor);

                futures.add(future);
            }
        }

        // Wait for all tasks to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        // Shutdown the executor
        executor.shutdown();

        // Print results
        System.out.println("\nFinal Results:");
        System.out.println("Diagonal: " + results.get("diagonal"));
        System.out.println("Vertical: " + results.get("vertical"));
        System.out.println("Horizontal: " + results.get("horizontal"));
    }
}
