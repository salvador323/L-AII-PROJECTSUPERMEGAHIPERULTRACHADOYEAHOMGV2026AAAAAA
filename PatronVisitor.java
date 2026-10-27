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

    public PatronVisitor(SymbolTable table){
        this.table = table;
    }

    //recorre la lista de sentencias en orden
    public void visit(ProgramNode nodo){
        for(Node n : nodo.sentencias){
            if (n instanceof Assign) {
                Assign a = (Assign) n;
                visit(a);
            } else if (n instanceof DeclGroupNode) {
                DeclGroupNode d = (DeclGroupNode) n;
                visit(d);
            } else if (n instanceof ReadNode) {
                ReadNode r = (ReadNode) n;
                visit(r);
            } else if (n instanceof WriteNode) {
                WriteNode w = (WriteNode) n;
                visit(w);
            }
        }
    }

    // 5. <sent> -> <tipo><listaid>;
    public void visit(DeclGroupNode nodo){
        for(String id : nodo.ids){
            table.addSymbol(id, nodo.tipo);
        }
    }

    // 6. <sent> -> id = <expresion>;
    public void visit(Assign nodo){
        if (nodo.expresion instanceof Num) {
            Num num = (Num) nodo.expresion;
            table.updateSymbol(nodo.id, num.valor);
        } else if (nodo.expresion instanceof Var) {
            Var v = (Var) nodo.expresion;
            table.usesSymbol(v.nombre);
            table.updateSymbol(nodo.id, v.nombre);
        }
        //falta BinOp
    }

    // 7. <sent> -> leer(<listaid>);
    public void visit(ReadNode nodo){
        for(String id : nodo.ids){
            Symbol s = table.lookup(id);
            if(s == null){
                System.out.println("Error semantico, la variable " + id + " no ha sido declarada");
            } else {
                s.initialized = true;
            }
        }
    }

    // 8. <sent> -> escribir(<expresion>); revisa que sea valido
    public void visit(WriteNode nodo){
        if (nodo.expresion instanceof Var) {
            Var v = (Var) nodo.expresion;
            table.usesSymbol(v.nombre);
        }
    }
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
    
    //PENDIENTE: Agregar metodo visit(ProgramNode) para recorrer sentencias
    //PENDIENTE: Agregar metodo visit(ReadNode)
}

*/
