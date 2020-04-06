import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.Vector;

public class ServerMain {
    private Vector<ClientHandler> clients;
    static final Logger rootLogger = LogManager.getRootLogger();

    public ServerMain() {
        clients = new Vector<>();
        ServerSocket server = null;
        Socket socket = null;

        try {
            MainDB.connect();
            server = new ServerSocket(8189);
            rootLogger.info("Server is running! Address: " + getLocalIpAddress() + ":" + server.getLocalPort());

            while (true) {
                socket = server.accept();
                new ClientHandler(this, socket);
            }

        } catch (IOException e) {
            rootLogger.error(e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                rootLogger.error(e.getMessage());
            }
            try {
                server.close();
            } catch (IOException e) {
                rootLogger.error(e.getMessage());
            }
            MainDB.disconnect();
        }
    }

    public static String getLocalIpAddress() {
        String ip = null;
        try {
            Enumeration<NetworkInterface> b = NetworkInterface.getNetworkInterfaces();
            while (b.hasMoreElements()) {
                for (InterfaceAddress f : b.nextElement().getInterfaceAddresses())
                    if (f.getAddress().isSiteLocalAddress())
                        return ip = String.valueOf(f.getAddress()).replace("/", "");
            }
        } catch (SocketException e) {
            rootLogger.error(e.getMessage());
        }
        return null;
    }

    public void subscribe(ClientHandler client) {
        clients.add(client);
        broadcastClientList();
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client);
        broadcastClientList();
    }

    public void broadcastMsg(ClientHandler from, String msg) {
        for (ClientHandler o : clients) {
            if (!o.checkBlackList(from.getNick())) {
                o.sendMsg(msg);
            }
        }
    }

    public void sendPersonalMsg(ClientHandler from, String nickTo, String msg) {
        for (ClientHandler o : clients) {
            if (o.getNick().equals(nickTo)) {
                if (!o.checkBlackList(from.getNick())) {
                    //получатель
                    o.sendMsg(from.getNick() + ": " + msg + " (private to you)");
                    //отправитель
                    from.sendMsg(from.getNick() + ": " + msg + " (private to " + nickTo + ")");
                    return;
                } else {
                    from.sendMsg("Пользователь " + nickTo + " занес вас в черный список");
                    return;
                }
            }
        }
        from.sendMsg("Клиент с ником " + nickTo + " не найден в чате");
    }


    public boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getNick().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public void broadcastClientList() {
        StringBuilder sb = new StringBuilder();
        sb.append("/clientlist ");
        for (ClientHandler o : clients) {
            sb.append(o.getNick() + " ");
        }
        String out = sb.toString();
        for (ClientHandler o : clients) {
            o.sendMsg(out);
        }
    }
}
