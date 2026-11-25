package compilador.lexico;

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
    private final int getLinea;

    public Token(String atributo, String lexema){
        this(atributo, lexema, 0);
    }

    public Token(String atributo, String lexema, int getLinea){
        this.atributo = atributo;
        this.lexema = lexema;
        this.getLinea = getLinea;
    }

    public String getAtributo(){
        return this.atributo;
    }

    public String getLexema(){
        return this.lexema;
    }

    public int getLinea(){
        return this.getLinea;
    }
}
