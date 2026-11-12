package compilador.intermedio;

/* __________________________________________________________________________________
    PruebaIntermedio.java
    Cliente del compilador que ejercita la fase de código intermedio.
    Mismo pipeline que ParserTest y PruebaVisitor: archivo → lexer → parser → AST → visitor.
    La única diferencia es que aquí el visitor es IntermedioVisitor.
__________________________________________________________________________________
*/
import compilador.frontend.ProgramNode;
import compilador.frontend.Parser;
import compilador.lexico.Lexer;
import compilador.lexico.LectorArchivo;
import java.util.List;

public class PruebaIntermedio {

    public static void main(String[] args) {

        // PASO 1: leer el archivo fuente y obtener una lista de líneas.
        LectorArchivo lector = new LectorArchivo("Prog.txt");
        List<String> lineas = lector.leerArchivo();
        if (lineas.isEmpty()) { System.err.println("Archivo vacío"); return; }

        // PASO 2: analizador léxico. Solo guarda las líneas; los tokens
        // se van pidiendo uno a uno cuando el parser los necesita.
        Lexer lexer = new Lexer();
        lexer.lexer(lineas);

        // PASO 3: analizador sintáctico. Construye el AST.
        // Aquí ya se respetó la precedencia (multiplicación antes que suma).
        Parser parser = new Parser(lexer);
        ProgramNode arbol = (ProgramNode) parser.programa();

        System.out.println("Programa: " + arbol.nombre);
        System.out.println("--- TAC generado ---");

        // PASO 4: fase de código intermedio.
        // El visitor recorre el MISMO AST y produce cuádruplas.
        IntermedioVisitor gen = new IntermedioVisitor();
        gen.visit(arbol);

        // PASO 5: imprimir el TAC resultante (una cuádrupla por línea).
        for (TACInstruction q : gen.getCodigo()) {
            System.out.println(q);
        }
    }
}