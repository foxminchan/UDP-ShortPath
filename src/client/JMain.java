package client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class JMain {
    private JTextField txtColumn;
    private JTextField txtRow;
    private JPanel frmMain;
    private JTable tblData;
    private JButton btnFindShortWay;
    private JTextField txtShortPath;
    private JTextField txtWeighted;

    public JMain() {
        ActionListener listener = e -> {
            try {
                if (txtColumn.getText().isEmpty() || txtRow.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin");
                    return;
                }
                int row = Integer.parseInt(txtRow.getText());
                int col = Integer.parseInt(txtColumn.getText());
                if (row <= 0 || col <= 0 || col > 30 || row > 30) {
                    JOptionPane.showMessageDialog(null, "Vui lòng nhập số dương nhỏ hơn 30");
                    return;
                }
                DefaultTableModel model = new DefaultTableModel(row, col);
                tblData.setModel(model);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Lỗi: Kiểu dữ liệu không trùng khớp");
            }
        };
        txtColumn.addActionListener(listener);
        txtRow.addActionListener(listener);

        btnFindShortWay.addActionListener(e -> {
            try {
                int row = Integer.parseInt(txtRow.getText());
                int col = Integer.parseInt(txtColumn.getText());
                int[][] data = new int[row][col];
                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < col; j++) {
                        if (tblData.getValueAt(i, j) == null) {
                            JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin");
                            return;
                        }
                        data[i][j] = Integer.parseInt(tblData.getValueAt(i, j).toString());
                        System.out.print(data[i][j] + " ");
                    }
                    System.out.println();
                }
                sendAndReceiveDatagram(data, row, col);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Lỗi: Kiểu dữ liệu không trùng khớp");
            }
        });

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("JMain");
        frame.setContentPane(new JMain().frmMain);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void sendAndReceiveDatagram(int[][] data, int row, int col) {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            InetAddress ipAddress = InetAddress.getByName("localhost");
            byte[] sendData;
            byte[] receiveData = new byte[1024];
            StringBuilder sentence = new StringBuilder();
            sentence.append(row).append(" ").append(col).append(" ");
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    sentence.append(data[i][j]).append(" ");
                }
            }
            sendData = sentence.toString().getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, 9876);
            clientSocket.send(sendPacket);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String modifiedSentence = new String(receivePacket.getData());
            String[] result = modifiedSentence.split(" ");
            txtWeighted.setText(result[0]);
            for (int i = 1; i < result.length; i++) {
                txtShortPath.setText(result[i]);
            }
        } catch (Exception e) {
            System.out.println("Socket: " + e.getMessage());
        }
    }
}
