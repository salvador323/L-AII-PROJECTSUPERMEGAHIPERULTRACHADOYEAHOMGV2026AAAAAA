import java.util.List;

public class PruebaIntermedio {

    public static void main(String[] args) {
        LectorArchivo lector = new LectorArchivo("Prog.txt");
        List<String> lineas = lector.leerArchivo();
        if (lineas.isEmpty()) { System.err.println("Archivo vacío"); return; }

        Lexer lexer = new Lexer();
        lexer.lexer(lineas);

        Parser parser = new Parser(lexer);
        ProgramNode arbol = (ProgramNode) parser.programa();

        System.out.println("Programa: " + arbol.nombre);
        System.out.println("--- TAC generado ---");

        IntermedioVisitor gen = new IntermedioVisitor();
        gen.visit(arbol);
        for (TACInstruction q : gen.getCodigo()) {
            System.out.println(q);
        }
    }
}