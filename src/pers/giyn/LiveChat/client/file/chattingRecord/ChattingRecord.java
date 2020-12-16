package pers.giyn.LiveChat.client.file.chattingRecord;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author 许继元
 */
public class ChattingRecord {
    public String id;
    public String nickName;
    public String msg;
    /**
     * 0 for self
     * 1 for opposite
     */
    public byte state;

    public ChattingRecord(String id, String nickName, String msg, byte state) {
        this.id = id;
        this.nickName = nickName;
        this.msg = msg;
        this.state = state;
    }

    public ChattingRecord() {
        this("", "", "", (byte) 0);
    }

    public void toFile(DataOutputStream stream) throws IOException {
        stream.writeByte(this.state);
        stream.writeInt(this.msg.getBytes(StandardCharsets.UTF_8).length);
        stream.write(this.msg.getBytes(StandardCharsets.UTF_8));
    }

    public static ChattingRecord toData(DataInputStream stream) throws IOException {
        ChattingRecord resChattingRecord;
        resChattingRecord = new ChattingRecord();
        resChattingRecord.state = stream.readByte();
        byte[] msg = new byte[stream.readInt()];
        stream.read(msg);
        resChattingRecord.msg = new String(msg, StandardCharsets.UTF_8);

        return resChattingRecord;
    }
}
