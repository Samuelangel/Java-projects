
package proyectointegrador;

public class Linea {
    //ATRIBUTOS
    private String etiqueta;
    private String codop;
    private String operando;

    public Linea(String etiqueta, String codop, String operando) {
        this.etiqueta = etiqueta;
        this.codop = codop;
        this.operando = operando;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getCodop() {
        return codop;
    }

    public String getOperando() {
        return operando;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public void setCodop(String codop) {
        this.codop = codop;
    }

    public void setOperando(String operando) {
        this.operando = operando;
    }
    
    
}
