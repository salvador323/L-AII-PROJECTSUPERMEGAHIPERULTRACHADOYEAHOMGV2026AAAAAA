import java.util.ArrayList;
import java.util.List;

public class IntermedioVisitor {

    private List<TACInstruction> codigo = new ArrayList<>();
    private int temp = 0;

    private String nuevoTemp() { return "_t" + (++temp); }

    private void emit(String op, String a1, String a2, String res) {
        codigo.add(new TACInstruction(op, a1, a2, res));
    }

    public List<TACInstruction> getCodigo() { return codigo; }

    // ---- Sentencias (void, igual que Salvador) ----

    public void visit(ProgramNode n) {
        for (Node s : n.sentencias) {
            if (s instanceof DeclGroupNode)   visit((DeclGroupNode) s);
            else if (s instanceof Assign)     visit((Assign) s);
            // read/write se dejan para la siguiente iteración
        }
    }

    public void visit(DeclGroupNode n) {
        // las declaraciones no generan código
    }

    public void visit(Assign n) {
        String val = genExpr(n.expresion);
        emit("", val, null, n.id);   // res = val
    }

    // ---- Expresiones (String, despacho por instanceof) ----

    private String genExpr(Node n) {
        if (n instanceof Num) return ((Num) n).valor;      // literal tal cual
        if (n instanceof Var) return ((Var) n).nombre;     // nombre de variable
        if (n instanceof BinOp) {
            BinOp b = (BinOp) n;
            String izq = genExpr(b.izq);
            String der = genExpr(b.der);
            String t   = nuevoTemp();
            emit(b.operador, izq, der, t);
            return t;
        }
        return "?";
    }
}