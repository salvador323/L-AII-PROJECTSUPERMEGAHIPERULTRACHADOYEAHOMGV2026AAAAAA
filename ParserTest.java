/* __________________________________________________________________________________
    ParserTest.java
    Diseñado para validar la correcta construcción del AST y la precedencia de operadores.
    Código generado por gemini
__________________________________________________________________________________
*/

import java.util.List;

public class ParserTest {

    public static void main(String[] args) {
        // 1. Entrada: Leer el archivo Prog.txt
        LectorArchivo lector = new LectorArchivo("ProgError.txt");
        List<String> lineas = lector.leerArchivo();

        if (lineas.isEmpty()) {
            System.err.println("El archivo está vacío o no se encontró.");
            return;
        }

        // Resultado esperado para la prueba de precedencia
        String expresionEsperada = "((10O - (3O * 2O)) - 1O)";

        // 2. Manejo de errores
        try {
            // 3. Orden de armado
            Lexer lexer = new Lexer();
            lexer.lexer(lineas);

            Parser parser = new Parser(lexer);
            Node arbol = parser.programa();

            // 4. Salida y verificación de resultados
            if (arbol instanceof ProgramNode) {
                ProgramNode prog = (ProgramNode) arbol;
                System.out.println("Programa: " + prog.nombre);
                System.out.println("Número de sentencias: " + prog.sentencias.size());
                System.out.println("----------------------------------------");

                String expresionObtenida = "";

                for (Node sentencia : prog.sentencias) {
                    System.out.println(describirSentencia(sentencia));

                    // Extraer la expresión de la asignación para verificar la precedencia
                    if (sentencia instanceof Assign) {
                        Assign assign = (Assign) sentencia;
                        expresionObtenida = stringifyExpresion(assign.expresion);
                    }
                }

                System.out.println("----------------------------------------");
                System.out.println("Expresión obtenida : " + expresionObtenida);
                System.out.println("Expresión esperada : " + expresionEsperada);

                if (expresionEsperada.equals(expresionObtenida)) {
                    System.out.println("Resultado          : OK");
                } else {
                    System.out.println("Resultado          : FALLO");
                }
            }

        } catch (RuntimeException e) {
            System.err.println("Error durante el análisis: " + e.getMessage());
        }
    }

    // Métodos auxiliares estáticos
    
    private static String stringifyExpresion(Node nodo) {
        if (nodo instanceof Num) {
            return ((Num) nodo).valor;
        } else if (nodo instanceof Var) {
            return ((Var) nodo).nombre;
        } else if (nodo instanceof BinOp) {
            BinOp bin = (BinOp) nodo;
            return "(" + stringifyExpresion(bin.izq) + " " + bin.operador + " " + stringifyExpresion(bin.der) + ")";
        }
        return "";
    }

    private static String describirSentencia(Node nodo) {
        if (nodo instanceof DeclGroupNode) {
            DeclGroupNode decl = (DeclGroupNode) nodo;
            return "Declaración -> Tipo: " + decl.tipo + ", Variables: " + decl.ids;
        } else if (nodo instanceof Assign) {
            Assign assign = (Assign) nodo;
            return "Asignación  -> " + assign.id + " = " + stringifyExpresion(assign.expresion);
        } else if (nodo instanceof ReadNode) {
            ReadNode read = (ReadNode) nodo;
            return "Lectura     -> leer " + read.ids;
        } else if (nodo instanceof WriteNode) {
            WriteNode write = (WriteNode) nodo;
            return "Escritura   -> escribir " + stringifyExpresion(write.expresion);
        }
        return "Sentencia no reconocida";
    }
}