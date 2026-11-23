package compilador.frontend;


import java.util.ArrayList;
import java.util.List;

import compilador.lexico.Lexer;

/* __________________________________________________________________________________
    PatronVisitor.java
    Diseñado por:
        Franco Monroy Meredith Alejandra
        Muñoz Montalvo Ricardo Antonio
        Taboada Ramirez Salvador

    Clase que contiene toda la lógica del patrón visitor.
__________________________________________________________________________________
*/
public class PatronVisitor {

    private SymbolTable table;
    private String buzon = "";//no perder el valor al pasar de num a assign
    private List<String> errores = new ArrayList<>(); // creamos una lista de errores para almacenar los mensajes de error

    public PatronVisitor(SymbolTable table){
        this.table = table;
    }

    public boolean hayErrores(){
        return !errores.isEmpty();
    }

    public List<String> getErrores(){
        return errores;
    }

    //recorre la lista de sentencias en orden
    public void visit(ProgramNode nodo){
        for(Node n : nodo.sentencias){
            n.accept(this);
        }
    }

    // 5. <sent> -> <tipo><listaid>;
    public void visit(DeclGroupNode nodo){
        for(String id : nodo.ids){
            boolean add = table.addSymbol(id, nodo.tipo);
            if(add == false){
                errores.add("Error semantico: La variable '" + id + "' ya fue declarada");
            }
        }
    }

    
    // 6. <sent> -> id = <expresion>;
    public void visit(Assign nodo){
        Symbol sym = table.lookup(nodo.id);
        if(sym==null){
            errores.add("Error semantico: La variable '" + nodo.id + "' no ha sido delcarada");
            buzon = "";
            nodo.expresion.accept(this);
        }else{
            buzon = "";
            nodo.expresion.accept(this);//comparar tipos
            if(buzon.equals("error")){
                return;
            }

            if(sym.type.equals(buzon)){
                sym.initialized = true;//marcar como inicializada
            }else{
                errores.add("Error semantico: Los tipos son incompatibles para la variable '" + nodo.id + "' (se esperaba " + sym.type + " pero se obtuvo " + buzon + ")");
            }
        }
    }

    // 7. <sent> -> leer(<listaid>);
    public void visit(ReadNode nodo){
        for(String id : nodo.ids){
            Symbol sym = table.lookup(id);
            if(sym == null){
                errores.add("Error semantico: La variable '" + id + "' no ha sido declarada");
            } else {
                sym.initialized = true;
            }
        }
    }

    // 8. <sent> -> escribir(<expresion>);
    public void visit(WriteNode nodo){
        buzon = "";
        nodo.expresion.accept(this);
    }

    public void visit(Num nodo){
        if(Lexer.esBin(nodo.valor)){
            buzon = "binario";
        }else if(Lexer.esHexa(nodo.valor)){
            buzon = "hexa";
        }else if(Lexer.esOct(nodo.valor)){
            buzon = "octal";
        }else{
            buzon = "error";
        }
    }

    public void visit(Var nodo){
        Symbol sym = table.lookup(nodo.nombre);
        if(sym == null){
            errores.add("Error semantico: La variable '" + nodo.nombre + "' no ha sido declarada");
            buzon = "error";
        }else{
            buzon = sym.type;
            if(sym.initialized == false){
                errores.add("Error semantico: La variable '" + nodo.nombre + "' no ha sido inicializada");
            }
        }
    }

    // 10B + 101B
    public void visit(BinOp nodo){
        nodo.izq.accept(this);
        String buzon_izq = buzon;
        nodo.der.accept(this);
        String buzon_der = buzon;

        if(buzon_izq.equals("error") || buzon_der.equals("error")){
            buzon = "error";
            return;
        }

        if(!buzon_izq.equals(buzon_der)){
            errores.add("Error semantico: No se pueden mezclar tipos en una operación");
            buzon = "error";
        }else{
            buzon = buzon_izq;
        }
    }

}