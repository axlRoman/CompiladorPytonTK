/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:        SEMESTRE:Ago/Dic 2023            HORA: 7 HRS
 *:                                   
 *:               
 *:    # Clase con la funcionalidad del Generador de COdigo Intermedio
 *                 
 *:                           
 *: Archivo       : GenCodigoInt.java
 *: Autor         : Fernando Gil  
 *: Fecha         : 03/SEP/2014
 *: Compilador    : Java JDK 7
 *: Descripción   :  
 *:                  
 *:           	     
 *: Ult.Modif.    :
 *:  Fecha       Modificó          Modificacion
 *:  29/11/2023   Julian           Se agregaron las acciones semanticas para GCI
 *:=============================================================================
 *:-----------------------------------------------------------------------------
 */


package compilador;
import general.Linea_BE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;


public class GenCodigoInt {
 
    public static final int NIL = 0;
    private Compilador cmp;
    private int        consecutivoTmp;
    private int        cont = 0;
    private int        pre = 0;
    private int        c3d = 0;
   
    private String     infija = "";
    private int        consecutivoEtiq; 
    //private String preAnalisis;
    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //
	public GenCodigoInt ( Compilador c ) {
        cmp = c;
    }
    // Fin del Constructor
    //--------------------------------------------------------------------------
	
 
     private String tempnuevo () {
        return "t" + consecutivoTmp++;
    }    
        
    //--------------------------------------------------------------------------
	
    private String etiqnueva () {
        return "etiq" + consecutivoEtiq++;
    }    
        
    //--------------------------------------------------------------------------
    
    private void emite ( String c3d ) {
        cmp.iuListener.mostrarCodInt ( c3d + "\n" );
    }

    //--------------------------------------------------------------------------
    
    public void generar () {
        
        //preAnalisis = cmp.be.preAnalisis.complex;
        consecutivoTmp = 1;
        consecutivoEtiq = 1;
        cont = 0;
        pre = 0;
        c3d = 0;
        infija = "";
        PROGRAMA ();
    } 
    
    // Fin de analizar
    //--------------------------------------------------------------------------
   
    //************EMPAREJAR**************//
    private void emparejar ( String t ) {
	if (cmp.be.preAnalisis.complex.equals ( t ) )
		cmp.be.siguiente ();
	else
		errorEmparejar ( "Se esperaba " + t + " se encontró " +
                                 cmp.be.preAnalisis.lexema );
    }	
	
    //--------------------------------------------------------------------------
    // Metodo para devolver un error al emparejar
    //--------------------------------------------------------------------------

    private void errorEmparejar ( String _token ) {
        String msjError = "ERROR SINTACTICO: ";
              
        if ( _token.equals ( "id" ) )
            msjError += "Se esperaba un identificador" ;
        else if ( _token.equals ( "num" ) )
            msjError += "Se esperaba una constante entera" ;
        else if ( _token.equals ( "num.num" ) )
            msjError += "Se esperaba una constante real";
        else if ( _token.equals ( "literal" ) )
            msjError += "Se esperaba una literal";
        else if ( _token.equals ( "oparit" ) )
            msjError += "Se esperaba un Operador Aritmetico";
        else if ( _token.equals ( "oprel" ) )
            msjError += "Se esperaba un Operador Relacional";
        else 
            msjError += "Se esperaba " + _token;
                
        cmp.me.error ( Compilador.ERR_SINTACTICO, msjError );    
    }            

    // Fin de ErrorEmparejar
    //--------------------------------------------------------------------------
	
    //--------------------------------------------------------------------------
    // Metodo para mostrar un error sintactico
 
    private void error ( String _token ) {
        cmp.me.error ( cmp.ERR_SINTACTICO,
         "ERROR SINTACTICO: en la produccion del simbolo  " + _token );
    }
 
    
    //--------------------------------------------------------------------------
    //  *  *   *   *            CODIGO DE LOS PROCEDURES          *  *  *  *
    //--------------------------------------------------------------------------


    //Autor: Julian Rodolfo Villa Cruz - No. Control: 20130764
    //PROGRAMA -> INSTRUCCION PROGRAMA |  ε
    
    /*
    INSTRUCCION  PROGRAMA1 {1}  | ϵ {2}
    1
    PROGRAMA.tipo := if INSTRUCCIÓN.tipo == VACIO and PROGRAMA1.tipo == VACIO then 
             VACIO 
          else 
             ERROR_TIPO
    2
    PROGRAMA.tipo := VACIO


    */
    private void PROGRAMA() {
        if (cmp.be.preAnalisis.equals("def") //pro->funcion-> def
                || cmp.be.preAnalisis.equals("int") || cmp.be.preAnalisis.equals("float")//pro->proposicion-> esto...
                || cmp.be.preAnalisis.equals("id") || cmp.be.preAnalisis.equals("if") || cmp.be.preAnalisis.equals("while") 
                || cmp.be.preAnalisis.equals("print") || cmp.be.preAnalisis.equals("string")) {
            
            infija = "";
            cont = 0;
            pre = 0;
            c3d = 0;
            
            INSTRUCCION();
            PROGRAMA();
        }else 
        {
            // PROGRAMA -> empty 
            
            if ( cmp.be.preAnalisis.complex != ( "$" ) )
                error ( "[PROGRAMA] = Declaración o documento en blanco esperado. " 
                        + cmp.be.preAnalisis.numLinea );
        }
        
    }

     //Autor: Julian Rodolfo Villa Cruz - No. Control: 20130764    
    /*
    INSTRUCCION -> FUNCION {3} | PROPOSICION	{4}
    3
    INSTRUCCIÓN.tipo := FUNCION.tipo
    4
    INSTRUCCIÓN.tipo := PROPOSICION.tipo
    */
    

    private void INSTRUCCION() {
        
        if (cmp.be.preAnalisis.equals("def")) {
            FUNCION();
        
        } 
        else if (cmp.be.preAnalisis.equals("int") || cmp.be.preAnalisis.equals("float") || 
                 cmp.be.preAnalisis.equals("string") || cmp.be.preAnalisis.equals("id") || 
                 cmp.be.preAnalisis.equals("if") || cmp.be.preAnalisis.equals("while") || 
                 cmp.be.preAnalisis.equals("print")) 
        {

            PROPOSICION();

        } else {
            error("[INSTRUCCION] -> Falta de palabra reservada. " 
                    + "en la línea: " + cmp.be.preAnalisis.numLinea);
        }
    }
    
    
    //Autor: Julian Rodolfo Villa Cruz - No. Control: 20130764
    //FUNCION -> def id ( ARGUMENTOS ) : TIPO_RETORNO {5} PROPOSICIONES_OPTATIVAS  return RESULTADO :: {6}
    /*
    5
    FUNCION.tipoaux := if buscaTipo ( id.entrada ) == nil then
                      begin
                         añadeTipo ( id.entrada, ARGUMENTOS.tipo || ‘->’ || TIPO_RETORNO.tipo )
                         VACIO
                      end 
                   else
                      ERROR_TIPO  //”Identificador ya fue declarado id.lexema”
    6
    FUNCION.tipo := if FUNCION.tipoaux == VACIO and PROPOSICIONES_OPTATIVAS == VACIO then
                   Begin
                      If ( RESULTADO.tipo == TIPO_RETORNO.tipo ) or
                         ( TIPO_RETORNO.tipo == “float” and RESULTADO.tipo == “int” ) then 
                             VACIO
                      Else 
                         ERROR_TIPO   // Tipo del resultado retornado no es compatible con el tipo
                                      // de retorno de la función
                   End
                Else
                   ERROR_TIPO // “Errores de tipo en la declaración de la funcion id.lexema”*/
    private void FUNCION() {

        if (cmp.be.preAnalisis.equals("def")) {

            emparejar("def");
//            id = cmp.be.preAnalisis;
            emparejar("id");
            emparejar("(");
            ARGUMENTOS();
            emparejar(")");
            emparejar(":");
            TIPO_RETORNO();
            PROPOSICIONES_OPTATIVAS();
            emparejar("return");
            RESULTADO();
            emparejar(":");
            emparejar(":");
        }
        else 
        {
            error("[FUNCION] Expresión inválida "
                    + "se esperaba def y se encontró "
                    + cmp.be.preAnalisis.complex + cmp.be.preAnalisis.numLinea);
        }
    }
    
    
    //Autor: Julian Rodolfo Villa Cruz - No. Control: 20130764
    //DECLARACION_VARS	-> TIPO_DATO id  DECLARACION_VARS’ {27}

    /*
    DECLARACION_VARS := if buscaTipo ( id.entrada ) == nil && DECLARACION_VARS’ == VACIO then
                      begin
                         añadeTipo ( id.entrada, TIPO_DATO.tipo)
                         VACIO
                      end
                   else
    ERROR_TIPO // identificador ya fue declarado id.lexema

    */
    private void DECLARACION_VARS() {
        if (cmp.be.preAnalisis.equals("int") || cmp.be.preAnalisis.equals("float") || 
            cmp.be.preAnalisis.equals("string")) 
        {
            TIPO_DATO();
//            id = cmp.be.preAnalisis;
            emparejar("id");
            DECLARACION_VARS_P();

        } else {
            error("[declaracion_vars]: Se esperaba un tipo de dato 'int', 'float', 'string'");
        }
    }
    
    
    //Autor: Julian Rodolfo Villa Cruz - No. Control: 20130764
    //DECLARACION_VARS’	->, id  DECLARACION_VARS’{28}| ϵ{29}
    /*
    28
    DECLARACION_VARS’ := if buscaTipo ( id.entrada ) == nil && DECLARACION_VARS’.tipo == VACIO then
                      begin
                         VACIO
                      end
                   else
    ERROR_TIPO // identificador ya fue declarado id.lexema
    29
    DECLARACION_VARS’ := VACIO
    */
    private void DECLARACION_VARS_P() {
        if (cmp.be.preAnalisis.equals(",")) {
            emparejar(",");
//            id = cmp.be.preAnalisis;
            emparejar("id");
            DECLARACION_VARS_P();
        } else {
            //ε->vacio
        }
    }
   
    
    //Autor: Francisco Axel Roman Cardoza - No. Control: 19130971
    //TIPO_RETORNO -> void | TIPO_DATO
    private void TIPO_RETORNO() {
        if (cmp.be.preAnalisis.equals("void")) //Primeros (TIPO_RETORNO) = {void, int, float, string}
        {
            emparejar("void");
        } 
        else if (cmp.be.preAnalisis.equals("int") || cmp.be.preAnalisis.equals("float") || 
                 cmp.be.preAnalisis.equals("string")) 
        {
            TIPO_DATO();
        } else {
            error ( "[TIPO_RETORNO] : Se esperaba un tipo de dato."  
            + cmp.be.preAnalisis.numLinea ); 
        }
    }
    

    //Autor: Francisco Axel Roman Cardoza - No. Control: 19130971
    // RESULTADO -> EXPRESION | void
    public void RESULTADO() {
        //Primeros (RESULTADO) = {void, literal, id, num, num.num, (, opsuma, empty, opmult, (, empty}
        Atributos EXPRESION =  new Atributos();

        if  (cmp.be.preAnalisis.equals ( "literal" ) || cmp.be.preAnalisis.equals ( "id" )      ||
             cmp.be.preAnalisis.equals ( "num" )     || cmp.be.preAnalisis.equals ( "num.num" ) ||
             cmp.be.preAnalisis.equals ( "(" ) )  
        {
            EXPRESION(EXPRESION);

        } 
        else if (cmp.be.preAnalisis.equals("void")) 
        {
            emparejar("void");

        } else {
            // Manejar error sintáctico o lanzar una excepción si el preAnalisis no es válido.
            error ( " [RESULTADO]: literal, identificador, entero o flotante esperado" + 
                        "No. Linea: " + cmp.be.preAnalisis.numLinea );    
        }
    }


    //Autor: Francisco Axel Roman Cardoza - No. Control: 19130971
    // PROPOSICIONES_OPTATIVAS -> PROPOSICION PROPOSICIONES_OPTATIVAS | ε
    public void PROPOSICIONES_OPTATIVAS() {
        //Primeros (PROPOSICIONES_OPTATIVAS) = {id, if, while, print, int, float, string, empty}


        if ( cmp.be.preAnalisis.equals ( "int" )    || cmp.be.preAnalisis.equals ( "float" ) || 
             cmp.be.preAnalisis.equals ( "string" ) || cmp.be.preAnalisis.equals ( "id" )    || 
             cmp.be.preAnalisis.equals ( "if" )     || cmp.be.preAnalisis.equals ( "while" ) || 
             cmp.be.preAnalisis.equals ( "print" ) )
        {
            PROPOSICION();
            PROPOSICIONES_OPTATIVAS();

        } else {
            //ε->vacio
        }
    }

    
    //Autor: Francisco Axel Roman Cardoza - No. Control: 19130971
    //PROPOSICION -> DECLARACION_VARS | id PROPOSICION_P | if CONDICION : PROPOSICIONES_OPTATIVAS else : PROPOSICIONES_OPTATIVAS :: 
    // | while CONDICION : PROPOSICIONES_OPTATIVAS :: | print ( EXPRESION )
    public void PROPOSICION() {
        //Primeros (PROPOSICION) = {id, if, while, print, int, float, string}
        Atributos PROPOSICION_P = new Atributos ();
        Atributos EXPRESION = new Atributos();
        Linea_BE id = new Linea_BE ();

        if ( cmp.be.preAnalisis.equals ( "int" ) || cmp.be.preAnalisis.equals ( "float" ) || 
             cmp.be.preAnalisis.equals ( "string" ) )
        {
            DECLARACION_VARS ();
            
        }
        else if ( cmp.be.preAnalisis.equals( "id" ) )
        {
          
            id = cmp.be.preAnalisis;
            emparejar ( "id" );
            PROPOSICION_P ( PROPOSICION_P );
            emite(  id.lexema+ ":=" +PROPOSICION_P.lugar);
            cmp.cua.agregar(new Cuadruplo ( ":=", PROPOSICION_P.lugar,"",id.lexema) );
         
        }
        else if ( cmp.be.preAnalisis.equals ( "if" ) )
        {
            emparejar ( "if" );
            CONDICION ();
            emparejar ( ":" );
            PROPOSICIONES_OPTATIVAS ();
            
            emparejar ( "else" );
            emparejar ( ":" );
            PROPOSICIONES_OPTATIVAS ();

            emparejar ( ":" );
            emparejar ( ":" );
        }
        else if ( cmp.be.preAnalisis.equals ( "while" ) )
        {
            emparejar ( "while" );
            CONDICION();
            emparejar ( ":" );
            PROPOSICIONES_OPTATIVAS ();
            
            
            emparejar ( ":" );
            emparejar ( ":" );
        }
        else if ( cmp.be.preAnalisis.equals ( "print" ) )
        {
         
            emparejar ( "print" );
            emparejar ( "(" );
            EXPRESION ( EXPRESION );
            emparejar ( ")" );
            
            
        }
        else
            error ( "[PROPOSICION]: Declaración no válida, en la linea: " +
                    cmp.be.preAnalisis.getNumLinea () );
    }
    
    
    //Autor: Francisco Axel Roman Cardoza - No. Control: 19130971
    //PROPOSICION_P -> opasig EXPRESION | ( LISTA_EXPRESIONES )
    public void PROPOSICION_P(Atributos PROPOSICION_P) {
        Atributos EXPRESION = new Atributos ();
                
        if (cmp.be.preAnalisis.equals("opasig")) 
        {
            emparejar("opasig");
            EXPRESION(EXPRESION);
            PROPOSICION_P.lugar = EXPRESION.lugar;
        } 
        else if (cmp.be.preAnalisis.equals("(")) 
        {
            emparejar("(");
            LISTA_EXPRESIONES();
            emparejar(")");
        } else {
            // Manejar error sintáctico o lanzar una excepción si el preAnalisis no es válido.
            error ( "[PROPOSICION_p]: Error de expresión en operador " +
                        "No. Linea: " + cmp.be.preAnalisis.numLinea  );
        }
    }
    
    //Autor: Francisco Axel Roman Cardoza - No. Control: 19130971
    //CONDICION -> EXPERSION oprel EXPRESION
    public void CONDICION() 
    {
        Atributos EXPRESION = new Atributos ();
        Atributos EXPRESION1 = new Atributos ();

        if ( cmp.be.preAnalisis.equals ( "id" )      ||
             cmp.be.preAnalisis.equals ( "num" )     ||
             cmp.be.preAnalisis.equals ( "num.num" ) ||
             cmp.be.preAnalisis.equals ( "(" )       ||
             cmp.be.preAnalisis.equals ( "literal" ) ) 
        {
        
            EXPRESION ( EXPRESION );
            emparejar ( "oprel" );
            EXPRESION ( EXPRESION1 );
      
          
        } 
        else 
        {
            error ( "[CONDICION] Expresión inválida"
                    + " se esperaba id o num o num.num o ( o literal y se encontró "
                    + cmp.be.preAnalisis.complex + cmp.be.preAnalisis.numLinea );
        }
    }


    //Autor: Braulio Esteban Gonzalez Alanis - No. Control: 20131498
    //TIPO_DATO -> int | float | string
    private void TIPO_DATO ()
    {
        if ( cmp.be.preAnalisis.equals ( "int" ) )
        {
            emparejar ( "int" );
         
        }
        else if ( cmp.be.preAnalisis.equals ( "float" ) )
        {
            emparejar ( "float" );
        }
        else if ( cmp.be.preAnalisis.equals ( "string" ) )
        {
            emparejar ( "string" );
        }
        else
            error ( "[TIPO_DATO] : Tipo de dato esperado." +
                    "No. Línea: " + cmp.be.preAnalisis.numLinea );
    }
    
    
   
    //Autor: Braulio Esteban Gonzalez Alanis - No. Control: 20131498
    //ARGUMENTOS -> TIPO_DATO id ARGUMENTOS_P | ε
    private void ARGUMENTOS() {
        
        if (cmp.be.preAnalisis.equals("int") || cmp.be.preAnalisis.equals("float") || 
            cmp.be.preAnalisis.equals("string")) {
            
            TIPO_DATO( );
//            id = cmp.be.preAnalisis;          
            emparejar("id");
            
            ARGUMENTOS_P();
            
        } 
        else 
        {
            //ε->vacio
        }
    }
    
   
    //Autor: Braulio Esteban Gonzalez Alanis - No. Control: 20131498
    //ARGUEMNTOS_P -> , TIPO_DATO id ARGUEMNTOS_P | ε
    private void ARGUMENTOS_P() {
        
        if (cmp.be.preAnalisis.equals(",")) 
        {
            emparejar(",");
            TIPO_DATO();
            //id = cmp.be.preAnalisis;
            emparejar("id");    
            ARGUMENTOS_P( );
        } else {
            //ε->vacio
        }
    }
    

    //Autor: Braulio Esteban Gonzalez Alanis - No. Control: 20131498
    //LISTA_EXPRESIONES -> EXPRESION LISTA_EXPRESIONES_P | ε
    private void LISTA_EXPRESIONES() 
    {
        Atributos EXPRESION = new Atributos ();
        
        if (cmp.be.preAnalisis.equals("literal")
                || cmp.be.preAnalisis.equals("id")
                || cmp.be.preAnalisis.equals("num")
                || cmp.be.preAnalisis.equals("num.num")
                || cmp.be.preAnalisis.equals("(")) {
            
 // LISTA_EXPRESIONES -> EXPRESION  LISTA_EXPRESIONES'
            EXPRESION(EXPRESION);
            LISTA_EXPRESIONES_P();
    
    }
    }
    
    
    //Autor: Braulio Esteban Gonzalez Alanis - No. Control: 20131498
    //LISTA_EXPRESIONES_P -> , EXPRESION LISTA_EXPRESIONES | ε
    private void LISTA_EXPRESIONES_P() {
        
        Atributos EXPRESION = new Atributos ();
        
        if (cmp.be.preAnalisis.equals(",")) {
            
            emparejar(",");
            EXPRESION( EXPRESION);
            LISTA_EXPRESIONES_P();
            
        } else {
            //ε->vacio
        }
    }
    
  
    //Autor: Arturo Rosales Valdés - No. Control: 20130766
    //EXPRESION -> TERMINO EXPRESION_P | literal
    private void EXPRESION(Atributos EXPRESION) 
    {
        if (cmp.be.preAnalisis.equals("id")
                || cmp.be.preAnalisis.equals("num")
                || cmp.be.preAnalisis.equals("num.num")
                || cmp.be.preAnalisis.equals("(")) {
            if (cont == 0)
                EXPRESION.bandera = true;
            cont++;
            TERMINO();
            EXPRESION_P();
            if (EXPRESION.bandera == true)
            {
                EXPRESION.prefijo = infijo_a_prefijo ( infija, pre );
                EXPRESION.lugar = generar_c3d_expresion (EXPRESION.prefijo, c3d );
            }
            

    
        } else if (cmp.be.preAnalisis.equals("literal")) {
            //literal = cmp.be.preAnalisis;

            emparejar("literal");/********************************************************************************/
            //cmp.ts.anadeTipo ( literal.entrada, "string" );
              //      EXPRESION.tipo = "string";
            //accion semantica 11
            
           // EXPRESION.tipoexpre=2;
            //fin accion semantica 11
        } else {
            error ( "[EXPRESION] Expresión no válida." + "N° Línea: " 
                    + cmp.be.preAnalisis.numLinea );    
        }
    }
    
    
    //Autor: Arturo Rosales Valdés - No. Control: 20130766
    //EXPRESION_P -> opsuma TERMINO EXPRESION_P | ε
    private void EXPRESION_P() {
        Linea_BE opsuma = new Linea_BE ();
        if (cmp.be.preAnalisis.equals("opsuma")) {
            opsuma = cmp.be.preAnalisis;
            emparejar("opsuma");
            infija += opsuma.lexema + " ";
            pre++;
            c3d++;
            TERMINO();
            EXPRESION_P();
        } else {
            
            //ε->vacio
        }
    }
    
    
    //Autor: Arturo Rosales Valdés - No. Control: 20130766
    //TERMINO -> FACTOR TERMINO_P
    private void TERMINO() {
        if ( cmp.be.preAnalisis.equals ( "id" )      || cmp.be.preAnalisis.equals ( "num" ) || 
             cmp.be.preAnalisis.equals ( "num.num" ) || cmp.be.preAnalisis.equals ( "(" ) ) 
        {
            FACTOR();
            TERMINO_P();

       
        } else {
            error ( "[TERMINO]: se esperaba una expresion" );
        }
        //FIN ACCION {38}
    }
    
    
    //Autor: Arturo Rosales Valdés - No. Control: 20130766
    //TERMINO_P -> opmult FACTOR TERMINO_P | ε
    private void TERMINO_P() {
        Linea_BE opmult = new Linea_BE ();
        if (cmp.be.preAnalisis.equals("opmult")) {
            opmult = cmp.be.preAnalisis;
            emparejar("opmult");
            infija += opmult.lexema + " ";
            pre++;
            c3d++;
            FACTOR ();            
            TERMINO_P ();

       
        } else {
            //ε->vacio
        }
    }
    
    
    //Autor: Arturo Rosales Valdés - No. Control: 20130766
    //FACTOR -> id FACTOR_P | num | num.num | ( EXPRESION )
    private void FACTOR() {
        Atributos EXPRESION = new Atributos();
        Linea_BE id = new Linea_BE ();
        Linea_BE num = new Linea_BE ();
        Linea_BE num_num = new Linea_BE ();

        if ( cmp.be.preAnalisis.equals ( "id" ) ) 
         {
            id = cmp.be.preAnalisis;
            emparejar ( "id" );
            infija += id.lexema + " ";
            pre++;
            c3d++;
            FACTOR_P();
            
        } 
         else if ( cmp.be.preAnalisis.equals ( "num" ) ) 
         {
            num = cmp.be.preAnalisis;
            emparejar ( "num" );
            infija += num.lexema + " ";
            pre++;
            c3d++;
            
        } 
        else if ( cmp.be.preAnalisis.equals ( "num.num" ) ) 
        {
            num_num = cmp.be.preAnalisis;
            emparejar ( "num.num" );
            infija += num_num.lexema + " ";
            pre++;
            c3d++;
        } 
        else if ( cmp.be.preAnalisis.equals ( "(" ) ) 
        {
            emparejar ( "(" );
            infija += "( ";
            pre++;
            
            EXPRESION ( EXPRESION );
            emparejar ( ")" );
            infija += ") ";
            pre++;
            
        }
        else 
        {
            error( "[FACTOR] : Se esperaba un id, num, num.num o (. No. de línea: " 
                    + cmp.be.preAnalisis.numLinea );    
        }
    }
    
    
    //Autor: Arturo Rosales Valdés - No. Control: 20130766
    //FACTOR_P -> ( LISTA_EXPRESIONES ) | ε
    private void FACTOR_P() {

        if ( cmp.be.preAnalisis.equals ( "(" ) )
            {
                emparejar ( "(" );
                LISTA_EXPRESIONES ();
                emparejar (")" );
                
            }
            else
            {
       
            }
    }

    // Método que convierte una expresión infija a prefija que recibe como parámetro
    // la expresión infija y el número de símbolos terminales que la componen
    public static String infijo_a_prefijo ( String infix, int num ) 
    {
        // Pilas para almacenar los operandos y los operadores
        Stack <String> OperandoStack = new Stack <> ();
        Stack <String> OperadorStack = new Stack<> ();
        
        // Arreglo en el que se almacenan los componentes
        String aux [] = new String [ num ];
        // Variable para obtener substrings
        int ini = 0;
        
        for ( int i = 0, j = 0; i < infix.length () && j < aux.length; i++ ) 
        {
            // En caso de que el componente de la cadena sea igual a ' ', se 
            // obtiene una subcadena y se guarda en el arreglo
            if ( infix.charAt ( i ) == ' ' )
            {
                aux [ j ] = infix.substring ( ini, i );
                ini = i + 1;
                j++;
            }
        }

        // Se recorre que al arreglo que contiene a los símbolos terminales
        for ( String token : aux )
        {
            // Si el símbolo es un operando se ingresa en la pila de operandos
            if ( esOperando ( token.charAt ( 0 ) ) )
            {
                OperandoStack.push ( token + " " );
            } 
            else if ( token.charAt ( 0 ) == '(' || OperadorStack.isEmpty () || 
                      isp ( token.charAt ( 0 ) ) > isp ( OperadorStack.peek ().charAt ( 0 ) ) )
            {
                // En caso de que se cumpla con las condiciones anteriores se mete
                // el símbolo el la pila de operadores
                OperadorStack.push ( token + " " );
            } 
            else if ( token.charAt ( 0 ) == ')' ) 
            {
                // Si el operador es un ) se almacenan los datos hasta que se encuentre un (
                while ( OperadorStack.peek ().charAt ( 0 ) != '(' )
                {
                    String operator     = OperadorStack.pop();
                    String RightOperand = OperandoStack.pop ();
                    String LeftOperand  = OperandoStack.pop ();
                    String operand      = operator + LeftOperand + RightOperand;
                    OperandoStack.push ( operand );
                }

                OperadorStack.pop ();

            } 
            // Se compara el nivel de precedencia de los tokens
            else if ( isp ( token.charAt ( 0 ) ) <= isp ( OperadorStack.peek ().charAt ( 0 ) ) )
            {
                // Mientras que la pila de operadores no este vacía y haya símbolos
                // con niveles de precedencia inferiores se almacenan
                while ( !OperadorStack.isEmpty () && 
                        isp ( token.charAt ( 0 ) ) <= isp ( OperadorStack.peek ().charAt ( 0 ) ) )
                {
                    String operator     = OperadorStack.pop ();
                    String RightOperand = OperandoStack.pop  ();
                    String LeftOperand  = OperandoStack.pop  ();
                    String operand      = operator + LeftOperand + RightOperand;
                    OperandoStack.push ( operand );
                }

                OperadorStack.push(token + " ");

            }
        }
    
        // Mientras que la pila de los operadores no sea empty se almacenan los datos
        while ( !OperadorStack.isEmpty () )
        {
            String operator     = OperadorStack.pop ();
            String RightOperand = OperandoStack.pop  ();
            String LeftOperand  = OperandoStack.pop  ();
            String operand      = operator + LeftOperand + RightOperand;
            OperandoStack.push ( operand );
        }

        // Se guarda el resultado en una String y se regresa el valor
        String pre = OperandoStack.pop () + " ";
        
        return pre;
    }

    //--------------------------------------------------------------------------
    
    // Método para determinar el índice o el nivel de precedencia de un caracter
    public static int isp ( char token ) 
    {
        switch ( token ) 
        {
            // Si el token es un * o / se asigna un 2
            case '*':
            case '/':
                return 2;

            // Si el token es un + o - se asigna un 1
            case '+':
            case '-':
                return 1;

            default:
                return -1;
        }
    }
    
    //--------------------------------------------------------------------------

    // Método para generar el código de tres direcciones de una expresion
    private  String generar_c3d_expresion ( String prefija, int num )
    {
        // Variables usadas en el método
        String aux [] = new String [ num ];
        String temp = "";
        // Variable para obtener subcadenas
        int ini = 0;
        
        // Se almacenan los símbolos terminales en un arreglo
        for ( int i = 0, j = 0; i < prefija.length () && j < aux.length; i++ ) 
        {
            if ( prefija.charAt ( i ) == ' ' )
            {
                // Se obtiene una substring y se almacena en el arreglo
                aux [ j ] = prefija.substring ( ini, i );
                ini = i + 1;
                j++;
            }
        }

        // Variable para recorrer el arreglo
        int tam = aux.length;
        int j = 1;
        // Se recorre el arreglo
        for (int i = 0; i < tam; i++) 
        {
            for ( ; j < tam; j++ ) 
            {
                // Se evalúa si dentro de la cadena se encuentra un operador 
                // seguido de dos operandos
                if ( esOperador ( aux [ i ].charAt ( 0 ) ) && 
                     esOperando ( aux [ j ].charAt ( 0 ) ) && 
                     esOperando ( aux [ j + 1 ].charAt ( 0 ) ) )
                {
                    // En caso de que si se cumpla la condición se realiza lo siguiente
                    
                    // Se le da el valor a una variable temporal
                    temp = tempnuevo ();
                    // Se genera el c3d y se muestra
                    emite ( temp + ":=" + aux [ j ] + aux [ i ] + aux [ j + 1 ] );
                    // Se asigna la variable temporal en la posición del operador y 
                    // se quitan los operandos
                    aux [ i ]     = temp;
                    aux [ j ]     = "";
                    aux [ j + 1 ] = "";
                    // Se inicializan los valores
                    i = 0;
                    j = 0;
                    // Se disminuye el tamaño del recorrido
                    tam -= 2;
                    // Se crea un arreglo del tamaño de tam
                    String aux1 [] = new String [ tam ];

                    // Se pasa el arreglo resultante a un arreglo auxiliar
                    for ( int l = 0, k = 0; l < tam && k < prefija.length (); k++ ) 
                    {
                        if ( aux [ k ] != "" )
                        {
                            aux1 [ l ] = aux [ k ];
                            l++;
                        }
                    }

                    // Se pasan los valores al arreglo original
                    for ( int l = 0, k = 0; l < tam && k < tam; k++, l++ ) 
                    {
                        aux [ l ] = aux1 [ k ];  
                    }

                    // En caso de que la posición 0 del arreglo sea una variable
                    // temporal se rompe el ciclo
                    if ( aux [ 0 ] == temp )
                        break;
                } 
                else 
                {
                    // Se aumenta j y se rompe e ciclo
                    j++;
                    break;
                }
            }
        }
        
        // Se regresa la variable temporal actual
        return temp;
    }

    //--------------------------------------------------------------------------
    
    // Método que determina si un elemento es un operador
    private static boolean esOperador ( char c )
    {
        // Si el caracter es igual a + o * (operadores soportados por la gramática)
        // se regresa true
        if ( c == '+' || c == '*' )
            return true;
        else
            return false;
    }
   
    //--------------------------------------------------------------------------

    // Método que determina si un elemento es un operando
    private static boolean esOperando ( char c )
    {
        // Si el caracter es una letra o un número se regresa true
        if ( c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' )
            return true;
        else
            return false;
    }
    
    //--------------------------------------------------------------------------
}

    //--------------------------------------------------------------------------
    //::