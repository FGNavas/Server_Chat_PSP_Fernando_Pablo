package ServerApps;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Esta clase representa un servidor de chat que puede manejar múltiples clientes.
 */
public class Server implements Runnable {
    // El número máximo de clientes permitidos
    private static int max_client;
    // El socket del servidor
    private ServerSocket serverSocket;
    // El número actual de clientes conectados
    private static int actualclients;
    // Mapa que almacena los flujos de salida de los clientes
    public static Map<Integer, DataOutputStream> clients = new HashMap<>();
    // Mapa que almacena los nombres de usuario de los clientes
    public static Map<Integer, String> idName = new HashMap<>();
    // Indica si el servidor debe detenerse
    private static boolean serverStop = false;
    // Manejador de listas de clientes
    private ListHandler listHandler;

    /**
     * Constructor para inicializar un nuevo servidor.
     *
     * @param max_client El número máximo de clientes permitidos.
     */
    public Server(int max_client) {
        this.max_client = max_client;
        this.actualclients = 0;

    }

    public int getMax_client() {
        return max_client;
    }

    public static void setMax_client(int NewMax_client) {
        max_client = NewMax_client;
    }

    public static int getActualclients() {
        return actualclients;
    }

    public static void setActualclients(int actualclients) {
        Server.actualclients = actualclients;
    }

    public static Map<Integer, DataOutputStream> getClients() {
        return clients;
    }

    public static void setClients(Map<Integer, DataOutputStream> clients) {
        Server.clients = clients;
    }

    public static Map<Integer, String> getIdName() {
        return idName;
    }

    public static void setIdName(Map<Integer, String> idName) {
        Server.idName = idName;
    }

    public static boolean isServerStop() {
        return serverStop;
    }

    public static void setServerStop(boolean serverStop) {
        Server.serverStop = serverStop;
    }

    /**
     * Método principal que se ejecuta cuando se inicia el hilo del servidor.
     */
    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(ServerValues.DEFAULT_PORT);
            // Imprimir información sobre el servidor
            System.out.println("ServerApps.Server iniciado");
            System.out.println("\tDirección IP del servidor: " + InetAddress.getLocalHost().getHostAddress());
            System.out.println("\tPuerto de escucha del servidor: " + ServerValues.DEFAULT_PORT);
            System.out.println("\tClientes permitidos: " + max_client);
            // Esperar conexiones de clientes
            while (!serverStop) {
                // Manejar cliente

                Socket clientSocket = serverSocket.accept();
                // System.out.println("cliente aceptado: " + clientSocket.getInetAddress());
                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
                //   System.out.println("Input y output asignado");
                // Cliente rechazado
                if (actualclients >= max_client) {
                    dos.writeBoolean(false);
                    dos.flush();
                    dis.close();
                    dos.close();
                    clientSocket.close();
                }

                // Cliente aceptado
                dos.writeBoolean(true);

                // Recibir nombre de usuario
                String username = dis.readUTF();

                if (!username.equals(ServerValues.OUT_FINAL)) {
                    // Asignar id
                    int idAssigned = registerClient(username, dos);

                    dos.writeInt(idAssigned);
                    dos.flush();
                    // Iniciar hilo del cliente
                    Thread thread = new Thread(new ClientHandler(idAssigned, dis));
                    thread.start();
                }
            }
            disconectClients();
            System.out.println("Apagando Servidor");
            serverSocket.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * Desconecta a todos los clientes del servidor.
     */
    private void disconectClients() {
        if (idName != null && !idName.isEmpty()) {
            idName.forEach((k, v) -> {
                deleteClient(k);
            });
        }
    }

    /**
     * Registra a un nuevo cliente en el servidor.
     *
     * @param username El nombre de usuario del nuevo cliente.
     * @param dos      El flujo de salida del cliente.
     * @return El ID asignado al nuevo cliente.
     */

    private int registerClient(String username, DataOutputStream dos) {
        int idAssigned = assingID();
        idName.put(idAssigned, username);
        clients.put(idAssigned, dos);
        actualclients++;
        return idAssigned;
    }

    /**
     * Elimina a un cliente del servidor.
     *
     * @param id El ID del cliente a eliminar.
     * @return true si el cliente fue eliminado correctamente, false si el cliente no existe.
     */
    public static boolean deleteClient(int id) {
        if (clients.containsKey(id) && idName.containsKey(id)) {
            DataOutputStream dos = clients.get(id);
            try {
                dos.writeUTF(ServerValues.OUT_FINAL);
                dos.close();
            } catch (IOException e) {
            }
            clients.remove(id);

            idName.remove(id);
            actualclients--;
            return true;
        } else {
            return false;
        }
    }

    private int assingID() {
        Random rand = new Random();
        int id;
        do {
            id = rand.nextInt(max_client);
        } while (idName.containsKey(id));
        return id;
    }

    /**
     * Clase interna que maneja la comunicación con un cliente específico.
     */
    private static class ClientHandler implements Runnable {

        private int clientId;
        private DataInputStream clientDatainput;

        public ClientHandler(int clientId, DataInputStream dis) {
            setClientId(clientId);
            setClientDatainput(dis);
        }

        public DataInputStream getClientDatainput() {
            return clientDatainput;
        }

        public void setClientDatainput(DataInputStream clientDatainput) {
            this.clientDatainput = clientDatainput;
        }

        public int getClientId() {
            return clientId;
        }

        public void setClientId(int clientId) {
            this.clientId = clientId;
        }

        /**
         * Método principal que se ejecuta cuando se inicia el hilo del cliente.
         */

        @Override
        public void run() {
            boolean conectionOK = true;
            //  System.out.println("Inicia run");
            try {
                while (conectionOK && !serverStop) {
                    String message = clientDatainput.readUTF();
                    //      System.out.println(message);
                    conectionOK = manageMessage(message);
                }

            } catch (IOException e) {

            } finally {
                System.out.println("Cliente: " + getClientId() + " desconectado");
                deleteClient(getClientId());

            }

        }

        /**
         * Gestiona el mensaje recibido del cliente y toma acciones según su contenido.
         *
         * @param message El mensaje recibido del cliente.
         * @return true si la conexión con el cliente debe continuar, false si debe desconectarse.
         */
        private boolean manageMessage(String message) {
            String[] messageParts = message.split(ServerValues.SEPARATOR, 3);
            String whatDo = messageParts[0];
            String forWho = messageParts[1];
            String theMessage = messageParts[2];

            boolean continueConec = true;
            switch (whatDo) {
                case (ServerValues.BROADCAST):
                    broadcastMessage(getClientId(), theMessage);
                    break;
                case (ServerValues.PRIVATE):
                    privateMessage(getClientId(), forWho, theMessage);
                    break;
                case (ServerValues.CLIENT_DISCONNECT):
                    continueConec = false;
                    break;
            }

            return continueConec;
        }

        /**
         * Envía un mensaje privado a un cliente específico.
         *
         * @param clientId   El ID del cliente que envía el mensaje.
         * @param forWho     El ID del cliente receptor del mensaje.
         * @param theMessage El mensaje a enviar.
         */
        private void privateMessage(int clientId, String forWho, String theMessage) {
            int idDest = Integer.parseInt(forWho);
            DataOutputStream dos = clients.get(idDest);
            try {
                dos.writeUTF(prepareMessage(ServerValues.PRIVATE, String.valueOf(clientId), theMessage));
                dos.flush();
            } catch (IOException e) {
                System.out.println("Error al enviar mensaje");
                throw new RuntimeException(e);
            }


        }

        /**
         * Envía un mensaje de difusión a todos los clientes.
         *
         * @param senderClientId El ID del cliente que envía el mensaje de difusión.
         * @param theMessage     El mensaje a enviar.
         */
        private void broadcastMessage(int senderClientId, String theMessage) {
            String userName = idName.get(senderClientId);
            clients.forEach((k, v) -> {
                DataOutputStream dosS = v;
                try {
                    String mens = prepareMessage(ServerValues.BROADCAST, userName, theMessage);

                    dosS.writeUTF(mens);
                    dosS.flush();
                } catch (IOException e) {
                    System.out.println("Error: envio en broadcast");
                }

            });
        }

        /**
         * Prepara un mensaje para ser enviado, incluyendo su tipo y destinatario.
         *
         * @param tipeMessage El tipo de mensaje (privado o de difusión).
         * @param forWho      El destinatario del mensaje.
         * @param theMessage  El mensaje a enviar.
         * @return El mensaje preparado para su envío.
         */
        private String prepareMessage(String tipeMessage, String forWho, String theMessage) {
            StringBuilder sb = new StringBuilder();
            sb.append(tipeMessage);
            sb.append(ServerValues.SEPARATOR);
            sb.append(forWho);
            sb.append(ServerValues.SEPARATOR);
            sb.append(theMessage);
            return sb.toString();
        }
    }
}



