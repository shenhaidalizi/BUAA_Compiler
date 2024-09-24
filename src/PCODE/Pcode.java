package PCODE;

public class Pcode {
    private ASP code;
    private Object a1;
    private Object a2;

    public Pcode(ASP code){
        this.code = code;
    }

    public Pcode(ASP code, Object a1){
        this.code = code;
        this.a1 = a1;
    }

    public Pcode(ASP code, Object a1, Object a2){
        this.code = code;
        this.a1 = a1;
        this.a2 = a2;
    }

    public void setA1(Object a1){
        this.a1 = a1;
    }

    public void setA2(Object a2){
        this.a2 = a2;
    }

    public ASP getCode(){
        return code;
    }

    public Object getA1(){
        return a1;
    }

    public Object getA2(){
        return a2;
    }

    @Override
    public String toString(){
        if (code.equals(ASP.TAG)){
            return a1.toString() + ": ";
        }
        if (code.equals(ASP.FUC)){
            return "FUNC @" + a1.toString() + ":";

        }
        if (code.equals(ASP.CALL)){
            return "$" + a1.toString();
        }
        if (code.equals(ASP.PRINTF)){
            return code + " " + a1;
        }
        String test1;
        String test2;

        boolean is_NULL1 = (a1 != null);
        boolean is_NULL2 = (a2 != null);

        if (is_NULL1){
            test1 = a1.toString();
        }
        else {
            test1 = "";
        }

        if (is_NULL2){
            test2 = ", " + a2.toString();
        }
        else {
            test2 = "";
        }


        return code + " " + test1 + test2;


    }

}
