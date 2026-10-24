/* __________________________________________________________________________________
    Lexer.java
    Diseñado por:
        Franco Monroy Meredith Alejandra
        Sandoval Cuevas Pedro

    Clase que contiene toda la lógica del analizador léxico.
__________________________________________________________________________________
*/

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private List<String> tablaSimbolos = new ArrayList<>();
    private List<String> palabrasReservadas = new ArrayList<>();
    
    private int contaLine = 0; // guarda el numero del renglon actual
    private int contcaracter = 0; // guarda la posicion exacta de la letra
    
    private List<String> lineas = new ArrayList<>(); // guarda el texto del programa en renglones
    
    private String[] reservadas = {
        "programa", "binario", "octal", "hexa", "leer", "escribir", "finprograma"
    };
    
    public void lexer(List<String> lineas){
        this.lineas=lineas;
    }
    
    // metodo para mandar generar un solo token cada que lo requiera el parser
    // la modificacion principal que hice fue dividir los if's para que se evaluaran de forma independiente y no se crearan bucles infinitos al leer los caracteres
    public Token getToken(){
        
        String lexemaLeido = "";

        while (contaLine < lineas.size()){
            String linea = lineas.get(contaLine); 
            
            // Fin de línea?
            if(contcaracter >= linea.length()){
                // si es asi saltamos ala otra linea y reiniamos el contar caracter.
                contaLine ++; 
                contcaracter = 0; 
                // no es vacio el lexema?
                if(!lexemaLeido.isEmpty()){
                    //si es cierto
                    //clasificamos el lexema.
                    return clasificarToken(lexemaLeido, contaLine + 1); 
                }
                continue; 
            }
            
            char c = linea.charAt(contcaracter);
            
            // Espacios en blanco?
            if (Character.isWhitespace(c)) {
                contcaracter++; 
                if(!lexemaLeido.isEmpty()){
                    return clasificarToken(lexemaLeido, contaLine + 1); 
                }
                continue; 
            }

            // Caracteres simples?
            if (c == ';' || c == '=' || c == '-' || c == '+' || c == '*' || c == '/' || c == ',' || c == '(' || c == ')') {
                if(!lexemaLeido.isEmpty()){
                    // Si ya veníamos acumulando un lexema anterior , devolvemos primero el lexema.
                    // Sin avanzar el puntero, para que en la siguiente vuelta lea el caracter simple
                    return clasificarToken(lexemaLeido, contaLine + 1); 
                }
                contcaracter++; 
                return clasificarToken(Character.toString(c)    , contaLine + 1); 
            }

            // Acumulación de palabras / literales continuos
            while(contcaracter < linea.length()){
                char actual = linea.charAt(contcaracter);
                
                // Si encontramos un delimitador (espacio o caracter simple), detenemos la acumulación
                if(Character.isWhitespace(actual) || actual == ';' || actual == '=' || actual == '-' || actual == '+' || actual == '*' || actual == '/' || actual == ',' || actual == '(' || actual == ')'){
                    break; 
                }
                lexemaLeido += actual; 
                contcaracter++; 
            }

            return clasificarToken(lexemaLeido, contaLine + 1); 
        }
        
        return new Token ("$","EOF");
    }

    
    private Token clasificarToken(String lexema, int numeroLinea) {
        
        if (esIdentificador(lexema)) {
            if (esReservada(lexema)) {
                return agregarToken("Reservada", lexema);
            } else {
               return agregarToken("Identificador", lexema);
            }
        } else if (esBin(lexema)) {
            return agregarToken("Binario", lexema);
        } else if (esOct(lexema)) {
            return agregarToken("Octal", lexema);
        } else if (esHexa(lexema)) {
            return agregarToken("Hexadecimal", lexema);
        } else if (esCterSimple(lexema)) {
            return agregarToken("Caracter Simple", lexema);
        } else {
            System.err.println("Error Léxico: Lexema no reconocido '" + lexema + "' en la línea " + numeroLinea);
            return new Token("ERROR",lexema); // agregue esta linea para que no diera nullpointerexception
        }
    }
    
    private Token agregarToken(String tipo, String lexema) { // este metodo deberia llamarse generaToken
        Token tokennuevo = new Token("","");
        switch (tipo) {
            case "Reservada":
                switch (lexema) {
                    case "programa": tokennuevo =(new Token("300", lexema)); break;
                    case "binario": tokennuevo =(new Token("301", lexema)); break;
                    case "octal": tokennuevo =(new Token("302", lexema)); break;
                    case "hexa": tokennuevo =(new Token("303", lexema)); break;
                    case "leer": tokennuevo =(new Token("304", lexema)); break;
                    case "escribir": tokennuevo =(new Token("305", lexema)); break;
                    case "finprograma": tokennuevo =(new Token("306", lexema)); break;
                }
                break;
            case "Identificador":
                tokennuevo =(new Token("295", lexema));
                if (!tablaSimbolos.contains(lexema)) {
                    tablaSimbolos.add(lexema);
                }
                break;
            case "Binario":
                tokennuevo =(new Token("296", lexema));
                break;
            case "Octal":
                tokennuevo =(new Token("297", lexema));
                break;
            case "Hexadecimal":
                tokennuevo =(new Token("298", lexema));
                break;
            case "Caracter Simple":
                tokennuevo =(new Token(Integer.toString((int) lexema.charAt(0)), lexema));
                break;
        }
        return tokennuevo;
    }

    // Autómata
    public boolean esReservada(String lexema) {
        for (String palabra : reservadas) {
            if (lexema.equals(palabra)) {
                if (!palabrasReservadas.contains(lexema)) {
                    palabrasReservadas.add(lexema);
                }
                return true;
            }
        }
        return false;
    }

     public boolean esIdentificador(String lexema){
        String estadoActual = "q0";

        for(char entrada : lexema.toCharArray()){
            switch(estadoActual){
                case "q0":
                    if(entrada >='a' && entrada <='z')
                        estadoActual = "q1";
                    else 
                        return false;
                    break;
                case "q1":
                    if(entrada >='a' && entrada <='z')
                        estadoActual = "q1";
                    else
                        return false;
                    break;
            }
        }
        return estadoActual.equals("q1"); // si el estado final es q1, entonces es un identificador
    }

    public static boolean esBin(String lexema){
        String estadoActual = "q0";

        for(char entrada : lexema.toCharArray()){
            switch (estadoActual){
                case "q0":
                    if(entrada == '0'||entrada =='1')
                        estadoActual = "q2";
                    else
                        return false;

                    break;

                case "q2" :
                    if(entrada == '0' || entrada =='1')
                        estadoActual = "q2";
                    else if(entrada == 'B')
                        estadoActual = "q3";
                    else
                        return false;

                    break;
            }  
        }

        return estadoActual.equals("q3"); // si el estado final es q3, entonces es un binario
    }

    public static boolean esOct(String lexema){
        String estadoActual = "q0";
        for(char entrada : lexema.toCharArray()){
            switch (estadoActual){
                case "q0":
                    if(entrada >='0' && entrada <='7')
                        estadoActual = "q4";
                    else
                        return false;

                    break;

                case "q4":
                    if(entrada >= '0' && entrada <= '7')
                        estadoActual = "q4";
                    else if(entrada == 'O')
                        estadoActual = "q5";
                    else
                        return false;

                    break;
            }     
        }
        return estadoActual.equals("q5"); // si el estado final es q5, entonces es un octal

    }

    public static boolean esHexa(String lexema){
        String estadoActual = "q0";
        for(char entrada : lexema.toCharArray()){
            switch (estadoActual){
                case "q0":
                    if((entrada >='0'&& entrada <='9')||(entrada >='A'&& entrada <='F'))
                        estadoActual = "q6";
                    else
                        return false;

                    break;

                case "q6":
                    if((entrada >='0'&& entrada <='9')||(entrada >='A'&& entrada <='F'))
                        estadoActual = "q6";
                    else if(entrada == 'X')
                        estadoActual = "q7";
                    else
                        return false;

                    break;
            }
        }  
        return estadoActual.equals("q7"); // si el estado final es q7, entonces es un hexadecimal
    }

    public boolean esCterSimple(String lexema){
        String estadoActual = "q0";
        for(char entrada : lexema.toCharArray()){
            switch(estadoActual){
                case "q0":
                    if(entrada == ';' || 
                        entrada == '=' ||
                        entrada == '-' ||
                        entrada == '+' ||
                        entrada == '*' ||
                        entrada == '/' ||
                        entrada == ',' ||
                        entrada == '(' ||
                        entrada == ')')
                    
                    estadoActual = "q8";
                else
                    return false;

                break;
            }
        }
        return estadoActual.equals("q8"); // si el estado final es q8, entonces es un caracter simple

    }
    
    //metodo para obtener la tabla de simbolos.
    public List<String> getTablaSimbolos(){
        return this.tablaSimbolos;
    }
}