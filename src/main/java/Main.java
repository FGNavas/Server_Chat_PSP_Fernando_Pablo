import ServerApps.ListHandler;
import ServerApps.ManageServer;
import ServerApps.Server;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Esta clase contiene el método principal que inicia el servidor de chat.
 */
public class Main {
    /**
     * Método principal que inicia el servidor de chat.
     *
     * @param args Argumentos de la línea de comandos (no se utilizan en este programa).
     */
    public static void main(String[] args) {
        System.out.println("Iniciando servidor de chat...");
        int maxClients = 0;
        boolean validInput = false;
        Scanner sc = new Scanner(System.in);
        // Se solicita al usuario que ingrese el número permitido de clientes
        while (!validInput) {
            try {
                System.out.println("Ingrese el número permitido de clientes: ");
                maxClients = sc.nextInt();
                validInput = true;
            } catch (InputMismatchException e) {
                // Se maneja el caso en que se ingrese un valor no válido
                System.out.println("Error: Ingrese un número entero válido.");
                sc.nextLine();
            }
        }
        // Se crea una instancia del servidor con el número máximo de clientes especificado
        Server server = new Server(maxClients);
        ListHandler listHandler = new ListHandler();
        ManageServer manageServer = new ManageServer(server, sc, listHandler);
        Thread threadManager = new Thread(manageServer);
        threadManager.start();


    }


}
