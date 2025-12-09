
package parte_2;

public class Linea {
    //ATRIBUTOS
    private String etiqueta;// Almacena la etiqueta de la línea.
    private String codop;// Almacena el código de operación.
    private String operando;// Almacena el operando.
    private Linea siguiente;// Referencia a la siguiente línea en la secuencia.
    private String ADDR;// Almacena la dirección de memoria.
    private String size;// Almacena el tamaño de la línea.
    private String porCalcular;// Almacena información por calcular o procesar.
    

    public Linea(String etiqueta, String codop, String operando, Linea siguiente, String ADDR) {
        this.etiqueta = etiqueta;
        this.codop = codop;
        this.operando = operando;
        this.siguiente = siguiente;
        this.ADDR = ADDR;
        this.size = " ";// Tamaño inicialmente vacío.
        this.porCalcular = " ";// Información por calcular inicialmente vacía.
    }
    // MÉTODOS GETTERS
    public String getEtiqueta() {
        return etiqueta;
    }

    public String getCodop() {
        return codop;
    }

    public String getOperando() {
        return operando;
    }

    public Linea getSiguiente() {
        return siguiente;
    }

    public String getADDR() {
        return ADDR;
    }

    public String getSize() {
        return size;
    }

    public String getPorCalcular() {
        return porCalcular;
    }
    
    // MÉTODOS SETTERS
    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public void setCodop(String codop) {
        this.codop = codop;
    }

    public void setOperando(String operando) {
        this.operando = operando;
    }

    public void setSiguiente(Linea siguiente) {
        this.siguiente = siguiente;
    }

    public void setADDR(String ADDR) {
        this.ADDR = ADDR;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setPorCalcular(String porCalcular) {
        this.porCalcular = porCalcular;
    }
    
}
