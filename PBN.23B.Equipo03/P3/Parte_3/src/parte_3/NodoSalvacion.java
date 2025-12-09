package parte_3;

// Inicio de la clase NodoSalvacion
public class NodoSalvacion {// Declaración de variables miembro públicas
    public String CODOP;// Código de operación
    public String Operando;// Operando
    public String AddrMode;// Modo de dirección
    public String SourceForm;// Forma de origen
    public String byteCalcular;// Bytes a calcular
    public String byteTotal;// Total de bytes

    public NodoSalvacion(String CODOP, String Operando, String AddrMode, String SourceForm, String byteCalcular, String byteTotal) {// Inicialización de variables miembro con los valores proporcionados
        this.CODOP = CODOP;
        this.Operando = Operando;
        this.AddrMode = AddrMode;
        this.SourceForm = SourceForm;
        this.byteCalcular = byteCalcular;
        this.byteTotal = byteTotal;
    }
 
}//Fin de la clase NodoSalvacion
