/*  
__________________________________________________________________________________
    Token.java
    Diseñado por:
        Franco Monroy Meredith Alejandra
        Sandoval Cuevas Pedro

    Clase que contiene la estructura del token que generará el analizador léxico.
__________________________________________________________________________________
*/

public class Token {
    private final String atributo;
    private final String lexema;

    public Token(String atributo, String lexema){
        this.atributo = atributo;
        this.lexema = lexema;
    }

    public String getAtributo(){
        return this.atributo;
    }

    public String getLexema(){
        return this.lexema;
    }
}
