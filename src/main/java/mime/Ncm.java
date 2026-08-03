package mime;

import lombok.NoArgsConstructor;

/**
 * @author charlottexiao
 */
@NoArgsConstructor
public class Ncm {

    /**
     * NCM的文件路径
     */
    private String ncmFile;

    /**
     * 转换后文件路径
     */
    private String outFile;

    /**
     * 头信息
     */
    private Mata mata;

    /**
     * 封面信息
     */
    private byte[] image;

    public String getNcmFile() {
        return ncmFile;
    }

    public void setNcmFile(String ncmFile) {
        this.ncmFile = ncmFile;
    }

    public String getOutFile() {
        return outFile;
    }

    public void setOutFile(String outFile) {
        this.outFile = outFile;
    }

    public Mata getMata() {
        return mata;
    }

    public void setMata(Mata mata) {
        this.mata = mata;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }


}
