package service;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/chat/{username}")
public class ChatServer {

    private static final Map<String, Session> onlineUsers = new ConcurrentHashMap<>();
    private static final JedisPool jedisPool = new JedisPool("localhost", 6379);

    private String username;

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        this.username = username;
        onlineUsers.put(username, session);
        System.out.println(username + " 连接成功");
        try (Jedis jedis = jedisPool.getResource()) {
            for (String key : jedis.keys("chat:*:" + username)) {
                for (String msg : jedis.lrange(key, 0, -1)) {
                    session.getBasicRemote().sendText("[历史] " + msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        JsonObject obj = JsonParser.parseString(message).getAsJsonObject();
        String to = obj.get("to").getAsString();
        String msg = obj.get("msg").getAsString();

        String formatted = username + " 对你说: " + msg;

        // 存入 Redis
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.rpush("chat:" + username + ":" + to, formatted);
            jedis.rpush("chat:" + to + ":" + username, formatted);
        }

        // 发送消息
        Session toSession = onlineUsers.get(to);
        if (toSession != null && toSession.isOpen()) {
            try {
                toSession.getBasicRemote().sendText(formatted);
            } catch ( IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        onlineUsers.remove(username);
        System.out.println(username + " 已断开");
    }
}

