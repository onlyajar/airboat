package onlyajar.airboat.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class BioServer {
    private final int port;
    ServerSocket aServerSocket;
    ExecutorService executorService = Executors.newCachedThreadPool();

    private final KitSocketHandler socketHandler;

    public BioServer(int port, KitSocketHandler socketHandler) {
        this.port = port;
        this.socketHandler = socketHandler;
    }
    public void startup() throws IOException {
        startup(null);
    }

    public void startup(SSLServerSocketFactory sslServerSocketFactory) throws IOException {
        try(ServerSocket serverSocket = sslServerSocketFactory == null?
                new ServerSocket(port): sslServerSocketFactory.createServerSocket(port)){
            aServerSocket = serverSocket;
            if(aServerSocket instanceof SSLServerSocket){
                ((SSLServerSocket)serverSocket).setNeedClientAuth(false);
            }
            while (true){
                final Socket socket = serverSocket.accept();
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        socketHandler.accept(new KitSocket(socket));
                    }
                });
            }
        }catch (IOException e){
            e.fillInStackTrace();
            throw e;
        }
    }

    public void shutdown(){
        if(aServerSocket != null){
            try {
                aServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
