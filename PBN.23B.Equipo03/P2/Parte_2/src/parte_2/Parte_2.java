/*
    UNIVERCIDAD DE GUADALAJARA
    CENTRO UNIVERSITARIO DE LOS ALTOS
    INGENIERÍA EN COMPUTACIÓN
    PROGRAMACIÓN DE BAJO NIVEL
    PROYECTO INTEGRADOR
            EQUIPO 3
*/

package parte_2;

import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Parte_2 {
    static Linea PrimerLinCod=null,NewLinCod=null,FinLinCod=null; //Para guardar en una lista las lineas del codigo
    static boolean Comentario = false;//Variable que indica si la linea es un comentario
    
//**************************************************************** PARTE 1 *******************************************************
    //METODO PARA EVALUAR ETIQUETA
    static boolean validarEtiq(String etiqueta){
        boolean banEtiq=false;
        if(etiqueta.length()<=8){
            for(int i = 0; i < etiqueta.length(); i++){
                char c = etiqueta.charAt(i);//Para revisar caracter por caracter
                if(Character.isLetter(c) && i == 0){ 
                    banEtiq =true;
                }//Fin if primer caracter debe ser una letra
                else if((Character.isLetterOrDigit(c) || c == '_' || (c==':' && i==etiqueta.length()-1)) && i!=0){
                    banEtiq =true;
                }//Fin if validacion letras numeros, guion bajo o dos puntos al final
                else{
                    banEtiq=false;
                    i=etiqueta.length();
                }//fin no es una etiqueta
            }//Fin for
        }//Fin largo menor a 8   
        return banEtiq;
    }//Fin validar etiqueta
    
    //METODO PARA EVALUAR CODOP
    static boolean validarCodop(String codop){
        boolean banCodop=false;
        int punto=0;//Auxiliar para validar que solo ponga un punto
        if(codop.length()<=5){
            for(int i = 0; i < codop.length(); i++){
                char c = codop.charAt(i);
                if(Character.isLetter(c)){
                    banCodop =true;
                }//Fin if solo letras
                else if(c=='.'&&punto==0&&i!=0){
                    banCodop =true;
                    punto++;
                }//Fin si no: puede ser un solo punto
                else{
                    banCodop=false;
                    i=codop.length();
                }//Fin else
            }//Fin for para recorrer el CODOP
        }//Fin no mas de 5 caracteres 
        return banCodop;
    }//FINAL DE METODO PARA EVALUAR CODOP
    
    //METODO IDENTIFICAR Y EVALUAR COMENTARIO
    static boolean Comentario(String x){
        boolean Coment=false;
        char c = x.charAt(0);//Para evaluar caracter por caracter
        if(c==';'){
            Comentario=true;
        }//Fin debe empezar con punto y coma
        if (x.length() > 81){
            Coment = true;
        } 
        return Coment;
    }//Fin validacion comentario
    
    //METODO PARA LEER EL ASM
    static void Leer(){
        try{
            RandomAccessFile auxArchivo = new RandomAccessFile("P1ASM.asm","r");//r es para solo leer el archivo
            long cursorActual;//Para saber donde estamos en el asm
            cursorActual = auxArchivo.getFilePointer();//Puntero en el archivo
            FileReader leerArchivo = new FileReader("P1ASM.asm");//leo el archivo
            String lecturaLinea; //Guarda cada linea
            
            //Aqui es donde empieza a leer por linea
            while(cursorActual!=auxArchivo.length()){//mientras el lector no llegue al final del archivo
                lecturaLinea = auxArchivo.readLine();//leo la linea
                cursorActual = auxArchivo.getFilePointer();
                Comentario=false;
                if(!(lecturaLinea.equals(""))){//El if hace que se ignore cuando hay un linea de codigo vacia
                    Comentario(lecturaLinea);
                }
                if(!(Comentario)){ //Si no es un comentario, debe ser una linea de codigo
                    String[] campos = lecturaLinea.split("\\s+");//Separa por bloques segun cada espacios o tabulación
                    NewLinCod = new Linea(" "," "," ",null," "); //Inicializo valores
                    if(campos.length<=3 && campos.length>1){//If para validacion de solo 4 bloques no mas
                        if(!(campos[0].equals(""))){
                            if(validarEtiq(campos[0])){
                                if(campos[0].endsWith(":")){
                                    NewLinCod.setEtiqueta(campos[0].substring(0, campos[0].length()-1).toUpperCase());//Guardado de etiqueta en un nuevo elemento en la lista
                                }else{
                                    NewLinCod.setEtiqueta(campos[0].toUpperCase());//Guardar etiqueta en una nueva linea de la lista
                                }
                            }
                            else{
                                NewLinCod.setEtiqueta("Formato Etiqueta");//Guardar el error de la etiqueta 
                            }
                        }//Busco una etiqueta
                        if(validarCodop(campos[1])){
                            NewLinCod.setCodop(campos[1].toUpperCase());
                            if(campos.length==3){//Si hay siguiente bloque debe ser el operando
                                NewLinCod.setOperando(campos[2].toUpperCase()); 
                            }
                            else if(NewLinCod.getCodop().equals("END")){
                                if(NewLinCod.getOperando().equals(" ")){//Validacion del end
                                    cursorActual=auxArchivo.length();//Para salir del archivo
                                }
                                else{
                                    System.out.println("EL END NO DEBE DE LLEVAR OPERANDO");
                                    NewLinCod.setOperando(" ");
                                }
                            }
                            //GUARDAR LINEA EN LA LISTA
                            if(PrimerLinCod==null){
                                PrimerLinCod=NewLinCod;
                            }
                            else{
                                FinLinCod.setSiguiente(NewLinCod);
                            }
                            FinLinCod=NewLinCod;
                        }//Fin CODOP correcto
                        else{
                            System.out.println("ERROR "+campos[1]+"No es un CODOP");
                        }//Fin error con codigo de operacion
                    }//Fin no mas de 3 bloques
                    else{
                        if(campos.length>3){
                            System.out.println("ERROR mas de 3 bloques en la linea");
                        }
                    }
                }//Fin if: no es un comentario, es una linea de codigo
                else{
                    if (Comentario (lecturaLinea)){
                        System.out.println("COMENTARIO CON EXCESO DE CARACTERES");
                        System.out.println("");
                    }//Fin comentario incorrecto
                    else {
                        System.out.println("COMENTARIO");
                        System.out.println("");
                    }//Fin comentario correcto
                }//Fin else no es linea de codigo, es un comentario 
            }//Fin del while
            
            //Si el end no existe en el archivo
            if(cursorActual==auxArchivo.length() && (!NewLinCod.getCodop().equals("END"))){
                System.out.println("END NO LOCALIZADO");
                PrimerLinCod=null;
                FinLinCod=null;
            }
            leerArchivo.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    
//******************************************************************** PARTE 2 *************************************************
    
    //METODO DE APOYO PARA PASAR DE OCTAL A DECIMAL...
    public static int octalADecimal(int octal) {//Este metodo lo usaremos para validar que no pase de los bits permitidos
    int decimal = 0;//vatriable a retornar valor obtenido
    int potencia = 0;//variable de apoyo
    // Ciclo infinito que se rompe cuando octal es 0
    while (true) {
        if (octal == 0) {//HASTA QUE OCTAL = 0 SE PARA EL BUCLE
            break;
        } else {
            int temp = octal % 10;
            decimal += temp * Math.pow(8, potencia);
            octal = octal / 10;
            potencia++;
            //PROCESO DE CONVERSION...
        }//fin del else para procesar la conversion
    }//final del while para validar y retornar valor
    return decimal; //regresa el valor para trabajarlo
    }//FIN METODO PARA PASAR OCTAL A DECIMAL...
    
    //METODO PARA PASAR DE BINARIO A DECIMAL...
    public static int binarioADecimal(String binario) {
        //La funcion se va a usar para validar que no se pase de los bits permitidos
        int decimal = 0;//variable a retornar valor
        int longitud = binario.length();//variable de apoyo

        for (int i = 0; i < longitud; i++) {//ciclo for para recorrer el arreglo
            char digito = binario.charAt(i);//funcion para analizar el caracter donde se encuentra el puntero
            int valorDigito = Character.getNumericValue(digito);//esta funcion sirve...
            //sirve para guardar un numero entero usando un string 
            
            // Multiplicar el valor actual por 2 elevado a la posición
            int exponente = longitud - 1 - i;//variable para revisar la potencia o exponente del numero.
            decimal += valorDigito * Math.pow(2, exponente);//para hacer la operacion 2^n y sacar el valor del bit
        }//fin del for

        return decimal;//regresa el valor calculado
    }//fin de funcion binario a decimal
    
     //METODO PARA VALIDAR UN HEXADECIMAL
    static boolean ValidarHexadecimal(String Hexa){
        boolean Valido=false;  //variable para aceptar o denegar
        if(Hexa.matches("[0-9A-F]+")){ // funcion que valida que tenga caracteres permitidos
            if(Hexa.length()<=4){ //si tiene el num mayor permitido (FFFF), o menos. Entonces acepta...
                Valido=true;
            }//fin del segundo if de validar valor
        }//fin de primer for para validar porma de operando
        return Valido;
    }//Fin evaluar hexadecimal
    
    //METODO PARA EVALUAR UN OCTAL
    static boolean ValidarOctal(String Oct){
      boolean banOctal = false;//variable booleana para retornar valor
        int Octal;//variable de apoyo octal
        if(Oct.matches("[0-7]+")){ // valida que tenga numeros solo del 0 al 7
            Octal = Integer.parseInt(Oct); //convierte de String s entero
            if(octalADecimal(Octal)<=65535){ //revisa que sea menor o igual a 16 bits que es lo permitido
                banOctal=true;
            } //fin del segundo if para validar que no se pase del valor permitido 
        }//fin del primer if para validar la forma de operando, que tenga solo numeros octales
      //proceso...
      return banOctal;
    }//Fin evaluar octal
    
    //METODO PARA EVALUAR UN BINARIO
    static boolean ValidarBinario(String nBin){
        boolean Valido=false;  // variable que sirve para validar o denegar 
        if(nBin.matches("[0-1]+")){ //revisa que solo tenga valores permitidos (0 y 1).
            if(nBin.length()<=16){ //revisa que cumpla con la cantidad de bits permitidos.
                Valido=true; //valida verdadero
            }//fin segundo if para revisar la longitud de los bites
        }//fin del primer if para revisar la forma de operando, que solo tenga 1 y 0
        return Valido;//regresa el valor
        
    }//Fin metodo para evaluar binario..
    
    //METODO PARA PASAR DE CUALQUIER BASE A DECIMAL
    static int ConvertirADecimal(String Operando){
        int Decimal=0;//variable para retornar el valor
        switch(Operando.charAt(0)){
            case '$':// para caso hexadecimal
                if(ValidarHexadecimal(Operando.substring(1))){
                    Decimal = Integer.parseInt(Operando.substring(1), 16);
                    //pasa el valor de hexadecimal a decimal
                }
                else{
                    Decimal = -1; //si no cumple manda estado de error
                }
            break; //corte de instruccion
            case '@'://caso octal
                if(ValidarOctal(Operando.substring(1))){
                    Decimal = octalADecimal(Integer.parseInt(Operando.substring(1)));
                    //pasa de octal a decimal
                }
                else{
                    Decimal = -1;//si no cumple manda estado de error
                }
            break;//crte de instruccion
            case '%': //caso binario
                if(ValidarBinario(Operando.substring(1))){
                    Decimal = binarioADecimal(Operando.substring(1));
                    //cambia de binario a decimal
                }
                else{
                    Decimal=-1;//si no cumple manda estado de error
                }
            break;//corte de instruccion
            default: //caso default (ya no ocupa hacerle conversion)
                if(Operando.matches("[0-9]+")&&Integer.parseInt(Operando)<65535){
                    Decimal = Integer.parseInt(Operando);
                    //lee decimal y lo pasa de String a Int
                }
                else{
                    Decimal=-1;//si no cumple manda estado de error
                }
            break;//corte de instruccion
        }//fin del switch
        return Decimal;
    }//Fin metodo convertir a decimal
    
    //METODO PARA EVALUAR UN ADDR IMM
    static boolean IMM(String operando, String FormaOpr){
        boolean imm=false;                                //Esta variable es para ver si el operando en cuestion si es un IMM
        int AUX=0;                                        //Variable auxiliar para guardar el valor del operando
        AUX=ConvertirADecimal(operando.substring(1)); //Se convierte a decimal el operando
        if(AUX!=-1){                                  //Este if es para seguir con el proceso en todo caso de que este escrito correctamente el operando                                  
            if(FormaOpr.equals("#opr8i") && AUX<256 && AUX>=0){ //Verifica que el operando sea de tipo #opr8i a traves del valor del operando
                imm=true;                                       //Manda un verdadero validando como correcto
            }
            else if(FormaOpr.equals("#opr16i")){
                if(AUX<=65535 && AUX>255){       //verifica que el operando se encuentre entre los valores asignados
                    imm=true;                                  //Manda un verdadero validando como correcto
                } 
            } 
        }
        return imm;                          //retorna el valor que se le haya asignado a la variable, viendo si es verdadera o falsa
    }
    
    //METODO PARA EVALUAR UN ADDR EXT
    static boolean EXT(String operando){ 
        boolean ext=false;               //Esta variable retorna si es que es verdadero que es un EXT
            if(ConvertirADecimal(operando)>255){  //Convierte a decimal y verifica que el opernado se encuentre entre los limites 
                ext=true;             //Manda un verdadero validando como correcto
            }
        return ext;               //Retorna el valor que se le haya asignado a la variable
    }//Fin metodo para dir
    
    //METODO PARA EVALUAR UN ADDR IDX  
    static String IDX(String Operando){
        String idx="0";                               //Esta variable sirve para guardar el tipo idx en custion
        if (Operando.matches("^[^,]*,[^,]*$")){       //Esta funcion sirve para validar que tenga una sola coma
                String partes[]=Operando.split(",");  //Esta funcion separa y guarda valores si hay una ',' entre estos
                if(partes[0].matches(".*\\d.*")){     //Esta funcion verifica si en la primer parte del operando hay un digito
                    int valor=Integer.parseInt(partes[0]); //Siendo que la primer parte del operando en un digito se manda a guardar en valor como entero
                    if(partes[1].equals("X") || partes[1].equals("Y") ||
                       partes[1].equals("SP") || partes[1].equals("PC")){ //Aqui verifica si la segunda parte del operando coincide con eso
                            if(valor>-17 && valor<=15){                   //Se mira que el valor (primera parte del operando) este entre los limites
                                idx="IDX(5b)";                            // Si es asi se declara como este tipo de indexado
                            }
                            else{
                                idx="0";                                  //si no marca error
                            }
                    }
                    else if((partes[1].startsWith("+") || partes[1].startsWith("-") || partes[1].endsWith("+") ||
                             partes[1].endsWith("-"))&& (partes[1].contains("X") ||
                             partes[1].contains("Y") || partes[1].contains("SP")) ){ //Aqui verifica si la segunda parte del operando coincide con eso
                                if(valor>0 && valor<9){                    //Se mira que el valor (primera parte del operando) este entre los limites
                                    idx="IDX(pre-inc)";                    // Si es asi se declara como este tipo de indexado
                                }
                                else{
                                    idx="0";                               //si no marca error
                                }
                    }
                    else{
                        idx="0"; //Si tenia alguna coincidencia con algunos de los otros requerimientos pero no cumple todo correctamente marca error
                    }
                }
                else if(partes[0].equals("A")||partes[0].equals("B")||partes[0].equals("D")){
                    if(partes[1].equals("X") || partes[1].equals("Y") ||
                        partes[1].equals("SP") || partes[1].equals("PC")){ //Aqui valida que tanto la primera parte como la segunda tenga coincidencias con estos parametros
                            idx="IDX(acc)";
                    }
                }
                else{
                    idx="0";   //Si no cumple con ninguna de todas las condiciones marca error
                }
        }
            return idx;  //retorna el tipo de indexado que sea
    }//Fin metodo para IDX
    
    //METODO PARA ADDR IDX1
    static boolean IDX1(String Operando){
        boolean idx1=false;                            //esta es la bandera que retorna si es cierto que es ese tipo de direccionamiento o no
        String partes[]=Operando.split(",");           //separa el operando en dos partes
        if(partes[0].matches(".*\\d.*")){              //mira que la primera parte del operando sean unicamente digitos
            int valor=Integer.parseInt(partes[0]);     //si la primera parte del operando es un digito la convierte en entero
            if(partes[1].equals("X") || partes[1].equals("Y") ||
               partes[1].equals("SP") || partes[1].equals("PC")){   //Mira que la segunda parte tenga coincidencias con estas condiciones
                    if((valor>-257 && valor<-16)||(valor>15 && valor<256)){ //Verifica que la primera parte del operando se encuentre entre estos limites
                        idx1=true;                    //retorna un verdadero validando que si es este tipo de direccionamiento
                    }
            }
        }
        return idx1;                                  //retorna un booleano para verificar si es IDX1 o no
    }//Fin modo dir IDX1
            
    //METODO PARA ADDR IDX2
    static boolean IDX2(String Operando){
        boolean idx2=false;                          //esta es la bandera que retorna si es cierto que es ese tipo de direccionamiento o no
        String partes[]=Operando.split(",");         //separa el operando en dos partes
        if(partes[0].matches(".*\\d.*")){            //mira que la primera parte del operando sean unicamente digitos
            int valor=Integer.parseInt(partes[0]);
            if(partes[1].equals("X") || partes[1].equals("Y") ||
               partes[1].equals("SP") || partes[1].equals("PC")){ //Mira que la segunda parte tenga coincidencias con estas condiciones
                    if(valor>255 && valor<65536){    //Verifica que la primera parte del operando se encuentre entre estos limites
                        idx2=true;                   //retorna un verdadero validando que si es este tipo de direccionamiento
                    }
            }
        }
        return idx2;                                 //retorna un booleano para verificar si es IDX2 o no
    }//fin del metodo IDX2
    
    //METODO PARA ADDR [D,IDX]
    static boolean IdxD(String Operando){
        boolean idx=false;                          //esta es la bandera que retorna si es cierto que es ese tipo de direccionamiento o no
        String partes[]=Operando.split(",");        //separa el operando en dos partes
            if(partes[0].equals("[D")){             //Verifica que la primera parte del operando sea esta
                if(partes[1].equals("X]") || partes[1].equals("Y]") ||
                   partes[1].equals("SP]") || partes[1].equals("PC]")){ //Mira que la segunda parte tenga coincidencias con estas condiciones
                        idx=true;                   //retorna un verdadero validando que si es este tipo de direccionamiento
                }
                else{
                    idx=false;                      //retorna un falso validando que no es este tipo de direccionamiento
                }
            }
            return idx;                             //retorna un booleano para verificar si es [D,IDX] o no
    }//fin del metodo IdxD

    //METODO PARA [IDX2]
    static boolean Idx2C(String Operando){
        boolean idx=false;                          //esta es la bandera que retorna si es cierto que es ese tipo de direccionamiento o no
        String partes[]=Operando.split(",");        //separa el operando en dos partes
        String valor = partes[0].substring(1);      //guarda la primera parte del operando      
        if(valor.matches(".*\\d.*")){               //mira que la primera parte del operando sean unicamente digitos
            int numero = Integer.parseInt(valor);   //comvierte a valor en un entero
            if(numero<=65535 && (partes[1].equals("X]") || partes[1].equals("Y]") ||
                                 partes[1].equals("SP]") || partes[1].equals("PC]"))){ //Mira que la segunda parte tenga coincidencias con estas condiciones
                idx=true;                           //retorna un verdadero validando que si es este tipo de direccionamiento
            }
        }
        return idx;                                 //retorna un booleano para verificar si es [IDX2] o no
    }//fin del metodo Idx2C
    
    //METODO PARA BUSCAR ETIQUETA
    static boolean BuscarEtiqueta(String Etiqueta){
        boolean existe=false, fin=false;// Variable para indicar si la etiqueta existe y si hemos llegado al final de la estructura
        Linea Auxiliar = PrimerLinCod;// Variable auxiliar para iterar a través de la estructura

        while(existe==false && fin ==false){   // Bucle para recorrer la estructura hasta encontrar la etiqueta o llegar al final
            if(Auxiliar.getEtiqueta().equals(Etiqueta)){
                    existe=true;// La etiqueta existe, establece el indicador a true
            }
            else{
                if(Auxiliar!=FinLinCod){
                    Auxiliar=Auxiliar.getSiguiente();// Avanzar a la siguiente línea si no hemos llegado al final
                }
                else{
                    fin=true;// Establece el indicador a true
                }
            }
        }
        return existe;// Devuelve true si la etiqueta fue encontrada, de lo contrario, devuelve false
    }//Fin encontrar etiqueta
    
    public static void main(String[] args) {
        Leer();//Llamo el metodo
        if(PrimerLinCod!=null){
            Salvacion.GuardarADDR(PrimerLinCod);
            new Tabla().setVisible(true);
        }
        
    }   
}