
package parte_5;

import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class Salvacion {
    static boolean encontrado;//Varible que utilizaremos para saber si el codop existe
    static int coincidencias=0;
    static NodoSalvacion auxSalvacion=null;
    
    static String Separarform(String forma) {
        String campo3 = forma; //Almacena el source form en el campo 3
        String resultado; //Variable para  almacenar el resultado
        StringBuilder campo3Separado = new StringBuilder();// Construye la cadena dividida 
        for (int i = 0; i < campo3.length(); i += 2) {
            campo3Separado.append(campo3.substring(i, Math.min(i + 2, campo3.length()))); //Separamos la cadena cada dos digitos
            if (i + 2 < campo3.length()) {
                campo3Separado.append(",");//Agrega una coma en entre cada conjunto de dos digitos, menos en el cojunto final
            }
        }
        resultado = campo3Separado.toString();//StringBuilder se convierte en una cadena y se guarda en resultado
        return resultado;//Devuelve la cadena resultado con una coma cada dos digitos
    }//Fin de Separarform
    
    static void BuscarCodop(Linea auxiliar) {//Buscar es el codigo de operancion que se buscara en la salvacion
        String Buscar = auxiliar.getCodop();
        try {
            RandomAccessFile auxArchivo = new RandomAccessFile("Salvation_Tabop.txt", "r");//r es para solo leer el archivo
            long cursorActual;//Para saber donde estamos
            cursorActual = auxArchivo.getFilePointer();//Puntero en el archivo
            FileReader leerArchivo = new FileReader("Salvation_Tabop.txt");//leeo el archivo del exel en txt
            String lecturaLinea;
            encontrado = false;
            coincidencias = 0;
            IdentificacionDirectivas(auxiliar);
            //Aqui es donde empieza a leer por linea
            while (cursorActual != auxArchivo.length() && encontrado == false) {//mientras el lector no llegue al final del archivo
                lecturaLinea = auxArchivo.readLine();//leeo la linea
                cursorActual = auxArchivo.getFilePointer();
                String[] campos = lecturaLinea.split("\\s+");//Separamos el txt por tabuladores
                if (campos[0].equals(Buscar)) {//En la primera palabra del txt estan los codop asi que si esa palabra es igual al CODOP buscado, lo encotnramos
                    auxSalvacion = new NodoSalvacion(campos[0], campos[1], campos[2], Separarform(campos[3]), campos[4], campos[5]); // Crea un objeto "auxSalvacion" con los datos de la línea y los separa con luna coma cada dos digitos
                    coincidencias++;//Incrementa el contador de coincidencias
                    IdentificarADDR(auxiliar, auxSalvacion); // Llama a la función "IdentificarADDR"
                }//Fin comparacion con salvacion
            }//Fin del while
            if (!encontrado) {
                if (coincidencias == 0) {
                    auxiliar.setADDR("ERROR");
                    Parte_5.Errores.add("ERROR. ADDR no aceptado en codop: " + auxiliar.getEtiqueta() + " " + auxiliar.getCodop() + " porque NO EXISTE");
                } else if (coincidencias == 1 && (auxSalvacion.AddrMode.equals("REL") || auxSalvacion.AddrMode.equals("REL(9-bit)"))) {
                    IdentificacionREL(auxiliar, auxSalvacion);
                    if (!encontrado) {
                        auxiliar.setADDR("ERROR");
                        Parte_5.Errores.add("ERROR.  OPR fuera de rango en linea: " + auxiliar.getEtiqueta() + " " + auxiliar.getCodop() + " " + auxiliar.getOperando());
                    }
                } else if (coincidencias > 1) {
                    auxiliar.setADDR("ERROR");
                    Parte_5.Errores.add("ERROR. OPR fuera de rango en linea: " + auxiliar.getEtiqueta() + " " + auxiliar.getCodop() + " " + auxiliar.getOperando());
                } else {
                    auxiliar.setADDR("ERROR");
                    Parte_5.Errores.add("ERROR. ADDR no aceptado en codop: " + auxiliar.getEtiqueta() + " " + auxiliar.getCodop() + " porque no cuenta para el proyecto");
                }
            }
            leerArchivo.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//Fin leer y buscar en salvacion
    
    //Metodo para analizar vaor de rr en IDX
    static String calculoRR(String registro){
         switch(registro){//swittch de desicion
            case "X"://caso X = 00
                if(registro.equals("X")){//valida si registro = X
                registro = "00";//asigna valor 00
             }
            break;//quebra y siguiente...
            case "Y"://caso Y 01
                if(registro.equals("Y")){//valida si registro = Y
                registro = "01";//asigna valor 01
                }//quebra y siguiente...
            break;
            case "SP"://caso 10
                if(registro.equals("SP")){// valida igualdad a SP
                registro = "10";//valor = 10
                }//...
            break;
            case "PC": //caso 11
                if(registro.equals("PC")){//valida igualdad a PC
                registro = "11";//valor = 11
                }//.

            break;    
        }// fin switch rr
        return registro;
    }//fin de metodo RR
    
    //Metodo para pasar de decimal a Binario
    public static String decimalABinario(int numeroDecimal) {
        if (numeroDecimal < 0 || numeroDecimal > 15) {//valida que este entre el 0 -15
            return "Error"; // El número debe estar en el rango de 0 a 15 para representarse en 4 bits
        }
        StringBuilder resultado = new StringBuilder();
        for (int i = 3; i >= 0; i--) {
            int bit = (numeroDecimal >> i) & 1; // Obtén el i-ésimo bit del número
            resultado.append(bit);
        }
        return resultado.toString();
    }//fin metodo Decimal a binario
    
    //calcular complemento2 (c2)binario
    public static String calcularComplementoDos(String numeroBinario) {
        int longitud = numeroBinario.length();//toma valor del ultimo numero de izq. a derecha
        StringBuilder complemento = new StringBuilder();

        // Invertir los bits del número
        for (int i = 0; i < longitud; i++) {
            char bit = numeroBinario.charAt(i);
            complemento.append((bit == '0') ? '1' : '0');
        }

        // Sumar 1 al número invertido
        int carry = 1;
        for (int i = longitud - 1; i >= 0; i--) {
            char bit = complemento.charAt(i);
            if (bit == '1' && carry == 1) {
                complemento.setCharAt(i, '0');
            } else if (bit == '0' && carry == 1) {
                complemento.setCharAt(i, '1');
                carry = 0;
            }//fin del else if 
        }//termina de hacer las sumas

        return complemento.toString();
    }//fin de metodo para comlemento 2
    
    static String idxIncrement(String opr, String sourceform){
        String xb=" ", postbyte=" ";//Variables auxiliares para guardar los valores
        char signo=' ';//Indica que signo tiene
        int valor =0;//Guarda el valor del operando
        
        String frmbase [] = sourceform.split(",");//Separo el souceforme por byte
        if(frmbase[1].equals("xb")){//si el segundo byte del postbyte corresponde al xb
            postbyte=frmbase[0].concat(" ");//guardar el valor del byte calculado y un espacio despues
        }//Fin segundo byte xb
        else if(frmbase[2].equals("xb")){//Si es el tercero
            postbyte=frmbase[0].concat(" ").concat(frmbase[1].concat(" "));//guardo el valor de los dos calculados con espacios entre si
        }//fin el tercer byte corresponde a xb
        
        String operando[] = opr.split(",");//Separo el operando: valor,registro
        if (operando[1].startsWith("+") || operando[1].startsWith("-")) {//si es pre
            xb = "10";//10 corresponde a 1p de la tabla 4
            signo = operando[1].charAt(0); //Guardo el signo que tiene
            operando[1] = operando[1].substring(1); //obtengo el registro sin signo
        }//fin es pre
        else if (operando[1].endsWith("+") || operando[1].endsWith("-")) {//si termina con signo el registro
            xb = "11";//11 corresponde a 1p es 1 cuando es post (tabla 4)
            signo = operando[1].charAt(operando[1].length() - 1);//obtengo el signo
            operando[1] = operando[1].substring(0, operando[1].length() - 1);//obtengo el registro sin signo
        }//fin es post
        xb = calculoRR(operando[1]).concat(xb);//calculo el rr del valor binario y ya tengo rr1p de la tabla 4     
        valor = Integer.parseInt(operando[0]);//guardo el valor absoluto del operando
        if (signo == '+') {//Si es positivo
            valor = valor - 1; //Restar 1 al valor del operando
            xb = xb.concat(decimalABinario(valor));//agrego el valor en binario a xb para ya tener rr1pnnnn
        }//fin positivo
        else if (signo == '-') {//si es negativo
            xb = xb.concat(calcularComplementoDos(decimalABinario(valor)));//Concatena xb con ComplementoDos en binario, obteniendo ya la forma rr1pnnnn
        }//fin negativo
        postbyte = postbyte.concat(Integer.toHexString(Parte_5.binarioADecimal(xb)));//obtengo el postbyte con lo calculado y el binario obtenido en hexadecimal
        return postbyte.toUpperCase();
    }//postbyte del idx increment
    
    //metodo para calcular postbyte de los IDX forma Acc
    static String idxAcc(String opr, String sourceform){
        String postbyte=" ", xb="111";//valor de los 3 primeros bits de la forma 111rr1aa
        String frmbase [] = sourceform.split(",");//separa por comas los bits
        if(frmbase[1].equals("xb")){//valida que el segundo espacio sea igual a xb
            postbyte=frmbase[0].concat(" ");//iguala postbyte a la forms concatenada a un espacio a la derecha
        }//fin del if 
        else if(frmbase[2].equals("xb")){//valida si el form = "xb"
            postbyte=frmbase[0].concat(" ").concat(frmbase[1].concat(" "));
            //concatena la primera parte con la segunda entre espacios y lo guarda en la variable postbyte
        }//fin de else if
        
        String operando[] = opr.split(",");//array operando separado por comas
        xb=xb.concat(calculoRR(operando[1]).concat("1"));//valora a xb como...
        //xb, concatenado a el valor de RR, concatenado a 1.
        if(operando[0].equals("A")){//si operando = A
            xb=xb.concat("00"); //los ultimos 2 bites son 00
        }
        else if(operando[0].equals("B")){//si operando = B
            xb=xb.concat("01");//ultimos 2 bits son 01
        }
        else if (operando[0].equals("D")){//si operando = C
            xb=xb.concat("10");//ultimos 2 bits son 10
        }
        postbyte = postbyte.concat(Integer.toHexString(Parte_5.binarioADecimal(xb)));
        //da el valor a postbyte de postbyte concatenado a xb y lo convierte a Hexadecimal
        return postbyte.toUpperCase();
        //retorna el valor de postbyte hexadecimal y lo convierte a mayusculas.
    }
    
    /**
     * Esta funcion sirve para calcular la forma postbyte de un idx5b
     * @param opr Este es el operando del codop
     * @param sourceform Esta es la forma base del codop
     * @return Devuelve la forma calculada del idx5b
     */
    static String idx5b (String opr, String sourceform){
        String postbyte=" ", xb=" ";
        int valor=0;
        String frmbase [] = sourceform.split(",");                    //Guarda en este arreglo la forma base separada
        if(frmbase[1].equals("xb")){                                  //Identifica en la forma base el conjunto de bits xb
            postbyte=frmbase[0].concat(" ");                          //Quita el xb y guarda ensa parte en el postbyte
        } 
        else if(frmbase[2].equals("xb")){                             //si el xb esta en la segunda posicion lo quita tambien
            postbyte=frmbase[0].concat(" ").concat(frmbase[1].concat(" "));
        }
        if(opr.startsWith(",")){
            opr="0".concat(opr);
        }
        String operando[] = opr.split(",");
        xb=calculoRR(operando[1]).concat("0");                           //realiza el calculo de xb con la funcion calculoRR
        valor=Integer.parseInt(operando[0]);                             //Convierte el valor a entero
        if(valor==-16){                                                   //Verifica si el valo es igual a 16
            xb=xb.concat("10000");                                       //si es asi concatena eso
        }
        else if(valor<0){                                        //Si el valor es menor a 0 se le resta para la forma calculada
            valor=valor*-1;
            xb=xb.concat("1").concat(calcularComplementoDos(decimalABinario(valor)));//Se agrega un 1 antes del ComplementoDos en binario
        }
        else{
            xb=xb.concat("0").concat(decimalABinario(valor));//Se concatena '0' y la representación en binario del valor.
        }
        xb=Integer.toHexString(Parte_5.binarioADecimal(xb));// Se convierte el valor de xb (binario) a hexadecimal
        if(xb.length()==1){
            xb="0".concat(xb);
        }
        postbyte = postbyte.concat(xb);
        return postbyte.toUpperCase();                        //retorna la forma postbye y ecribe todo en mayusculas
    }
    
    static String idx1(String opr, String sourceform){
        String postbyte=" ", xb=" ", ff=" ";//auxiliares para calcular el postbyte
        String frmbase [] = sourceform.split(",");//separa el source form por byte
        if(frmbase[1].equals("xb") && frmbase[2].equals("ff")){//si la forma si es la correcta
            postbyte=frmbase[0].concat(" ");//Guarda el byte ya calculado
            String operando[] = opr.split(",");//se separa el opernado valor,registro
            xb="111".concat(calculoRR(operando[1])).concat("00");//Obtengo 111rr0z de la tabla 4
            if(operando[0].startsWith("-")){//Si es negativo
                xb=xb.concat("1");//obtener s ahora tengo completo 111rr0zs
                if(operando[0].equals("-256")){
                    ff="00";//es el byte correspondiente
                }//Para -256 calcular directo el ff
                else{
                    ff=Integer.toBinaryString(Integer.parseInt(operando[0].substring(1)));//Calcular valor en binario y guardarlo en ff
                    ff=calcularComplementoDos(ff);//Calcula el ComplmentoDos de ff
                    ff=Integer.toHexString(Parte_5.binarioADecimal(ff));//pasa el complemento a hexadecimal
                }//fin es negativo pero no 256
            }//fin el valor es negativo
            else{
                xb=xb.concat("0");//corresponde a s de 111rr0zs
                ff=Integer.toHexString(Integer.parseInt(operando[0]));//Lo pasa directamente a hexadecimal
            }//fin es positivo
        }//fin souce form correspondiente 
        if(ff.length()!=2){
            ff="0".concat(ff);
        }
        postbyte = postbyte.concat(Integer.toHexString(Parte_5.binarioADecimal(xb))).concat(" "+ff);
        return postbyte.toUpperCase();
    }//fin postbye idx1
    
    static String idx2(String opr, String sourceform) {//Inicia idx2
        String postbyte = " ", xb = "", ee = " ", ff = " ";//Inicializa las variables postbyte, xb, ee y ff, todas en blanco
        String frmbase[] = sourceform.split(",");//Divide el sourceform con comas

        if (frmbase[1].equals("xb") && frmbase[2].equals("ee") && frmbase[3].equals("ff")) {// Verifica si el formato de origen coincide con "xb,ee,ff"
            postbyte = frmbase[0].concat(" ");//Combina el primer elemento de "frmbase" con un espacio y lo asigna a "postbyte"
            String operando[] = opr.split(",");//Divide el operando con comas
          
            if(operando[0].startsWith("[")){
                operando[0] = operando[0].substring(1);
                operando[1]=operando[1].substring(0, operando[1].length()-1);
                xb = "111".concat(calculoRR(operando[1])).concat("011");//Para calculo de "xb" de [idx2]
            }
            else{
                xb = "111".concat(calculoRR(operando[1])).concat("010");// Crea una cadena "xb" que consta de "111" seguido del resultado de "calculoRR(operando[1])" y "010"
            }
            ee = Parte_5.validarDireccion(operando[0]);//Llama a la función "validarDireccion" de Parte_5 con el primer componente del operando
            ff = ee.substring(2, 4);//Extrae los caracteres de "ee" desde la posición 2 hasta la 3
            ee = ee.substring(0, 2);//Extrae los caracteres de "ee" desde la posición 0 hasta la 1
        }//Fin de if

        postbyte = postbyte + Integer.toHexString(Parte_5.binarioADecimal(xb)) + " " + ee + " " + ff;//Convierte la parte "xb" en formato binario a decimal y luego a hexadecimal, y concatena el valor en hexadecimal de "xb", "ee" y "ff"
        return postbyte.toUpperCase();//Convierte la cadena resultante a mayúsculas y la devuelve como resultado
    }//Termina idx2

    static void IdentificarADDR(Linea LinCod,NodoSalvacion AUX){
        if(LinCod.getOperando().equals(" ")){//Primer caso no hay operando
            if(AUX.Operando.equals("-")){//La estructura de operando que coincide
                LinCod.setADDR("INH");//ADDR correspondiente
                LinCod.setPorCalcular(AUX.byteCalcular+ " bytes");
                LinCod.setForm(AUX.SourceForm);        //guarda la forma base en el arreglo
                LinCod.setCop(AUX.SourceForm);         //guarda la forma calculada en el arreglo
                LinCod.setSize(AUX.byteTotal+" bytes");
                encontrado=true;//Ya lo encontro, puede terminar la busquerda
            }//Fin es inh
        }//Fin no hay operando
        else if(LinCod.getOperando().startsWith("#")){//Si el operando parece de tipo IMM
            if(AUX.Operando.equals("#opr8i")||AUX.Operando.equals("#opr16i")){//Puede ser de cualquiera de estas dos estructuras para coincidir
                if(Parte_5.IMM(LinCod.getOperando(),AUX.Operando)){//Evaluo si si es IMM
                    //GUARDA LOS DATOS CORRESPONDIENTES
                    LinCod.setADDR("IMM"); 
                    LinCod.setPorCalcular(AUX.byteCalcular+ " bytes");
                    LinCod.setForm(AUX.SourceForm);
                    LinCod.setSize(AUX.byteTotal+" bytes");
                    encontrado=true;
                }//Fin si es IMM
                else if(Parte_5.validarEtiq(LinCod.getOperando().substring(1))) {//Si el operando es una etiqueta
                    //Guarda los datos correspondientes, la validación del operando será después
                    LinCod.setADDR("IMM");
                    LinCod.setPorCalcular(AUX.byteCalcular + " bytes");
                    LinCod.setSize(AUX.byteTotal + " bytes");
                    LinCod.setForm(AUX.SourceForm);
                    encontrado = true;
                }
            }
        }
        else if(LinCod.getOperando().startsWith("[D")){//Si comienza de esta forma el operando
            if(AUX.Operando.equals("[D,xysp]")){//Si estructura en la salvacion debe coincidir con esta
                if(Parte_5.IdxD(LinCod.getOperando())){//Valido que este bien el operando
                    LinCod.setADDR("[D,IDX]"); //Se identifica el modo de direccionamiento
                    LinCod.setPorCalcular(AUX.byteCalcular+ " bytes"); //Guarda la cantidad de bytes que faltan de calcular
                    LinCod.setSize(AUX.byteTotal+" bytes");//Guarda los bytes totales
                    encontrado=true;//Activo la bandera
                    LinCod.setForm(AUX.SourceForm);//Guarda el source form
                    String operando[]=LinCod.getOperando().split(",");//Separo el operando -> D,registro
                    //CALCULO DEL BYTE DE LA FORMA 111rr111, de la tabla 4
                    String bin="111".concat(calculoRR(operando[1].substring(0, operando[1].length()-1))).concat("111");
                    bin= Integer.toHexString(Parte_5.binarioADecimal(bin)).toUpperCase();//calculo el hexadecimal del valor
                    if(bin.length()==1){
                        bin="0".concat(bin);//Completa a byte
                    }//Fin si hace falta completar el byte
                    LinCod.setCop(AUX.SourceForm.substring(0, 2).concat(" ").concat(bin));
                }//Si es de la forma [D,IDX]
            }//Fin si es [D,IDX]
        }//Fin comienza con [D   
        else if(LinCod.getOperando().startsWith("[")){//Si comienza de esta forma el operando
            if(AUX.Operando.equals("[oprx16,xysp]")){//Si estructura en la salvacion debe coincidir con esta
                if(Parte_5.Idx2C(LinCod.getOperando())){//Evalua que el operando
                    LinCod.setADDR("[IDX2]");//Establece la dirección de memoria como "[IDX2]".
                    LinCod.setPorCalcular(AUX.byteCalcular+ " bytes");//Establece la cantidad de bytes por calcular.
                    LinCod.setSize(AUX.byteTotal+" bytes");//Establece el tamaño total en bytes.
                    encontrado=true;//Marca que se ha encontrado una coincidencia.
                    LinCod.setForm(AUX.SourceForm);
                    LinCod.setCop(idx2(LinCod.getOperando(), LinCod.getForm()));
                }
            }//Fin si es [IDX2]
        }    
        else if(LinCod.getOperando().contains(",")){//Si tiene una coma pero no los elementos anteriores
            if(AUX.Operando.equals("oprx0_xysp")){//Debe coincidir con esta estructura
                if(!(Parte_5.IDX(LinCod.getOperando()).equals("0"))){//Valido que el operando completo tenga la forma completa
                    LinCod.setADDR(Parte_5.IDX(LinCod.getOperando()));
                    LinCod.setForm(AUX.SourceForm);
                    if(LinCod.getADDR().equals("IDX(pre-inc)")){
                        LinCod.setCop(idxIncrement(LinCod.getOperando(), LinCod.getForm()));
                    }
                    else if(LinCod.getADDR().equals("IDX(acc)")){
                        LinCod.setCop(idxAcc(LinCod.getOperando(), LinCod.getForm()));
                    }
                    else{
                        LinCod.setCop(idx5b(LinCod.getOperando(), LinCod.getForm()));
                    }
                    LinCod.setPorCalcular(AUX.byteCalcular+ " bytes");
                    LinCod.setSize(AUX.byteTotal+" bytes");
                    encontrado=true;
                }
            }//Fin estructura correspondiente
            else if(AUX.Operando.equals("oprx9,xysp")){//Si llego hasta esta estructura
                if(Parte_5.IDX1(LinCod.getOperando())){//Debe ser IDX1
                    LinCod.setADDR("IDX1");
                    LinCod.setPorCalcular(AUX.byteCalcular+ " bytes");
                    LinCod.setSize(AUX.byteTotal+" bytes");
                    LinCod.setForm(AUX.SourceForm);
                    LinCod.setCop(idx1(LinCod.getOperando(), LinCod.getForm()));
                    encontrado=true;
                }//Validar operando con de IDX
            }
            else if(AUX.Operando.equals("oprx16,xysp")){//Si llego hasta esta estructura
                if(Parte_5.IDX2(LinCod.getOperando())){//Evaluar si es IDX2
                    LinCod.setADDR("IDX2");
                    LinCod.setPorCalcular(AUX.byteCalcular+ " bytes");
                    LinCod.setSize(AUX.byteTotal+" bytes");
                    encontrado=true;
                    LinCod.setForm(AUX.SourceForm);
                    LinCod.setCop(idx2(LinCod.getOperando(), LinCod.getForm()));
                    encontrado=true;
                }//Fin si es IDX2
            }
        }//Fin tiene comas   
        else if(Parte_5.ConvertirADecimal(LinCod.getOperando())!=-1){//Si es directamente un valor en cualquier base
            if(AUX.Operando.equals("opr8a")&&Parte_5.ConvertirADecimal(LinCod.getOperando())<256 
                && Parte_5.ConvertirADecimal(LinCod.getOperando())>=0){//Forma de DIR
                    LinCod.setADDR("DIR");
                    LinCod.setPorCalcular(AUX.byteCalcular+ " bytes");
                    LinCod.setSize(AUX.byteTotal+" bytes");
                    LinCod.setForm(AUX.SourceForm);
                    encontrado=true;
            }//Fin es DIR
            else if(AUX.Operando.equals("opr16a")){//Estructura de EXT
               if(Parte_5.EXT(LinCod.getOperando())){
                    LinCod.setADDR("EXT");
                    LinCod.setPorCalcular(AUX.byteCalcular+ " bytes");
                    LinCod.setSize(AUX.byteTotal+" bytes");
                    LinCod.setForm(AUX.SourceForm);
                    encontrado=true;
               }
            }//Fin es EXT
        }//Fin es DIR o EXT
        else if(Parte_5.validarEtiq(LinCod.getOperando())){//DIR O EXT con etiqueta
            if (AUX.Operando.equals("opr8a")) {//Si se encontró coincidencia con un directo
                //Se va a guardar como directo si tiene una etiqueta y despues se identifica si es DIR o EXT de manera correcta
                LinCod.setADDR("DIR");
                LinCod.setPorCalcular(AUX.byteCalcular + " bytes");
                LinCod.setSize(AUX.byteTotal + " bytes");
                LinCod.setForm(AUX.SourceForm);
                encontrado = true;
            }
        }
    }
    
    static void IdentificacionREL(Linea LinCod,NodoSalvacion AUX){
        if(AUX.AddrMode.equals("REL")){//Si el operando es REL, hay dos posibilidades
            if(AUX.Operando.equals("rel8")){//Si es de este tipo
                if(Parte_5.validarEtiq(LinCod.getOperando())){//Cuando tiene una palabra
                    LinCod.setADDR("REL (8b)");
                    LinCod.setPorCalcular(AUX.byteCalcular+ " bytes");
                    LinCod.setSize(AUX.byteTotal+" bytes");
                    LinCod.setForm(AUX.SourceForm);
                    encontrado=true;
                }//Fin si coincide con una etiqueta
            }//Fin es rel8
            if(AUX.Operando.equals("rel16")){//Si es de este tipo
                if(Parte_5.validarEtiq(LinCod.getOperando())){//Cuando el operando es una etiqueta
                    LinCod.setADDR("REL (16b)");
                    LinCod.setPorCalcular(AUX.byteCalcular+ " bytes");
                    LinCod.setSize(AUX.byteTotal+" bytes");
                    LinCod.setForm(AUX.SourceForm);
                    encontrado=true;
                }//Fin si existe la etiqueta
            }//Fin es rel16
        }//Fin es relativo
        else if(AUX.AddrMode.equals("REL(9-bit)")){//Si es de este tipo
            if(LinCod.getOperando().contains(",")){//Debe tener una copa
                String auxiliar[]=LinCod.getOperando().split(",");//Separar por comas
                if(auxiliar.length==2){//Debe tener solo dos bloques
                    if(auxiliar[0].equals("A") || auxiliar[0].equals("B") || auxiliar[0].equals("D") ||
                        auxiliar[0].equals("X") || auxiliar[0].equals("Y") || auxiliar[0].equals("SP")){//Estructuras validas para el primer bloque
                        if(Parte_5.validarEtiq(auxiliar[1])){//Si la parte dos es una etiqueta
                            LinCod.setADDR("REL (9b)");
                            LinCod.setPorCalcular(AUX.byteCalcular+ " bytes");
                            LinCod.setSize(AUX.byteTotal+" bytes");
                            LinCod.setForm(AUX.SourceForm);
                            encontrado=true;
                        }//Fin parte dos con etiqueta valida
                    }//Fin estructura del primer bloque
                }//Fin si son dos bloques
                        }//Fin si contiene una coma
        }//Fin rel(9-bit)
        else{
            System.out.println("CODOP O ADDR NO RECONOCIDO");
            System.out.println("CODOP= "+AUX.CODOP);
            System.out.println("OPERANDO= "+LinCod.getOperando());
            System.out.println("Forma del Operando= "+AUX.Operando);
            LinCod.setADDR( "OPR fuera de rango");
        }//Fin codop que no vamos a evaluar
    }
    
    static void IdentificacionDirectivas(Linea LinCod){ // Método para identificar las directivas
        String auxiliar = LinCod.getCodop();
        String tamPala = " ";
        String postbyte=" ";//Variable que guarda el postbyte
        String postbyteAux = " ";//Auxiliar para calcular el postbyte
        if(auxiliar.contains(".")){  // Verificamos si el CODOP contiene un punto (.)
            String[] campos = auxiliar.split("\\.");
            if(campos.length==2){
                auxiliar=campos[0];
                tamPala = campos[1];// Si contiene un punto, dividimos el CODOP y el tamaño de la palabra
                encontrado=true;
            }
        } //fin de if
        switch(auxiliar){
            case "ORG": // Directiva ORG: Establece la dirección de inicio del programa
                if(Parte_5.ConvertirADecimal(LinCod.getOperando())==-1){
                    LinCod.setADDR("ERROR");
                    Parte_5.Errores.add("ERROR. opr fuera de rango en: "+LinCod.getEtiqueta()+" "+LinCod.getCodop()+" "+LinCod.getOperando());
                } 
                else{
                    LinCod.setADDR("DIRECT");
                    encontrado=true;
                } //Fin de la directiva ORG
            break;
            case "END":  // Directiva END: Marca el final del programa
                LinCod.setADDR("DIRECT");
                encontrado=true;
            break; //Fin de la directiva END
            case "EQU":  // Directiva EQU: Asigna un valor constante a una etiqueta
                if(LinCod.getEtiqueta().equals(" ") || Parte_5.ConvertirADecimal(LinCod.getOperando())==-1){  
                    LinCod.setADDR("ERROR");
                    Parte_5.Errores.add("ERROR en: "+LinCod.getEtiqueta()+" EQU "+LinCod.getOperando()+" porque");
                    if(LinCod.getEtiqueta().equals(" ")){
                        Parte_5.Errores.add(" no tiene etiqueta");
                    }
                    else{
                        Parte_5.Errores.add(" el OPR esta fuera de rango");
                    }
                }
                else{
                    LinCod.setADDR("DIRECT");
                    encontrado=true;
                }
            break; //fin de la directiva EQU
            case "DC": // Manejo de la directiva "DC" que define constantes o datos
                int tam=0,auxtam=0;
                boolean oprBien=true,mayor255=false;
                if(!(LinCod.getOperando().equals(" "))){ 
                    if(LinCod.getOperando().contains(",")){ // Si el operando contiene comas, hay múltiples valores
                        String [] partOpr = LinCod.getOperando().split(",");//Separar por valores
                        auxtam=partOpr.length;
                        for(int i=0; i<auxtam; i++){//para revisar cada valor
                            if(Parte_5.ConvertirADecimal(partOpr[i])!=-1){ //Si el operando esta bien
                               tam++;//Numero de valores para calcular el peso en memoria
                               if(Parte_5.ConvertirADecimal(partOpr[i])>255){ //Validar si estaria fuera de rango en un .b
                                    mayor255=true;
                                }//Fin if es mayor de 255
                            }//Fin el operando es un valor en alguna base
                            else if(partOpr[i].startsWith("\"")&&partOpr[i].endsWith("\"")){//Si empieza y termina con comillas
                                tam=tam+partOpr[i].substring(1, partOpr[i].length()-1).length();
                            }//Fin el valor esta en ascci
                            else{
                                System.out.println("entra con "+partOpr[i]);
                                oprBien=false;
                            }//Fin el operando tiene algun error
                        }//Fin for
                    }//Fin contiene comas
                    else if(LinCod.getOperando().startsWith("\"")&&LinCod.getOperando().endsWith("\"")){  // Si el operando está entre comillas, es una cadena
                        tam=LinCod.getOperando().substring(1, LinCod.getOperando().length()-1).length();//el tamaño es igual al numero de caracteres entre las comillas
                    }//Fin, esta en codigo ascci
                    
                    if(tamPala.equals("B")){
                        if(tam!=0){ // Si el tamaño de la palabra es en bytes
                            if(oprBien && !mayor255){
                                LinCod.setSize(String.valueOf(tam)+" bytes");
                                LinCod.setADDR("DIRECT");
                                if(LinCod.getOperando().contains(",")){
                                    String [] valores = LinCod.getOperando().split(",");//Separar por valores
                                    for(int i=0;i<valores.length;i++){
                                        if(valores[i].contains("\"")){
                                            for(int j=1; j<valores[i].length()-1;j++){
                                                int valorASCII = (int) valores[i].charAt(j);
                                                postbyteAux= Integer.toHexString(valorASCII);//Metodo para identificar el ascci
                                                if(postbyteAux.length()==1){
                                                    postbyteAux="0".concat(postbyteAux);//Completar a byte
                                                }
                                                if(postbyte.equals(" ")){
                                                    postbyte=postbyteAux;//Si es el primer byte 
                                                }
                                                else{
                                                    postbyte=postbyte.concat(" ").concat(postbyteAux);//Si ya hay un byte antes
                                                }
                                            }
                                        }//Fin es de codigo ascci
                                        else{
                                            postbyteAux=Integer.toHexString(Parte_5.ConvertirADecimal(valores[i])).toUpperCase();//Calcular el valor hexadecimal del operando
                                            if(postbyteAux.length()==1){
                                                    postbyteAux="0".concat(postbyteAux);//Completar a byte
                                            }
                                            if (postbyte.equals(" ")) {
                                                postbyte = postbyteAux;//Si es el primer byte calculado
                                            } else {
                                                postbyte = postbyte.concat(" ").concat(postbyteAux);//Si ya hay un byte antes
                                            }
                                        }
                                        LinCod.setCop(postbyte);//Guarda el postbyte
                                    }
                                }
                                else {
                                    if (LinCod.getOperando().contains("\"")) {
                                        for (int j = 1; j < LinCod.getOperando().length() - 1; j++) {
                                            int valorASCII = (int) LinCod.getOperando().charAt(j); //Guardo el valor en el ascii
                                            postbyteAux = Integer.toHexString(valorASCII);//Calcula el valor hexadecimal del ascii
                                            if (postbyteAux.length() == 1) {
                                                postbyteAux = "0".concat(postbyteAux);//Completa a byte
                                            }
                                            if (postbyte.equals(" ")) {
                                                postbyte = postbyteAux;//Es el primer byte
                                            } else {
                                                postbyte = postbyte.concat(" ").concat(postbyteAux);//Ya se habia calculado un byte
                                            }
                                        }
                                        LinCod.setCop(postbyte);//Guardar el postbyte
                                    }
                                }
                            }
                            else{
                                LinCod.setADDR("ERROR");//Si es incorrecto
                                Parte_5.Errores.add("ERROR. OPR fuera de rango en: "+LinCod.getEtiqueta()+" "+LinCod.getCodop()+" "+LinCod.getOperando());
                            }
                        }//Fin, hay mas de un valor en el operando
                        else{
                            //Si el operando es un valor
                            if (Parte_5.ConvertirADecimal(LinCod.getOperando()) != -1 && Parte_5.ConvertirADecimal(LinCod.getOperando()) < 255) {
                                LinCod.setSize("1 bytes");
                                LinCod.setADDR("DIRECT");
                                //Calcular el valor hexadecimal del operando
                                postbyteAux = Integer.toHexString(Parte_5.ConvertirADecimal(LinCod.getOperando())).toUpperCase();
                                if (postbyteAux.length() == 1) {
                                    postbyteAux = "0".concat(postbyteAux);//Completar a byte
                                }
                                if (postbyte.equals(" ")) {
                                    postbyte = postbyteAux;//Es el primer byte
                                } else {
                                    postbyte = postbyte.concat(" ").concat(postbyteAux);//Ya se habia calculado un byte
                                }
                                LinCod.setCop(postbyte);//Guardar el postbyte en el asm
                            }
                            else{
                                LinCod.setADDR("ERROR");
                                Parte_5.Errores.add("ERROR.  OPR fuera de rango en: "+LinCod.getEtiqueta()+" "+LinCod.getCodop()+" "+LinCod.getOperando());
                            }
                        }//Fin es un solo valor
                    }//Fin es de tamaño B
                    else if(tamPala.equals("W")){
                        if(tam!=0){ // Si el tamaño de la palabra es en palabras (2 bytes por palabra)
                            LinCod.setSize(String.valueOf(tam * 2) + " bytes");
                            LinCod.setADDR("DIRECT");
                            if (LinCod.getOperando().contains(",")) {
                                String[] valores = LinCod.getOperando().split(",");//Separar por valores
                                for (int i = 0; i < tam; i++) {
                                    if (valores[i].contains("\"")) {
                                        for (int j = 1; j < valores[i].length() - 1; j++) {
                                            int valorASCII = (int) valores[i].charAt(j);
                                            postbyteAux = Integer.toHexString(valorASCII);
                                            if (postbyteAux.length()< 4) {
                                                for(int m=postbyteAux.length();m<4;m++){
                                                    postbyteAux = "0".concat(postbyteAux);
                                                }
                                            }
                                            postbyteAux=postbyteAux.substring(0, 2).concat(" ").concat(postbyteAux.substring(2, 4));
                                            if (postbyte.equals(" ")) {
                                                postbyte = postbyteAux;
                                            } else {
                                                postbyte = postbyte.concat(" ").concat(postbyteAux);
                                            }
                                        }
                                    }//Fin es de codigo ascci
                                    else {
                                        postbyteAux = Integer.toHexString(Parte_5.ConvertirADecimal(valores[i])).toUpperCase();
                                        if (postbyteAux.length() < 4) {
                                            for (int m = postbyteAux.length(); m < 4; m++) {
                                                postbyteAux = "0".concat(postbyteAux);
                                            }
                                        }
                                        postbyteAux=postbyteAux.substring(0, 2).concat(" ").concat(postbyteAux.substring(2, 4));
                                        if (postbyte.equals(" ")) {
                                            postbyte = postbyteAux;
                                        } else {
                                            postbyte = postbyte.concat(" ").concat(postbyteAux);
                                        }
                                    }
                                    LinCod.setCop(postbyte); //Guarda el postbyte en el objeto
                                }
                            } else {
                                if (LinCod.getOperando().contains("\"")) {
                                    for (int j = 1; j < LinCod.getOperando().length() - 1; j++) {
                                        int valorASCII = (int) LinCod.getOperando().charAt(j);
                                        postbyteAux = Integer.toHexString(valorASCII);
                                        if (postbyteAux.length() < 4) {
                                            for (int m = postbyteAux.length(); m < 4; m++) {
                                                postbyteAux = "0".concat(postbyteAux);
                                            }
                                        }
                                        postbyteAux=postbyteAux.substring(0, 2).concat(" ").concat(postbyteAux.substring(2, 4));
                                        if (postbyte.equals(" ")) {
                                            postbyte = postbyteAux;
                                        } else {
                                            postbyte = postbyte.concat(" ").concat(postbyteAux);
                                        }
                                    }
                                    LinCod.setCop(postbyte);
                                }
                            }
                        } else { // Manejo de otros casos de tamaño de palabra no definidos
                            if (Parte_5.ConvertirADecimal(LinCod.getOperando()) != -1) {
                                LinCod.setSize("2 bytes");
                                LinCod.setADDR("DIRECT");
                                postbyteAux = Integer.toHexString(Parte_5.ConvertirADecimal(LinCod.getOperando())).toUpperCase();
                                if (postbyteAux.length() < 4) {
                                    for (int m = postbyteAux.length(); m < 4; m++) {
                                        postbyteAux = "0".concat(postbyteAux);
                                    }
                                }
                                postbyteAux = postbyteAux.substring(0, 2).concat(" ").concat(postbyteAux.substring(2, 4));
                                if (postbyte.equals(" ")) {
                                    postbyte = postbyteAux;
                                } else {
                                    postbyte = postbyte.concat(" ").concat(postbyteAux);
                                }
                                LinCod.setCop(postbyte);
                            } else {
                                LinCod.setADDR("ERROR");
                                Parte_5.Errores.add("ERROR.  OPR fuera de rango en: "+LinCod.getEtiqueta()+" "+LinCod.getCodop()+" "+LinCod.getOperando());
                            }
                        }
                    }//Fin es de tamaño w
                    else{
                        LinCod.setADDR("ERROR");
                        Parte_5.Errores.add("ERROR.  ADDR no aceptado en: "+LinCod.getEtiqueta()+" "+LinCod.getCodop()+" "+LinCod.getOperando()+ " porque el tamano de la palabra es incorrecto");
                    }
                }
                else{
                    LinCod.setADDR("ERROR");
                    Parte_5.Errores.add("ERROR.  OPR fuera de rango en codop: "+LinCod.getEtiqueta()+" "+LinCod.getCodop()+" con opr: "+LinCod.getOperando());
                }
            break; //Fin de DC
            case "DS":  // Directiva DS: Reserva espacio de memoria en bytes
                String opr = LinCod.getOperando().toString();
                if(opr.matches("\\d+")){
                    if(tamPala.equals("B")){
                        LinCod.setADDR("DIRECT");
                        LinCod.setSize(LinCod.getOperando()+" bytes");
                        for(int i=0;i<Integer.parseInt(LinCod.getOperando());i++){
                            if(postbyte.equals(" ")){
                                postbyte="00";
                            }
                            else{
                                postbyte=postbyte.concat(" 00");
                            }
                        }
                        LinCod.setCop(postbyte);
                    }
                    else if(tamPala.equals("W")){ // Reserva espacio de memoria en palabras (2 bytes por palabra)
                        LinCod.setADDR("DIRECT");
                        LinCod.setSize(String.valueOf(Integer.parseInt(LinCod.getOperando())*2)+" bytes");
                        for(int i=0;i<Integer.parseInt(LinCod.getOperando());i++){
                            if(postbyte.equals(" ")){
                                postbyte="00 00";
                            }
                            else{
                                postbyte=postbyte.concat(" 00 00");
                            }
                        }
                        LinCod.setCop(postbyte);
                    }
                    else{
                        LinCod.setADDR("ERROR");
                        Parte_5.Errores.add("ERROR.  ADDR no aceptado en: "+LinCod.getEtiqueta()+" "+LinCod.getCodop()+" "+LinCod.getOperando()+ " porque el tamano de la palabra es incorrecto");
                    }
                }
                else{
                    LinCod.setADDR("ERROR");
                    Parte_5.Errores.add("ERROR.  OPR fuera de rango en: "+LinCod.getEtiqueta()+" "+LinCod.getCodop()+" "+LinCod.getOperando());
                }
            break;
            default:
                encontrado=false;
            break;
        }
        if(encontrado==true){
            if(!((LinCod.getCodop().contains("DS")) || (LinCod.getCodop().contains("DC"))) && !(LinCod.getADDR().equals("ERROR"))){
                LinCod.setSize("0 bytes");
            }
            if(!(LinCod.getADDR().equals("ERROR"))){
                LinCod.setPorCalcular("0 bytes");
            }
        } //Fin del switch
    } // Fin IdentificacionDirectivas
    
    
    
}//Fin clase