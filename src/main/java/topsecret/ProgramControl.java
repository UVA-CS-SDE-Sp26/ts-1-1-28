package topsecret;

import java.io.IOException;
import java.util.List;

public class ProgramControl {
    private FileHandler fileHandler;
    public ProgramControl(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    // no arguments: return list
    public String handleArgs() throws IOException {
        List<String> fileList = fileHandler.listFiles();
        String ret = "";
        for(int i = 1; i <= fileList.size(); i++) {
            ret += String.format("%02d", i) + " " + fileList.get(i - 1) + "\n";
        }
        return ret;
    }

    // number argument: return file contents with default cipher
    public String handleArgs(int index) throws IOException {
        List<String> fileList = fileHandler.listFiles();
        String fileName = fileList.get(index - 1);
        return fileHandler.readFile(fileName);
    }

    // number and cipher arguments: return file contents with given cipher
    //public String handleArgs(int index, Object cipher) {
    //
    //}
}
