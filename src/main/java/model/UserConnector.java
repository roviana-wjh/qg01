package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserConnector {
    private final SimpleConnectionPool connectionPool;

    public UserConnector(SimpleConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public int Login(String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = connectionPool.getConnection();
            String sql = "SELECT id FROM users WHERE username = ? AND password = ?";
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

    public boolean registerUser(String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            if (isUsernameExists(username)) {
                return false;
            }
            conn = connectionPool.getConnection();
            String sql = "INSERT INTO users (username,password,score,status,money) VALUES (?, ?,100,'yes',100)";
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
    public List<goods> browseGoods() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<goods> goodsList = new ArrayList<>();
        String sql = "SELECT gid, goodsname AS name, review AS description, username, url, type, price FROM goods ";
        try{
            conn = connectionPool.getConnection();
            pstmt=conn.prepareStatement(sql);
            rs=pstmt.executeQuery();
            while (rs.next()) {
                goodsList.add(new goods( rs.getInt("gid"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("username"),
                        rs.getString("url"),
                        rs.getString("type"),
                        rs.getFloat("price")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return goodsList;
    }
    public int getSellerIDbygid(int gid) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int sellerID = 0;
        String sql = "SELECT sellusername FROM trade WHERE gid = ?";
        try{
            conn = connectionPool.getConnection();
            pstmt=conn.prepareStatement(sql);
            pstmt.setInt(1,gid);
            rs=pstmt.executeQuery();
            while (rs.next()) {
                sellerID = rs.getInt("sellusername");
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(rs, pstmt, conn);
        }
        return sellerID;
    }
    public List<transaction> getTransactions(int username) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<transaction> transactions = new ArrayList<>();
        String sql="select gid,goodsname,price,sellusername,buyusername,type from trade where buyusername = ?";
        try {
            conn = connectionPool.getConnection();
             stmt = conn.prepareStatement(sql);
            stmt.setInt(1,username );
            rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(new transaction(rs.getInt("gid"),rs.getString("goodsname"),rs.getFloat("price"), rs.getString("type") ));
            }
            } catch (SQLException e) {
            throw new RuntimeException(e);
        }
     finally {
            closeResources(null, stmt, conn);
        }
        return transactions;
    }
    public List<trade> getUnrecievedTransactions(int username) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<trade> transactions = new ArrayList<>();
        String sql="select sellusername,buyusername,gid,price,status,goodsname,type from trade where buyusername = ? and status='unrecieved'";
        try {
            conn = connectionPool.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1,username );
            rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(new trade(rs.getInt("sellusername"),rs.getInt("buyusername"),rs.getInt("gid"),rs.getFloat("price"),
                        rs.getString("status"),rs.getString("goodsname"),rs.getString("type") ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            closeResources(null, stmt, conn);
        }
        return transactions;
    }

    public void confirmTransaction(int username, int gid) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql="update trade set status='received' where buyusername = ? and gid = ?";
        try {
            conn = connectionPool.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1,username );
            stmt.setInt(2,gid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void cancelTransaction(int gid) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql="delete from trade where gid = ?";
        try{
            conn = connectionPool.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1,gid);
            stmt.executeUpdate();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<transaction> getPendingTransactions(String username) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<transaction> transactions = new ArrayList<>();
        String sql="select gid,goodsname,price,sellusername,buyusername,type from trade where buyusername = ? and status='pending'";
        try {
            conn = connectionPool.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1,username );
            rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(new transaction(rs.getInt("gid"),rs.getString("goodsname"),rs.getFloat("price"),rs.getString("type") ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            closeResources(null, stmt, conn);
        }
        return transactions;
    }
    public List<recievedTrade> getRecievedTransactions(int username) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<recievedTrade> transactions = new ArrayList<>();
        String sql="select gid,goodsname,price,sellusername,buyusername,type from trade where buyusername = ? and status='received'";
        try{
            conn = connectionPool.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1,username );
            rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(new recievedTrade(rs.getInt("gid"),rs.getInt("buyusername"), rs.getString("goodsname"), rs.getFloat("price")," " ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(null, stmt, conn);
        }
        return transactions;
    }
    public int getSellerId(int gid) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql="select sellusername from trade where gid = ?";
        int sellerId = 0;
        try{
            conn = connectionPool.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1,gid);
            rs = stmt.executeQuery();
            while (rs.next()) {
                 sellerId=rs.getInt("sellusername");
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            closeResources(null, stmt, conn);
        }
        return sellerId;
    }

public void reviewTransaction(String review,int gid) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql="update trade set review=? where gid = ? ";
        try{
            conn = connectionPool.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1,review );
            stmt.setInt(2,gid );
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
}
public void getReview(int gid) throws SQLException {

}
    public boolean isUsernameExists(String username) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = connectionPool.getConnection();
            String sql = "SELECT id FROM users WHERE username = ?";
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
    public List<goods> getDelete(int username) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql="select gid,username,price,url,review,goodsname,type from goods where username = ?";
        List<goods> goodss = new ArrayList<>();
        try{
            conn = connectionPool.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,username );
            rs = pstmt.executeQuery();
            while (rs.next()) {
                goodss.add(new goods(rs.getInt("gid"),rs.getString("goodsname"),
                        rs.getString("review"),rs.getInt("username"),rs.getString("url"),
                        rs.getString("goodsname"),rs.getFloat("price")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return goodss;
    }
    public void deleteGoods(String username, int gid) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql="delete from goods where username = ? and gid = ?";
        try {
            conn = connectionPool.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setInt(2, gid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(null, pstmt, conn);
        }
    }
    public void editGoods(int gid,float price,String goodsname,int choice) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql2="update goods set price = ? where gid = ?";
        String sql3="update goods set goodsname = ? where gid = ?";
        switch(choice){
            case 2:
                try{
                    conn = connectionPool.getConnection();
                    pstmt = conn.prepareStatement(sql2);
                    pstmt.setInt(1,gid);
                    pstmt.setFloat(2,price);
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }finally {
                    closeResources(null, pstmt, conn);
                }
                break;
            case 3:
                try{
                    conn = connectionPool.getConnection();
                    pstmt = conn.prepareStatement(sql3);
                    pstmt.setInt(1,gid);
                    pstmt.setString(2,goodsname);
                    pstmt.executeUpdate();
                }catch (SQLException e) {
                    throw new RuntimeException(e);
                }finally {
                    closeResources(null, pstmt, conn);
                }
                break;
        }
    }
    public List<updateDao> getupdate(int username) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql="select sellusername,buyusername,gid,status,price from trade where sellusername = ?";
        List<updateDao> updateDaos = new ArrayList<>();
        try{
            conn = connectionPool.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,username );
            rs = pstmt.executeQuery();
            while (rs.next()) {
                updateDaos.add(new updateDao(rs.getInt("sellusername"),rs.getInt("buyusername"),
                        rs.getInt("gid"),rs.getString("status"),rs.getFloat("price")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(null, pstmt, conn);
        }
        return updateDaos;
    }
    public void updateStatus(int gid) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql="update trade set status = 'pending' where gid = ?";
        try{
            conn = connectionPool.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,gid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(null, pstmt, conn);
        }

    }
    public float getMoney(int username) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql="select money from users where username = ?";
        float money = 0;
        try {
            conn = connectionPool.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, username);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                 money = rs.getFloat("money");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(null, pstmt, conn);
        }
        return money;
    }
    public void insertMoney(int username,float money) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql2="update users set money =money+ ? where username = ?";
        try{
            conn = connectionPool.getConnection();
            pstmt = conn.prepareStatement(sql2);
            pstmt.setFloat(1, money);
            pstmt.setInt(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void addNotifictions(int username, String context,int gid,String time) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql="insert into annoucement values( ?,?,?,?)  ";
        try{
            conn = connectionPool.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,username);
            pstmt.setString(2,context);
            pstmt.setInt(3,gid);
            pstmt.setString(4,time);
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(null, pstmt, conn);
        }
    }
    public List<notification> getNotifictions(int username) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql="select * from annoucement where username = ?  ";
        List<notification> notifications = new ArrayList<>();
        try{
            conn = connectionPool.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                notifications.add(new notification(rs.getInt("username"),rs.getString("message"),rs.getInt("gid"),rs.getString("time")));
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(null, pstmt, conn);
        }
        return notifications;
    }
    public void buygoods(int username, int buyername,int gid,float price,String goodsname) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql="insert into trade(sellusername,buyusername,gid,price,goodsname,status,payway) values( ?,?,?,?,?,'unrecieved',0) ";
        try{
            conn = connectionPool.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,username);
            pstmt.setInt(2,buyername);
            pstmt.setInt(3,gid);
            pstmt.setFloat(4,price);
            pstmt.setString(5,goodsname);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(null, pstmt, conn);
        }
    }
    public void deleteGoods(int gid) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql="delete from goods where gid = ?";
        try{
            conn = connectionPool.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,gid);
            pstmt.executeUpdate();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(null, pstmt, conn);
        }
    }
    public void consume(int username,float price){
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql="update users set money=money-? where username=?";
        try{
            conn = connectionPool.getConnection();
            pstmt=conn.prepareStatement(sql);
            pstmt.setFloat(1, price);
            pstmt.setInt(2,username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public  void sellgoods(int username,float price){
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql="update users set money=money+? where username=?";
        try{
            conn = connectionPool.getConnection();
            pstmt=conn.prepareStatement(sql);
            pstmt.setFloat(1, price);
            pstmt.setInt(2,username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void changePayway(int gid) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql="update trade set payway=1 where gid = ?";
        try{
            conn = connectionPool.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,gid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(null, pstmt, conn);
        }
    }
    public int getPayway(int gid) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql="select payway from trade where gid = ?";
        int payway = 0;
        try{
            conn = connectionPool.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,gid);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                payway= rs.getInt("payway");
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(null, pstmt, conn);
        }
        return payway;
    }
    public float getPrice(int gid) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql="select price from trade where gid = ?";
        float price = 0;
        try{
            conn = connectionPool.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,gid);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                price= rs.getFloat("price");
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(null, pstmt, conn);
        }
        return price;
    }
    public int getBuyuser(int gid) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql="select buyusername from trade where gid = ?";
        int buyuser = 0;
        try{
            conn = connectionPool.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,gid);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                buyuser = rs.getInt("buyusername");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(null, pstmt, conn);
        }
        return buyuser;
    }
    public String getGoodsname(int gid) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql="select goodsname from trade where gid = ?";
        String goodsname = "";
        try{
            conn = connectionPool.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,gid);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                goodsname = rs.getString("goodsname");
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(null, pstmt, conn);
        }
        return goodsname;
    }
    public void denouce(int username,int denocee,String reason) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "insert into denouce (denoucer,denoucee,statement,status)values(?,?,?,'no')";
        try {
            conn= connectionPool.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, username);
            pstmt.setInt(2, denocee);
            pstmt.setString(3, reason);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(null, pstmt, conn);
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
    public void saveGoods(int username,float price,String url,String goodsname,String type) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        Random random = new Random();
        int gid=random.nextInt();
        try {
            conn = connectionPool.getConnection();
            String sql = "insert into pregoods values (?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gid);
            pstmt.setInt(2, username);
            pstmt.setFloat(3, price);
            pstmt.setString(4, url);
            pstmt.setString(5, goodsname);
            pstmt.setString(6, type);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("检查用户名失败: " + e.getMessage());
        } finally {
            closeResources(null, pstmt, conn);
        }
    }
    public List<goodsDao> selectGoodsByType(String type) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql="select * from goods where type = ? ";
        List<goodsDao> goodsDaos = new ArrayList<>();
        try{
            conn = connectionPool.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,type);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                goodsDaos.add(new goodsDao(rs.getInt("gid"),
                        rs.getString("goodsname"),rs.getFloat("price"),
                        rs.getInt("username"),rs.getString("url")));
            }
        }catch (SQLException e) {

        }finally {
            closeResources(null, pstmt, conn);
        }
        return goodsDaos;
    }
    public List<goodsDao> priceByorder() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "select * from goods order by price";
        List<goodsDao> goodsDaos = new ArrayList<>();
        try {
            conn = connectionPool.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                goodsDaos.add(new goodsDao(rs.getInt("gid"),
                        rs.getString("goodsname"), rs.getFloat("price"),
                        rs.getInt("username"), rs.getString("url")));
            }
        } catch (SQLException e) {

        } finally {
            closeResources(null, pstmt, conn);
        }
        return goodsDaos;
    }
    public List<goodsDao>  priceByOrderDesc() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "select * from goods order by price desc";
        List<goodsDao> goodsDaos = new ArrayList<>();
        try {
            conn = connectionPool.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                goodsDaos.add(new goodsDao(rs.getInt("gid"),
                        rs.getString("goodsname"), rs.getFloat("price"),
                        rs.getInt("username"), rs.getString("url")));
            }
        } catch (SQLException e) {

        } finally {
            closeResources(null, pstmt, conn);
        }
        return goodsDaos;
    }
    public void setPaypassword(int username,String password) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try{
            conn = connectionPool.getConnection();
            pstmt = conn.prepareStatement("update users set paypassword = ? where username = ?");
            pstmt.setString(1, password);
            pstmt.setInt(2, username);
            pstmt.executeUpdate();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(null, pstmt, conn);
        }
    }
    public boolean justifypaypassword(int username,String password) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "select paypassword from users where username = ?";
        String paypassword="";
        try{
            conn = connectionPool.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                paypassword = rs.getString("paypassword");

            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources(null, pstmt, conn);
        }
        System.out.println(paypassword+123);
        return paypassword.equals(password);
    }
    public static void main(String[] args) {
        SimpleConnectionPool pool = SimpleConnectionPool.getInstance();
        UserConnector connector = new UserConnector(pool);

        // 测试注册
        boolean registered = connector.registerUser("testuser", "password123");
        System.out.println("注册结果: " + (registered ? "成功" : "失败"));

        // 测试登录
        int userId = connector.Login("testuser", "password123");
        System.out.println("登录结果: " + (userId != -1 ? "成功，用户ID: " + userId : "失败"));

        pool.shutdown();
    }
}
