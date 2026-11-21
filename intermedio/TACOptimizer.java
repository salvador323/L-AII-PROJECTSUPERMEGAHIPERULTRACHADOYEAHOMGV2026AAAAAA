package compilador.intermedio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TACOptimizer
 * Tres pasadas iteradas hasta punto fijo, adaptadas del ejemplo del profesor:
 *   1) Plegado de constantes
 *   2) Propagación de constantes
 *   3) Eliminación de código muerto
 *
 * Adaptaciones al proyecto:
 *   - Los literales llevan sufijo B/O/X (no son decimales puros como en el PDF).
 *   - La asignación se representa con op = "" (no con "=").
 *   - Los temporales son "_tN" (no "tN").
 */
public class TACOptimizer {

    public List<TACInstruction> optimize(List<TACInstruction> program) {
        List<TACInstruction> current = program;
        boolean changed;
        do {
            int prev = current.size();
            current = foldConstants(current);
            current = propagateConstants(current);
            current = eliminateDeadCode(current);
            changed = current.size() != prev;
        } while (changed);
        return current;
    }

    // 1) PLEGADO: 10B + 11B → 101B, 7O * 1O → 7O, ADFX - 0X → ADFX
    private List<TACInstruction> foldConstants(List<TACInstruction> program) {
        List<TACInstruction> out = new ArrayList<>();
        for (TACInstruction q : program) {
            if (esOperacion(q) && esLiteral(q.arg1) && esLiteral(q.arg2)) {
                char sufA = sufijo(q.arg1);
                char sufB = sufijo(q.arg2);
                if (sufA == sufB) {
                    int va = parseLit(q.arg1);
                    int vb = parseLit(q.arg2);
                    Integer r = null;
                    switch (q.op) {
                        case "+": r = va + vb; break;
                        case "-": r = va - vb; break;
                        case "*": r = va * vb; break;
                        case "/": if (vb != 0) r = va / vb; break;
                    }
                    if (r != null) {
                        out.add(new TACInstruction("", formatLit(r, sufA), null, q.result));
                        continue;
                    }
                }
            }
            out.add(q);
        }
        return out;
    }

    // 2) PROPAGACIÓN: si "a = 101B", reemplaza usos de a por 101B
    private List<TACInstruction> propagateConstants(List<TACInstruction> program) {
        List<TACInstruction> out = new ArrayList<>();
        Map<String, String> constMap = new HashMap<>();
        for (TACInstruction q : program) {
            String a1 = constMap.getOrDefault(q.arg1, q.arg1);
            String a2 = constMap.getOrDefault(q.arg2, q.arg2);
            if (!esOperacion(q) && esLiteral(a1)) {
                constMap.put(q.result, a1);
            } else {
                constMap.remove(q.result);
            }
            out.add(new TACInstruction(q.op, a1, a2, q.result));
        }
        return out;
    }

    // 3) CÓDIGO MUERTO: elimina temporales _tN nunca leídos
    private List<TACInstruction> eliminateDeadCode(List<TACInstruction> program) {
        Set<String> used = new HashSet<>();
        for (TACInstruction q : program) {
            if (esVariable(q.arg1)) used.add(q.arg1);
            if (esVariable(q.arg2)) used.add(q.arg2);
        }
        List<TACInstruction> out = new ArrayList<>();
        for (TACInstruction q : program) {
            if (q.result == null) { out.add(q); continue; }
            if (!q.result.startsWith("_t")) { out.add(q); continue; }  // variables reales se conservan
            if (used.contains(q.result)) { out.add(q); }
            // temporal no usado → se descarta
        }
        return out;
    }

    // ---------------- helpers ----------------

    private boolean esOperacion(TACInstruction q) {
        return q.op != null && !q.op.isEmpty();
    }

    private boolean esLiteral(String s) {
        return s != null && s.matches("\\d+[BOX]");
    }

    private boolean esVariable(String s) {
        return s != null && s.matches("[a-z]+|_t\\d+");
    }

    private char sufijo(String s) {
        return s == null ? '?' : s.charAt(s.length() - 1);
    }

    private int parseLit(String s) {
        char suf = sufijo(s);
        int base = suf == 'B' ? 2 : suf == 'O' ? 8 : 16;
        return Integer.parseInt(s.substring(0, s.length() - 1), base);
    }

    private String formatLit(int v, char suf) {
        int base = suf == 'B' ? 2 : suf == 'O' ? 8 : 16;
        return Integer.toString(v, base).toUpperCase() + suf;
    }
}