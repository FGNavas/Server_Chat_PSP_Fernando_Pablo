package ServerApps;

import PersonalizedExceptions.SendDataError;
import ServerApps.Server;
import ServerApps.ServerValues;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
/**
 * Clase que gestiona la lista de clientes conectados al servidor y envía actualizaciones a los clientes.
 */
public class ListHandler implements Runnable {
    private boolean serverDown =false;

    private int actualListSize;
    /**
     * Constructor de la clase ListHandler.
     */
    public ListHandler() {
        actualListSize = Server.clients.size();
    }
    /**
     * Método que indica si el servidor está detenido.
     * @return true si el servidor está detenido, false de lo contrario.
     */

    public boolean isServerDown() {
        return serverDown;
    }
    /**
     * Método para establecer el estado del servidor.
     * @param serverDown true para detener el servidor, false para mantenerlo en ejecución.
     */
    public void setServerDown(boolean serverDown) {
        this.serverDown = serverDown;
    }

    /**
     * Método que se ejecuta en un hilo para manejar la lista de clientes y enviar actualizaciones.
     */
    @Override
    public void run() {

        while (!serverDown) {
            if(Server.clients != null && !Server.clients.isEmpty()) {
                int checkSize = Server.clients.size();
                if (actualListSize != checkSize) {
                    sendActualizedList(checkSize);
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.err.println("Error: interrupción durante el tiempo de espera");
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Cerrando Gestor de lista");
       // sendDisconnect();
    }
    /**
     * Método para enviar la lista actualizada a todos los clientes conectados.
     * @param newListSize El nuevo tamaño de la lista de clientes.
     */
    private void sendActualizedList(int newListSize) {
        Server.clients.forEach((k,v)->{
            DataOutputStream dos;
            dos =v;
            try {
                String list = prepareList();
                dos.writeUTF(list);
                dos.close();
            } catch (IOException e) {
                System.err.println("Error: al enviar lista");
            }
        });
        // Despues de mandar la lista actualizamos el tamaño guardado para la proxima comprobacion
        actualListSize = newListSize;
    }
    /**
     * Método para enviar un mensaje de desconexión a todos los clientes conectados. En este estado se encarga
     * el servidor
     */
    private void sendDisconnect() {
        Server.clients.forEach((k, v) -> {
            DataOutputStream dos;
            dos = v;
            try {
                dos.writeUTF(ServerValues.END_LIST_LISTENER);
                dos.close();
            } catch (IOException e) {
                System.err.println("Error: al enviar desconexion");
            }
        });

    }
    /**
     * Método para preparar la lista de clientes actualizada.
     * @return La lista de clientes actualizada en formato de cadena.
     */

    private String prepareList() {
        StringBuilder sb = new StringBuilder();
        sb.append(ServerValues.ACTUALIZED_LIST);
        Server.idName.forEach((k, v) -> {
                    sb.append(k);
                    sb.append(ServerValues.SEPARATOR);
                    sb.append(v);
                    sb.append(ServerValues.SEPARATOR);
                }
        );

        return sb.toString();
    }


}



