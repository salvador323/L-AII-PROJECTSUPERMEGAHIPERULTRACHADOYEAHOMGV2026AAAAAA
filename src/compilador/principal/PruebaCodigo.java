package compilador.principal;

import compilador.intermedio.IntermedioVisitor;
import compilador.intermedio.GeneradorCodigo;
import compilador.intermedio.TACInstruction;
import compilador.frontend.ProgramNode;
import compilador.frontend.Parser;
import compilador.lexico.Lexer;
import compilador.lexico.LectorArchivo;
import java.util.List;

public class PruebaCodigo {

    public static void main(String[] args) {

        // PASO 1: leer el archivo fuente.
        LectorArchivo lector = new LectorArchivo("Prog.txt");
        List<String> lineas = lector.leerArchivo();
        if (lineas.isEmpty()) { System.err.println("Archivo vacío"); return; }

        // PASO 2: análisis léxico (genera tokens bajo demanda).
        Lexer lexer = new Lexer();
        lexer.lexer(lineas);

        // PASO 3: análisis sintáctico (construye el AST).
        // Aquí ya se respetó la precedencia de operadores.
        Parser parser = new Parser(lexer);
        ProgramNode arbol = (ProgramNode) parser.programa();

        // PASO 4: fase de código intermedio (AST → TAC).
        IntermedioVisitor gen = new IntermedioVisitor();
        gen.visit(arbol);
        List<TACInstruction> tac = gen.getCodigo();

        System.out.println("Programa: " + arbol.nombre);
        System.out.println("--- TAC generado ---");
        for (TACInstruction q : tac) System.out.println(q);

        // PASO 5: fase de generación (TAC → Assembly).
        // Recibe la lista de cuádruplas, NO el árbol.
        GeneradorCodigo genAsm = new GeneradorCodigo();
        genAsm.generar(tac);

        System.out.println();
        System.out.println("--- Assembly generado ---");
        for (String linea : genAsm.getAsm()) System.out.println(linea);
    }
}