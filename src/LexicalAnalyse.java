import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LexicalAnalyse {

    public String code;
    private int index = 0;
    private ArrayList<Outer> Outers = new ArrayList<>();

    private int start = 0;
    private int linenum = 1;

    public LexicalAnalyse(String code){
        this.code = code;
    }

    ArrayList<Outer> Outers(){

        while (!end()){
            //System.out.println(index);
            start = index;
            analyse();
        }

        return Outers;
    }



    private char getChar(){
        index++;
        return code.charAt(index - 1);
    }

    private char top(){
        if (end()){
            return '\0';
        }
        return code.charAt(index);
    }

    private char sectop(){
        if (index + 1 >= code.length()){
            return '\0';
        }
        return code.charAt(index + 1);
    }

    private boolean check(char s){
        if (end()){
            return false;
        }
        else if (code.charAt(index) == s){
            index++;
            return true;
        }
        else {
            return false;
        }
    }


    private boolean end(){
        return index >= code.length();
    }

    public void analyse(){
        Character c = getChar();


            //System.out.println(c);
            if(c == '+'){
                addOuter(Word.PLUS);
            }
            else if(c == '*'){
                addOuter(Word.MULT);
            }
            else if(c == '%'){
                addOuter(Word.MOD);
            }
            else if(c == '-'){
                addOuter(Word.MINU);
            }
            else if(c == '('){
                addOuter(Word.LPARENT);
            }
            else if(c == ')'){
                addOuter(Word.RPARENT);
            }
            else if(c == '{'){
                addOuter(Word.LBRACE);
            }
            else if(c == '}'){
                addOuter(Word.RBRACE);
            }
            else if(c == '['){
                addOuter(Word.LBRACK);
            }
            else if(c == ']'){
                addOuter(Word.RBRACK);
            }
            else if(c == ';'){
                addOuter(Word.SEMICN);
            }
            else if(c == ','){
                addOuter(Word.COMMA);
            }
            else if(c == '|'){
                if (check('|')){
                    addOuter(Word.OR);
                }
            }
            else if(c == '&'){
                if (check('&')){
                    addOuter(Word.AND);
                }
            }
            else if(c == '='){
                if (check('=')){
                    addOuter(Word.EQL);
                }
                else {
                    addOuter(Word.ASSIGN);
                }
            }
            else if(c == '>'){
                if (check('=')){
                    addOuter(Word.GEQ);
                }
                else {
                    addOuter(Word.GRE);
                }
            }
            else if(c == '<'){
                if (check('=')){
                    addOuter(Word.LEQ);
                }
                else {
                    addOuter(Word.LSS);
                }
            }
            else if(c == '!'){
                if (check('=')){
                    addOuter(Word.NEQ);
                }
                else {
                    addOuter(Word.NOT);
                }
            }
            else if(c == '"'){
                analyseString();
            }
            else if(c == '/'){
                if (check('/')){
                    while (top() != '\n' && !end()){
                        getChar();
                    }
                }
                else if (check('*')){
                    while (!end()){
                        if (top() == '*' && sectop() == '/'){
                            getChar();
                            getChar();
                        }
                        getChar();
                    }
                }
                else {
                    addOuter(Word.DIV);
                }
            }
            else if (c == ' ' || c == '\r' || c == '\t'){

            }
            else if(isDigit(c)){
                analyseNum();
            }
            else if(isLetter(c)){
                analyseId();
            }
            else{

            }



    }


    private void analyseId(){
        while (isAlphaNumeric(top())){
            getChar();
        }

        String str = code.substring(start, index);
        Word word = new Words().getType(str);
        if (word == null){
            word = Word.IDENFR;
        }
        addOuter(word);
    }

    private void analyseNum(){
        while (isDigit(top())){
            getChar();
        }
        addOuter(Word.INTCON,Integer.parseInt(code.substring(start, index)));
    }

    private void analyseString(){
        StringBuilder stringBuilder = new StringBuilder();
        while (top() != '"' && !end()){
            if (top() == '\\'){
                if (sectop() == 'n'){
                    stringBuilder.append('\n');
                    getChar();
                    getChar();
                }else {
                    return;
                }
            }
            else if (top() == '%' && sectop() == 'd'){
                stringBuilder.append(top());
                stringBuilder.append(sectop());
                getChar();
                getChar();
            }
            else if (top() == 32 || top() == 33 || (top() <= 126 && top() >= 40)) {
                stringBuilder.append(top());
                getChar();
            }
            else {
                return;
            }
        }

        if (end()){
            return;
        }

        getChar();

        addOuter(Word.STRCON, stringBuilder.toString());

    }

    public static boolean isDigit(Character s){
        if(s.equals('0')
                || s.equals('1')
                || s.equals('2')
                || s.equals('3')
                || s.equals('4')
                || s.equals('5')
                || s.equals('6')
                || s.equals('7')
                || s.equals('8')
                || s.equals('9')
        ){
            return true;
        }
        return false;
    }

    public static boolean isLetter(Character c){
        if(c.equals('a')
                || c.equals('b')
                || c.equals('c')
                || c.equals('d')
                || c.equals('e')
                || c.equals('f')
                || c.equals('g')
                || c.equals('h')
                || c.equals('i')
                || c.equals('j')
                || c.equals('k')
                || c.equals('l')
                || c.equals('m')
                || c.equals('n')
                || c.equals('o')
                || c.equals('p')
                || c.equals('q')
                || c.equals('r')
                || c.equals('s')
                || c.equals('t')
                || c.equals('u')
                || c.equals('v')
                || c.equals('w')
                || c.equals('x')
                || c.equals('y')
                || c.equals('z')
                || c.equals('A')
                || c.equals('B')
                || c.equals('C')
                || c.equals('D')
                || c.equals('E')
                || c.equals('F')
                || c.equals('G')
                || c.equals('H')
                || c.equals('I')
                || c.equals('J')
                || c.equals('K')
                || c.equals('L')
                || c.equals('M')
                || c.equals('N')
                || c.equals('O')
                || c.equals('P')
                || c.equals('Q')
                || c.equals('R')
                || c.equals('S')
                || c.equals('T')
                || c.equals('U')
                || c.equals('V')
                || c.equals('W')
                || c.equals('X')
                || c.equals('Y')
                || c.equals('Z')
                || c.equals('_')

        ){
            return true;
        }
        return false;
    }



    private boolean isAlphaNumeric(char c) {
        return isLetter(c) || isDigit(c);
    }

    public void printOuters(FileWriter writer) throws IOException{
        for (Outer Outer : Outers) {
            writer.write(Outer.toString() + "\n");
        }

        writer.flush();
        writer.close();
    }

    public ArrayList<Outer> getOuters() {
        return Outers;
    }

    private void addOuter(Word word){
        addOuter(word, null);
    }

    private void addOuter(Word word, Object o){
        String str = code.substring(start, index);
        Outers.add(new Outer(word, str, o, linenum));
    }



}
