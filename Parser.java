/* __________________________________________________________________________________
    Parser.java
    Diseñado por:
        Franco Monroy Meredith Alejandra
        Muñoz Montalvo Ricardo Antonio
        Taboada Ramirez Salvador

    Clase que contiene toda la lógica del parser.
__________________________________________________________________________________
*/


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

    // 12. <expresión>	-> <expr><exprf>
    // 13. <exprf>	-> <oper><expr><exprf>
    // 14. <exprf>	->
    // 22. <oper>	-> +
    // 23. <oper>	-> -
    // 24. <oper>	-> /
    // 25. <oper>	-> *
    private Node expresion(){
        Node left = expr();

        // 22. <oper>	-> +
        // 23. <oper>	-> -
        // 24. <oper>	-> /
        // 25. <oper>	-> *
        while(coincide("+") || coincide("-") || coincide("*") || coincide("/")) {
            String op = a.getLexema();
            match(op);
            Node right = expr();
            left = new BinOp(op,left,right);
        }
        return left;
    }

    // 15. <expr>	-> id
    // 16. <expr>	-> litbinaria
    // 17. <expr>	-> litoctal
    // 18. <expr>	-> lithexa
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

    // 19. <tipo>	-> binario
    // 20. <tipo>	-> octal
    // 21. <tipo>	-> hexa
    private String tipo() {
        String t = a.getLexema();
        if(coincide("binario")) match("binario");
        else if (coincide("octal")) match("octal");
        else if ( coincide("hexa")) match("hexa");
        else throw new RuntimeException("Se esperaba un tipo de dato");
        return t;
    }    
}