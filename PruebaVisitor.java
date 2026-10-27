import java.util.ArrayList;
import java.util.List;

/**
 * PruebaVisitor (version chiquita): un solo programa que muestra
 * que SI funciona y que NO funciona todavia.
 *
 * Camino completo que sigue el programa:
 *   texto -> Lexer (tokens) -> Parser (arbol) -> Visitor (recorre) -> Tabla (guarda y valida)
 * Código generado por Claude.
 */
public class PruebaVisitor {

    public static void main(String[] args) {

        // PASO 0: updateSymbol imprime con System.err y lo demas con System.out.
        // Aqui juntamos las dos salidas para que los mensajes salgan en orden.
        System.setErr(System.out);

        // PASO 1: preparamos el programa de prueba como una lista de lineas
        // (un renglon del codigo fuente = un elemento de la lista).
        List<String> lineas = new ArrayList<>();
        lineas.add("programa p");
        lineas.add("binario x, y;");
        lineas.add("x = 101B;");        // SI funciona: sale "Bien"
        lineas.add("x = 7O;");          // SI funciona: detecta el error de tipo
        lineas.add("y = x;");           // NO funciona: da un error FALSO
        lineas.add("y = 1B + 10B;");    // NO funciona: BinOp se ignora (no sale nada)
        lineas.add("finprograma");

        // Mostramos el codigo y lo que esperamos que pase con cada linea
        System.out.println("Codigo:");
        for (String l : lineas) {
            System.out.println("    " + l);
        }
        System.out.println();
        System.out.println("x = 101B;     -> deberia salir 'Bien'                (SI funciona)");
        System.out.println("x = 7O;       -> deberia salir 'Mal' (tipo)          (SI funciona)");
        System.out.println("y = x;        -> sale 'Mal' pero NO deberia          (NO funciona)");
        System.out.println("y = 1B + 10B; -> no sale nada, se ignora             (NO funciona)");
        System.out.println();
        System.out.println("--- Salida real ---");

        // PASO 2: creamos el Lexer y le damos las lineas.
        // IMPORTANTE: esto va ANTES de crear el Parser, porque el Parser
        // pide el primer token en cuanto se construye.
        Lexer lexer = new Lexer();
        lexer.lexer(lineas);

        // PASO 3: creamos el Parser, que usa al Lexer para leer los tokens.
        Parser parser = new Parser(lexer);

        // PASO 4: le pedimos al Parser que construya el ARBOL (AST).
        // programa() devuelve un Node, por eso hacemos el cast a ProgramNode:
        // este es el nodo principal que trae la lista de sentencias adentro.
        ProgramNode arbol = (ProgramNode) parser.programa();

        // PASO 5: creamos la Tabla de Simbolos (empieza vacia).
        SymbolTable tabla = new SymbolTable();

        // PASO 6: creamos el Visitor y le pasamos la tabla,
        // para que el Visitor y nosotros compartamos la misma tabla.
        PatronVisitor visitor = new PatronVisitor(tabla);

        // PASO 7: el Visitor recorre el arbol. Entra por el ProgramNode y va
        // sentencia por sentencia, mandando cada nodo (declaracion, asignacion,
        // lectura, escritura) a su visit correspondiente. Los mensajes de
        // "Bien", "Mal" y los errores salen en este paso.
        visitor.visit(arbol);

        // PASO 8: revisamos como quedo la tabla al final.
        // Aqui se ve que variables quedaron inicializadas y con que valor.
        System.out.println();
        System.out.println("--- Tabla al final ---");
        for (Symbol s : tabla.table.values()) {
            System.out.println(s.name + " | " + s.type + " | inicializada=" + s.initialized + " | valor=" + s.value);
        }
    }
}