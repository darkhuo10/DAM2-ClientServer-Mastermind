import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MastermindClient {
    public static final int PORT = 3320;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", PORT);
        System.out.println("Conectado al servidor");
        System.out.println("Instrucciones:" +
                "\n\tIntroduce los datos que se piden en el formato que se pide." +
                "\n\tAl introducir los colores recibirás una de estas respuesta:" +
                "\n\t\t'No': El color no forma parte del código"+
                "\n\t\t'--': El color forma parte del código pero la posición no es la correcta"+
                "\n\t\t'Si': El color forma parte del código y está en la posición correcta");
        System.out.println("Colores: [rojo, verde, azul, amarillo, rosa, naranja]");
        System.out.println("-------------------------------------------------------------------");


        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduce tu nombre: ");
        String nombre = scanner.nextLine();
        JSONObject clientJson = new JSONObject();
        clientJson.put("nombre", nombre);

        int aciertos = 0;
        String answers;
        System.out.println("-------------------------------------------------------------------");
        System.out.println("Formato del código: color1 color2 color3 color4 color5 color6");
        while (aciertos < 6) {
            System.out.print("Introduce código de colores separado por espacios: ");

            String a = scanner.next();
            String b = scanner.next();
            String c = scanner.next();
            String d = scanner.next();
            String e = scanner.next();
            String f = scanner.next();

            scanner.nextLine();
            clientJson.put("a", a);
            clientJson.put("b", b);
            clientJson.put("c", c);
            clientJson.put("d", d);
            clientJson.put("e", e);
            clientJson.put("f", f);

            output.println(clientJson);

            String response = input.readLine();
            JSONObject serverJson = new JSONObject(response);
            aciertos = serverJson.getInt("aciertos");
            answers = serverJson.getString("respuestas");
            System.out.println(answers);
            if (aciertos == 6) {
                int intentos = serverJson.getInt("intentos");
                System.out.println("Código adivinado, has ganado!");
                System.out.println(nombre + ": has tardado " + intentos + " intentos en adivinar el código.");
                System.out.println("¿Deseas ver los mejores resultados? [S/N]");
                String respuesta = scanner.nextLine();
                output.println(respuesta);
                String serverResponse;
                while ((serverResponse = input.readLine()) != null) {
                    System.out.println(serverResponse);
                }
            }
        }
        socket.close();
        System.out.println("Desconectado del servidor");
    }
}
