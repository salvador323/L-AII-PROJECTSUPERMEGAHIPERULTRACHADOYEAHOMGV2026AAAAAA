/* __________________________________________________________________________________
    Node.java
    Diseñado por:
        Franco Monroy Meredith Alejandra
        Muñoz Montalvo Ricardo Antonio
        Taboada Ramirez Salvador

    Clase base abstracta de todos los nodos del AST
__________________________________________________________________________________
*/
public abstract class Node {
    public abstract void accept(PatronVisitor visitor);
}
