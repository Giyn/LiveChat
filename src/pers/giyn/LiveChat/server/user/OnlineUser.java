package pers.giyn.LiveChat.server.user;

import java.nio.channels.AsynchronousSocketChannel;

/**
 * @author 许继元
 */
public class OnlineUser {
    public final User user;
    public AsynchronousSocketChannel sc;
    /**
     * For receiving
     */
    public String publicKey;
    /**
     * For broadcasting
     */
    public String privateKey;

    public OnlineUser(AsynchronousSocketChannel sc, String publicKey, String privateKey, User user) {
        this.sc = sc;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.user = user;
    }
}
