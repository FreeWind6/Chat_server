import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private ServerMain server;
    private String nick;
//    private List<String> blackList;

    public ClientHandler(ServerMain server, Socket socket) {
        try {
            this.socket = socket;
            this.server = server;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
//            this.blackList = new ArrayList<>();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        while (true) {
                            String str = in.readUTF();
                            if (str.startsWith("/auth")) {
                                String[] tokes = str.split(" ");
                                String newNick = AuthService.getNickByLoginAndPass(tokes[1], tokes[2]);
                                if (newNick != null) {
                                    if (!server.isNickBusy(newNick)) {
                                        sendMsg("/authok " + newNick);
                                        nick = newNick;
                                        server.subscribe(ClientHandler.this);
                                        break;
                                    } else {
                                        sendMsg("Учетная запись уже используется");
                                    }
                                } else {
                                    sendMsg("Неверный логин/пароль");
                                }
                            }
                        }

                        while (true) {
                            String str = in.readUTF();
                            if (str.startsWith("/")) {
                                if (str.equals("/end")) {
                                    out.writeUTF("/serverclosed");
                                    break;
                                }
                                if (str.startsWith("/w ")) { // /w nick3 lsdfhldf sdkfjhsdf wkerhwr
                                    String[] tokens = str.split(" ", 3);
                                    //  String m = str.substring(tokens[1].length() + 4);
                                    server.sendPersonalMsg(ClientHandler.this, tokens[1], tokens[2]);
                                }
                                if (str.startsWith("/blacklist ")) { // /w nick3 lsdfhldf sdkfjhsdf wkerhwr
                                    String[] tokens = str.split(" ");
                                    if (tokens[1].equals(getNick())) {
                                        sendMsg("Вы не можете заблокировать самого себя!");
                                    } else {
                                        String strGetNick = AuthService.getIdByNicknameFromMain(getNick());
                                        String strTokens1 = AuthService.getIdByNicknameFromMain(tokens[1]);
                                        if (strTokens1 != null) {
                                            String insertTable = AuthService.insertTable(strGetNick, strTokens1);
                                            sendMsg("Вы добавили пользователя  " + tokens[1] + " в черный список!");
                                        } else {
                                            sendMsg("Пользователя " + tokens[1] + " нет в базе!");
                                        }


                                    }
                                    //blackList.add(tokens[1]);
                                }
                            } else {
                                server.broadcastMsg(ClientHandler.this, nick + ": " + str);
                            }
                            System.out.println("Client: " + str);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        server.unsubscribe(ClientHandler.this);
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String str) {
        try {
            out.writeUTF(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/*    public List<String> getBlackList() {
        return blackList;
    }*/

    public String getNick() {
        return nick;
    }

    public boolean checkBlackList(String nick) {
        String strGetNickCheck = AuthService.getIdByNicknameFromMain(getNick());
        String strTokensCheck = AuthService.getIdByNicknameFromMain(nick);
        String getNick = AuthService.getIdByNick1AndNick2FromBlocklist(strGetNickCheck, strTokensCheck);
        if (getNick != null) {
            return true;
        }
        return false;
    }
}
