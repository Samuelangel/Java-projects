package parte_5;

public class Tabsim {
    //Atributos
    String tipo;
    private String si, ti;

    //Constructor
    public Tabsim(String tipo, String si, String ti) {
        this.tipo = tipo;
        this.si = si;
        this.ti = ti;
    }
    
    //Metodos setter y getter
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setSi(String si) {
        this.si = si;
    }

    public void setTi(String ti) {
        this.ti = ti;
    }

    public String getTipo() {
        return tipo;
    }

    public String getSi() {
        return si;
    }

    public String getTi() {
        return ti;
    }
    
}
