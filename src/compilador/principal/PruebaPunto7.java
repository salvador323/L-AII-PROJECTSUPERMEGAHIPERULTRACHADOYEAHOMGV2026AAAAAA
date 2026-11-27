package compilador.principal;

/* __________________________________________________________________________________
    PruebaPunto7.java
    Prueba automatica del punto 7 (mensajes del parser):
      - el error trae la linea
      - el error dice que token se encontro
      - se rechaza texto despues de finprograma
      - se avisa cuando falta finprograma
    Cada caso dice que mensaje espera; al final se imprime cuantos pasaron.
__________________________________________________________________________________
*/

import compilador.frontend.Parser;
import compilador.lexico.Lexer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PruebaPunto7 {

    private static int pasaron = 0;
    private static int fallaron = 0;

    // esperado == null  -> el programa debe ACEPTARSE (sin error)
    // esperado != null  -> debe fallar y el mensaje debe contener TODOS esos textos
    private static void caso(String nombre, String[] esperado, String... programa) {
        List<String> lineas = new ArrayList<>(Arrays.asList(programa));
        String resultado;   // lo que paso de verdad
        boolean ok;

        try {
            Lexer lexer = new Lexer();
            lexer.lexer(lineas);
            Parser parser = new Parser(lexer);
            parser.programa();
            resultado = "ACEPTADO";
            ok = (esperado == null);
        } catch (RuntimeException e) {
            String msg = e.getMessage();
            resultado = msg;
            if (esperado == null) {
                ok = false;                       // no debia fallar
            } else {
                ok = true;
                for (String texto : esperado) {
                    if (!msg.contains(texto)) ok = false;
                }
                // la linea nunca debe ser 0 (senal de que el Lexer no la pasa)
                if (msg.contains("linea: 0") || msg.contains("linea:0")) ok = false;
            }
        }

        if (ok) pasaron++; else fallaron++;
        System.out.println((ok ? "[OK]    " : "[FALLA] ") + nombre);
        System.out.println("         -> " + resultado);
        if (!ok && esperado != null) {
            System.out.println("         esperaba que contuviera: " + Arrays.toString(esperado));
        }
        if (!ok && esperado == null) {
            System.out.println("         esperaba: ACEPTADO");
        }
    }

    public static void main(String[] args) {

        System.out.println("---- Programas que SI deben aceptarse ----");
        caso("1. programa correcto", null,
            "programa p",
            "binario a, b;",
            "leer(a);",
            "b = a + 1B;",
            "escribir(b);",
            "finprograma");

        caso("2. renglones vacios despues de finprograma (no es texto)", null,
            "programa p",
            "binario a;",
            "finprograma",
            "",
            "   ");

        System.out.println();
        System.out.println("---- Problema C: dice que se encontro + Problema B: dice la linea ----");
        caso("3. falta ; (se encuentra un identificador, linea 3)",
            new String[]{"linea: 3", "se esperaba: ;", ": x"},
            "programa p",
            "binario x",
            "x = 1B;",
            "finprograma");

        caso("4. falta = (se encuentra un binario, linea 3)",
            new String[]{"linea: 3", "se esperaba: =", ": 1B"},
            "programa p",
            "binario x;",
            "x 1B;",
            "finprograma");

        caso("5. falta = (se encuentra un octal, linea 3)",
            new String[]{"linea: 3", "se esperaba: =", ": 7O"},
            "programa p",
            "octal x;",
            "x 7O;",
            "finprograma");

        caso("6. falta = (se encuentra un hexa, linea 3)",
            new String[]{"linea: 3", "se esperaba: =", ": 1X"},
            "programa p",
            "hexa x;",
            "x 1X;",
            "finprograma");

        caso("7. falta ; (se encuentra una palabra reservada, linea 3)",
            new String[]{"linea: 3", "se esperaba: ;", ": escribir"},
            "programa p",
            "binario x",
            "escribir(x);",
            "finprograma");

        caso("8. la linea cuenta los renglones vacios (se detecta en linea 6)",
            new String[]{"linea: 6", "se esperaba: ;", ": binario"},
            "programa p",
            "binario x;",
            "",
            "",
            "binario z",
            "binario y;",
            "finprograma");

        caso("9. expresion no valida (se encuentra ;, linea 3)",
            new String[]{"linea:3", "Expresion no v", ": ;"},
            "programa p",
            "binario x;",
            "x = ;",
            "finprograma");

        caso("10. sentencia no reconocida (linea 3)",
            new String[]{"Sentencia no reconocida", "linea: 3"},
            "programa p",
            "binario x;",
            "= 5;",
            "finprograma");

        System.out.println();
        System.out.println("---- Problema A: texto despues de finprograma ----");
        caso("11. texto basura despues de finprograma (linea 4)",
            new String[]{"linea:4", "hay texto despues de finprograma", ": esto"},
            "programa p",
            "binario a;",
            "finprograma",
            "esto es basura");

        caso("12. una sentencia valida despues de finprograma (linea 4)",
            new String[]{"linea:4", "hay texto despues de finprograma", ": binario"},
            "programa p",
            "binario a;",
            "finprograma",
            "binario b;");

        caso("13. un segundo programa despues del primero",
            new String[]{"linea:3", "hay texto despues de finprograma", ": programa"},
            "programa p",
            "finprograma",
            "programa q",
            "finprograma");

        caso("14. la palabra eof despues de finprograma (no debe colarse)",
            new String[]{"linea:3", "hay texto despues de finprograma", ": eof"},
            "programa p",
            "finprograma",
            "eof");

        System.out.println();
        System.out.println("---- Falta finprograma ----");
        caso("15. el archivo termina sin finprograma",
            new String[]{"final del archivo"},
            "programa p",
            "binario a;",
            "a = 1B;");

        System.out.println();
        System.out.println("==== Pasaron: " + pasaron + "   Fallaron: " + fallaron + "   Total: " + (pasaron + fallaron) + " ====");
    }
}