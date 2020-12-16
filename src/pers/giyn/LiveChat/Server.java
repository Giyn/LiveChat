package pers.giyn.LiveChat;

import pers.giyn.LiveChat.server.ServerBusinesses;

import java.io.IOException;

/**
 * @author 许继元
 */
public class Server {
    public static void main(String[] args) throws IOException {
        ServerBusinesses server = new ServerBusinesses();
        server.start();
    }
}
