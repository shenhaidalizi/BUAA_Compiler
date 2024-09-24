package PCODE;

import java.util.HashMap;

public class StackBlock {
    private Integer pc;
    private Integer rsp;
    private Integer pnum;
    private Integer calltmp;
    private Integer nowtmp;
    private HashMap<String, Varition>varitionHashMap;

    public StackBlock(Integer pc, HashMap<String, Varition>varitionHashMap, Integer rsp, Integer pnum, Integer calltmp, Integer nowtmp){
        this.pc = pc;
        this.calltmp = calltmp;
        this.nowtmp = nowtmp;
        this.pnum =pnum;
        this.rsp = rsp;
        this.varitionHashMap = varitionHashMap;
    }

    public Integer getPc() {
        return pc;
    }

    public HashMap<String, Varition> getVaritionHashMap() {
        return varitionHashMap;
    }

    public Integer getRsp() {
        return rsp;
    }

    public Integer getPnum() {
        return pnum;
    }

    public Integer getCalltmp() {
        return calltmp;
    }

    public Integer getNowtmp() {
        return nowtmp;
    }

}
