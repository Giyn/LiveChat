package pers.giyn.LiveChat.client.file.chattingRecord;

import pers.giyn.LiveChat.client.file.FileFolder;

import java.io.*;
import java.util.ArrayList;

/**
 * @author 许继元
 */
public class ChattingRecordManager {
    public static void saveChattingRecord(String srcId, String dstId, ArrayList<ChattingRecord> record) throws IOException {
        File file = new File(FileFolder.getDefaultDirectory() + "/" + srcId + "/" + dstId + ".dat");

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(file));
        outputStream.writeInt(record.size());

        for (ChattingRecord chattingRecord : record) {
            chattingRecord.toFile(outputStream);
        }
        outputStream.close();
    }

    public static ArrayList<ChattingRecord> readChattingRecord(String srcId, String dstId) throws IOException {
        File file = new File(FileFolder.getDefaultDirectory() + "/" + srcId + "/" + dstId + ".dat");
        ArrayList<ChattingRecord> resArrayList = new ArrayList<>();

        if (!file.exists() || file.isDirectory()) {
            return resArrayList;
        }
        DataInputStream inputStream = new DataInputStream(new FileInputStream(file));

        try {
            int size = inputStream.readInt();
            System.out.println(size);
            for (int i = 0; i < size; ++i) {
                resArrayList.add(ChattingRecord.toData(inputStream));
            }
            inputStream.close();
        } catch (IOException e) {
            inputStream.close();
            e.printStackTrace();
        }

        return resArrayList;
    }
}
