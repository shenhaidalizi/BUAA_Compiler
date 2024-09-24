import java.util.HashMap;

public class Words {
    private HashMap<String, Word> keyWords;

    public Words() {
        keyWords = new HashMap<>();
        keyWords.put("main", Word.MAINTK);
        keyWords.put("const", Word.CONSTTK);
        keyWords.put("int", Word.INTTK);
        keyWords.put("break", Word.BREAKTK);
        keyWords.put("continue", Word.CONTINUETK);
        keyWords.put("if", Word.IFTK);
        keyWords.put("else", Word.ELSETK);
        keyWords.put("!", Word.NOT);
        keyWords.put("&&", Word.AND);
        keyWords.put("||", Word.OR);
        keyWords.put("while", Word.WHILETK);
        keyWords.put("getint", Word.GETINTTK);
        keyWords.put("printf", Word.PRINTFTK);
        keyWords.put("return", Word.RETURNTK);
        keyWords.put("+", Word.PLUS);
        keyWords.put("-", Word.MINU);
        keyWords.put("void", Word.VOIDTK);
        keyWords.put("*", Word.MULT);
        keyWords.put("/", Word.DIV);
        keyWords.put("%", Word.MOD);
        keyWords.put("<", Word.LSS);
        keyWords.put("<=", Word.LEQ);
        keyWords.put(">", Word.GRE);
        keyWords.put(">=", Word.GEQ);
        keyWords.put("==", Word.EQL);
        keyWords.put("!=", Word.NEQ);
        keyWords.put("=", Word.ASSIGN);
        keyWords.put(";", Word.SEMICN);
        keyWords.put(",", Word.COMMA);
        keyWords.put("(", Word.LPARENT);
        keyWords.put(")", Word.RPARENT);
        keyWords.put("[", Word.LBRACK);
        keyWords.put("]", Word.RBRACK);
        keyWords.put("{", Word.LBRACE);
        keyWords.put("}", Word.RBRACE);
    }

    public Word getType(String ident) {
        return keyWords.get(ident);
    }


}