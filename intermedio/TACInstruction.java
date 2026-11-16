package compilador.intermedio;

/* __________________________________________________________________________________
    TACInstruction.java
    Una sola instrucción de código intermedio (una "cuádrupla").
    Formato: result = arg1 op arg2
    Máximo 3 operandos (dos fuentes + un destino) → de ahí "código de tres direcciones".
__________________________________________________________________________________
*/
public class TACInstruction {
    // Los 4 campos de una cuádrupla.
    // op     = operador (+, -, *, /) o "" si es una asignación simple
    // arg1   = primer operando (izquierda)
    // arg2   = segundo operando (derecha). Puede ser null en asignaciones simples.
    // result = dónde se guarda el resultado (temporal _tN o variable real)
    public String op, arg1, arg2, result;

    public TACInstruction(String op, String arg1, String arg2, String result) {
        this.op = op;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.result = result;
    }

    @Override
    public String toString() {
        // Dos formatos de impresión:
        // 1) Asignación simple (op vacío y arg2 nulo):  "res = _t3"
        // 2) Operación binaria:                          "_t1 = 3O * 2O"
        return arg2 == null && op.isEmpty() ?
               result + " = " + arg1 :
               result + " = " + arg1 + " " + op + " " + arg2;
    }
}