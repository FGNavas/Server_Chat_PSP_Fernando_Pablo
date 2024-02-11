# Servidor de Chat Fernando y Pablo

Este proyecto implementa un servidor de chat que permite la comunicación entre múltiples clientes a través de una red.

## Contenido

- [Descripción](#descripción)
- [Requisitos](#requisitos)
- [Instalación](#instalación)
- [Uso](#uso)
- [Instrucciones de uso del servidor de chat](#Instrucciones de uso del servidor de chat)
- [Créditos](#créditos)


## Descripción

El servidor de chat proporciona una plataforma para que múltiples clientes se conecten y se comuniquen entre sí. Utiliza sockets para la comunicación en red y permite el intercambio de mensajes en tiempo real.
Aun le falta por intgrar mas funcionalidades.

## Requisitos

- Java Development Kit (JDK) 8 o superior
- Maven (opcional, para la gestión de dependencias y la compilación)

## Instalación

1. Clona el repositorio a tu máquina local.
2. Asegúrate de tener Java y Maven instalados.
3. Compila el proyecto utilizando Maven: `mvn compile`
4. Ejecuta el servidor de chat: `mvn exec:java -Dexec.mainClass="nombre.del.paquete.Main"`

## Uso

1. Ejecuta el servidor de chat utilizando las instrucciones de instalación.
2. Ejecuta el cliente de chat en múltiples terminales o máquinas.
3. Conecta los clientes al servidor utilizando la dirección IP y el puerto del servidor.
4. Comienza a chatear

## Instrucciones de uso del servidor de chat

### Paso 1: Iniciar el servidor

Después de abrir el proyecto en el entorno de desarrollo, encuentra la clase `Main` desde donde se ejecutará el proyecto. La clase `Main` solicitará al usuario un número entero mayor que 0, que será el máximo de clientes que permitirá el servidor. Luego, iniciará los hilos necesarios para el servidor, que incluyen:

- `Server`: Encargado de gestionar las conexiones de los clientes y sus hilos.
- `ListHandler`: Encargado de comunicar al cliente el listado de usuarios conectados. Esta función aún no está implementada, por lo que su hilo no se ejecutará.
- `ManageServer`: Clase encargada de gestionar las funcionalidades del servidor.
- `ServerValues`: Clase que engloba todas las constantes del servidor.
- `ServerListener`: Clase aún no finalizada e implementada. Su función será escuchar peticiones de clientes en una red local para indicarles su dirección IP y permitirles la conexión.

### Paso 2: Conectar clientes

El servidor permanecerá a la escucha de nuevos clientes hasta que la clase `ManageServer` indique que se cierre el servidor. Cuando un cliente se conecta, debe esperar a leer un booleano que le enviará el servidor. Si se alcanza el máximo de clientes, no permitirá la conexión. Una vez aceptada la conexión, el servidor esperará a que el usuario envíe su nombre de usuario elegido. Luego, el servidor generará un ID para este cliente y lo enviará al cliente para que quede identificado en la sesión. Además, se realizará la asociación del nombre de usuario y su ID a través de dos mapas: `idName` y `clients`. Se iniciará el hilo del cliente mediante la clase privada `ClientHandler`. El flujo de mensajes permanecerá hasta que el cliente se desconecte o sea expulsado desde el servidor.

## Créditos

Este proyecto fue desarrollado como practica para la asignatura de Programacion de Servicios y Procesos por los integrantes
    -Fernando https://github.com/FGNavas
    -Pablo https://github.com/Samupabs



