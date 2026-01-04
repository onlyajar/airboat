package onlyajar.airboat.net;

import androidx.arch.core.executor.ArchTaskExecutor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.net.ssl.SSLSocketFactory;

import onlyajar.airboat.executor.AppExecutor;

public class ForwarderSocketHandler implements AcceptSocketHandler {
    private final String targetIP;
    private final int targetPort;
    private final DataProtocol protocol;
    private SSLSocketFactory sslSocketFactory = null;

    public ForwarderSocketHandler(String targetIP, int targetPort, DataProtocol protocol) {
        this.targetIP = targetIP;
        this.targetPort = targetPort;
        this.protocol = protocol;
    }

    public ForwarderSocketHandler(String targetIP, int targetPort, DataProtocol protocol, SSLSocketFactory sslSocketFactory) {
        this.targetIP = targetIP;
        this.targetPort = targetPort;
        this.protocol = protocol;
        this.sslSocketFactory = sslSocketFactory;
    }

    @Override
    public void accept(AcceptSocket kitSocket) {
        AppExecutor.getInstance().executeOnIO(() -> {
            try {
                ForwarderSession forwarderSession = new ForwarderSession(protocol);
                TcpClient tcpClient = new TcpClient
                        .Builder(targetIP, targetPort)
                        .setSSLSocketFactory(sslSocketFactory)
                        .setProtocol(protocol)
                        .build();
                tcpClient.connect();
                InputStream clientIn = kitSocket.getInputStream();
                OutputStream clientOut = kitSocket.getOutputStream();
                InputStream targetIn = tcpClient.getInputStream();
                OutputStream targetOut = tcpClient.getOutputStream();
                forwarderSession.setAlive(true);
                Thread forwardThread = new Thread(new StreamForwarder(clientIn, targetOut, forwarderSession, "Client->Target"));
                Thread reverseThread = new Thread(new StreamForwarder(targetIn, clientOut, forwarderSession, "Target->Client"));
                forwardThread.start();
                reverseThread.start();
                forwardThread.join();
                reverseThread.join();
                tcpClient.close();
                kitSocket.close();
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        });
    }
}
