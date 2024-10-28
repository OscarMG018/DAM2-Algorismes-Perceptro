package com.example;

public class Perceptron {

    private double[] weights;
    private double bias;
    private double learningRate = 0.1;
    private boolean print;

    public Perceptron(int inputSize,double learningRate,boolean print) {
        this.learningRate = learningRate;
        this.print = print;
        weights = new double[inputSize];
        bias = 0.0;
        // Inicialització dels pesos aleatòriament
        for (int i = 0; i < weights.length; i++) {
            weights[i] = Math.random() - 0.5;
        }
    }

    private int relu(double sum) {
        return sum >= 0 ? 1 : 0; // Sortida 1 per senar, 0 per parell
    }

    private int predict(int[] inputs) {
        double sum = bias;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i] * inputs[i];
        }
        return relu(sum);
    }

    public void train(int[][] inputData, int[] labels, int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int i = 0; i < inputData.length; i++) {
                int prediction = predict(inputData[i]);//forward pass
                int error = labels[i] - prediction;

                //Backward pass
                for (int j = 0; j < weights.length; j++) {
                    weights[j] += learningRate * error * inputData[i][j];
                }
                bias += learningRate * error;
            }
        }
    }

    public double testAccuracy(int[][] inputData, int[] labels) {
        int correct = 0;
        for (int i = 0; i < inputData.length; i++) {
            int prediction = predict(inputData[i]);
            boolean isCorrect = (prediction == labels[i]);
            if (isCorrect) {
                correct++;
            }

            // Imprimir entrada i resultat
            if (print) {
                System.out.print("Entrada: ");
                for (int bit : inputData[i]) {
                    System.out.print(bit);
                }
                System.out.println(" -> Resultat: " + (prediction == 0 ? "Yes" : "No") +
                                " (Esperat: " + (labels[i] == 0 ? "Yes" : "No") + ")");
            }
        }
        return (correct / (double) inputData.length) * 100.0;
    }

    public void resetWeights() {
        for (int i = 0; i < weights.length; i++) {
            weights[i] = 0;
        }
        bias = 0;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

}
