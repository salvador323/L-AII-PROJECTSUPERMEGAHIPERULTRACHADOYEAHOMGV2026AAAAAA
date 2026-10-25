/* __________________________________________________________________________________
    BinOp.java
    Diseñado por:
        Franco Monroy Meredith Alejandra
        Muñoz Montalvo Ricardo Antonio
        Taboada Ramirez Salvador

    Clase que contiene el nodo BinOp que almacena la estructura correcta de la operacion
__________________________________________________________________________________
*/
public class BinOp extends Node {
    public String operador; // se necesita el operador de la expresion
    public Node izq; // valor de la izquierda
    public Node der; // valor de la derecha

    // constructor para recibir y guardar los datos
    public BinOp(String operador, Node izq, Node der) {
        this.operador = operador;
        this.izq = izq;
        this.der = der;
    }
}