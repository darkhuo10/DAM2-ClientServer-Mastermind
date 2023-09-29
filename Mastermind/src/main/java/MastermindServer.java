    import org.json.JSONObject;

    import java.io.*;
    import java.net.ServerSocket;
    import java.net.Socket;
    import java.time.LocalDate;
    import java.time.LocalTime;
    import java.util.*;

    public class MastermindServer {
        public static final int PORT = 3320;
        public static final String[] COLORES = new String[]{"rojo", "verde", "azul", "amarillo", "rosa", "naranja"};

        public static void main(String[] args) throws IOException {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciado en el puerto " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Cliente conectado desde " + socket.getInetAddress());

                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

                Random random = new Random();
                int[] code = {random.nextInt(6),
                        random.nextInt(6),
                        random.nextInt(6),
                        random.nextInt(6),
                        random.nextInt(6),
                        random.nextInt(6)};
                List<String> codigo = new ArrayList<>();
                for (int i = 0; i < code.length; i++) {
                    codigo.add(i, COLORES[code[i]]);
                }
                System.out.println(Arrays.toString(codigo.toArray()));

                int intentos = 0;
                while (true) {
                    String request = input.readLine();
                    JSONObject clientJson = new JSONObject(request);
                    String nombre = clientJson.getString("nombre");
                    String[] guess = {clientJson.getString("a"), clientJson.getString("b"), clientJson.getString("c"), clientJson.getString("d"), clientJson.getString("e"), clientJson.getString("f")};
                    List<String> guessList =  Arrays.stream(guess).toList();

                    JSONObject serverJson = new JSONObject();
                    String[] answer = new String[]{"No", "No", "No", "No", "No", "No"};
                    int aciertos = 0;
                    intentos++;

                    for (int i = 0; i < 6; i++) {
                        if (codigo.get(i).equalsIgnoreCase(guessList.get(i))){
                            answer[i] = "Si";
                            aciertos++;
                        }
                        else if (codigo.contains(guessList.get(i))) {
                            answer[i] = "--";
                        }
                    }

                    serverJson.put("aciertos", aciertos);
                    serverJson.put("respuestas", Arrays.toString(answer));
                    serverJson.put("intentos", intentos);

                    output.println(serverJson);
                    if (aciertos == 6) {
                        System.out.println("El jugador ha adivinado el código.");
                        if (intentos < 6) { // Se guardan los resultados de la gente que lo consigue en 5 intentos o menos
                            PrintWriter printWriter = new PrintWriter(new FileWriter("src/main/resources/resultados.txt", true), true);
                            printWriter.println("Nombre: " + nombre + ", Intentos: " + intentos + " Fecha: " + LocalDate.now() + " Hora: " + LocalTime.now().getHour() + ":" + LocalTime.now().getMinute());
                        }
                        String respuesta = input.readLine();
                        if (respuesta.equalsIgnoreCase("s")) {
                            Scanner sc = new Scanner(new FileReader("src/main/resources/resultados.txt"));
                            while (sc.hasNext()) {
                                String linea = sc.nextLine();
                                output.println(linea);
                            }
                            output.println("Fin del juego");
                        } else if (respuesta.equalsIgnoreCase("n")) {
                            output.println("Fin del juego");
                        } else {
                            output.println("No has seleccionado ninguna opción.");
                            output.println("Fin del juego");
                        }

                        break;
                    }
                }
                socket.close();
                System.out.println("Cliente desconectado");
            }
        }
    }
