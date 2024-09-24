import java.io.*;

public class FileOp {
    private FileReader reader;
    private String code;
    private FileWriter writer;

    public FileOp() throws IOException{
        reader = new FileReader(new File("testfile.txt"));
        writer = new FileWriter(new File("pcoderesult.txt"));
        code = deCode();

    }

    private String deCode() throws IOException{
        BufferedReader bf = new BufferedReader(reader);
        StringBuffer str = new StringBuffer();
        String s = null;

        while((s = bf.readLine()) != null){
            str.append(s).append("\n");
        }

        return str.toString();
    }

    public String getCode(){
        return code;
    }

    public FileWriter getWriter(){
        return writer;
    }

}
