package PCODE;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class PcodeMaker {
    private final ArrayList<Pcode>pcodes;
    private final ArrayList<StackBlock>stackBlocks = new ArrayList<>();
    private final ArrayList<Integer>stack = new ArrayList<>();
    private HashMap<String, Varition>varitionHashMap = new HashMap<>();
    private final HashMap<String, FUNCTION>functionHashMap = new HashMap<>();
    private final HashMap<String, Integer>TagTable = new HashMap<>();
    private int pc = 0;

    private int mainhere;

    private final ArrayList<String>outs = new ArrayList<>();
    private FileWriter writer;
    private Scanner scanner;


    public PcodeMaker(ArrayList<Pcode> pcodes, FileWriter writer, Scanner scanner){
        this.pcodes = pcodes;
        this.writer = writer;
        this.scanner = scanner;
        int i = 0;
        int j = 0;
        int k = 0;
        for (i = 0;i < pcodes.size();i++){
            Pcode pcode = pcodes.get(i);

            boolean is_main = pcode.getCode().equals(ASP.MAIN);
            boolean is_tag = pcode.getCode().equals(ASP.TAG);
            boolean is_fun = pcode.getCode().equals(ASP.FUC);

            if (is_main){
                mainhere = i;
            }

            if(is_tag){
                TagTable.put((String) pcode.getA1(), i);
            }

            if (is_fun){
                String JB = (String) pcode.getA1();
                System.out.println(JB);
                int baga = (int) pcode.getA2();
                System.out.println( baga);
                functionHashMap.put(JB, new FUNCTION(i, baga));
            }
        }
    }

    private void ps(int baga){
        stack.add(baga);
    }

    private int pp(){
        return stack.remove(stack.size() - 1);
    }

    private Varition find_v(String type){
        if(varitionHashMap.containsKey(type)){
            return varitionHashMap.get(type);
        }
        else {
            return stackBlocks.get(0).getVaritionHashMap().get(type);
        }
    }

    private int getadd(Varition varition, int tag){
        int add = 0;
        int baga = varition.getWeishu() - tag;
        if(baga == 0){
            add = varition.getTag();
        }
        if (baga == 1){
            int xiba = pp();
            if (varition.getWeishu() == 1){
                add = varition.getTag() + xiba;
            }
            else {
                add = varition.getTag() + varition.getWei2() * xiba;
            }
        }
        if(baga == 2){
            int j = pp();
            int i = pp();
            add = varition.getTag() + varition.getWei2() * i + j;
        }
        return add;
    }

    public void out() throws IOException{
        for (String s : outs){
            writer.write(s);
        }
        writer.flush();
        writer.close();
    }

    public void execve(){
        ArrayList<Integer> rseg = new ArrayList<>();
        boolean is_main = false;
        int jmpcl = 0;
        int tmpnum = 0;


        for (;pc < pcodes.size();pc++ ){
            //System.out.println(pc);
            Pcode pcode = pcodes.get(pc);
            //System.out.println(pcode.getCode());

            switch (pcode.getCode()){


                case E:{
                    int baga1 = pp();
                    int baga2 = pp();
                    if (baga1 == baga2){
                        ps(1);
                    }
                    else ps(0);

                }
                break;
                case G:{
                    int baga1 = pp();
                    int baga2 = pp();
                    if (baga1 > baga2){
                        ps(1);
                    }
                    else ps(0);
                }
                break;
                case L:{
                    int baga1 = pp();
                    int baga2 = pp();
                    if (baga1 < baga2){
                        ps(1);
                    }
                    else ps(0);
                }
                break;
                case GE:{
                    int baga1 = pp();
                    int baga2 = pp();
                    if (baga1 >= baga2){
                        ps(1);
                    }
                    else ps(0);
                }
                break;
                case LE:{
                    int baga1 = pp();
                    int baga2 = pp();
                    if (baga1 <= baga2){
                        ps(1);
                    }
                    else ps(0);
                }
                break;
                case NE:{
                    int baga1 = pp();
                    int baga2 = pp();
                    if (baga1 != baga2){
                        ps(1);
                    }
                    else ps(0);
                }
                break;
                case PLUS:{
                    int baga1 = pp();
                    int baga2 = pp();
                    ps(baga2 + baga1);
                }
                break;
                case MIN:{
                    int baga1 = pp();
                    int baga2 = pp();
                    ps(baga2 - baga1);
                }
                break;
                case CHU:{
                    int baga1 = pp();
                    int baga2 = pp();
                    ps(baga2 / baga1);
                }
                break;
                case CHENG:{
                    int baga1 = pp();
                    int baga2 = pp();
                    ps(baga2 * baga1);
                }
                break;
                case MO:{
                    int baga1 = pp();
                    int baga2 = pp();
                    ps(baga2 % baga1);
                }
                break;
                case PS:{
                    if ((pcode.getA1() instanceof Integer)){
                        ps((Integer) pcode.getA1());
                    }
                }
                break;
                case PP:{
                    int para = pp();
                    int point = pp();
                    stack.set(point, para);
                }
                break;
                case YU:{
                    boolean baga1;
                    boolean baga2;
                    baga1 = (pp() != 0);
                    baga2 = (pp() != 0);
                    ps((baga2 && baga1) ? 1 : 0);
                }
                break;
                case HUO:{
                    boolean baga1;
                    boolean baga2;
                    baga1 = (pp() != 0);
                    baga2 = (pp() != 0);
                    ps((baga2 || baga1) ? 1 : 0);
                }
                break;
                case FEI:{
                    boolean baga1;
                    baga1 = (pp() != 0);
                    ps((!baga1) ? 1 : 0);
                }
                break;
                case NEG:{
                    ps(-pp());
                }
                break;
                case POS:{
                    ps(pp());
                }
                break;
                case JZ:{
                    if (stack.get(stack.size() - 1) == 0){
                        pc = TagTable.get((String) pcode.getA1());
                    }
                }
                break;
                case JNZ:{
                    if (stack.get(stack.size() - 1) != 0){
                        pc = TagTable.get((String) pcode.getA1());
                    }
                }
                break;
                case JMP:{
                    pc = TagTable.get((String) pcode.getA1());
                }
                break;
                case FUC:{
                    if (!is_main){
                        pc = mainhere - 1;
                    }
                }
                break;
                case MAIN:{
                    is_main = true;
                    stackBlocks.add(new StackBlock(pcodes.size(), varitionHashMap, stack.size() - 1, 0, 0, 0));
                    varitionHashMap = new HashMap<>();
                }
                break;
                case EDFC:{

                }
                break;
                case PARA:{
                    Varition para = new Varition(rseg.get(rseg.size() - jmpcl + tmpnum));
                    int bagapara = (int) pcode.getA2();
                    para.setWeishu(bagapara);
                    if(bagapara == 2){
                        para.setWei2(pp());

                    }
                    varitionHashMap.put((String) pcode.getA1(), para);
                    tmpnum++;
                    if (tmpnum == jmpcl){
                        rseg.subList(rseg.size() - jmpcl, rseg.size()).clear();
                    }
                }
                break;
                case VAR:{
                    Varition varition = new Varition(stack.size());
                    varitionHashMap.put((String) pcode.getA1(), varition);
                }
                break;
                case VALUE:{
                    Varition varition = find_v((String) pcode.getA1());
                    int baga = (int) pcode.getA2();
                    int add = getadd(varition, baga);
                    ps(stack.get(add));
                }
                break;
                case ADDRESS:{
                    Varition varition = find_v((String) pcode.getA1());
                    int baga = (int) pcode.getA2();
                    int add = getadd(varition, baga);
                    ps(add);
                }
                break;
                case DIMVAR:{
                    Varition varition = find_v((String) pcode.getA1());
                    int baga = (int) pcode.getA2();
                    varition.setWeishu(baga);
                    if (baga == 1){
                        int i = pp();
                        varition.setWei1(i);
                    }
                    if(baga == 2){
                        int ii = pp();
                        int i = pp();
                        varition.setWei1(i);
                        varition.setWei2(ii);
                    }
                }
                break;
                case PLACEHOLDER:{
                    Varition varition = find_v((String) pcode.getA1());
                    int baga = (int) pcode.getA2();
                    if(baga == 0){
                        ps(0);
                    }
                    if(baga == 1){
                        int length =  varition.getWei1();
                        for (int i = 0;i < length;i++){
                            ps(0);
                        }
                    }
                    if(baga == 2){
                        int length = varition.getWei1() * varition.getWei2();
                        for (int i = 0;i < length; i++){
                            ps(0);
                        }
                    }
                }
                break;
                case RPARA:{
                    int baga = (int) pcode.getA1();
                    if (baga == 0){
                        rseg.add(stack.size() - 1);
                    }
                    else {
                        rseg.add(stack.get(stack.size() - 1));
                    }
                }
                break;
                case CALL:{
                    FUNCTION function = functionHashMap.get((String) pcode.getA1());
                    stackBlocks.add(new StackBlock(pc, varitionHashMap, stack.size() - 1, function.getCanshu(), function.getCanshu(), tmpnum));
                    pc = function.getTag();
                    varitionHashMap = new HashMap<>();
                    jmpcl = function.getCanshu();
                    tmpnum = 0;
                }
                break;
                case RETURN:{
                    int baga = (int) pcode.getA1();
                    StackBlock stackBlock = stackBlocks.remove(stackBlocks.size() - 1);
                    pc = stackBlock.getPc();
                    varitionHashMap = stackBlock.getVaritionHashMap();
                    jmpcl = stackBlock.getCalltmp();
                    tmpnum = stackBlock.getNowtmp();
                    if(baga == 1){
                        stack.subList(stackBlock.getRsp() + 1 - stackBlock.getPnum(), stack.size() - 1).clear();
                    }
                    else {
                        stack.subList(stackBlock.getRsp() + 1 - stackBlock.getPnum(), stack.size()).clear();
                    }
                }
                break;
                case GETINT: {
                    //System.out.println("CAN YOU SCANNER?");
                    int baga = scanner.nextInt();
                    System.out.println(baga);
                    ps(baga);
                    //System.out.println("CAN YOU BREAK?");
                }
                break;
                case PRINTF:{
                    String s = (String) pcode.getA1();
                    int n = (int) pcode.getA2();
                    //System.out.println(str + baga);
                    StringBuilder stringBuilder = new StringBuilder();
                    ArrayList<Integer> paras = new ArrayList<>();
                    int index = n - 1;
                    for (int i = 0; i < n; i++){
                        paras.add(pp());

                    }
                    System.out.println(paras);
                    for (int i = 0; i < s.length(); i++){
                        if (i + 1 < s.length()){
                            if(s.charAt(i) == '%' && s.charAt(i + 1) == 'd'){
                                stringBuilder.append(paras.get(index--).toString());
                                i++;
                                continue;
                            }
                        }
                        stringBuilder.append(s.charAt(i));
                    }
                    String baga = stringBuilder.substring(0, stringBuilder.length());
                    System.out.println(baga);
                    outs.add(baga);
                }
                break;

                case TAG :{

                }
                break;
                case EXIT:{
                    return;
                }
                default:
                    break;
            }

        }




    }







}
