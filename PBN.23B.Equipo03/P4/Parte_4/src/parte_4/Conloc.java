
package parte_4;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class Conloc {
    static File archivolst = new File("P1ASM.lst");
    static File archivotabsim = new File("TABSIM.txt");
    static ArrayList <Tabsim> etiquetas = new ArrayList<>();
    static Tabsim etq;
    static String conloc = " "; //Variable en la que se guardara el valor del conloc de cada linea
    
    static void CrearArchivoList(){ 
        try {
            // Crear el archivo list
            archivolst.createNewFile();

            RandomAccessFile auxArchivo = new RandomAccessFile("P1ASM.lst", "rw");
            auxArchivo.seek(auxArchivo.length());

            // Escribir en el documento
            auxArchivo.writeBytes("TIPO,        ");
            auxArchivo.writeBytes("VALOR,       ");
            auxArchivo.writeBytes("ETQ,         ");
            auxArchivo.writeBytes("CODOP,       ");
            auxArchivo.writeBytes("OPER \n");

            // Cerrar el archivo después de escribir
            auxArchivo.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("No se ha creado el archivo");
        }
    }//fin crear
    
    public static String sumarHexadecimal(String hexadecimal, int entero) {
        // Convertir el valor hexadecimal a entero
        int valorHexadecimal = Integer.parseInt(hexadecimal, 16);

        // Sumar el entero al valor hexadecimal
        int suma = valorHexadecimal + entero;

        // Obtener la representación hexadecimal del resultado
        String resultadoHexadecimal = Integer.toHexString(suma).toUpperCase();

        return resultadoHexadecimal;
    }
    
     //funcion para llenar el txt list
    static void LlenarList(Linea auxiliar) {
        int bytes = 0;//variable int de apoyo, tomará el valor de los bytes de cada linea 
        boolean equ = false;//variable booleana de apoyo, indica cuando la linea tiene el CODOP "EQU"
        try {
            RandomAccessFile auxArchivo = new RandomAccessFile("P1ASM.lst", "rw"); //Encuentro el archivo y accedo para leer y escribir
            if (!(auxiliar.getSize().equals(" "))) {//analiza el valor de size, si no tiene valor es que es una linea con error
                auxArchivo.seek(auxArchivo.length()); //Seek posiciona el puntero donde escribir, length es para decirle donde esta el final
                if (auxiliar.getCodop().equals("ORG")) {//analiza si el CODOP es igual a ORG para seguir validando
                    if (!(auxiliar.getADDR().equals("ERROR"))) {//si el ADDR es "ERROR", quiere decir que no se puede calcular el conloc
                        auxArchivo.writeBytes("DIR_INIC,        "); // escribe esto en el txt list
                        conloc = Parte_4.validarDireccion(auxiliar.getOperando());//valor toma el valor de operando en 2bytes 
                        auxArchivo.writeBytes(conloc + ",       ");//escribe el valor en 2 bytes y sin $ en el .lst, el espacio es para organizar
                    }//fin de segundo if
                    else {
                        Parte_4.Errores.add("NO SE PUEDE CALCULAR EL CONLOC PORQUE OPR DE ORG ES INCORRECTO");//manda error por fallas en estructura de org
                        System.out.println("NO SE PUEDE CALCULAR EL CONLOC PORQUE OPR DE ORG ES INCORRECTO"); //manda error por fallas en estructura de org
                    }//fin del else
                }//fin es el codop es org
                else if (auxiliar.getCodop().equals("EQU")) { //analiza que el OPR sea = EQU
                    auxArchivo.writeBytes("VALOR,          ");// identifica que es tipo VALOR y lo escribe en el .lst
                    auxArchivo.writeBytes(Parte_4.validarDireccion(auxiliar.getOperando()) + ",       ");//escribe su valor en 2bytes en el .lst
                    auxiliar.setConloc(Parte_4.validarDireccion(auxiliar.getOperando()));//valida la direccion y la establece en conloc en 2bytes
                    equ = true;//Indica que la linea es de un EQU
                }//fin el codop es EQU
                else {
                    if (!(conloc.equals(" "))) {//valida que ya se haya inicializado el conloc
                        auxArchivo.writeBytes("CONTLOC,       ");// identifica que es de tipo conloc y lo escribe en el .lst
                        auxArchivo.writeBytes(Parte_4.validarDireccion("$".concat(conloc)) + ",       ");//escribe en el .lst el nuevo valor del conloc en 2bytes
                        auxiliar.setConloc(Parte_4.validarDireccion("$".concat(conloc))); //guarda en el arreglo el valor del conloc en 2bytes de la linea correspondiente
                        String[] campos = auxiliar.getSize().split("\\s+");//guarda los bytes de la linea examinada
                        bytes = Integer.parseInt(campos[0]);//lo converte de String a entero
                        conloc = sumarHexadecimal(conloc, bytes);//añade los bytes de la linea al valor del conloc
                    }//fin cuando el conloc si esta inicializado
                }//fin de else, el codop no es equ ni org
                if (!(conloc.equals(" ")) || equ) {//si el conloc esta incializado o la linea es de un EQU
                    if (auxiliar.getEtiqueta().equals(" ")) {// si la linea no tiene etiqueta
                        auxArchivo.writeBytes("NULL,       "); //escribe null en la parte de etiqueta
                    }//fin la linea no tiene etiqueta
                    else {//inicio de else
                        auxArchivo.writeBytes(auxiliar.getEtiqueta() + ",       "); //escribe esto en el txt
                    }//fin si tiene etiqueta
                    auxArchivo.writeBytes(auxiliar.getCodop() + ",       ");//escribe el codop en el .lst
                    if (auxiliar.getOperando().equals(" ")) {//si no tiene operando
                        auxArchivo.writeBytes("NULL,       ");//escribe esta parte en el txt list ...
                    }//Fin no tiene operando
                    else {
                        auxArchivo.writeBytes(auxiliar.getOperando() + ",       ");//escribe en el lst el operando
                    }//fin si tiene operando
                    auxArchivo.writeBytes("\n");//da un salto de linea en el .lst
                }//fin el conloc si esta inicializado
                equ = false;//Para que siempre inicie considerando que la linea no es de un equ
            }//Fin no es una linea con error
            auxArchivo.close();
        } catch (IOException ex) {
            Parte_4.Errores.add("ERROR");
            System.out.println("ERROR");
            ex.printStackTrace();
        }//fin catch
    }//Fin metodo llenar list  
    
    //metodo para crear la tabsim
    static void Creartabsim(){
        try {
            // Crear el archivo tabsin
            archivotabsim.createNewFile();

            RandomAccessFile auxArchivo = new RandomAccessFile("TABSIM.txt", "rw");
            auxArchivo.seek(auxArchivo.length());

            // Escribir en el documento
            auxArchivo.writeBytes("TIPO            ");//escribe en txt
            auxArchivo.writeBytes("Si       ");//escribe en txt
            auxArchivo.writeBytes("Ti            ");//escribe en txt
            auxArchivo.writeBytes("\n");//escribe en txt

            // Cerrar el archivo después de escribir
            auxArchivo.close();//cerrar archivo y guardar
        } catch (IOException ex) {
            ex.printStackTrace(); //escribe en consola
            System.out.println("No se ha creado el archivo");//en caso de error no crea nada y manda aviso
            Parte_4.Errores.add("No se ha creado el archivo");//en caso de error no crea nada y manda aviso
        }
    }// fin metodo crear tabsim
    
    //funcion para llenar el tabsim
    static void LlenarTabsim(Linea auxiliar) {
        try { // try para el proceso
            RandomAccessFile auxArchivo = new RandomAccessFile("TABSIM.txt", "rw"); //Encuentro el archivo y accedo para leer y escribir
            etq = new Tabsim(" ", " ", " ");
            auxArchivo.seek(auxArchivo.length()); //Seek posiciona el puntero donde escribir, length es para decirle donde esta el final
            if (!(auxiliar.getEtiqueta().equals(" ")) && !(auxiliar.getADDR().equals("ERROR")) && !(auxiliar.getConloc().equals(" "))) {
                //en la anterior linea  comprueba si los valores de etiqueta y ADDR
                //del objeto Linea son diferentes de " " y "ERROR", para seguir con la validacion
                if (auxiliar.getCodop().equals("EQU")) {//analiza si el CODOP es igual a EQU
                    auxArchivo.writeBytes("ABSOLUTA       ");// aqui valida que sea Absoluta si y solo si es de forma EQU
                    etq.tipo = "ABSOLUTA";
                }//fin del segundo if
                else {//si no es EQU(absoluta),entonces...
                    auxArchivo.writeBytes("RELATIVA       "); //es relativa, por que es de forma ORG
                    etq.tipo = "RELATIVA";
                }//fin del else
                auxArchivo.writeBytes(auxiliar.getEtiqueta() + "       "); // escribe la etiqueta en txt
                etq.setSi(auxiliar.getEtiqueta());
                auxArchivo.writeBytes(auxiliar.getConloc() + "       "); // escribe el CONTLOC en el txt
                etq.setTi(auxiliar.getConloc());
                auxArchivo.writeBytes("\n"); //hace el salto de linea para la siguiente instruccion...
                etiquetas.add(etq);
            }//fin del primer if todo bien con la linea del asm
            auxArchivo.close();
        } catch (IOException ex) { //catch por si no es valido el proceso
            Parte_4.Errores.add("ERROR");
            System.out.println("ERROR");//imprimer error
            ex.printStackTrace();//imprime en la consola
        }//fin catch
    } //fin del metodo llenar tabsim
    
}//fin clase metodosP3
 