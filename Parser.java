/* __________________________________________________________________________________
    Parser.java
    Diseñado por:
        Franco Monroy Meredith Alejandra
        Muñoz Montalvo Ricardo Antonio
        Taboada Ramirez Salvador

    Clase que contiene toda la lógica del parser.
__________________________________________________________________________________
*/
import java.util.ArrayList;
import java.util.List;


public class Parser {
    private Lexer lexer;
    private Token a;

    public Parser(Lexer lexer){
        this.lexer = lexer;
        this.a = lexer.getToken();
    }

    private boolean coincide(String esperado){
        if(a==null) return false;

        boolean esId = "id".equals(esperado) && "295".equals(a.getAtributo());
        boolean esLitBin = "litbinaria".equals(esperado) && "296".equals(a.getAtributo());
        boolean esLitOct = "litoctal".equals(esperado) && "297".equals(a.getAtributo());
        boolean esLitHex = "lithexa".equals(esperado) && "298".equals(a.getAtributo());
        boolean esLiteral = esperado.equalsIgnoreCase(a.getLexema()) || esperado.equals(a.getAtributo());

        return esLiteral || esId || esLitBin || esLitOct || esLitHex;
    }

    private void match(String atributoEsperado) {
        if(coincide(atributoEsperado)){
            a = lexer.getToken();
        } else {
            throw new RuntimeException("Error sintactico, se esperaba: " + atributoEsperado);
        }
    }

    // 1. <inicio>	-> programa id <sentencias> finprograma
    public Node programa(){
        match("programa");
        String nombrePrograma = a.getLexema(); 
        match("id");

        List<Node> sentencias = sentencias();

        match("finprograma");
        return new ProgramNode(nombrePrograma, sentencias);
    }

    // 2. <sentencias>	-> <sent><sentf>
    // 3. <sentf>	-> <sent><sentf>
    // 4. <sentf>	->
    private List<Node> sentencias() {
        List<Node> lista = new ArrayList<>();
        
        while (a != null && !coincide("finprograma")) {
            Node nodoSentencia = sent(); 
            lista.add(nodoSentencia);
        }
        return lista;
    }

    // 5. <sent>	-> <tipo><listaid>;
    // 6. <sent>	-> id = <expresión>;
    // 7. <sent>	-> leer (<listaid>);
    // 8. <sent>	-> escribir (<expresión>);
    private Node sent(){
        // 5. <sent>	-> <tipo><listaid>;
        if(coincide("binario") || coincide("octal") || coincide("hexa")) {
            String tipo = tipo();
            List<String> ids = listaId();
            match(";");
            return new DeclGroupNode(tipo, ids);
        }
        // 6. <sent>	-> id = <expresión>;
        else if(coincide("id")) {
            String id = a.getLexema();
            match("id");
            match("=");
            Node exp = expresion();
            match(";");
            return new Assign(id,exp);
        }
        // 7. <sent>	-> leer (<listaid>);
        else if ( coincide("leer")){
            match("leer");
            match("(");
            List<String> ids= listaId();
            match(")");
            match(";");
            return new ReadNode(ids);
        }
        // 8. <sent>	-> escribir (<expresión>);
        else if (coincide("escribir")){
            match("escribir");
            match("(");
            Node exp = expresion();
            match(")");
            match(";");
            return new WriteNode(exp);
        }
        else {
            throw new RuntimeException("Sentencia no reconocida");
        }
    }

    // 9. <listaid>	-> id<listaf>
    // 10. <listaf>	-> , id<listaf>
    // 11. <listaf>	->
    private List<String> listaId(){
        List<String> ids = new ArrayList<>();
        ids.add(a.getLexema());
        match("id");

        while(coincide(",")){
            match(",");
            ids.add(a.getLexema());
            match("id");
        }
        return ids;
    }

    // 12. <expresión>	-> <expterm><exprf>
    // 13. <exprf>	-> <oper><expterm><exprf>
    // 14. <exprf>	->
    // 15. <expterm> -> <expr><termf>
    // 16. <termf> -> <operterm><expr><termf>
    // 17. <termf> -> 
    private Node expresion(){
        Node left = expterm(); // guarda lo que esta antes del operdor

        // 25. <oper>	-> +
        // 26. <oper>	-> -
        while(coincide("+") || coincide("-")) {
            String op = a.getLexema(); // guarda el operador
            match(op); // lo consume
            Node right = expterm(); // guarda lo que esta despues del oeprador
            left = new BinOp(op,left,right); // se crea la estructura de la operacion juntanto los elementos
        }
        return left;
    }

    private Node expterm(){
        Node left = expr(); // guarda lo que esta antes del operdor

        // 27. <operterm>	-> /
        // 28. <operterm>	-> *
        while(coincide("*") || coincide("/")) {
            String op = a.getLexema(); // guarda el operador
            match(op); // lo consume
            Node right = expr(); // guarda lo que esta despues del oeprador
            left = new BinOp(op,left,right); // se crea la estructura de la operacion juntanto los elementos
        }
        return left;
    }

    // 18. <expr>	-> id
    // 19. <expr>	-> litbinaria
    // 20. <expr>	-> litoctal
    // 21. <expr>	-> lithexa
    private Node expr(){
        if(coincide("id")){
            String id = a.getLexema();
            match("id");
            return new Var(id);
        } else if (coincide("litbinaria") || coincide("litoctal") || coincide("lithexa")) {
            String val = a.getLexema();
            if(coincide("litbinaria")) match("litbinaria");
            else if (coincide("litoctal")) match("litoctal");
            else match("lithexa");
            return new Num(val);
        } else {
            throw new RuntimeException("Expresion no válida: " + (a != null ? a.getLexema(): "EOF"));
        }
    }

    // 22. <tipo>	-> binario
    // 23. <tipo>	-> octal
    // 24. <tipo>	-> hexa
    private String tipo() {
        String t = a.getLexema();
        if(coincide("binario")) match("binario");
        else if (coincide("octal")) match("octal");
        else if ( coincide("hexa")) match("hexa");
        else throw new RuntimeException("Se esperaba un tipo de dato");
        return t;
    }
}
