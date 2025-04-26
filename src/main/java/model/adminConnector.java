package model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class adminConnector {
    private final SimpleConnectionPool connectionPool;

    public adminConnector(SimpleConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }
    public int Login(String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = connectionPool.getConnection();
            String sql = "SELECT id FROM admin WHERE username = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return -1;
        } catch (SQLException e) {
            System.err.println("用户认证失败: " + e.getMessage());
            return -1;
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    public List<userdao> GetAllUsers() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM users";
        List<userdao> users = new ArrayList<>();
        try{
            conn = connectionPool.getConnection();
            pstmt= conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                users.add(new userdao(rs.getInt("username"),rs.getInt("score")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(rs, pstmt, conn);
        }
        return users;
    }
    public boolean registerUser(String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {

            if (isUsernameExists(username)) {
                return false;
            }

            conn = connectionPool.getConnection();
            String sql = "INSERT INTO admin (username,password) VALUES (?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);


            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("用户注册失败: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, pstmt, conn);
        }
    }
    public boolean isUsernameExists(String username) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = connectionPool.getConnection();
            String sql = "SELECT id FROM admin WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);

            rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("检查用户名失败: " + e.getMessage());
            return false;
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    public void deletePregoods(int gid) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try{
            conn = connectionPool.getConnection();
            String sql = "DELETE FROM pregoods WHERE gid = ?";
            pstmt= conn.prepareStatement(sql);
            pstmt.setInt(1, gid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void closeResources(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) connectionPool.releaseConnection(conn);
        } catch (SQLException e) {
            System.err.println("关闭资源时出错: " + e.getMessage());
        }
    }
    public List<user> browseUser() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<user>users = new ArrayList<user>();
        try {
            conn = connectionPool.getConnection();
            String sql = "SELECT (username,password,status,score,money) FROM admin";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                users.add(new user(rs.getInt("username"), rs.getString("password"), rs.getString("status"), rs.getInt("score"), rs.getFloat("money")));
            }
        } catch (SQLException e) {
            System.err.println("检查用户名失败: " + e.getMessage());

        } finally {
            closeResources(rs, pstmt, conn);
        }
        return users;
    }
    public List<trade> searchTransaction() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<trade>trades = new ArrayList<trade>();
        try {
            conn = connectionPool.getConnection();
            String sql = "SELECT sellusername,buyusername,gid,price,status,goodsname,type from trade";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                trades.add(new trade(rs.getInt("sellusername"),
                        rs.getInt("buyusername"),
                        rs.getInt("gid"),
                        rs.getFloat("price"),
                        rs.getString("status"),
                        rs.getString("goodsname"),
                        rs.getString("type")));
            }
        } catch (SQLException e) {
            System.err.println("检查用户名失败: " + e.getMessage());

        } finally {
            closeResources(rs, pstmt, conn);
        }
        return trades;
    }
    public List<user> getPunishedUser() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql="select username,password,status,score,money from users where status='no'";
        List<user>users=new ArrayList<>();
        try{
            conn = connectionPool.getConnection();
            pstmt=conn.prepareStatement(sql);
            rs=pstmt.executeQuery();
            while (rs.next()) {
                users.add(new user(rs.getInt("username"), rs.getString("password"), rs.getString("status"), rs.getInt("score"), rs.getFloat("money")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(rs, pstmt, conn);
        }
        return users;
    }
    public void recoverPunishedUser(int username) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql="update users set status='yes' where username=?";
        try{
            conn = connectionPool.getConnection();
            pstmt=conn.prepareStatement(sql);
            pstmt.setInt(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(rs, pstmt, conn);
        }
    }
    public int statistic(){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int x=0;
        try{
            conn = connectionPool.getConnection();
            String sql = "SELECT sum(price) FROM trade";
            pstmt=conn.prepareStatement(sql);
            rs=pstmt.executeQuery();
            while (rs.next()){
                x=rs.getInt("sum(price)");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(rs, pstmt, conn);
        }
        return x;
    }
    public List<denouce>dealDenouce(){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<denouce> denouces=new ArrayList<>();
        try{
            conn = connectionPool.getConnection();
            String sql="select denoucer,denoucee,status,statement from denouce";
            pstmt=conn.prepareStatement(sql);
            rs=pstmt.executeQuery();
            while (rs.next()){
                denouces.add(new denouce(rs.getInt("denoucer"),rs.getInt("denoucee"),
                        rs.getString("status"),rs.getString("statement")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(rs, pstmt, conn);
        }
        return denouces;
    }
    public List<pregoods> getPregoods() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT gid,username,price,picture,goodsname,type FROM pregoods";
        List<pregoods> pregoodsList=new ArrayList<pregoods>();
        try {
         conn = connectionPool.getConnection();
         pstmt = conn.prepareStatement(sql);
         rs = pstmt.executeQuery();
         while (rs.next()) {
             pregoodsList.add(new pregoods(rs.getInt("gid"),rs.getInt("username"),
                     rs.getFloat("price"),rs.getString("picture"),
                     rs.getString("goodsname"),rs.getString("type")));
         }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(rs, pstmt, conn);
        }
        return pregoodsList;
    }
    public void setPregoods(int gid) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "insert into goods (gid,username,price,url,goodsname,type) select gid,username,price,picture,goodsname,type from pregoods where gid=?";
        try{
            conn = connectionPool.getConnection();
            pstmt=conn.prepareStatement(sql);
            pstmt.setInt(1, gid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(rs, pstmt, conn);
        }
    }
    public void dealUser(int username){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = connectionPool.getConnection();
            String sql = "update users set status='no' where username=?";
            pstmt=conn.prepareStatement(sql);
            pstmt.setInt(1,username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(rs, pstmt, conn);
        }
    }
    public void deleteDenouce(int denoucee){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "delete from denouce where denoucee=?";
        try{
            conn = connectionPool.getConnection();
            pstmt=conn.prepareStatement(sql);
            pstmt.setInt(1,denoucee);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}