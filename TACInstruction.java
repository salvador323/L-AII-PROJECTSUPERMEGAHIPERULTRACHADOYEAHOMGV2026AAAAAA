public class TACInstruction {
    public String op, arg1, arg2, result;

    public TACInstruction(String op, String arg1, String arg2, String result) {
        this.op = op;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.result = result;
    }
    @Override
    public String toString() {
        return arg2 == null && op.isEmpty() ?
               result + " = " + arg1 :
               result + " = " + arg1 + " " + op + " " + arg2;
    }
}