package compilador.intermedio;

import java.util.ArrayList;
import java.util.List;

public class GeneradorCodigo {

    // Lista de líneas de assembly que se van generando.
    private List<String> asm = new ArrayList<>();

    // Recorre cada cuádrupla del TAC y la traduce a instrucciones.
    // No toca el AST: trabaja solo sobre la lista de cuádruplas.
    public void generar(List<TACInstruction> tac) {
        for (TACInstruction q : tac) {
            switch (q.op) {

                // Para operaciones aritméticas: 3 pasos.
                // 1) carga el operando izquierdo en el acumulador
                // 2) aplica la operación con el derecho
                // 3) guarda el acumulador en el resultado (temporal o variable)
                case "+":
                    asm.add("MOV eax, " + q.arg1);
                    asm.add("ADD eax, " + q.arg2);
                    asm.add("MOV " + q.result + ", eax");
                    break;
                case "-":
                    asm.add("MOV eax, " + q.arg1);
                    asm.add("SUB eax, " + q.arg2);
                    asm.add("MOV " + q.result + ", eax");
                    break;
                case "*":
                    asm.add("MOV eax, " + q.arg1);
                    asm.add("MUL " + q.arg2);
                    asm.add("MOV " + q.result + ", eax");
                    break;
                case "/":
                    asm.add("MOV eax, " + q.arg1);
                    asm.add("DIV " + q.arg2);
                    asm.add("MOV " + q.result + ", eax");
                    break;

                // op vacío = asignación simple.
                // Solo se mueve arg1 al resultado.
                default:
                    asm.add("MOV " + q.result + ", " + q.arg1);
            }
        }
    }

    public List<String> getAsm() { return asm; }
}