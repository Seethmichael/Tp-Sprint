package etu1870.framework;

public class FileUpload{
    String fileName;
    String path;
    byte[] bytes;


    // constructors
    public FileUpload(String fileName, byte[] bytes){
        this.setBytes(bytes);
        this.setFileName(fileName);
    }

    // _ _ _  GET SET _ _ _

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public byte[] getBytes() {
        return bytes;
    }
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    
}