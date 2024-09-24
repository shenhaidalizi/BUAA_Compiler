import PCODE.ASP;
import PCODE.Pcode;
import PCODE.PcodeMaker;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GrammarAnalyse {
    private ArrayList<Outer> Outers;
    private int index ;
    private ArrayList<String> grammars;
    private Outer Outer;
    private PcodeMaker pcodeMaker;

    private Word nowWord;

    private ArrayList<Error> errors = new ArrayList<>();
    private boolean backto = false;
    private int codeblock = -1;//area
    private int is_while = 0;


    private ArrayList<Pcode> pcodes = new ArrayList<>();
    private int blocknum = -1;//areaid

    public ArrayList<Pcode> getPcodes(){
        return pcodes;
    }


    public GrammarAnalyse(ArrayList<Outer> Outers) {
        this.Outers = Outers;
        index = 0;
        grammars = new ArrayList<>();
        //pcodeMaker = new PcodeMaker();
        compUnit();
    }



    private Outer getNowOuter() {
        return Outers.get(index);
    }

    private Outer getNextOuter() {
        return Outers.get(index + 1);
    }

    private Outer getNextTwoOuter() {
        return Outers.get(index + 2);
    }

    private void addOuter() {//getWord()
        Outer = Outers.get(index);
        grammars.add(Outer.toString());
        index++;
    }

    private void addWord(){
        Outer = Outers.get(index);
        index++;
    }



    private void compUnit() {
        //System.out.println(Outers.size());
        Outer now = getNowOuter();

        while (now.type == Word.CONSTTK || (now.type == Word.INTTK && getNextOuter().getType() == Word.IDENFR) && !(getNextTwoOuter().getType() == Word.LPARENT)) {
            decl();
            now = getNowOuter();
        }

        while (now.getType()== Word.VOIDTK || (now.getType()== Word.INTTK && getNextOuter().getType() == Word.IDENFR && getNextTwoOuter().getType() == Word.LPARENT)) {
            funcDef();
            now = getNowOuter();
        }

        boolean is_mainFuncDef = true;
        is_mainFuncDef = now.getType()== Word.INTTK && getNextOuter().getType()== Word.MAINTK;
        if (is_mainFuncDef) {
            mainFuncDef();
        }
        else {
            error();
        }
        grammars.add("<CompUnit>");
    }

    private void decl() {
        Outer now = getNowOuter();
        boolean is_CONSTTK = true;
        boolean is_INTTK = true;
        is_CONSTTK = now.getType()== Word.CONSTTK;
        is_INTTK = now.getType()== Word.INTTK;
        if (is_CONSTTK) {
            constDecl();
        } else if (is_INTTK) {
            varDecl();
        }
    }

    private void funcDef() {
        funcType();

        Outer outer = getNowOuter();

        addOuter();
        Pcode pcode = new Pcode(ASP.FUC, outer.getValue());
        pcodes.add(pcode);

        addOuter();
        Outer now = getNowOuter();
        boolean is_RPARENT = true;
        ArrayList<Integer> parms = new ArrayList<>();
        is_RPARENT = now.getType()== Word.RPARENT;
        if (is_RPARENT == false) {
            parms = funcFParams();
        }
        addOuter();
        block();
        pcode.setA2(parms.size());
        pcodes.add(new Pcode(ASP.RETURN, 0));
        pcodes.add(new Pcode(ASP.EDFC));
        grammars.add("<FuncDef>");
    }

    private ArrayList<Integer> funcFParams() {
        ArrayList<Integer> pdim = new ArrayList<>();
        int dim = funcFParam();
        pdim.add(dim);

        Outer now = getNowOuter();

        boolean is_COMMA = true;
        is_COMMA = now.getType()==Word.COMMA;
        while (is_COMMA) {
            addOuter();
            dim = funcFParam();
            pdim.add(dim);
            now = getNowOuter();
            is_COMMA = now.getType()== Word.COMMA;
        }
        grammars.add("<FuncFParams>");
        return pdim;
    }

    private int funcFParam() {
        int dim = 0;

        boolean is_LBRACK = true;

        addOuter();
        Outer outer = getNowOuter();

        addOuter();
        Outer now = getNowOuter();

        is_LBRACK = now.getType()==Word.LBRACK;
        if (is_LBRACK) {
            dim++;
            addOuter();
            addOuter();
            now = getNowOuter();
            is_LBRACK = now.getType()== Word.LBRACK;
            if (is_LBRACK) {
                dim++;
                addOuter();
                constExp();
                addOuter();
            }
        }
        pcodes.add(new Pcode(ASP.PARA, outer.getValue() ,dim));
        grammars.add("<FuncFParam>");
        return dim;
    }

    private void constExp() {
        addExp();
        grammars.add("<ConstExp>");
    }

    private int addExp() {
        int dim = mulExp();
        boolean is_PLUS = true;
        boolean is_MIN = true;

        Outer now = getNowOuter();

        is_MIN = now.getType()== Word.MINU;
        is_PLUS = now.getType()== Word.PLUS;

        while (is_MIN || is_PLUS) {
            Outer outer = now;
            grammars.add("<AddExp>");
            addOuter();
            dim = mulExp();

            if (outer.getType()== Word.PLUS){
                pcodes.add(new Pcode(ASP.PLUS));
            }
            else {
                pcodes.add(new Pcode(ASP.MIN));
            }


            now = getNowOuter();
            is_MIN = now.getType()== Word.MINU;
            is_PLUS = now.getType()==Word.PLUS;
        }

        grammars.add("<AddExp>");
        return dim;
    }

    private int mulExp() {
        int dim = unaryExp();

        boolean is_MUL = true;
        boolean is_DIV = true;
        boolean is_MOD = true;


        Outer now = getNowOuter();

        is_DIV = now.getType()== Word.DIV;
        is_MUL = now.getType()== Word.MULT;
        is_MOD = now.getType()== Word.MOD;
        while (is_DIV || is_MOD || is_MUL) {
            Outer outer = now;

            grammars.add("<MulExp>");
            addOuter();

            dim = unaryExp();

            if (outer.getType()== Word.MULT){
                pcodes.add(new Pcode(ASP.CHENG));
            }
            else if (outer.getType()==Word.DIV){
                pcodes.add(new Pcode(ASP.CHU));
            }
            else {
                pcodes.add(new Pcode(ASP.MO));
            }

            now = getNowOuter();
            is_DIV = now.getType()==Word.DIV;
            is_MUL = now.getType()==Word.MULT;
            is_MOD = now.getType()== Word.MOD;
        }

        grammars.add("<MulExp>");
        return dim;
    }

    private int unaryExp() {
        int dim = 0;
        boolean is_IDENFR = true;
        boolean is_LPARENT = true;
        boolean is_RPARENT = true;
        Outer now  = getNowOuter();

        is_IDENFR = now.getType()== Word.IDENFR;
        if (is_IDENFR) {
            is_LPARENT = getNextOuter().getType()== Word.LPARENT;
            if (is_LPARENT) {
                addOuter();
                is_LPARENT = getNowOuter().getType()== Word.LPARENT;
                if (is_LPARENT) {
                    addOuter();
                    is_RPARENT = getNowOuter().getType()== Word.RPARENT;
                    if (is_RPARENT) {
                        addOuter();
                    } else {
                        funcRParams();
                        addOuter();
                    }
                    pcodes.add(new Pcode(ASP.CALL, now.getValue()));
                    //查找函数表，如果为void dim = -1；

                }
            } else {
                dim = primaryExp();
            }
        } else if (now.getType()== Word.LPARENT || now.getType()== Word.INTCON) {
            dim = primaryExp();
        } else {
            int ret = unaryOp();
            dim = unaryExp();
            //1 for + , 0 for - , -1 for !

            if (ret == 0){
                pcodes.add(new Pcode(ASP.NEG));
            }
            else if (ret == -1){
                pcodes.add(new Pcode(ASP.FEI));
            }

        }
        grammars.add("<UnaryExp>");
        return dim;
    }

    private int unaryOp() {
        Outer outer = getNowOuter();
        int ret;
        if (outer.getType()==Word.PLUS){
            ret = 1;
        }
        else if (outer.getType()== Word.MINU){
            ret = 0;
        }
        else {
            ret = -1;
        }
        addOuter();
        grammars.add("<UnaryOp>");
        return ret;
    }

    private int primaryExp() {
        int dim = 0;

        boolean is_LPARENT = true;
        boolean is_IDENFR = true;

        is_LPARENT = getNowOuter().getType()== Word.LPARENT;
        is_IDENFR = getNowOuter().getType() == Word.IDENFR;
        if (is_LPARENT) {
            addOuter();
            exp();
            addOuter();
        } else if(is_IDENFR) {
            Outer outer = getNowOuter();
            dim = lVal();
            if (dim == 0){
                pcodes.add(new Pcode(ASP.VALUE, outer.getValue(), dim));
            }
            else {
                pcodes.add(new Pcode(ASP.ADDRESS, outer.getValue(), dim));
            }
        } else {
            number();
        }
        grammars.add("<PrimaryExp>");
        return dim;
    }

    private int exp() {
        int dim = addExp();
        grammars.add("<Exp>");
        return dim;
    }

    private int lVal() {
        int dim = 0;
        Outer outer = null;


        boolean is_IDENFR = true;
        boolean is_LBRACK = true;
        boolean is_RBRACK = true;

        is_IDENFR = getNowOuter().getType() == Word.IDENFR;
        if (is_IDENFR) {
            outer = getNowOuter();
            pcodes.add(new Pcode(ASP.PS, outer.getValue()));
            addOuter();
            is_LBRACK = getNowOuter().getType() == Word.LBRACK;
            if (is_LBRACK) {
                dim++;
                addOuter();
                exp();
                is_RBRACK = getNowOuter().getType() == Word.RBRACK;
                if (is_RBRACK) {
                    addOuter();
                    is_LBRACK = getNowOuter().getType() == Word.LBRACK;
                    if (is_LBRACK) {
                        dim++;
                        addOuter();
                        exp();
                        is_RBRACK = getNowOuter().getType() == Word.RBRACK;
                        if (is_RBRACK) {
                            addOuter();
                        }
                    }
                }
            }
        }
        grammars.add("<LVal>");
        return 0;
    }

    private void funcRParams() {


        boolean is_COMMA = true;

        int dim = exp();
        pcodes.add(new Pcode(ASP.RPARA, dim));

        while (getNowOuter().getType() == Word.COMMA) {
            addOuter();
            dim = exp();
            pcodes.add(new Pcode(ASP.RPARA, dim));
        }

        grammars.add("<FuncRParams>");
    }

    private void block() {

        boolean is_LBRACE = true;
        boolean is_RBRACE = true;

        is_LBRACE = getNowOuter().getType() == Word.LBRACE;

        if (is_LBRACE) {
            addOuter();
            while (!(getNowOuter().getType() == Word.RBRACE)) {
                blockItem();
            }
            if (index < Outers.size()) {
                addOuter();
            } else {
                grammars.add(getNowOuter().toString());
            }
        }
        grammars.add("<Block>");
    }

    private void blockItem() {
        boolean is_INTTK = true;
        boolean is_CONSTTK = true;

        is_CONSTTK = getNowOuter().getType() == Word.CONSTTK;
        is_INTTK = getNowOuter().getType() == Word.INTTK;

        if (is_CONSTTK || is_INTTK) {
            decl();
        } else {
            stmt();
        }
    }

    private void stmt() {
        if(getNowOuter().getType() == Word.LBRACE){
            block();
        }
        else if(getNowOuter().getType() == Word.IFTK){
            addOuter();
            if (getNowOuter().getType() == Word.LPARENT) {
                addOuter();
                cond();
                if (getNowOuter().getType() == Word.RPARENT) {
                    addOuter();
                    stmt();
                    if (getNowOuter().getType() == Word.ELSETK) {
                        addOuter();
                        stmt();
                    }
                }
            }
        }
        else if(getNowOuter().getType() == Word.WHILETK){
            addOuter();
            if (getNowOuter().getType() == Word.LPARENT) {
                addOuter();
                cond();
                if (getNowOuter().getType() == Word.RPARENT) {
                    addOuter();
                    stmt();
                }
            }
        }
        else if(getNowOuter().getType() == Word.BREAKTK || getNowOuter().getType() == Word.CONTINUETK){
            addOuter();
            if (getNowOuter().getType() == Word.SEMICN) {
                addOuter();
            }
        }
        else if(getNowOuter().getType() == Word.RETURNTK){
            boolean tag = false;
            addOuter();
            if (getNowOuter().getType() == Word.SEMICN) {
                addOuter();
            } else {
                tag = true;
                exp();
                if (getNowOuter().getType() == Word.SEMICN) {
                    addOuter();
                }
            }
            if (tag){
                pcodes.add(new Pcode(ASP.RETURN, 1));
            }else {
                pcodes.add(new Pcode(ASP.RETURN, 0));
            }
        }
        else if(getNowOuter().getType() == Word.PRINTFTK){
            addOuter();
            Outer outer = null;
            int para = 0;
            if (getNowOuter().getType() == Word.LPARENT) {
                addOuter();
                if (getNowOuter().getType() == Word.STRCON) {
                    outer = getNowOuter();
                    addOuter();
                    while (getNowOuter().getType() == Word.COMMA) {
                        para++;
                        addOuter();
                        exp();
                    }
                    if (getNowOuter().getType() == Word.RPARENT) {
                        addOuter();
                        if (getNowOuter().getType() == Word.SEMICN) {
                            addOuter();
                        }
                    }
                }
            }
            pcodes.add(new Pcode(ASP.PRINTF, outer.getObject(), para));
        }
        else if(getNowOuter().getType() == Word.IDENFR){
            if (getNextOuter().getType() == Word.LPARENT){
                exp();
                if (getNowOuter().getType() == Word.SEMICN){
                    addOuter();
                }
            }
            else {
                int last_index = index;
                ArrayList<String> last_grammars = new ArrayList<>(grammars);
                ArrayList<Pcode> last_pcodes = new ArrayList<>(pcodes);
                Outer outer = getNowOuter();
                int dim = lVal();

                if (getNowOuter().getType() == Word.ASSIGN){
                    pcodes.add(new Pcode(ASP.ADDRESS, outer.getValue(), dim));
                    addOuter();
                    if (getNowOuter().getType() == Word.GETINTTK){
                        pcodes.add(new Pcode(ASP.GETINT));
                        addOuter();
                        if (getNowOuter().getType() == Word.LPARENT){
                            addOuter();
                            if (getNowOuter().getType() == Word.RPARENT){
                                addOuter();
                                if (getNowOuter().getType() == Word.SEMICN){
                                    addOuter();
                                }
                            }
                        }
                    }else {
                        exp();
                        if (getNowOuter().getType() == Word.SEMICN){
                            addOuter();
                        }
                    }

                    pcodes.add(new Pcode(ASP.PP, outer.getValue()));

                }else {
                    index = last_index;
                    grammars = new ArrayList<>(last_grammars);
                    pcodes = new ArrayList<>(pcodes);

                    exp();
                    if (getNowOuter().getType() == Word.SEMICN){
                        addOuter();
                    }

                }

            }
        }
        else {
            if (getNowOuter().getType() == Word.SEMICN) {
                addOuter();
            } else {
                exp();
                if (getNowOuter().getType() == Word.SEMICN) {
                    addOuter();
                }
            }
        }


        grammars.add("<Stmt>");
    }


   

    private void cond() {
        lOrExp();
        grammars.add("<Cond>");
    }

    private void lOrExp() {
        lAndExp();

        while (getNowOuter().getType() == Word.OR) {
            grammars.add("<LOrExp>");
            addOuter();
            lAndExp();
        }

        grammars.add("<LOrExp>");
    }

    private void lAndExp() {
        eqExp();

        while (getNowOuter().getType() == Word.AND) {
            grammars.add("<LAndExp>");
            addOuter();
            eqExp();
        }

        grammars.add("<LAndExp>");
    }

    private void eqExp() {
        relExp();

        while (getNowOuter().getType() == Word.EQL || getNowOuter().getType() == Word.NEQ) {
            grammars.add("<EqExp>");
            addOuter();
            relExp();
        }

        grammars.add("<EqExp>");
    }

    private void relExp() {
        addExp();

        while (getNowOuter().getType() == Word.GRE || getNowOuter().getType() == Word.LSS || getNowOuter().getType() == Word.GEQ || getNowOuter().getType() == Word.LEQ) {
            grammars.add("<RelExp>");
            addOuter();
            addExp();
        }

        grammars.add("<RelExp>");
    }

    private void funcType() {
        addOuter();
        grammars.add("<FuncType>");
    }

    private void constDecl() {
        addOuter();
        addOuter();
        constDef();
        boolean is_COMMA;
        Outer now = getNowOuter();
        is_COMMA = now.getType() == Word.COMMA;
        while (is_COMMA) {
            addOuter();
            constDef();
            now = getNowOuter();
            is_COMMA = now.getType() == Word.COMMA;
        }
        addOuter();
        grammars.add("<ConstDecl>");
    }

    private void constDef() {
        Outer outer = null;
        int dim = 0;

        boolean is_IDENFR = true;
        boolean is_LBRACK = true;
        boolean is_RBRACK = true;
        boolean is_ASSIGN = true;

        is_IDENFR = getNowOuter().getType() == Word.IDENFR;

        if (is_IDENFR) {
            outer = getNowOuter();
            pcodes.add(new Pcode(ASP.VAR, outer.getValue()));
            addOuter();

            is_LBRACK = getNowOuter().getType() == Word.LBRACK;
            if (is_LBRACK) {
                dim++;
                addOuter();
                constExp();
                is_RBRACK = getNowOuter().getType() == Word.RBRACK;
                if (is_RBRACK) {
                    addOuter();
                    is_LBRACK = getNowOuter().getType() == Word.LBRACK;
                    if (is_LBRACK) {
                        dim++;
                        addOuter();
                        constExp();
                        is_RBRACK = getNowOuter().getType() == Word.RBRACK;
                        if (is_RBRACK) {
                            addOuter();
                        }
                    }
                }
            }
        }

        if (dim > 0){
            pcodes.add(new Pcode(ASP.DIMVAR, outer.getValue(), dim));
        }

        is_ASSIGN = getNowOuter().getType() == Word.ASSIGN;
        if (is_ASSIGN) {
            addOuter();
            constInitVal();
        }
        grammars.add("<ConstDef>");
    }

    private void constInitVal() {
        boolean is_LBRACE = true;
        boolean is_RRBRACE = true;
        is_LBRACE = getNowOuter().getType() == Word.LBRACE;
        if (is_LBRACE) {
            addOuter();
            is_RRBRACE = getNowOuter().getType() == Word.RBRACE;
            if (is_RRBRACE) {
                addOuter();
            } else {
                constInitVal();
                while (getNowOuter().getType() == Word.COMMA) {
                    addOuter();
                    constInitVal();
                }
                if (getNowOuter().getType() == Word.RBRACE) {
                    addOuter();
                }
            }
        } else {
            constExp();
        }
        grammars.add("<ConstInitVal>");
    }

    private void varDecl() {
        addOuter();
        varDef();
        Outer now = getNowOuter();

        boolean is_COMMA = now.getType() == Word.COMMA;
        while (is_COMMA) {
            addOuter();
            varDef();
            now = getNowOuter();
            is_COMMA = now.getType() == Word.COMMA;
        }

        addOuter();
        grammars.add("<VarDecl>");
    }

    private void varDef() {
        int dim = 0;
        boolean is_IDENFR = getNowOuter().getType() == Word.IDENFR;
        boolean is_LBRACK;
        boolean is_RBRACK;
        boolean is_ASSIGN;

        Outer outer = null;

        if (is_IDENFR) {
            outer = getNowOuter();
            pcodes.add(new Pcode(ASP.VAR, outer.getValue()));
            addOuter();
            is_LBRACK = getNowOuter().getType() == Word.LBRACK;
            if (is_LBRACK) {
                dim++;
                addOuter();
                constExp();
                is_RBRACK = getNowOuter().getType() == Word.RBRACK;
                if (is_RBRACK) {
                    addOuter();
                    is_LBRACK = getNowOuter().getType() == Word.LBRACK;
                    if (is_LBRACK) {
                        dim++;
                        addOuter();
                        constExp();
                        is_RBRACK = getNowOuter().getType() == Word.RBRACK;
                        if (is_RBRACK) {
                            addOuter();
                        }
                    }
                }
            }
        }

        if (dim > 0){
            pcodes.add(new Pcode(ASP.DIMVAR, outer.getValue(), dim));
        }

        is_ASSIGN = getNowOuter().getType() == Word.ASSIGN;
        if (is_ASSIGN) {
            addOuter();
            initVal();
        }else {
            pcodes.add(new Pcode(ASP.PLACEHOLDER, outer.getValue(), dim));
        }
        grammars.add("<VarDef>");
    }

    private void initVal() {
        boolean is_LBRACE = getNowOuter().getType() == Word.LBRACE;
        if (is_LBRACE) {
            addOuter();
            boolean is_RBRACE = getNowOuter().getType() == Word.RBRACE;
            if (is_RBRACE) {
                addOuter();
            } else {
                initVal();
                while (getNowOuter().getType() == Word.COMMA) {
                    addOuter();
                    initVal();
                }
                is_RBRACE = getNowOuter().getType() == Word.RBRACE;
                if (is_RBRACE) {
                    addOuter();
                }
            }
        } else {
            exp();
        }
        grammars.add("<InitVal>");
    }

    private void number() {
        Outer outer = getNowOuter();
        pcodes.add(new Pcode(ASP.PS, outer.getObject()));
        addOuter();
        grammars.add("<Number>");
    }

    private void mainFuncDef() {
        boolean is_INTTK = getNowOuter().getType() == Word.INTTK;
        if (is_INTTK) {
            addOuter();
            boolean is_MAINTK = getNowOuter().getType() == Word.MAINTK;
            if (is_MAINTK) {
                addOuter();
                pcodes.add(new Pcode(ASP.MAIN, "main"));
                boolean is_LPARENT = getNowOuter().getType() == Word.LPARENT;
                if (is_LPARENT) {
                    addOuter();
                    boolean is_RPARENT = getNowOuter().getType() == Word.RPARENT;
                    if (is_RPARENT) {
                        addOuter();
                        block();
                    }
                }
            }
        }

        pcodes.add(new Pcode(ASP.EXIT));
        grammars.add("<MainFuncDef>");
    }

    private void error(String type){
        errors.add(new Error(Outer.getLn(), type));
        System.out.println(Outer.getLn() + " " + type);
    }

    private void error(String type, int ln){
        errors.add(new Error(ln, type));
        System.out.println(ln + " " + type);
    }

    private void error(){

    }

    public void printErrors(FileWriter writer) throws IOException{
        errors.sort(new Comparator<Error>() {
            @Override
            public int compare(Error o1, Error o2) {
                return o1.getN() - o2.getN();
            }
        });
        for (Error error : errors){
            writer.write(error + "\n");
        }
        writer.flush();
        writer.close();
    }




    public void printOuters(FileWriter writer) throws IOException {
        for (String str : grammars) {
            writer.write(str + "\n");
        }

        writer.flush();
        writer.close();
    }

    public void print(){
        for (String str : grammars){
            System.out.println(str);
        }
    }
}
