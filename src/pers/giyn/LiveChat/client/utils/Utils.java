package pers.giyn.LiveChat.client.utils;

import pers.giyn.LiveChat.GUI.friend.Friend;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.security.KeyPairGenerator.getInstance;

/**
 * @author 许继元
 */
public class Utils {

    public static class PackageUtils {
        public static ArrayList<byte[]> SecondaryPack(byte[] src, int blockSize) {
            ArrayList<byte[]> resArrayList = new ArrayList<>();
            byte[] md5 = MD5Utils.MD5Byte(src);

            if (blockSize <= md5.length + 1) {
                return null;
            }

            for (int i = 0; i < src.length; i += (blockSize - 1 - md5.length)) {
                byte[] temp = Convertion.BytesExpand(blockSize, md5);
                temp[md5.length] = 1;

                for (int j = md5.length + 1; j < blockSize; ++j) {
                    if (i + j - md5.length - 1 < src.length) {
                        temp[j] = src[i + j - md5.length - 1];
                    }
                }
                resArrayList.add(temp);
            }
            byte[] eof = Convertion.BytesExpand(blockSize, md5);
            eof[md5.length] = 0;
            resArrayList.add(eof);
            return resArrayList;
        }

        public static byte[] SecondaryUnPack(ArrayList<byte[]> src, int BANDWIDTH) {
            byte[] res = new byte[src.size() * BANDWIDTH];
            int offset = 0;

            for (byte[] frag : src) {
                if (frag[16] == 0) {
                    break;
                }
                for (int i = 17; i < frag.length; ++i) {
                    res[offset] = frag[i];
                    offset++;
                }
            }

            return res;
        }

        public static Map<String, String> readMD5andState(ByteBuffer data) {
            Map<String, String> resMap = new HashMap<>();
            byte[] md5 = new byte[16];
            data.get(md5, 0, 16);
            resMap.put("MD5", Hex.encodeHexString(md5));
            resMap.put("info", "" + data.get(16));
            return resMap;
        }

        public static byte[] filePack(String id, String name, byte[] file, String privateKey) {
            byte[] id_RSA, name_RSA, file_RSA;

            try {
                id_RSA = RSAUtils.privateEncrypt(id.getBytes(UTF_8), RSAUtils.getPrivateKey(privateKey));
                name_RSA = RSAUtils.privateEncrypt(name.getBytes(UTF_8), RSAUtils.getPrivateKey(privateKey));
                file_RSA = RSAUtils.privateEncrypt(file, RSAUtils.getPrivateKey(privateKey));
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
                return null;
            }
            byte[] data = new byte[2 + 4 + 4 + 4 + id_RSA.length + name_RSA.length + file_RSA.length];
            data[0] = 6;
            byte[] size1 = Convertion.int2Bytes(id_RSA.length),
                    size2 = Convertion.int2Bytes(file_RSA.length),
                    size3 = Convertion.int2Bytes(name_RSA.length);

            for (int i = 0; i < 4; ++i) {
                data[2 + i] = size1[i];
                data[6 + i] = size3[i];
                data[10 + i] = size2[i];
            }

            for (int i = 0; i < id_RSA.length; ++i) {
                data[2 + 4 + 4 + 4 + i] = id_RSA[i];
            }
            for (int i = 0; i < name_RSA.length; ++i) {
                data[2 + 4 + 4 + 4 + id_RSA.length + i] = name_RSA[i];
            }
            for (int i = 0; i < file_RSA.length; ++i) {
                data[2 + 4 + 4 + 4 + id_RSA.length + name_RSA.length + i] = file_RSA[i];
            }

            return data;
        }

        public static Map<String, Object> fileUnPack(ByteBuffer msg, String publicKey) {
            Map<String, Object> resMap = new HashMap<>();
            byte[] data = msg.array().clone(),
                    id = new byte[Convertion.bytes2Int(Convertion.BytesCapture(data, 2, 4))],
                    name = new byte[Convertion.bytes2Int(Convertion.BytesCapture(data, 6, 4))],
                    message = new byte[Convertion.bytes2Int(Convertion.BytesCapture(data, 10, 4))];

            if (id.length >= 0) {
                System.arraycopy(data, 14, id, 0, id.length);
            }
            for (int i = 0; i < name.length; ++i) {
                name[i] = data[14 + id.length + i];
            }
            for (int i = 0; i < message.length; ++i) {
                message[i] = data[14 + id.length + name.length + i];
            }

            try {
                resMap.put("name", new String(RSAUtils.publicDecrypt(name, RSAUtils.getPublicKey(publicKey)), UTF_8));
                resMap.put("file", RSAUtils.publicDecrypt(message, RSAUtils.getPublicKey(publicKey)));
                resMap.put("id", new String(RSAUtils.publicDecrypt(id, RSAUtils.getPublicKey(publicKey)), UTF_8));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return resMap;
        }

        public static byte[] friendListPack(byte friendType, ArrayList<Friend> friends, String privateKey) {
            byte[] friends_RSA;
            StringBuilder friend_PretreatString = new StringBuilder();

            for (Friend user : friends) {
                friend_PretreatString.append(user.toString()).append("\n");
            }

            try {
                friends_RSA = RSAUtils.privateEncrypt(friend_PretreatString.toString().getBytes(UTF_8), RSAUtils.getPrivateKey(privateKey));
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
                return null;
            }
            byte[] data = new byte[4096];
            data[0] = 5;
            data[1] = friendType;
            byte[] size1 = Convertion.int2Bytes(friends_RSA.length);
            System.arraycopy(size1, 0, data, 2, 4);
            for (int i = 0; i < friends_RSA.length; ++i) {
                data[2 + 4 + i] = friends_RSA[i];
            }

            return data;
        }

        public static ArrayList<Friend> friendListUnPack(ByteBuffer msg, String publicKey) {
            ArrayList<Friend> resArray = new ArrayList<>();
            byte[] data = msg.array(),
                    friendList = new byte[Convertion.bytes2Int(Convertion.BytesCapture(data, 2, 4))];

            if (friendList.length >= 0) {
                System.arraycopy(data, 6, friendList, 0, friendList.length);
            }

            try {
                String friendString = new String(RSAUtils.publicDecrypt(friendList, RSAUtils.getPublicKey(publicKey)), UTF_8);
                String[] temp = friendString.split("\n");

                for (String string : temp) {
                    String[] temp2 = string.split("@");
                    if (temp2.length == 2) {
                        resArray.add(new Friend(temp2[1], temp2[0]));
                    } else if (temp2.length == 3) {
                        resArray.add(new Friend(temp2[1], temp2[0], Integer.parseInt(temp2[2])));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return resArray;
        }

        public static byte[] messagePack(String id, byte msgType, String msg, String privateKey) {
            byte[] id_RSA, msg_RSA;
            try {
                id_RSA = RSAUtils.privateEncrypt(id.getBytes(UTF_8), RSAUtils.getPrivateKey(privateKey));
                msg_RSA = RSAUtils.privateEncrypt(msg.getBytes(UTF_8), RSAUtils.getPrivateKey(privateKey));
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
                return null;
            }
            byte[] data = new byte[2 + 4 + 4 + id_RSA.length + msg_RSA.length];
            data[0] = 3;
            data[1] = msgType;
            byte[] size1 = Convertion.int2Bytes(id_RSA.length),
                    size2 = Convertion.int2Bytes(msg_RSA.length);

            for (int i = 0; i < 4; ++i) {
                data[2 + i] = size1[i];
                data[6 + i] = size2[i];
            }
            for (int i = 0; i < id_RSA.length; ++i) {
                data[2 + 4 + 4 + i] = id_RSA[i];
            }
            for (int i = 0; i < msg_RSA.length; ++i) {
                data[2 + 4 + 4 + id_RSA.length + i] = msg_RSA[i];
            }

            return data;
        }

        public static Map<String, String> messageUnPack(ByteBuffer msg, String publicKey) {
            Map<String, String> resMap = new HashMap<>();
            byte[] data = msg.array(),
                    id = new byte[Convertion.bytes2Int(Convertion.BytesCapture(data, 2, 4))],
                    message = new byte[Convertion.bytes2Int(Convertion.BytesCapture(data, 6, 4))];

            if (id.length >= 0) {
                System.arraycopy(data, 10, id, 0, id.length);
            }

            for (int i = 0; i < message.length; ++i) {
                message[i] = data[10 + id.length + i];
            }

            try {
                resMap.put("msgType", "" + data[1]);
                resMap.put("message", new String(RSAUtils.publicDecrypt(message, RSAUtils.getPublicKey(publicKey)), UTF_8));
                resMap.put("id", new String(RSAUtils.publicDecrypt(id, RSAUtils.getPublicKey(publicKey)), UTF_8));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return resMap;
        }

        public static Map<String, String> errorUnPack(ByteBuffer error) {
            Map<String, String> resMap = new HashMap<>();
            byte[] data = error.array(),
                    msg = new byte[Convertion.bytes2Int(Convertion.BytesCapture(data, 2, 4))];
            if (msg.length >= 0) {
                System.arraycopy(data, 6, msg, 0, msg.length);
            }
            resMap.put("subStatus", "" + data[1]);
            resMap.put("message", new String(msg, UTF_8));

            return resMap;
        }

        public static Map<String, String> loginSucessUnPack(ByteBuffer loginPackage) {
            Map<String, String> resMap = new HashMap<>();
            byte[] data = loginPackage.array(),
                    publicKey = new byte[Convertion.bytes2Int(Convertion.BytesCapture(data, 1, 4))],
                    privateKey = new byte[Convertion.bytes2Int(Convertion.BytesCapture(data, 5, 4))];

            if (publicKey.length >= 0) {
                System.arraycopy(data, 9, publicKey, 0, publicKey.length);
            }
            for (int i = 0; i < privateKey.length; ++i) {
                privateKey[i] = data[9 + publicKey.length + i];
            }
            resMap.put("publicKey", new String(publicKey));
            resMap.put("privateKey", new String(privateKey));

            return resMap;
        }

        public static byte[] loginPack(String id, String password) {
            byte[] signInData = ("xxx" + id + password).getBytes(UTF_8);
            signInData[0] = 1;
            signInData[1] = (byte) id.getBytes(UTF_8).length;
            signInData[2] = (byte) password.getBytes(UTF_8).length;

            return signInData;
        }
    }

    public static class Convertion {

        public static byte[] int2Bytes(int n) {
            byte[] b = new byte[4];
            b[3] = (byte) (n & 0xff);
            b[2] = (byte) (n >> 8 & 0xff);
            b[1] = (byte) (n >> 16 & 0xff);
            b[0] = (byte) (n >> 24 & 0xff);

            return b;
        }

        public static int bytes2Int(byte[] bytes) {
            int int1 = bytes[3] & 0xff;
            int int2 = (bytes[2] & 0xff) << 8;
            int int3 = (bytes[1] & 0xff) << 16;
            int int4 = (bytes[0] & 0xff) << 24;

            return int1 | int2 | int3 | int4;
        }

        public static byte[] BytesCapture(byte[] src, int offset, int length) {
            byte[] result = new byte[length];

            for (int index = 0; index < length; index++) {
                try {
                    result[index] = src[index + offset];
                } catch (ArrayIndexOutOfBoundsException e) {
                    break;
                }
            }

            return result;
        }

        public static byte[] BytesExpand(int n, byte[] src) {
            byte[] result = new byte[n];

            for (int index = 0; index < n; index++) {
                try {
                    result[index] = src[index];
                } catch (ArrayIndexOutOfBoundsException e) {
                    break;
                }
            }

            return result;
        }
    }

    public static class MD5Utils {
        public static MessageDigest messagedigest;

        static {
            try {
                messagedigest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        public static byte[] MD5Byte(byte[] src) {
            return messagedigest.digest(src);
        }
    }

    public static class RSAUtils {
        public static final String RSA_ALGORITHM = "RSA";

        public static Map<String, String> createKeys(int keySize) {
            KeyPairGenerator kpg;
            try {
                kpg = getInstance(RSA_ALGORITHM);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
            }

            kpg.initialize(keySize);
            KeyPair keyPair = kpg.generateKeyPair();
            Key publicKey = keyPair.getPublic();
            String publicKeyStr = Base64.encodeBase64URLSafeString(publicKey.getEncoded());
            Key privateKey = keyPair.getPrivate();
            String privateKeyStr = Base64.encodeBase64URLSafeString(privateKey.getEncoded());

            Map<String, String> keyPairMap = new HashMap<>();
            keyPairMap.put("publicKey", publicKeyStr);
            keyPairMap.put("privateKey", privateKeyStr);

            return keyPairMap;
        }

        public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
            return (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        }

        public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
            return (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        }

        public static byte[] privateEncrypt(byte[] data, RSAPrivateKey privateKey) {
            try {
                Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
                cipher.init(Cipher.ENCRYPT_MODE, privateKey);
                return rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data, privateKey.getModulus().bitLength());
            } catch (Exception e) {
                throw new RuntimeException("加密字符串[" + Arrays.toString(data) + "]时遇到异常", e);
            }
        }

        public static byte[] publicDecrypt(byte[] data, RSAPublicKey publicKey) {
            try {
                Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, publicKey);
                return rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, data, publicKey.getModulus().bitLength());
            } catch (Exception e) {
                throw new RuntimeException("解密字符串[" + Arrays.toString(data) + "]时遇到异常", e);
            }
        }

        private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) throws IOException {
            int maxBlock;

            if (opmode == Cipher.DECRYPT_MODE) {
                maxBlock = keySize / 8;
            } else {
                maxBlock = keySize / 8 - 11;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] buff;
            int i = 0;

            try {
                while (datas.length > offSet) {
                    if (datas.length - offSet > maxBlock) {
                        buff = cipher.doFinal(datas, offSet, maxBlock);
                    } else {
                        buff = cipher.doFinal(datas, offSet, datas.length - offSet);
                    }
                    out.write(buff, 0, buff.length);
                    i++;
                    offSet = i * maxBlock;
                }
            } catch (Exception e) {
                throw new RuntimeException("加解密阈值为[" + maxBlock + "]的数据时发生异常", e);
            }
            byte[] resultDatas = out.toByteArray();
            out.close();

            return resultDatas;
        }
    }

    public static class FileUtils {

        public static byte[] readFile(File file) throws IOException {
            return Files.readAllBytes(file.toPath());
        }

        public static void saveFile(File file, byte[] data) throws IOException {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(data);
            outputStream.close();
        }
    }

    public static class Base64Utils {

        public static ImageIcon base642Image(String base64) {
            byte[] btDataFile = Base64.decodeBase64(base64);
            BufferedImage image;

            try {
                image = ImageIO.read(new ByteArrayInputStream(btDataFile));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return new ImageIcon(image);
        }

        public static String readBase64(File file) throws IOException {
            byte[] fileByte = Files.readAllBytes(file.toPath());

            return Base64.encodeBase64String(fileByte);
        }

        public static void outPutFile(String base64, File file) {
            byte[] btDataFile = Base64.decodeBase64(base64);

            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(btDataFile);
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
