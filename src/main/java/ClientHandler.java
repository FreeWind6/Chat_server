import mysql.HibernateUtil;
import mysql.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ClientHandler {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private ServerMain server;
    private String nick;
    final String password = "MZygpewJsCpRrfOr";
    String salt1 = KeyGenerators.string().generateKey();
    HibernateUtil hibernateUtil = new HibernateUtil();
    // https://razilov-code.ru/2018/03/16/aes-java/
    TextEncryptor encryptors1 = Encryptors.text(password, salt1);

    static final Logger rootLogger = LogManager.getRootLogger();

    public ClientHandler(ServerMain server, Socket socket) {
        try {
            this.socket = socket;
            this.server = server;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String str = in.readUTF();
                            TextEncryptor encryptors2 = Encryptors.text(password, str.substring(0, 16));
                            str = encryptors2.decrypt(str.substring(16));
                            if (str.startsWith("/auth")) {
                                String[] tokes = str.split(" ");
                                String pass = md5Custom(tokes[2]);
//                                String newNick = MainDB.getNickByLoginAndPass(tokes[1], pass);
                                String newNick = hibernateUtil.getNickByLoginAndPass(tokes[1], pass);
                                if (newNick != null) {
                                    if (!server.isNickBusy(newNick)) {
                                        sendMsg("/authok " + newNick);
                                        nick = newNick;
                                        server.subscribe(ClientHandler.this);
                                        rootLogger.warn("Сlient " + nick + " connected!");
                                        break;
                                    } else {
                                        sendMsg("Учетная запись уже используется!");
                                    }
                                } else {
                                    sendMsg("Неверный логин/пароль!");
                                }
                            }
                        }

                        while (true) {
                            String str = in.readUTF();
                            TextEncryptor encryptors2 = Encryptors.text(password, str.substring(0, 16));
                            str = encryptors2.decrypt(str.substring(16));
                            if (str.startsWith("/")) {
                                if (str.equals("/end")) {
                                    String textToEncrypt = "/serverclosed";
                                    String cipherText = encryptors1.encrypt(textToEncrypt);
                                    cipherText = salt1 + "" + cipherText;
                                    out.writeUTF(cipherText);
                                    rootLogger.warn("Сlient " + nick + " disconnected!");
                                    break;
                                }
                                if (str.startsWith("/w ")) {
                                    String[] tokens = str.split(" ", 3);
                                    server.sendPersonalMsg(ClientHandler.this, tokens[1], tokens[2]);
                                }
                                if (str.startsWith("/blacklist ")) {
                                    String[] tokens = str.split(" ");
                                    if (tokens[1].equals(getNick())) {
                                        sendMsg("Вы не можете заблокировать самого себя!");
                                    } else {
//                                        String strGetNick = MainDB.getIdByNicknameFromMain(getNick());
//                                        String strTokens1 = MainDB.getIdByNicknameFromMain(tokens[1]);
                                        Main strGetNick = hibernateUtil.getMainByNicknameFromMain(getNick());
                                        Main strTokens1 = hibernateUtil.getMainByNicknameFromMain(tokens[1]);
                                        if (strTokens1 != null) {
//                                            MainDB.insertTable(strGetNick, strTokens1);
                                            hibernateUtil.insertTable(strGetNick, strTokens1);
                                            sendMsg("Вы добавили пользователя  " + tokens[1] + " в черный список!");
                                        } else {
                                            sendMsg("Пользователя " + tokens[1] + " нет в базе!");
                                        }
                                    }
                                }
                            } else {
                                server.broadcastMsg(ClientHandler.this, nick + ": " + str);
                            }
                        }
                    } catch (IOException e) {
                        rootLogger.error("Maybe someone tried to connect, but failed. Error message: " + e.getMessage());
                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            rootLogger.error(e.getMessage());
                        }
                        try {
                            out.close();
                        } catch (IOException e) {
                            rootLogger.error(e.getMessage());
                        }
                        try {
                            socket.close();
                        } catch (IOException e) {
                            rootLogger.error(e.getMessage());
                        }
                        server.unsubscribe(ClientHandler.this);
                    }
                }
            }).start();
        } catch (IOException e) {
            rootLogger.error(e.getMessage());
        }
    }

    public static String md5Custom(String st) {
        MessageDigest messageDigest = null;
        byte[] digest = new byte[0];

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(st.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            rootLogger.error(e.getMessage());
        }

        BigInteger bigInt = new BigInteger(1, digest);
        String md5Hex = bigInt.toString(16);

        while (md5Hex.length() < 32) {
            md5Hex = "0" + md5Hex;
        }

        return md5Hex;
    }

    public void sendMsg(String str) {
        try {
            String textToEncrypt = str;
            String cipherText = encryptors1.encrypt(textToEncrypt);
            cipherText = salt1 + "" + cipherText;
            out.writeUTF(cipherText);
        } catch (IOException e) {
            rootLogger.error(e.getMessage());
        }
    }

    public String getNick() {
        return nick;
    }

    public boolean checkBlackList(String nick) {
//        String strGetNickCheck = MainDB.getIdByNicknameFromMain(getNick());
//        String strTokensCheck = MainDB.getIdByNicknameFromMain(nick);
//        String getNick = MainDB.getIdByNick1AndNick2FromBlocklist(strGetNickCheck, strTokensCheck);
        Main strGetNickCheck = hibernateUtil.getMainByNicknameFromMain(getNick());
        Main strTokensCheck = hibernateUtil.getMainByNicknameFromMain(nick);
        String getNick = hibernateUtil.getIdByNick1AndNick2FromBlocklist(strGetNickCheck, strTokensCheck);
        if (getNick != null) {
            return true;
        }
        return false;
    }
}
