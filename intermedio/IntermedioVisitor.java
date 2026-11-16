package compilador.intermedio;

import compilador.frontend.Node;
import compilador.frontend.Assign;
import compilador.frontend.ProgramNode;
import compilador.frontend.BinOp;
import compilador.frontend.Num;
import compilador.frontend.Var;
import compilador.frontend.DeclGroupNode;
import java.util.ArrayList;
import java.util.List;

public class IntermedioVisitor {

    // Lista donde se acumulan las cuádruplas generadas.
    private List<TACInstruction> codigo = new ArrayList<>();

    // Contador para nombrar temporales: _t1, _t2, _t3...
    private int temp = 0;

    // Cada llamada crea un temporal nuevo.
    private String nuevoTemp() { return "_t" + (++temp); }

    // Atajo para agregar una cuádrupla a la lista.
    private void emit(String op, String a1, String a2, String res) {
        codigo.add(new TACInstruction(op, a1, a2, res));
    }

    // El main de PruebaIntermedio usa esto para imprimir el TAC.
    public List<TACInstruction> getCodigo() { return codigo; }

    // ---------- SENTENCIAS ----------

    // Punto de entrada: recorre las sentencias en orden.
    public void visit(ProgramNode n) {
        for (Node s : n.sentencias) {
            // Despacho por instanceof: cada tipo va a su visit.
            if (s instanceof DeclGroupNode)   visit((DeclGroupNode) s);
            else if (s instanceof Assign)     visit((Assign) s);
            // read/write quedan pendientes para otra iteración.
        }
    }

    // Declaración: no genera código, solo existe para la tabla de símbolos.
    public void visit(DeclGroupNode n) { }

    // Asignación: 1) genera el TAC de la expresión, 2) emite "id = valor".
    public void visit(Assign n) {
        String val = genExpr(n.expresion);   // el resultado (temporal o literal)
        emit("", val, null, n.id);           // "id = val"
    }

    // ---------- EXPRESIONES ----------

    // Traduce una expresión a TAC y REGRESA su "valor" en texto:
    //   - hojas (Num, Var): regresan su texto, no emiten nada.
    //   - BinOp: emite una cuádrupla y regresa el temporal resultante.
    private String genExpr(Node n) {
        if (n instanceof Num) return ((Num) n).valor;      // literal tal cual
        if (n instanceof Var) return ((Var) n).nombre;     // nombre de variable

        if (n instanceof BinOp) {
            BinOp b = (BinOp) n;
            String izq = genExpr(b.izq);   // 1) resuelve hijo izquierdo
            String der = genExpr(b.der);   // 2) resuelve hijo derecho
            String t   = nuevoTemp();      // 3) crea temporal
            emit(b.operador, izq, der, t); // 4) emite operación
            return t;                      // 5) regresa el temporal al padre
        }
        return "?";
    }
}