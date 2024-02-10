package ServerApps;

/**
 * Esta clase define constantes utilizadas en la aplicación del servidor.
 */
public class ServerValues {

    //---------------------------------VALORES DEL SERVIDOR ------------------------------------------------------------

    // Valor por defecto del puerto del puerto del servidor
    public static final int DEFAULT_PORT = 60000;

    /* Mensaje para indicar al cliente que se ha llegado al maximo de capacidad del servidor
    al no estar implementado en el cliente no se hace uso de ella */
    public static final String MAX_CLIENT_REACHED = "MAX_CLIENT_REACHED";

    //Cabecera para indicar al cliente que se va a cerrar la conexion con el servidor
    public static final String OUT_FINAL = "SERVERDISC";
    //Valor para particionar el stream de entrada y salida
    public static final String SEPARATOR = "@";
    //Cabecera para indicar que es un mensaje privado
    public static final String PRIVATE = "PRIV";
    //Cabecera para indicar que es un mensaje para todos los clientes
    public static final String BROADCAST = "ALL";
    //Cabecera para indicar al cliente que su conexion va a ser cerrada
    public static final String CLIENT_DISCONNECT = "DISCO";
    //Cabecera para indicar que se va a cerrar el hilo ListHandler
    public static final String END_LIST_LISTENER = "ENDLISTENER";
    //Cabecera para indicar que se esta enviando una lista actualizada de clientes: id, nombre usuario
    public static final String ACTUALIZED_LIST = "ACTLIST";


    //---------------------------------VALORES DEL GESTOR DEL SERVIDOR -------------------------------------------------
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String USERLIST = "userlist";
    public static final String SHUTDOWN = "shutdown";
    public static final String KICK = "kick";
    public static final String STATUS = "status";
    public static final String HELP = "help";
    public static final String CHANGE_CAP = "changeCap";
    public static String[] commands = {USERLIST, SHUTDOWN, KICK, STATUS, HELP, CHANGE_CAP};

    public static String[] commandDesc = {"Listado de usuarios conectados", "Parar y terminar servidor", "Expulsar a un usuario",
            "Estado del servidor", "Listar comandos", "Cambiar la capacidad maxima de clientes"};


}
