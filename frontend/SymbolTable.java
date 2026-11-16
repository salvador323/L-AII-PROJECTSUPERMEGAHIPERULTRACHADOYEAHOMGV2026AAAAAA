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
//---------------------------------------------------------
    /*
    1 Inicio, Patrón Visitor
    */
    //Tabla de simbolos
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

    class SymbolTable{
        Map<String, Symbol> table = new HashMap<>();

        //Metodo para agregar
        public Boolean addSymbol(String name, String type){
            if(!table.containsKey(name)){
                table.put(name, new Symbol(name, type, null, false));
                return true;
            }else{
                return false;
            }
        }
        

        //Metodo que devuelve symbol o null (consulta)
        public Symbol lookup(String name){
            return table.get(name);
        }

        //Método para guardar el valor, vere si quitarlo o no 
        /*public Boolean saveSymbol(String name, Object value){
            Symbol sym = lookup(name);
            if(sym == null){
                return false;
            }else{
                sym.value = value;
                sym.initialized = true;
                return true;
            }
        }*/
       /* 

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
                System.out.println("Bien " + name + " = " + value);             
            }else{
                System.out.println("Mal, error semantico " + 
                                    "(tipo incompatible), la variable "+ name + "es de tipo " + sym.type+ " pero recibio " + value);
            }
        }

        //no debería de estar esto
        //Metodo para validar errores
        /*public void usesSymbol(String name){
            Symbol sym = lookup(name);
            if(sym == null){
                System.out.println("Error semantico, la variable " + name + " no ha sido declarada");
            } else if(!sym.initialized){
                System.out.println("Error semantico, la variable " + name + " se esta usando sin haber sido inicializada");
            }
        }*/
    }