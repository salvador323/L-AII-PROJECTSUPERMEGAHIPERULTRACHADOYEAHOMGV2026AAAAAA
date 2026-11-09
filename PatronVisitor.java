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
    private String buzon = "";//no perder el valor al pasar de num a assing

    public PatronVisitor(SymbolTable table){
        this.table = table;
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
                System.out.println("La variable " + id + " ya fue declarada, error semántico");
            }
        }
    }

    
    // 6. <sent> -> id = <expresion>;
    public void visit(Assign nodo){
            Symbol sym = table.lookup(nodo.id);
        if(sym==null){
            System.out.println("La variable " + nodo.id+ " no ha sido declarada");
        }else{
            buzon = "";
            nodo.expresion.accept(this);//comparar tipos
            if(sym.type.equals(buzon)){
                sym.initialized = true;//marcar como inicializada
            }else{
                System.out.println("Error, los tipos son incompatibles");
            }
        }
    }

    // 6. <sent> -> id = <expresion>;
    /*public void visit(Assign nodo){
        if (nodo.expresion instanceof Num) {
            Num num = (Num) nodo.expresion;
            table.updateSymbol(nodo.id, num.valor);
        } else if (nodo.expresion instanceof Var) {
            Var v = (Var) nodo.expresion;
            table.usesSymbol(v.nombre);
            table.updateSymbol(nodo.id, v.nombre);
        }
        //falta BinOp
    }*/

    // 7. <sent> -> leer(<listaid>);
    public void visit(ReadNode nodo){
        for(String id : nodo.ids){
            Symbol sym = table.lookup(id);
            if(sym == null){
                System.out.println("Error semantico, la variable " + id + " no ha sido declarada");
            } else {
                sym.initialized = true;
            }
        }
    }

    // 8. <sent> -> escribir(<expresion>);
    public void visit(WriteNode nodo){
        nodo.expresion.accept(this);
    }

    public void visit(Num nodo){
        if(Lexer.esBin(nodo.valor)){
            buzon = "binario";
        }else if(Lexer.esHexa(nodo.valor)){
            buzon = "hexa";
        }else if(Lexer.esOct(nodo.valor)){
            buzon = "octal";
        }
    }

    public void visit(Var nodo){
        Symbol sym = table.lookup(nodo.nombre);
        if(sym == null){
            System.out.println("La variable " + nodo.nombre + " no ha sido declarada");
        }else{
            buzon = sym.type;
            if(sym.initialized == false){
                System.out.println("La variable " + nodo.nombre + " no ha sido inicializada");
            }
        }
    }

    // 10B + 101B
    public void visit(BinOp nodo){
        nodo.izq.accept(this);
        String buzon_izq = buzon;
        nodo.der.accept(this);
        if(!buzon_izq.equals(buzon)){
            System.out.println("No se pueden mezclar tipos en una operación");
        }
    }

    /*
    // 10B + 101B
    public void visit(BinOp nodo){
        nodo.izq.accept(this);
        nodo.der.accept(this);
    }
    */

}




/*
antes
public class PatronVisitor {
    
    private SymbolTable table;

    public PatronVisitor(SymbolTable table){
        this.table = table;
    }

    // 5. <sent>    -> <tipo><listaid>;
    public void visit(DeclGroupNode nodo){
       for(String id:nodo.ids){
           table.addSymbol(id, nodo.tipo);
       }
    }
    
    // 6. <sent>    -> id = <expresion>;
    public void visit(Assign nodo){
        //PENDIENTE: Desempaquetar nodo.expresion antes de actualizar
        table.updateSymbol(nodo.id, nodo.expresion);
    }

    // 15. <expr>   -> id
    public void visit(Var nodo){
        table.usesSymbol(nodo.nombre);
    }
}

*/
