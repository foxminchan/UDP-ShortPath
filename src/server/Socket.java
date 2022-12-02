package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class Socket {
    public static void main(String[] args) {
        System.out.println("Server is running...");
        new Socket().receiveMatrix();
    }

    public void receiveMatrix() {
        int[][] matrix = new int[0][];
        do {
            try (DatagramSocket socket = new DatagramSocket(9876)) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
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
            } catch (Exception e) {
                System.out.println("Socket: " + e.getMessage());
            }
            findShortestPath(matrix);
        } while (matrix.length != 0);
    }

    private void findShortestPath(int[][] matrix) {
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
        pathMatrix[0][0] = String.valueOf(matrix[0][0]);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (i > 0 && j > 0) {
                    weight = weightMatrix[i - 1][j - 1] + matrix[i][j];
                    if (weight < weightMatrix[i][j]) {
                        weightMatrix[i][j] = weight;
                        pathMatrix[i][j] = pathMatrix[i - 1][j - 1] + " " + matrix[i][j];
                    }
                }
                if (i > 0) {
                    weight = weightMatrix[i - 1][j] + matrix[i][j];
                    if (weight < weightMatrix[i][j]) {
                        weightMatrix[i][j] = weight;
                        pathMatrix[i][j] = pathMatrix[i - 1][j] + " " + matrix[i][j];
                    }
                }
                if (j > 0) {
                    weight = weightMatrix[i][j - 1] + matrix[i][j];
                    if (weight < weightMatrix[i][j]) {
                        weightMatrix[i][j] = weight;
                        pathMatrix[i][j] = pathMatrix[i][j - 1] + " " + matrix[i][j];
                    }
                }
            }
        }
        System.out.println("Weight: " + weightMatrix[row - 1][col - 1]);
        System.out.println("Path: " + pathMatrix[row - 1][col - 1]);
        sendResult(weightMatrix[row - 1][col - 1], pathMatrix[row - 1][col - 1]);
    }

    private void sendResult(int weightMatrix, String pathMatrix) {
        try (DatagramSocket socket = new DatagramSocket()) {
            String result = weightMatrix + " " + pathMatrix;
            byte[] buffer = result.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, java.net.InetAddress.getLocalHost(), 9876);
            socket.send(packet);
        } catch (Exception e) {
            System.out.println("Socket: " + e.getMessage());
        }finally {
            System.out.println("Result sent");
        }
    }

}
