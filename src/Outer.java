public class Outer {
    public final Word type;
    public final String value;
    public final Object object;
    public final int ln;

    public Outer(Word word, String value, Object object, int ln){
        this.type = word;
        this.ln = ln;
        this.value = value;
        this.object = object;
    }

    public Word getType() {
        return type;
    }

    public int getLn() {
        return ln;
    }

    public Object getObject() {
        return object;
    }

    public String getValue() {
        return value;
    }



    @Override
    public String toString(){
        return this.type + " " + this.value;
    }
}

