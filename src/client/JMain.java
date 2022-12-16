package client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.net.*;

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
                    JOptionPane.showMessageDialog(null, "Lỗi: Vui lòng nhập đầy đủ thông tin");
                    return;
                }
                int row = Integer.parseInt(txtRow.getText());
                int col = Integer.parseInt(txtColumn.getText());
                if (row <= 0 || col <= 0 || col > 30 || row > 30) {
                    JOptionPane.showMessageDialog(null, "Lỗi: Vui lòng nhập số dương nhỏ hơn 30");
                    return;
                }
                DefaultTableModel model = new DefaultTableModel(row, col);
                tblData.setModel(model);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Lỗi: Kiểu dữ liệu không trùng khớp");
            }
        };
        txtColumn.addActionListener(listener);
        txtColumn.requestFocus();
        txtRow.addActionListener(listener);

        btnFindShortWay.addActionListener(e -> {
            if (tblData.getModel().getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "Lỗi: Vui lòng khởi tạo ma trận");
                return;
            }
            try {
                int row = Integer.parseInt(txtRow.getText());
                int col = Integer.parseInt(txtColumn.getText());
                int[][] data = new int[row][col];
                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < col; j++) {
                        if (tblData.getValueAt(i, j) == null) {
                            JOptionPane.showMessageDialog(null, "Lỗi: Vui lòng nhập đầy đủ thông tin");
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
        JFrame frame = new JFrame("Tìm đường đi ngắn nhất");
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
            String modifiedSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
            String[] result = modifiedSentence.split(" ");
            txtWeighted.setText(result[0]);
            String modifiedPath = new String(receivePacket.getData(), 0, receivePacket.getLength());
            txtShortPath.setText(modifiedPath.substring(modifiedPath.indexOf(" ") + 1));
        } catch (Exception e) {
            System.out.println("server.Server: " + e.getMessage());
        }
    }
}
