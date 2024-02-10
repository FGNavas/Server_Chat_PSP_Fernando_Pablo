package ServerApps;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

/**
 * Clase que gestiona los comandos del servidor y su configuración.
 */
public class ManageServer implements Runnable {

    private Server server;
    private ListHandler listHandler;
    private Scanner sc;

    /**
     * Constructor de la clase ManageServer.
     *
     * @param server      Instancia del servidor.
     * @param sc          Scanner para entrada de comandos.
     * @param listHandler Instancia del manejador de lista de usuarios.
     */
    public ManageServer(Server server, Scanner sc, ListHandler listHandler) {

        this.server = server;
        Thread serverthread = new Thread(server);
        serverthread.start();
        this.listHandler = listHandler;
        Thread listThread = new Thread(listHandler);
        listThread.start();
        this.sc = sc;
    }

    /**
     * Método que ejecuta el hilo de gestión del servidor.
     */
    @Override
    public void run() {
        while (!Server.isServerStop()) {
            String command = sc.next();
            manageCommand(command);
        }
        System.out.println("Cerrando Gestor del servidor.");
        while (!Server.isServerStop()) {
            try {
                Thread.sleep(1000); // Esperar 1 segundo antes de verificar de nuevo
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Método que gestiona los comandos recibidos por el servidor.
     *
     * @param command Comando recibido.
     */
    private void manageCommand(String command) {
        switch (command) {
            case ServerValues.SHUTDOWN:
                closeServer();
                break;
            case ServerValues.USERLIST:
                showUserlist();
                break;
            case ServerValues.KICK:
                kickUser();
                break;
            case ServerValues.STATUS:
                serverStatus();
                break;
            case ServerValues.HELP:
                helpCommand();
                break;
            case ServerValues.CHANGE_CAP:
                changeServerCap();
                break;
            default:
                System.out.println("Comando no valido. help para mostrar ayuda");
                break;
        }

    }

    /**
     * Método para cambiar la capacidad máxima de usuarios del servidor.
     */
    private void changeServerCap() {

        boolean validInput = false;
        System.out.println("Capacidad de usuarios actuales: " + server.getMax_client());
        System.out.println("\tClientes actualmente: " + Server.getActualclients());
        while (!validInput) {
            try {
                System.out.println("Introduce nueva capacidad, no puede ser menor a los clientes conectados");
                int maxClients = sc.nextInt();
                if (maxClients > Server.getActualclients()) {
                    Server.setMax_client(maxClients);
                    System.out.println("Capacidad maxima modificada");
                    validInput = true;
                } else {
                    System.out.println("La nueva capacidad es menor que los clientes conectados");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Ingrese un número entero válido.");
                sc.nextLine();
            }

        }
    }

    /**
     * Método para mostrar la lista de usuarios conectados.
     */
    private void helpCommand() {
        System.out.println("Comandos del servidor:");


        for (int i = 0; i < ServerValues.commands.length; i++) {
            System.out.println("\t" + ServerValues.commands[i] + "\t" + ServerValues.commandDesc[i]);
        }
    }

    /**
     * Método para mostrar el estado actual del servidor.
     */
    private void serverStatus() {
        System.out.println("Estado del servidor:");
        try {
            System.out.println("\tDireccion IP del servidor: " + InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
        }
        System.out.println("\tPuerto por defecto: " + ServerValues.DEFAULT_PORT);
        System.out.println("\tMaxima capacidad de clientes: " + server.getMax_client());
        System.out.println("\tNumero de usuarios: " + server.getActualclients() + "/" + server.getMax_client());

    }

    /**
     * Método para expulsar a un usuario del servidor.
     */
    private void kickUser() {
        System.out.println("Introduce el id del usuario:");
        String idUsu = sc.next();
        try {
            int idNum = Integer.parseInt(idUsu);
            Map<Integer, String> userList = server.getIdName();
            if (userList.containsKey(idNum)) {
                if (server.deleteClient(idNum)) {
                } else {
                    System.out.println("Error al borrar al usuario.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("El id del usuario debe ser un numero entero");
        }


    }

    /**
     * Método para mostrar la lista de usuarios conectados.
     */
    private void showUserlist() {
        Map<Integer, String> userList = server.getIdName();
        System.out.println("Listado de usuarios conectados:");
        System.out.println("Id \t Nombre");

        userList.forEach((k, v) -> System.out.println(k + "\t" + v));

    }

    /**
     * Método para cerrar el servidor.
     */
    private void closeServer() {
        boolean righ = false;
        String ask = "null";
        System.out.println("Esta seguro de querer cerrar el servidor Y/N:");
        while (!righ) {
            ask = sc.next().toLowerCase();
            if (ask.equals("y") || ask.equals("n")) {
                righ = true;
            } else {
                System.out.println("Comando no valido introduce Y/N");
            }
        }
        if (ask.equals("y")) {
            Socket clienteCierre = null;
            try {
                Server.setServerStop(true);
                listHandler.setServerDown(true);

                clienteCierre = new Socket("localhost", ServerValues.DEFAULT_PORT);
                DataInputStream dis = new DataInputStream(clienteCierre.getInputStream());
                DataOutputStream dos = new DataOutputStream(clienteCierre.getOutputStream());
                boolean boo = dis.readBoolean();
                if (boo) {
                    dos.writeUTF(ServerValues.OUT_FINAL);
                }
                clienteCierre.close();
            } catch (IOException e) {
                System.out.println("Cerrando server");
            }

        }

    }
}
