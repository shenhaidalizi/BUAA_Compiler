package PCODE;

public class TagMaker {
    private int counter = 0;

    public String AddTag(String tag){
        counter++;
        return "label_" + tag + "_" + counter;
    }
}
