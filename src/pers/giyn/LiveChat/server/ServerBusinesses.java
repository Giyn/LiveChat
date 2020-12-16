package pers.giyn.LiveChat.server;

import pers.giyn.LiveChat.server.user.OnlineUser;
import pers.giyn.LiveChat.server.user.User;
import pers.giyn.LiveChat.server.user.UsersContainer;
import org.apache.commons.codec.binary.Hex;
import pers.giyn.LiveChat.server.utils.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author 许继元
 */
public class ServerBusinesses {
    /**
     * Default
     */
    public static int PORT_DEFAULT = 543;
    public int port;
    /**
     * Bandwidth
     */
    public static final int BANDWIDTH = 1024 * 8;
    public HashMap<String, OnlineUser> onlineUserList = new HashMap<>();
    public ExecutorService executor = null;
    public AsynchronousChannelGroup channelGroup = null;
    public AsynchronousServerSocketChannel serverChannel = null;
    public Map<String, Map<String, ArrayList<byte[]>>> bufferDataMap = new HashMap<>();

    public ServerBusinesses() throws IOException {
        this(PORT_DEFAULT);
    }

    public ServerBusinesses(int port) throws IOException {
        this.port = port;
        this.init();
    }

    public void init() throws IOException {
        this.onlineUserList = new HashMap<>();
        this.executor = Executors.newFixedThreadPool(20);
        this.channelGroup = AsynchronousChannelGroup.withThreadPool(executor);
        this.serverChannel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(this.port));
    }

    public void start() {
        this.serverChannel.accept(null, new LoginHandler(this.serverChannel, this.onlineUserList));

        try {
            System.out.println("服务器启动成功!");
            while (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void secondaryPackAndSent(AsynchronousSocketChannel sc, byte[] data, int blockSize) throws InterruptedException, ExecutionException {
        ArrayList<byte[]> dataSeriesArrayList;
        dataSeriesArrayList = Utils.PackageUtils.SecondaryPack(data, blockSize);

        assert dataSeriesArrayList != null;
        for (byte[] datas : dataSeriesArrayList) {
            sc.write(ByteBuffer.wrap(datas)).get();
        }
    }

    public void secondaryPackAndSentWithLimit(AsynchronousSocketChannel sc, byte[] data, int blockSize) throws InterruptedException, ExecutionException {
        ArrayList<byte[]> dataSeriesArrayList = Utils.PackageUtils.SecondaryPack(data, blockSize);

        assert dataSeriesArrayList != null;
        for (byte[] datas : dataSeriesArrayList) {
            sc.write(ByteBuffer.wrap(datas)).get();
        }
    }

    public boolean sendMsg(String srcId, String dstId, String msg, int type) {
        boolean res;
        res = false;

        if (dstId != null) {
            OnlineUser user = this.onlineUserList.get(dstId);
            if (user == null) {
                res = true;
            } else {
                try {
                    secondaryPackAndSent(user.sc, Utils.PackageUtils.messagePack(srcId, (byte) type, msg, user.privateKey), BANDWIDTH);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } else {
            for (String id : this.onlineUserList.keySet()) {
                OnlineUser user = this.onlineUserList.get(id);
                try {
                    secondaryPackAndSent(user.sc, Utils.PackageUtils.messagePack(srcId, (byte) type, msg, user.privateKey), BANDWIDTH);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

        return res;
    }

    public void updateFriendList(OnlineUser user) {
        try {
            ArrayList<User> friendList = UsersContainer.INSTANCE.searchFriendAsUser(user.user.id);
            boolean[] onLine = new boolean[friendList.size()];

            for (int i = 0; i < onLine.length; ++i) {
                onLine[i] = onlineUserList.get(friendList.get(i).id) != null;
            }
            secondaryPackAndSent(user.sc, Utils.PackageUtils.friendListPack((byte) 1,
                    friendList, onLine,
                    user.privateKey), BANDWIDTH);
            friendList = UsersContainer.INSTANCE.searchApplyFriendAsUser(user.user.id);
            onLine = new boolean[friendList.size()];
            secondaryPackAndSent(user.sc, Utils.PackageUtils.friendListPack((byte) 2,
                    friendList, onLine,
                    user.privateKey), BANDWIDTH);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    class LoopingMessageHandler implements CompletionHandler<Integer, Object> {
        public AsynchronousSocketChannel sc;
        public OnlineUser user;
        public ByteBuffer msgData = ByteBuffer.allocate(BANDWIDTH);

        public LoopingMessageHandler(AsynchronousSocketChannel sc, OnlineUser user) {
            this.sc = sc;
            this.user = user;
        }

        public void process(String md5) {
            if (bufferDataMap.get(user.user.id).get(md5) == null) {
                return;
            }
            ByteBuffer msgData = ByteBuffer.wrap(Utils.PackageUtils.secondaryUnPack(bufferDataMap.get(user.user.id).get(md5), BANDWIDTH));
            bufferDataMap.get(user.user.id).remove(md5);

            try {
                if (msgData.get(0) == 3) {
                    Map<String, String> resMap = Utils.PackageUtils.messageUnPack(msgData, user.publicKey);
                    boolean isSend = false;
                    
                    for (String id : UsersContainer.INSTANCE.searchFriend(user.user.id)) {
                        if (id.equals(resMap.get("id"))) {
                            sendMsg(user.user.id, resMap.get("id"), resMap.get("message"), Integer.parseInt(resMap.get("msgType")));
                            isSend = true;
                        }
                    }

                    if (!isSend) {
                        try {
                            secondaryPackAndSent(sc, Utils.PackageUtils.errorPack((byte) 4, "您与" + resMap.get("id") + "并非好友！"), BANDWIDTH);
                        } catch (InterruptedException | ExecutionException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else if (msgData.get(0) == 5) {
                    ArrayList<String> FriendStrings = Utils.PackageUtils.friendListUnPack(msgData, user.publicKey);
                    if (msgData.get(1) == 1) {
                        for (String id : FriendStrings) {
                            if (UsersContainer.INSTANCE.users.get(id) == null) {
                                secondaryPackAndSent(sc, Utils.PackageUtils.errorPack((byte) 4, "Id:" + id + "不存在！"), BANDWIDTH);
                                break;
                            }
                            UsersContainer.INSTANCE.connectFriend(user.user.id, id);

                            try {
                                if (onlineUserList.get(id) != null) {
                                    updateFriendList(onlineUserList.get(id));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (msgData.get(1) == 2) {
                        for (String id : FriendStrings) {
                            if (UsersContainer.INSTANCE.users.get(id) == null) {
                                secondaryPackAndSent(sc, Utils.PackageUtils.errorPack((byte) 4, "Id:" + id + "不存在！"), BANDWIDTH);
                                break;
                            }
                            UsersContainer.INSTANCE.deleteFriend(user.user.id, id);

                            try {
                                if (onlineUserList.get(id) != null) {
                                    updateFriendList(onlineUserList.get(id));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    updateFriendList(user);
                }

                if (msgData.get(0) == 6) {
                    Map<String, Object> resMap = Utils.PackageUtils.fileUnPack(msgData, user.publicKey);
                    System.out.println("收到文件");
                    System.out.println("目标ID为：" + resMap.get("id"));
                    System.out.println("名字为：" + resMap.get("name"));
                    boolean isSend = false;

                    for (String id : UsersContainer.INSTANCE.searchFriend(user.user.id)) {
                        if (id.equals(resMap.get("id")) && onlineUserList.get(resMap.get("id")) != null) {
                            isSend = true;
                            byte[] data = Utils.PackageUtils.filePack(user.user.id, (String) resMap.get("name"), (byte[]) resMap.get("file"), onlineUserList.get(id).privateKey);
                            new Thread(
                                    () -> {
                                        try {
                                            secondaryPackAndSentWithLimit(onlineUserList.get(id).sc, data, BANDWIDTH);
                                        } catch (InterruptedException | ExecutionException e) {
                                            e.printStackTrace();
                                        }
                                    }
                            ).start();
                        }
                    }

                    if (!isSend) {
                        try {
                            sc.write(ByteBuffer.wrap(Utils.PackageUtils.errorPack((byte) 4, "您与" + resMap.get("id") + "并非好友!"))).get();
                        } catch (InterruptedException | ExecutionException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void enQueue(byte[] data) {
            String md5 = Hex.encodeHexString(Utils.Convertion.BytesCapture(data, 0, 16));
            ArrayList<byte[]> queue = bufferDataMap.get(this.user.user.id).computeIfAbsent(md5, k -> new ArrayList<>());
            queue.add(data);
        }

        @Override
        public void completed(Integer result, Object attachment) {
            msgData.flip();
            Map<String, String> dataMap = Utils.PackageUtils.readMD5andState(msgData);
            System.out.println(bufferDataMap.get(this.user.user.id).get(dataMap.get("MD5")));

            if ("1".equals(dataMap.get("info"))) {
                enQueue(msgData.array().clone());
            } else if ("0".equals(dataMap.get("info"))) {
                process(dataMap.get("MD5"));
            }
            msgData.clear();
            this.sc.read(this.msgData, null, this);
        }

        @Override
        public void failed(Throwable ex, Object attachment) {
            onlineUserList.remove(this.user.user.id);
            bufferDataMap.remove(this.user.user.id);
            userLogOut(user);
            System.out.println("用户" + this.user.user.toString() + "下线" + "!当前在线用户数量：" + onlineUserList.size());
        }
    }

    public void userLogOut(OnlineUser user) {
        for (String friendId : UsersContainer.INSTANCE.searchFriend(user.user.id)) {
            if (onlineUserList.get(friendId) != null) {
                updateFriendList(onlineUserList.get(friendId));
            }
        }
    }

    class LoginHandler implements CompletionHandler<AsynchronousSocketChannel, Object> {
        public AsynchronousServerSocketChannel serverChannel;
        public HashMap<String, OnlineUser> channelList;

        public LoginHandler(AsynchronousServerSocketChannel sc, HashMap<String, OnlineUser> cl) {
            this.serverChannel = sc;
            this.channelList = cl;
        }

        @Override
        public void completed(final AsynchronousSocketChannel sc, Object attachment) {
            this.serverChannel.accept(null, this);
            ByteBuffer signInMsg = ByteBuffer.allocate(BANDWIDTH);
            final Map<String, OnlineUser> channel = this.channelList;
            sc.read(signInMsg, 1000, TimeUnit.MILLISECONDS, this.channelList,
                    new CompletionHandler<Integer, Object>() {

                        @Override
                        public void completed(Integer result, Object attachment) {

                            signInMsg.flip();
                            if (signInMsg.get(0) == 1) {
                                Map<String, String> infoMap = Utils.PackageUtils.loginUnPack(signInMsg);
                                User targetUser = UsersContainer.INSTANCE.users.get(infoMap.get("id"));

                                if (targetUser == null) {
                                    try {
                                        sc.write(ByteBuffer.wrap(Utils.PackageUtils.errorPack((byte) 1, "用户名不存在")))
                                                .get();
                                    } catch (InterruptedException | ExecutionException e1) {
                                        e1.printStackTrace();
                                    }
                                    System.out.println("用户名不存在!");

                                    try {
                                        sc.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    return;

                                } else if (!Utils.LoginUtils.verifyPassword(infoMap.get("password"),
                                        targetUser.passwordMd5)) {
                                    try {
                                        sc.write(ByteBuffer.wrap(Utils.PackageUtils.errorPack((byte) 2, "密码错误"))).get();
                                    } catch (InterruptedException | ExecutionException e1) {
                                        e1.printStackTrace();
                                    }
                                    System.out.println("密码错误!");
                                    try {
                                        sc.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    return;
                                } else if (onlineUserList.get(infoMap.get("id")) != null) {
                                    try {
                                        sc.write(ByteBuffer.wrap(Utils.PackageUtils.errorPack((byte) 3, "您的账号在其他地方被登录了!"))).get();
                                    } catch (InterruptedException | ExecutionException e1) {
                                        e1.printStackTrace();
                                    }
                                    System.out.println("重复登录!");

                                    try {
                                        sc.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    return;
                                }

                                Map<String, String> keyPair1 = Utils.RSAUtils.createKeys(1024);  // For client
                                Map<String, String> keyPair2 = Utils.RSAUtils.createKeys(1024);  // For server
                                OnlineUser targetOnlineUser = new OnlineUser(sc, keyPair2.get("publicKey"),
                                        keyPair1.get("privateKey"), targetUser);
                                channel.put(infoMap.get("id"), targetOnlineUser);
                                bufferDataMap.put(infoMap.get("id"), new HashMap<>());

                                try {
                                    sc.write(ByteBuffer.wrap(Utils.PackageUtils.loginSuccessPack(keyPair1.get("publicKey"),
                                            keyPair2.get("privateKey")))).get();
                                    updateFriendList(targetOnlineUser);
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                }
                                LoopingMessageHandler msgHandler = new LoopingMessageHandler(sc, targetOnlineUser);
                                sc.read(msgHandler.msgData, null, msgHandler);

                                for (String friendId : UsersContainer.INSTANCE.searchFriend(targetOnlineUser.user.id)) {
                                    if (onlineUserList.get(friendId) != null) {
                                        updateFriendList(onlineUserList.get(friendId));
                                    }
                                }
                                System.out.println("用户" + targetUser.toString() + "上线!当前在线用户数量：" + channel.size());
                            } else {
                                try {
                                    sc.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void failed(Throwable ex, Object attachment) {
                            System.out.println("登录超时!");
                            try {
                                sc.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }

        @Override
        public void failed(Throwable ex, Object attachment) {
            System.out.println("连接失败: " + ex);
        }
    }
}
