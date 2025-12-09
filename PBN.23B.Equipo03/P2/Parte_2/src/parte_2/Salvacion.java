
package parte_2;

import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Salvacion {
    static boolean encontrado =false;//Varible que utilizaremos para saber si el codop existe
    static NodoSalvacion primer=null,Ultimo=null,Nuevo=null;//Guarda los datos de la linea en donde se encuentra el codop en la salvacion
    
    static void BuscarCodop(String Buscar){//Buscar es el codigo de operancion que se buscara en la salvacion
        try{
            RandomAccessFile auxArchivo = new RandomAccessFile("Salvation_Tabop.txt","r");//r es para solo leer el archivo
            long cursorActual;//Para saber donde estamos
            cursorActual = auxArchivo.getFilePointer();//Puntero en el archivo
            FileReader leerArchivo = new FileReader("Salvation_Tabop.txt");//leeo el archivo del exel en txt
            String lecturaLinea;
            
            //Aqui es donde empieza a leer por linea
            while(cursorActual!=auxArchivo.length() && encontrado==false){//mientras el lector no llegue al final del archivo
                lecturaLinea = auxArchivo.readLine();//leeo la linea
                cursorActual = auxArchivo.getFilePointer();
                String[] campos = lecturaLinea.split("\\s+");//Separamos el txt por tabuladores
                if(campos[0].equals(Buscar)){//En la primera palabra del txt estan los codop asi que si esa palabra es igual al CODOP buscado, lo encotnramos
                    //Esta parte es para guardar todas las lineas que identifico con el mismo CODOP en una lista
                    Nuevo = new NodoSalvacion(campos[0],campos[1],campos[2],campos[3],campos[4], campos[5],null); 
                    if(primer==null){
                        primer=Nuevo;
                    }
                    else{
                        Ultimo.siguiente=Nuevo;
                    }
                    Ultimo=Nuevo;
                }//Fin comparacion con salvacion
                else if(primer!=null && (!campos[0].equals(Buscar))){
                    //Cuando en primer si se guardo una linea con un codop igual al buscado y ya la siguiente linea no es igual, ya encontro el codop y puede salir
                    encontrado=true;
                }
            }//Fin del while
            leerArchivo.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }//Fin leer y buscar en salvacion
    
    //METODO PARA IDENTIFICAR EL ADDR DESDE EL EXCEL
    static void GuardarADDR(Linea LinCod){//LinCod es la linea de codigo que se busca evaluar
        boolean fin=false;//Varible que indica si el while debe de terminar
        while(!fin){
            BuscarCodop(LinCod.getCodop());//Busco el CODOP en la salvacion
            if(!encontrado){//Si no se encontro el codop
                //En caso de ser ORG, END, EQU O DS.B se deben considerar directivas, si no el CODOP no existe
                if(LinCod.getCodop().equals("ORG") || LinCod.getCodop().equals("END") || LinCod.getCodop().equals("EQU") || 
                        LinCod.getCodop().equals("DS.B")){
                    LinCod.setADDR("DIRECT");
                }//Fin CODOP es una directiva
                else{
                    LinCod.setADDR("ADDR no aceptado");
                    System.out.println("CODOP "+ LinCod.getCodop()+" NO EXISTE");
                }//Fin codop no existe
            }//Fin no se encontro al codop
            else{
                if(primer==Ultimo){//Significa que solo encotro un ADDR para el codop
                    if(primer.Operando.equals("-")){//Los indexados no tienen operando
                        if(LinCod.getOperando().equals(" ")){//Si el operando ingresando no existe
                            LinCod.setADDR("INH");
                            LinCod.setPorCalcular(primer.byteCalcular+ " bytes");//Guardo los bytes indicados en la salvacion
                            LinCod.setSize(primer.byteTotal+" bytes");//Guardo los bytes indicados en la salvacion
                        }//Fin si es inh
                        else{
                            LinCod.setADDR( "OPR fuera de rango");
                        }//fin operando incorrecto
                    }//Fin el codop es para un operando de estructura de inh
                    else if(primer.AddrMode.equals("IDX")){
                        if(!(Parte_2.IDX(LinCod.getOperando()).equals("-1"))){//Valido si si es un IDX
                            LinCod.setADDR(Parte_2.IDX(LinCod.getOperando()));//Guardo el tipo de IDX identificado
                            LinCod.setPorCalcular(primer.byteCalcular+ " bytes");
                            LinCod.setSize(primer.byteTotal+" bytes");
                        }//Fin si es un IDX
                        else{
                            LinCod.setADDR( "OPR fuera de rango");
                        }//Fin el operando no tiene estructura IDX
                    }
                    else if(primer.AddrMode.equals("IMM")){
                        if(Parte_2.IMM(LinCod.getOperando(), primer.Operando)){//Valido que la estructura del operando cumpla
                            LinCod.setADDR("IMM");
                            LinCod.setPorCalcular(primer.byteCalcular+ " bytes");
                            LinCod.setSize(primer.byteTotal+" bytes");
                        }//Operando si es de forma IMM
                        else{
                            LinCod.setADDR( "OPR fuera de rango");
                        }//Operando con la estructura incorrecta
                    }
                    else if(primer.AddrMode.equals("REL")){//Si el operando es REL, hay dos posibilidades
                        if(primer.Operando.equals("rel8")){//Si es de este tipo
                            if(LinCod.getOperando().startsWith("$")){//Si es una direccion debe ser en hexadecimal
                                if(Parte_2.ValidarHexadecimal(LinCod.getOperando().substring(1))){//Evaluo hexa
                                    LinCod.setADDR("REL (8b)");
                                    LinCod.setPorCalcular(primer.byteCalcular+ " bytes");
                                    LinCod.setSize(primer.byteTotal+" bytes");
                                }//Fin si tiene una direcccion valida
                            }//Si indica hexadecimal
                            else if(Parte_2.BuscarEtiqueta(LinCod.getOperando())){//Cuando tiene una palabra
                                LinCod.setADDR("REL (8b)");
                                LinCod.setPorCalcular(primer.byteCalcular+ " bytes");
                                LinCod.setSize(primer.byteTotal+" bytes");
                            }//Fin si coincide con una etiqueta
                            else{
                                LinCod.setADDR( "OPR fuera de rango");
                            }//Fin el operando esta mal
                        }//Fin es rel8
                        
                        if(primer.Operando.equals("rel16")){//Si es de este tipo
                            if(LinCod.getOperando().startsWith("$")){//Para evaluar direccion
                                if(Parte_2.ValidarHexadecimal(LinCod.getOperando().substring(1))){//Validar dir en hex
                                    LinCod.setADDR("REL (16b)");
                                    LinCod.setPorCalcular(primer.byteCalcular+ " bytes");
                                    LinCod.setSize(primer.byteTotal+" bytes");
                                }//Fin es correca la direccion del operando
                            }//Fin es un valor hexa
                            else if(Parte_2.BuscarEtiqueta(LinCod.getOperando())){//Cuando el operando es una etiqueta
                                LinCod.setADDR("REL (16b)");
                                LinCod.setPorCalcular(primer.byteCalcular+ " bytes");
                                LinCod.setSize(primer.byteTotal+" bytes");
                            }//Fin si existe la etiqueta
                            else{
                                LinCod.setADDR( "OPR fuera de rango");
                            }//Fin operando con estructura incorrecta
                        }//Fin es rel16
                    }//Fin es relativo
                    else if(primer.AddrMode.equals("REL(9-bit)")){//Si es de este tipo
                        if(LinCod.getOperando().contains(",")){//Debe tener una copa
                            String auxiliar[]=LinCod.getOperando().split(",");//Separar por comas
                            if(auxiliar.length==2){//Debe tener solo dos bloques
                            if(auxiliar[0].equals("A") || auxiliar[0].equals("B") || auxiliar[0].equals("D") ||
                                auxiliar[0].equals("X") || auxiliar[0].equals("Y") || auxiliar[0].equals("SP")){//Estructuras validas para el primer bloque
                                    if(auxiliar[1].startsWith("$")){//Si es una direccion
                                        if(Parte_2.ValidarHexadecimal(auxiliar[1].substring(1))){//Validar hexa
                                            LinCod.setADDR("REL (9b)");
                                            LinCod.setPorCalcular(primer.byteCalcular+ " bytes");
                                            LinCod.setSize(primer.byteTotal+" bytes");
                                        }//Fin direccion correcta
                                    }//Fin base hexa
                                    else if(Parte_2.BuscarEtiqueta(auxiliar[1])){//Si la parte dos es una etiqueta
                                        LinCod.setADDR("REL (9b)");
                                        LinCod.setPorCalcular(primer.byteCalcular+ " bytes");
                                        LinCod.setSize(primer.byteTotal+" bytes");
                                    }//Fin parte dos con etiqueta valida
                                }//Fin estructura del primer bloque
                            }//Fin si son dos bloques
                        }//Fin si contiene una coma
                    }//Fin rel(9-bit)
                    else{
                        System.out.println("CODOP O ADDR NO RECONOCIDO");
                        System.out.println("CODOP= "+primer.CODOP);
                        System.out.println("OPERANDO= "+LinCod.getOperando());
                        System.out.println("Forma del Operando= "+primer.Operando);
                        LinCod.setADDR( "OPR fuera de rango");
                    }//Fin codop que no vamos a evaluar
                }//Fin solo encontro una linea de coincidencia con el codop
                else{//Hay mas de una coincidencia
                    NodoSalvacion AUX = primer;//Auxiliar para movernos entre el filtro para ultima busqueda
                    boolean salir=false,siguiente;//Salir para ya no buscar, siguiente para avanzar en el filtro
                    while(salir==false){//Para buscar en el filtro
                        siguiente=false;//No avanzar a la siguiente hasta que se indique
                        if(LinCod.getOperando().equals(" ")){//Primer caso no hay operando
                            if(AUX.Operando.equals("-")){//La estructura de operando que coincide
                                LinCod.setADDR("INH");//ADDR correspondiente
                                LinCod.setPorCalcular(AUX.byteCalcular+ " bytes");
                                LinCod.setSize(AUX.byteTotal+" bytes");
                                salir=true;//Ya lo encontro, puede terminar la busquerda
                            }//Fin es inh
                            else{
                                siguiente=true;
                            }//Pasar al siguiente elemento del filtro hasta que coincida la estructura con el operando o se acabe el filtro
                        }//Fin no hay operando
                        else if(LinCod.getOperando().startsWith("#")){//Si el operando parece de tipo IMM
                            if(AUX.Operando.equals("#opr8i")||AUX.Operando.equals("#opr16i")){//Puede ser de cualquiera de estas dos estructuras para coincidir
                                if(Parte_2.IMM(LinCod.getOperando(),AUX.Operando)){//Evaluo si si es IMM
                                    LinCod.setADDR("IMM"); 
                                    LinCod.setPorCalcular(AUX.byteCalcular+ " bytes");
                                    LinCod.setSize(AUX.byteTotal+" bytes");
                                    salir=true;
                                }//Fin si es IMM
                                else{
                                    AUX=Ultimo;
                                }//Si no coincide debe marcar error y salir
                            }
                            else{
                                siguiente=true;
                            }//Else avanzar hasta encontrar la estrucutra correspondiente
                        }
                        else if(LinCod.getOperando().startsWith("[D")){//Si comienza de esta forma el operando
                            if(AUX.Operando.equals("[D,xysp]")){//Si estructura en la salvacion debe coincidir con esta
                                if(Parte_2.IdxD(LinCod.getOperando())){//Valido que este bien el operando
                                    LinCod.setADDR("[D,IDX]");
                                    LinCod.setPorCalcular(AUX.byteCalcular+ " bytes");
                                    LinCod.setSize(AUX.byteTotal+" bytes");
                                    salir=true;
                                }//Fin si es [D,IDX]
                                else{
                                    AUX=Ultimo;
                                }//Fin error con el operando
                            }//Fin estructura correcta
                            else{
                                siguiente=true;
                            }//Buscar estructura correcta
                        }//Fin parece [d,idx]
                        else if(LinCod.getOperando().startsWith("[")){//Si comienza de esta forma el operando
                            if(AUX.Operando.equals("[oprx16,xysp]")){//Si estructura en la salvacion debe coincidir con esta
                                if(Parte_2.Idx2C(LinCod.getOperando())){//Evalua que el operando
                                    LinCod.setADDR("[IDX2]");
                                    LinCod.setPorCalcular(AUX.byteCalcular+ " bytes");
                                    LinCod.setSize(AUX.byteTotal+" bytes");
                                    salir=true;
                                }//Fin si es [D,IDX2]
                                else{
                                    AUX=Ultimo;
                                }//No coincide con la estructura del operando
                            }//Fin estrucutra correspondiente
                            else{
                                siguiente=true;
                            }//Fin buscar estructura
                        }//Fin parece [IDX2]
                        else if(LinCod.getOperando().contains(",")){//Si tiene una coma pero no los elementos anteriores
                            if(AUX.Operando.equals("oprx0_xysp")){//Debe coincidir con esta estructura
                                if(!(Parte_2.IDX(LinCod.getOperando()).equals("0"))){//Valido que el operando completo tenga la forma completa
                                    LinCod.setADDR(Parte_2.IDX(LinCod.getOperando()));
                                    LinCod.setPorCalcular(AUX.byteCalcular+ " bytes");
                                    LinCod.setSize(AUX.byteTotal+" bytes");
                                    salir=true;
                                }//Fin estructura correspondiente
                                else{
                                    siguiente=true;
                                }//Fin ir a la siguiente linea para ver si el operando coincide
                            }//Fin es IDX
                            else if(AUX.Operando.equals("oprx9,xysp")){//Si llego hasta esta estructura
                                if(Parte_2.IDX1(LinCod.getOperando())){//Debe ser IDX1
                                    LinCod.setADDR("IDX1");
                                    LinCod.setPorCalcular(AUX.byteCalcular+ " bytes");
                                    LinCod.setSize(AUX.byteTotal+" bytes");
                                    salir=true;
                                }//Validar operando con de IDX1
                                else{
                                    siguiente=true;
                                }//Fin pasar a la siguiente estructura 
                            }//Fin es IDX1
                            else if(AUX.Operando.equals("oprx16,xysp")){//Si llego hasta esta estructura
                                if(Parte_2.IDX2(LinCod.getOperando())){//Evaluar si es IDX2
                                    LinCod.setADDR("IDX2");
                                    LinCod.setPorCalcular(AUX.byteCalcular+ " bytes");
                                    LinCod.setSize(AUX.byteTotal+" bytes");
                                    salir=true;
                                }//Fin si es IDX2
                                else{
                                    AUX=Ultimo;
                                }//Fin operando de forma incorrecta
                            }//Fin es IDX2
                            else{
                                siguiente=true;
                            }//Fin buscar primera estructura similar
                        }//Fin tiene comas
                        else if(Parte_2.ConvertirADecimal(LinCod.getOperando())!=-1){//Si es directamente un valor en cualquier base
                            if(AUX.Operando.equals("opr8a")&&Parte_2.ConvertirADecimal(LinCod.getOperando())<256 
                                    && Parte_2.ConvertirADecimal(LinCod.getOperando())>=0){//Forma de DIR
                                    LinCod.setADDR("DIR");
                                    LinCod.setPorCalcular(AUX.byteCalcular+ " bytes");
                                    LinCod.setSize(AUX.byteTotal+" bytes");
                                    salir=true;
                            }//Fin es DIR
                            else if(AUX.Operando.equals("opr16a")){//Estructura de EXT
                                if(Parte_2.EXT(LinCod.getOperando())){
                                    LinCod.setADDR("EXT");
                                    LinCod.setPorCalcular(AUX.byteCalcular+ " bytes");
                                    LinCod.setSize(AUX.byteTotal+" bytes");
                                    salir=true;
                                }//Fin es EXT
                                else{
                                    AUX=Ultimo;
                                }//Operando incorreccto
                            }//Fin estructura de EXT
                            else{
                                siguiente=true;
                            }//buscar estructura correspondiente
                        }//Fin es DIR o EXT
                        else if(Parte_2.BuscarEtiqueta(LinCod.getOperando())){//EXT con etiqueta
                            if(AUX.Operando.equals("opr16a")){//Estructura correspondiente
                                LinCod.setADDR("EXT");
                                LinCod.setPorCalcular(AUX.byteCalcular+ " bytes");
                                LinCod.setSize(AUX.byteTotal+" bytes");
                                salir=true;
                            }//Fin es EXT
                            else{
                                siguiente=true;
                            }//Buscar estructura
                        }//Parece EXT
                        else{
                            AUX=Ultimo;
                        }//Operando incorrecto
                    
                        if(AUX==Ultimo && salir==false){
                            LinCod.setADDR( "OPR fuera de rango");
                            salir=true;
                        }//Salir del while
                        else if(siguiente){
                            AUX=AUX.siguiente;
                        }//Ir a la siguiente linea de coincidencia con el CODOP
                    }//Fin whilw
                }//Fin filtro de la busqueda
                //INCILIZAR AUXILIARES DE BUSQUEDA
                encontrado=false;
                primer=null;
                Ultimo=null;
            }//Fin se encontro el CODOP en salvacion
            if(LinCod==Parte_2.FinLinCod){
                fin=true;
            }//Salir cuando se evalue la ultima linea de codigo
            else{
                LinCod=LinCod.getSiguiente();
            }//Evaluar en toda la lista de codigo
        }//Fin while identificar ADDR, recorrer lineas de codigo
    }//Fin guardar ADDR
}//Fin clase