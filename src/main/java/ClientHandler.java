import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

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
    String salt = "6eda6a88846ad4cb";
    TextEncryptor encryptors = Encryptors.text(password, salt);
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
                            str = encryptors.decrypt(str);
                            if (str.startsWith("/auth")) {
                                String[] tokes = str.split(" ");
                                String pass = md5Custom(tokes[2]);
                                String newNick = MainDB.getNickByLoginAndPass(tokes[1], pass);
                                if (newNick != null) {
                                    if (!server.isNickBusy(newNick)) {
                                        sendMsg("/authok " + newNick);
                                        nick = newNick;
                                        server.subscribe(ClientHandler.this);
                                        rootLogger.info("Клиент " + nick + " подключился!");
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
                            str = encryptors.decrypt(str);
                            if (str.startsWith("/")) {
                                if (str.equals("/end")) {
                                    String textToEncrypt = "/serverclosed";
                                    String cipherText = encryptors.encrypt(textToEncrypt);
                                    out.writeUTF(cipherText);
                                    rootLogger.info("Клиент " + nick + " отключился!");
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
                                        String strGetNick = MainDB.getIdByNicknameFromMain(getNick());
                                        String strTokens1 = MainDB.getIdByNicknameFromMain(tokens[1]);
                                        if (strTokens1 != null) {
                                            String insertTable = MainDB.insertTable(strGetNick, strTokens1);
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
                        rootLogger.error(e.getStackTrace());
                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            rootLogger.error(e.getStackTrace());
                        }
                        try {
                            out.close();
                        } catch (IOException e) {
                            rootLogger.error(e.getStackTrace());
                        }
                        try {
                            socket.close();
                        } catch (IOException e) {
                            rootLogger.error(e.getStackTrace());
                        }
                        server.unsubscribe(ClientHandler.this);
                    }
                }
            }).start();
        } catch (IOException e) {
            rootLogger.error(e.getStackTrace());
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
            rootLogger.error(e.getStackTrace());
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
            String cipherText = encryptors.encrypt(textToEncrypt);
            out.writeUTF(cipherText);
        } catch (IOException e) {
            rootLogger.error(e.getStackTrace());
        }
    }

/*    public static boolean contains(String str, String symbol) {
        return str.contains(symbol);
    }*/

    public String getNick() {
        return nick;
    }

    public boolean checkBlackList(String nick) {
        String strGetNickCheck = MainDB.getIdByNicknameFromMain(getNick());
        String strTokensCheck = MainDB.getIdByNicknameFromMain(nick);
        String getNick = MainDB.getIdByNick1AndNick2FromBlocklist(strGetNickCheck, strTokensCheck);
        if (getNick != null) {
            return true;
        }
        return false;
    }
}
