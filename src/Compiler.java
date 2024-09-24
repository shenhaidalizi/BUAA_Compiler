import PCODE.PcodeMaker;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Compiler {



    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        FileOp fileOp = new FileOp();
        LexicalAnalyse lexicalAnalyser = new LexicalAnalyse(fileOp.getCode());
        ArrayList<Outer> outers = lexicalAnalyser.Outers();
        GrammarAnalyse grammaticalAnalyser = new GrammarAnalyse(outers);

        PcodeMaker pcodeMaker = new PcodeMaker(grammaticalAnalyser.getPcodes(), fileOp.getWriter(), scanner);
        pcodeMaker.execve();
        //pcodeMaker.wocaonima();
        pcodeMaker.out();




    }
}
