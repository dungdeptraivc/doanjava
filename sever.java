import java.io.*;
import java.net.*;
import java.sql.*;
public class sever {
    static ServerSocket ss;
    static Socket s;
    static DataInputStream dis;
    static DataOutputStream dout;
    
    public static void main(String[] args) {
        try {
            ss = new ServerSocket(1201); // Mở cổng 1201
            System.out.println("Server đang chạy...");

            s = ss.accept(); // Chờ Client kết nối
            System.out.println("Client đã kết nối!");

            dis = new DataInputStream(s.getInputStream());
            dout = new DataOutputStream(s.getOutputStream());

            String msg = "";
            while (!msg.equals("exit")) {
                msg = dis.readUTF(); // Nhận tin nhắn từ Client
                System.out.println("Client: " + msg); // Hiển thị tin nhắn
                saveMessage("Client", "Server", msg); // Lưu vào SQL Server
                
                dout.writeUTF("Đã nhận: " + msg); // Phản hồi lại Client
                dout.flush();
            }

            dis.close();
            dout.close();
            s.close();
            ss.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Kết nối SQL Server
    public static Connection connectDB() {
        try {
            return DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=ChatApp;user=sa;password=12345678;");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Lưu tin nhắn vào SQL Server
    public static void saveMessage(String sender, String receiver, String message) {
        try (Connection conn = connectDB()) {
            String sql = "INSERT INTO messages (sender, receiver, message) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, sender);
            stmt.setString(2, receiver);
            stmt.setString(3, message);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
