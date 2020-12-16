package pers.giyn.LiveChat.client.file;

import java.io.File;

/**
 * @author 许继元
 */
public class FileFolder {

    public static String getDefaultDirectory() {
        return "./src/pers/giyn/LiveChat/client/chattingRecord";
    }
    static {
        init();
    }

    public static void init() {
        File file = new File(getDefaultDirectory());
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        file.mkdirs();
    }
}
