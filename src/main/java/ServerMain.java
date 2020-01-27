import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ServerMain {
    private Vector<ClientHandler> clients;

    public ServerMain() {
        clients = new Vector<>();
        ServerSocket server = null;
        Socket socket = null;

        try {
            AuthService.connect();
//            String str = AuthService.getNickByLoginAndPass("login1", "pass1");
//            System.out.println(str);
            server = new ServerSocket(8189);
            System.out.println("Сервер запущен!");

            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
                // clients.add(new ClientHandler(this, socket));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            AuthService.disconnect();
        }
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
                    o.sendMsg(from.getNick() + ":" + " from " + nickTo + ": " + msg);
                    from.sendMsg(from.getNick() + ":" + " to " + nickTo + ": " + msg);
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
