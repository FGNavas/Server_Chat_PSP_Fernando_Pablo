import PersonalizedExceptions.InvalidInetAddress;
import PersonalizedExceptions.InvalidPort;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Esta clase representa un hilo que escucha conexiones entrantes en un puerto UDP específico. No esta finalizada
 * y no esta implementada aun.
 */
public class ServerListener extends Thread {
    private int listenerPort;
    private InetAddress inetAddress;
    private static final String ACCESS_KEY = "HolaServidor";
    private static boolean listening = false;
    private static final String SEPARATOR = "@";

    /**
     * Constructor de la clase ServerListener.
     *
     * @param listenerPort Puerto en el que se va a escuchar.
     * @param inetAddress  Dirección IP en la que se va a escuchar.
     * @throws InvalidPort        Si el puerto no está en el rango válido.
     * @throws InvalidInetAddress Si la dirección IP es nula.
     */
    public ServerListener(int listenerPort, InetAddress inetAddress) throws InvalidPort, InvalidInetAddress {
        setListenerPort(listenerPort);
        setInetAddress(inetAddress);
    }

    /**
     * Obtiene la dirección IP en la que se está escuchando.
     *
     * @return La dirección IP en la que se está escuchando.
     */
    public InetAddress getInetAddress() {
        return inetAddress;
    }

    /**
     * Establece la dirección IP en la que se va a escuchar.
     *
     * @param inetAddress La dirección IP en la que se va a escuchar.
     * @throws InvalidInetAddress Si la dirección IP es nula.
     */
    public void setInetAddress(InetAddress inetAddress) throws InvalidInetAddress {
        if (inetAddress == null) {
            throw new InvalidInetAddress();
        }
        this.inetAddress = inetAddress;
    }

    /**
     * Obtiene el puerto en el que se está escuchando.
     *
     * @return El puerto en el que se está escuchando.
     */
    public int getListenerPort() {
        return listenerPort;
    }

    /**
     * Establece el puerto en el que se va a escuchar.
     *
     * @param listenerPort El puerto en el que se va a escuchar.
     * @throws InvalidPort Si el puerto no está en el rango válido.
     */
    public void setListenerPort(int listenerPort) throws InvalidPort {
        if (listenerPort < 49152 || listenerPort > 65535) {
            throw new InvalidPort();
        }

        this.listenerPort = listenerPort;
    }

    /**
     * Método que inicia la escucha en el puerto UDP especificado.
     */
    public void starListening() {
        listening = true;
        try {
            DatagramSocket ds = new DatagramSocket(listenerPort);
            System.out.println("Servidor comienza la escucha");
            while (listening) {
                byte[] reciveData = new byte[1024];
                DatagramPacket recivedPacket = new DatagramPacket(reciveData, reciveData.length);
                ds.receive(recivedPacket);
                InetAddress clientIP = recivedPacket.getAddress();
                int clientPort = recivedPacket.getPort();

                String clientMessage = new String(recivedPacket.getData(), 0, recivedPacket.getLength());
                String[] messageParts = clientMessage.split(SEPARATOR);
                if (messageParts.length == 2) {
                    String password = messageParts[0];
                    String destinationIP = messageParts[1];
                    if (validPassword(password)) {
                        String serverMessage = "";
                    }


                }


            }

        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * Verifica si la contraseña proporcionada es válida.
     *
     * @param password La contraseña proporcionada.
     * @return true si la contraseña es válida, de lo contrario false.
     */
    private boolean validPassword(String password) {
        return password.equals(ACCESS_KEY);
    }

    /**
     * Detiene la escucha en el puerto UDP.
     */
    public void stopListening() {
        listening = false;
    }

}
