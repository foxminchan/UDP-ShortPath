package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class Server {

    public static void main(String[] args) {
        System.out.println("server.Server is running...");
        new Server().receiveAndSendMatrix();
    }

    public void receiveAndSendMatrix() {
        try (DatagramSocket socket = new DatagramSocket(9876)) {
            int[][] matrix;
            do {
                System.out.println("Waiting for client...");
                DatagramPacket packet;
                byte[] buffer = new byte[1024];
                packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                int row = Integer.parseInt(new String(packet.getData(), 0, packet.getLength()).split(" ")[0]);
                System.out.println("Row: " + row);
                int col = Integer.parseInt(new String(packet.getData(), 0, packet.getLength()).split(" ")[1]);
                System.out.println("Col: " + col);
                matrix = new int[row][col];
                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < col; j++) {
                        matrix[i][j] = Integer.parseInt(new String(packet.getData(), 0, packet.getLength()).split(" ")[2 + i * col + j]);
                    }
                }
                System.out.println("Matrix: ");
                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < col; j++) {
                        System.out.print(matrix[i][j] + " ");
                    }
                    System.out.println();
                }
                byte[] sender = (findShortestPath(matrix) + "").getBytes();
                DatagramPacket packet2 = new DatagramPacket(sender, sender.length, packet.getAddress(), packet.getPort());
                socket.send(packet2);
            } while (matrix.length != 0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String findShortestPath(int[][] matrix) {
        int row = matrix.length;
        int col = matrix[0].length;
        int weight;
        int[][] weightMatrix = new int[row][col];
        String[][] pathMatrix = new String[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                weightMatrix[i][j] = Integer.MAX_VALUE;
                pathMatrix[i][j] = "";
            }
        }
        weightMatrix[0][0] = matrix[0][0];
        String begin = "[0, 0]";
        pathMatrix[0][0] = begin;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                String openBracket = " -> [";
                String closeBracket = "]";
                if (i > 0 && j > 0) {
                    weight = weightMatrix[i - 1][j - 1] + matrix[i][j];
                    if (weight < weightMatrix[i][j]) {
                        weightMatrix[i][j] = weight;
                        pathMatrix[i][j] = pathMatrix[i - 1][j - 1] + openBracket + i + "," + j + closeBracket;
                    }
                }
                if (i > 0) {
                    weight = weightMatrix[i - 1][j] + matrix[i][j];
                    if (weight < weightMatrix[i][j]) {
                        weightMatrix[i][j] = weight;
                        pathMatrix[i][j] = pathMatrix[i - 1][j] + openBracket + i + "," + j + closeBracket;
                    }
                }
                if (j > 0) {
                    weight = weightMatrix[i][j - 1] + matrix[i][j];
                    if (weight < weightMatrix[i][j]) {
                        weightMatrix[i][j] = weight;
                        pathMatrix[i][j] = pathMatrix[i][j - 1] + openBracket + i + "," + j + closeBracket;
                    }
                }
            }
        }
        System.out.println("Weight: " + weightMatrix[row - 1][col - 1]);
        System.out.println("Path: " + pathMatrix[row - 1][col - 1]);
        System.out.println("server.Server result: " + weightMatrix[row - 1][col - 1] + " " + pathMatrix[row - 1][col - 1]);
        return weightMatrix[row - 1][col - 1] + " " + pathMatrix[row - 1][col - 1];
    }
}
