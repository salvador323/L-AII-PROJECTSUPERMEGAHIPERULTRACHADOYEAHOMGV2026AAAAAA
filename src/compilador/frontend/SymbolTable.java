package compilador.frontend;

/* __________________________________________________________________________________
    SymbolTable.java
    Diseñado por:
        Franco Monroy Meredith Alejandra
        Muñoz Montalvo Ricardo Antonio
        Taboada Ramirez Salvador

    Clase que contiene la tabla de símbolos.
__________________________________________________________________________________
*/
import java.util.HashMap;
import java.util.Map;

    class Symbol{
        String name, type;
        Object value;
        Boolean initialized;

        public Symbol(String name, String type, Object value, boolean initialized){
            this.name = name;
            this.type = type;
            this.value = value;
            this.initialized = initialized;
        }
    }

    public class SymbolTable{
        Map<String, Symbol> table = new HashMap<>();

        //Método para agregar una variable, devuelve false si ya estaba declarada 
        public Boolean addSymbol(String name, String type){
            if(!table.containsKey(name)){
                table.put(name, new Symbol(name, type, null, false));
                return true;
            }else{
                return false;
            }
        }
        
        //Método que devuelve symbol o null si no existe (consulta)
        public Symbol lookup(String name){
            return table.get(name);
        }
    }
