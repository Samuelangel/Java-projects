package parte_5;

import java.util.ArrayList;
import java.util.List;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Proceso_S19 {

    static List<S19> DatosS19 = new ArrayList<S19>();//Variable para guardar los datos del S19
    static S19 AuxS19; //Objeto con las columnas del s19
    static String archivoASM; //Archivo donde se guarda
    static int cantidadS1=0; //Auxiliar para calcular el S5

    public static String NombreASM() {
        String nombreArchivo = archivoASM; //Obtengo el nombre del archivo
        String data = ""; //Variable para guardar el hexadecimal
        if(archivoASM.equals("P5ASM.asm")) {

        } else {//Si es una dirección en la computadora
            // Obtener un objeto Path
            Path path = Paths.get(archivoASM);
            // Obtener el nombre del archivo
            nombreArchivo = path.getFileName().toString();
        }//Fin else
        for (int i = 0; i < nombreArchivo.length(); i++) {
            char caracter = nombreArchivo.charAt(i);//Obtener caracter
            int codigoASCII = (int) caracter;//obtengo el valor en ascii
            String hexa = Integer.toHexString(codigoASCII).toUpperCase();  // Convertir a hexadecimal en mayusculas
            if(i!=nombreArchivo.length()-1){
                data = data + hexa.concat(" ");//separa por espacios
            }//Fin if
            else{
                data = data + hexa;//Lo pone junto
            }//fin else
        }//Fin for
        //System.out.println();
        return data;
    }

    public static String cc(String data) {
        String bytes[] = data.split(" ");
        int NumeroDeBytes = bytes.length;
        int resultado = NumeroDeBytes + 3;
        // Asegurarse de que el resultado tenga siempre dos dígitos
        String sumaDeBytes = String.format("%02X", resultado);
        return sumaDeBytes;
    }//Fin calcular cc (cantidad de bytes)
    
    public static String sumarHexadecimal(String hex1) {
        // Separar los valores hexadecimales por espacios
        String[] hexArray = hex1.split("\\s+");
        int sumaDecimal = 0;//Variable que guarada la suma
 
        for(int i=0; i<hexArray.length;i++){
            sumaDecimal = sumaDecimal+Parte_5.ConvertirADecimal("$".concat(hexArray[i]));
        }
        // Convertir la suma a formato hexadecimal
        String sumaHexadecimal = Integer.toHexString(sumaDecimal).toUpperCase();
        // Devolver el resultado
        return sumaHexadecimal;
    }//Fin sumar en hexadecimal
    
    public static String ck(String cc, String addr, String data) {
        String suma = "00";
        String bitLessImpor = " ";
        int entero = 0;
        suma=sumarHexadecimal(cc.concat(" ").concat(data).concat(" ").concat(addr));//Suma de bytes

        if(suma.length() < 2){
            bitLessImpor=suma;
        }//Fin guardar valor en hexa de la suma
        else{
            bitLessImpor = suma.substring(suma.length()-2,suma.length());
        }//Fin si es un byte identificar el menos importante
        entero = Parte_5.ConvertirADecimal("$".concat(bitLessImpor)); //valor decimal de la suma
        suma=Fase2.decimalABinario(entero);//Calcular valor en binario
        //COMPLETAR A BYTE
        suma = Fase2.completarBinarioAByte(suma, 8);
        
        // CALCULO DEL COMPLEMENTO A1
        StringBuilder complementoUno = new StringBuilder();
        for (int i = 0; i < suma.length(); i++) {
            char bit = suma.charAt(i);
            complementoUno.append((bit == '0') ? '1' : '0'); // Invertir cada bit del número binario
        }//Fin sacar complemento a1 
        
        //Calcular hexadecimal del complemento uno
        bitLessImpor = Integer.toHexString(Parte_5.ConvertirADecimal("%".concat(complementoUno.toString())));
        if (bitLessImpor.length() < 2) {
            bitLessImpor = "0".concat(bitLessImpor);
        }//Completar a byte
        return bitLessImpor.toUpperCase();
    }//Fin calculo de ck

    public static void S0() {
        AuxS19 = new S19 ("S0"," ","00 00"," "," ");//Instancio mi objeto
        AuxS19.setData(NombreASM());//En data va el valos ascii del nombre, para eso uso ese metodo
        AuxS19.setCc(cc(AuxS19.getData()));//Calculo cc con el metodo para contar los bytes
        AuxS19.setCk(ck(AuxS19.getCc(),AuxS19.getAddr(),AuxS19.getData()));//calculo ck una vez que tengo todos los bytes
        DatosS19.add(AuxS19);
    }//Fin obtener s0
    
    public static String ObtenerPostbytes(){
        String postbytes = " ";
        for(Linea asm:Parte_5.LineasASM){//Recorro todas las lineas del acm
            if(!(asm.getCop().equals(" "))){//Si tiene peso en memoria
                if(postbytes.equals(" ")){//Si es el primero con valor
                    postbytes = asm.getCop();
                }//Fin primero con valor
                else{
                    postbytes = postbytes.concat(" ").concat(asm.getCop());
                }//Si no es el primero con valor
            }//Fin si tiene peso en memoria
        }//Fin recorrer el asm
        return postbytes;
    }//Fin Obterner bytes
    
    public static void S1(){
        String datas[] = ObtenerPostbytes().split(" ");//Obtengo solo los bytes del cop del asm
        int bytes = datas.length;//Para saber cuantos bytes tengo
        String addr = Conloc.conlocOrg;//Identificar la posicion de memoria donde empieza el s1
        int aux1, aux2;//Variables auxiliares para recorrer datas
        cantidadS1 = bytes/16;//Cada s1 es de 16, entonces esto se hace para saber cuantos S1 tendre
        if(bytes % 16!=0){
            cantidadS1++;//Si me sobra al dividir en 16 significa que hace falta otro s1
        }//Fin sobra al dividir en 16
        for(int i=0; i<cantidadS1;i++){//For para hacer lo mismo para cada S1
            aux1=i*16;//cada S1 empieza en esta posicion de data
            if(bytes-aux1<16){
                aux2=bytes-aux1;
                aux2=aux2+aux1;
            }//Fin terminar en el fin de data
            else{
                aux2=aux1+16;
            }//Fin terminar de guardar en ese S1 hasta uno antes de un multiplo de 16 en data
            AuxS19 = new S19 ("S1"," "," "," "," ");//Instancio un objeto nuevo
            //Guardo el addr por bytes
            AuxS19.setAddr(addr.substring(0, 2).concat(" ").concat(addr.substring(2, 4)));
            for(int j=aux1; j<aux2;j++){
                if(bytes>j){
                   if(j%16==0){
                       AuxS19.setData(datas[j]);//Si es el primer postbyte de ese S1, no necesita espacio ni concatenar con lo anterior 
                   }//Fin primer postbyte
                   else{
                       AuxS19.setData(AuxS19.getData().concat(" ").concat(datas[j]));//Concateno lo anterior con el siguiente byte
                   }//Fin no es el primer postbyte
                }//Fin si hay un siguiente byte
            }//Fin for para recorrer data
            AuxS19.setCc(cc(AuxS19.getData()));//Calculo cc
            AuxS19.setCk(ck(AuxS19.getCc(), AuxS19.getAddr(), AuxS19.getData()));//calculo ck
            DatosS19.add(AuxS19);//Para agragra el nuevo s1 al s19
            addr=Conloc.sumarHexadecimal(addr, 16);//El conloc avanza para el siguiente s1
            for(int x=addr.length();x<4;x++){
                addr="0".concat(addr);
            }
        }//Fin for para cada S1
    }//Fin calculos de S1
    
    public static void S5(){
        String s1 = Integer.toHexString(cantidadS1);//Identifico la cantidad de s1
        AuxS19 = new S19 ("S5"," "," "," "," ");//Instancio mi objeto
        switch (s1.length()) {//Para completar a dos bytes
            case 1:
                s1="00 0".concat(s1);
                break;
            case 2:
                s1="00 ".concat(s1);
                break;
            case 3:
                s1="0".concat(s1.substring(0)).concat(" ").concat(s1.substring(1, 3));
                break;
            case 4:
                s1=s1.substring(0, 2).concat(" ").concat(s1.substring(2, 4));
                break;
            default:
                break;
        }//Switch para completar el addr de la manera corracta
        AuxS19.setAddr(s1);//Guardo el addr, con base a la calidad de s1 en dos bytes
        AuxS19.setCc("03");//cc es siempre este valor
        AuxS19.setCk(ck(AuxS19.getCc(), AuxS19.getAddr(), "00"));//calculo ck,data no tiene bytes
        DatosS19.add(AuxS19);//Para agregar el s5 al s19
    }//Fin calculos para S5
    
    //calculo para el s9 sencillo
    public static void S9Sencillo(){
         AuxS19 = new S19 ("S9"," "," "," "," ");//Instancio mi objeto
         AuxS19.setCc("03");//asigno valor predeterminado
         AuxS19.setAddr("00 00");// Addr siempre es 00 00 aqui 
         AuxS19.setCk("FC");// Siempre es FC
         DatosS19.add(AuxS19);//Se agrega al arraylist  
    }//fin de metodo S9
    
    
    //metodo para obtener el conloc 
    public static String ObtenerConloc(){
        String conloc = " ";
        for(Linea asm:Parte_5.LineasASM){//Recorro todas las lineas del acm
            if(!(asm.getConloc().equals(" "))){//Si tiene peso en memoria
                if(conloc.equals(" ")){//Si es el primero con valor
                    conloc = asm.getConloc();
                }//Fin primero con valor
                else{
                    conloc = conloc.concat(" ").concat(asm.getConloc());
                }//Si no es el primero con valor
            }//Fin si tiene peso en memoria
        }//Fin recorrer el asm
        return conloc;
    }//Fin Obterner conloc
    
    //metodo S9 con el END
    public static void S9Dificil() {
        String datas[] = ObtenerConloc().split(" ");
        String ultimo = datas[datas.length - 1];
        AuxS19 = new S19("S9", " ", " ", " ", " ");//Instancio mi objeto
        AuxS19.setCc("03");
        AuxS19.setAddr(ultimo);
        AuxS19.setAddr(AuxS19.getAddr().substring(0, 2).concat(" ").concat(AuxS19.getAddr().substring(2, 4)));
        AuxS19.setCk(ck(AuxS19.getCc(), AuxS19.getAddr(), "00"));
        DatosS19.add(AuxS19);
    }//fin de metodo para S9 con END
    
}//FIN DE LA CLASE
