package parte_5;

public class S19 {
    //Atributos
    private String Sn, cc, addr, data, ck;
    
    //Constructor
    public S19( String Sn, String cc, String addr, String data, String ck) {
        this.Sn = Sn;
        this.cc = cc;
        this.addr = addr;
        this.data = data;
        this.ck = ck;
    }

    //Metodos getters y setters
    public String getSn() {
        return Sn;
    }

    public void setSn(String Sn) {
        this.Sn = Sn;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCk() {
        return ck;
    }

    public void setCk(String ck) {
        this.ck = ck;
    }
}
