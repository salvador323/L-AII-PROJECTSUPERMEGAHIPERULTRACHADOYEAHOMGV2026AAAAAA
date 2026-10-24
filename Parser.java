/* __________________________________________________________________________________
    Parser.java
    Diseñado por:
        Franco Monroy Meredith Alejandra
        Muñoz Montalvo Ricardo Antonio
        Taboada Ramirez Salvador

    Clase que contiene toda la lógica del parser.
__________________________________________________________________________________
*/

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    // 1. <inicio>  -> programa id <sentencias> finprograma
    public Node programa(){
        match("programa");
        String nombrePrograma = a.getLexema(); 
        match("id");

        List<Node> sentencias = sentencias();

        match("finprograma");
        return new ProgramNode(nombrePrograma, sentencias);
    }

    // 2. <sentencias>  -> <sent><sentf>
    // 3. <sentf>   -> <sent><sentf>
    // 4. <sentf>   ->
    private List<Node> sentencias() {
        List<Node> lista = new ArrayList<>();
        
        while (a != null && !coincide("finprograma")) {
            Node nodoSentencia = sent(); 
            lista.add(nodoSentencia);
        }
        return lista;
    }

    // 5. <sent>    -> <tipo><listaid>;
    // 6. <sent>    -> id = <expresión>;
    // 7. <sent>    -> leer (<listaid>);
    // 8. <sent>    -> escribir (<expresión>);
    private Node sent(){
        // 5. <sent>    -> <tipo><listaid>;
        if(coincide("binario") || coincide("octal") || coincide("hexa")) {
            String tipo = tipo();
            List<String> ids = listaId();
            match(";");
            return new DeclGroupNode(tipo, ids);
        }
        // 6. <sent>    -> id = <expresión>;
        else if(coincide("id")) {
            String id = a.getLexema();
            match("id");
            match("=");
            Node exp = expresion();
            match(";");
            return new Assign(id,exp);
        }
        // 7. <sent>    -> leer (<listaid>);
        else if ( coincide("leer")){
            match("leer");
            match("(");
            List<String> ids= listaId();
            match(")");
            match(";");
            return new ReadNode(ids);
        }
        // 8. <sent>    -> escribir (<expresión>);
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

    // 9. <listaid> -> id<listaf>
    // 10. <listaf> -> , id<listaf>
    // 11. <listaf> ->
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

    // 12. <expresión>  -> <expr><exprf>
    // 13. <exprf>  -> <oper><expr><exprf>
    // 14. <exprf>  ->
    // 22. <oper>   -> +
    // 23. <oper>   -> -
    // 24. <oper>   -> /
    // 25. <oper>   -> *
    private Node expresion(){
        Node left = expr();

        // 22. <oper>   -> +
        // 23. <oper>   -> -
        // 24. <oper>   -> /
        // 25. <oper>   -> *
        while(coincide("+") || coincide("-") || coincide("*") || coincide("/")) {
            String op = a.getLexema();
            match(op);
            Node right = expr();
            left = new BinOp(op,left,right);
        }
        return left;
    }

    // 15. <expr>   -> id
    // 16. <expr>   -> litbinaria
    // 17. <expr>   -> litoctal
    // 18. <expr>   -> lithexa
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

    // 19. <tipo>   -> binario
    // 20. <tipo>   -> octal
    // 21. <tipo>   -> hexa
    private String tipo() {
        String t = a.getLexema();
        if(coincide("binario")) match("binario");
        else if (coincide("octal")) match("octal");
        else if ( coincide("hexa")) match("hexa");
        else throw new RuntimeException("Se esperaba un tipo de dato");
        return t;
    }    

    //---------------------------------------------------------
    /*
    1 Inicio, Patrón Visitor
    */
    //Tabla de simbolos
    class Symbol{
        String name, type;
        Object value;
        Boolean initialized;

        public Symbol(String name, String type, Object value, Boolean initialized){
            this.name = name;
            this.type = type;
            this.value = value;
            this.initialized = initialized;
        }
    }

    class SymbolTable{
        Map<String, Symbol> table = new HashMap<>();

        //Metodos a desarrollar
        //Metodo que valida si una variable ya fue declarada o no
        public void addSymbol(String name, String type){
            if(!table.containsKey(name)){
                table.put(name, new Symbol(name, type, null, false));
            }else{
                System.out.println("Error semantico, la variable " + name + " ya fue declarada");
            }
        }

        //Metodo que devuelve symbol o null
        public Symbol lookup(String name){
            return table.get(name);
        }


        public void updateSymbol(String name, Object value){
            Symbol sym = lookup(name);

            if(sym==null){ System.out.println("Error semantico " + name + " no ha sido declarada"); return;}

            boolean initialized = false;
            
            if(sym.type.equals("binario") && Lexer.esBin(value.toString())){
                initialized=true;
            }else if(sym.type.equals("octal") && Lexer.esOct(value.toString())){
                initialized=true;
            }else if(sym.type.equals("hexa") && Lexer.esHexa(value.toString())){
                initialized=true;
            }

            if(initialized){
                sym.value = value;
                sym.initialized = true;
                System.err.println("Bien " + name + " = " + value);             
            }else{
                System.err.println("Mal, error semantico " + 
                                    "(tipo incompatible), la variable "+ name + "es de tipo " + sym.type+ " pero recibio " + value);
            }
        }

        //Metodo para validar errores
        public void usesSymbol(String name){
            Symbol sym = lookup(name);
            if(sym == null){
                System.out.println("Error semantico, la variable " + name + " no ha sido declarada");
            } else if(!sym.initialized){
                System.out.println("Error semantico, la variable " + name + " se esta usando sin haber sido inicializada");
            }
        }
    }

    //Patron Visitor
    public class VisitorAssignament {
        
        private SymbolTable table;

        public VisitorAssignament(SymbolTable table){
            this.table = table;
        }

        // 5. <sent>    -> <tipo><listaid>;
        // nodo de declaracion
        public void visit(DeclGroupNode nodo){
           for(String id:nodo.ids){
               table.addSymbol(id, nodo.tipo);
           }
        }
        
        // 6. <sent>    -> id = <expresion>;
        // nodo de asignacion
        public void visit(Assign nodo){
            table.updateSymbol(nodo.id, nodo.val);
        }

        // 15. <expr>   -> id
        // nodo de lectura/escritura
        public void visit(Var nodo){
            table.usesSymbol(nodo.id);
        }
    }
}
