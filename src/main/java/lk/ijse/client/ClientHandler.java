package lk.ijse.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ClientHandler {
    private Socket socket;
    private List<ClientHandler> client;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String message = "";

    public ClientHandler(Socket socket, List<ClientHandler> client) {
        try {
            this.socket = socket;
            this.client = client;
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        new Thread(()->{
            try {
                while (socket.isConnected()) {
                    message = dataInputStream.readUTF();
                    for (ClientHandler clientHandler : client) {
                        if (clientHandler.socket.getPort() != socket.getPort()) {
                            clientHandler.dataOutputStream.writeUTF(message);
                            clientHandler.dataOutputStream.flush();
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }
}
