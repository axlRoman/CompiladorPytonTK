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


public class GenCodigoInt {
 
    private Compilador cmp;

      public static final String VACIO = "vacio";
    public static final String ERROR_TIPO = "error_tipo";
    public static final String NIL = "";
    public static final String VOID = "";
     ArrayList<Character> arrExprInfijo = new ArrayList<>();
    private int        consecutivoTmp;
    private int        consecutivoEtiq; 
    private String preAnalisis;
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
        
        preAnalisis = cmp.be.preAnalisis.complex;
        consecutivoTmp  = 1;
        consecutivoEtiq = 1;
        
        PROGRAMA (new Atributos());
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
    private void PROGRAMA(Atributos PROGRAMA) {
        Atributos INSTRUCCION = new Atributos();
        Atributos PROGRAMA1 = new Atributos ();
        if (preAnalisis.equals("def") //pro->funcion-> def
                || preAnalisis.equals("int") || preAnalisis.equals("float")//pro->proposicion-> esto...
                || preAnalisis.equals("id") || preAnalisis.equals("if") || preAnalisis.equals("while") || preAnalisis.equals("print") || preAnalisis.equals("string")) {
            
            INSTRUCCION(INSTRUCCION);
            PROGRAMA(PROGRAMA1);
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
    

    private void INSTRUCCION(Atributos INSTRUCCION) {

        Atributos FUNCION = new Atributos();
        Atributos PROPOSICION = new Atributos();
        
        if (preAnalisis.equals("def")) {
            FUNCION(FUNCION);
        
            INSTRUCCION.tipo=FUNCION.tipo;
        
        } 
        else if (preAnalisis.equals("int") || preAnalisis.equals("float") || 
                 preAnalisis.equals("string") || preAnalisis.equals("id") || 
                 preAnalisis.equals("if") || preAnalisis.equals("while") || 
                 preAnalisis.equals("print")) 
        {

            PROPOSICION(PROPOSICION);

            INSTRUCCION.tipo=PROPOSICION.tipo;

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
    private void FUNCION(Atributos FUNCION) {
        
        Atributos ARGUMENTOS = new Atributos();
        Atributos TIPO_RETORNO = new Atributos();
        Atributos PROPOSICIONES_OPTATIVAS = new Atributos();
        Atributos RESULTADO = new Atributos();
        Linea_BE id = new Linea_BE ();

        if (preAnalisis.equals("def")) {

            emparejar("def");
            id = cmp.be.preAnalisis;
            emparejar("id");
            emparejar("(");
            ARGUMENTOS(ARGUMENTOS);
            emparejar(")");
            emparejar(":");
            TIPO_RETORNO(TIPO_RETORNO);

            
            PROPOSICIONES_OPTATIVAS(PROPOSICIONES_OPTATIVAS);

            emparejar("return");
            RESULTADO(RESULTADO);

            emparejar(":");
            emparejar(":");
        }
        else 
        {
            error("[FUNCION] Expresión inválida "
                    + "se esperaba def y se encontró "
                    + preAnalisis + cmp.be.preAnalisis.numLinea);
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
    private void DECLARACION_VARS(Atributos DECLARACION_VARS ) {

        Atributos TIPO_DATO = new Atributos();
        Atributos DECLARACION_VARS_P = new Atributos();
        Linea_BE id = new Linea_BE ();

        if (preAnalisis.equals("int") || preAnalisis.equals("float") || 
            preAnalisis.equals("string")) 
        {
            TIPO_DATO(TIPO_DATO);
            id = cmp.be.preAnalisis;
            emparejar("id");

            DECLARACION_VARS_P(DECLARACION_VARS_P);

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
    private void DECLARACION_VARS_P(Atributos DECLARACION_VARS_P) {
        Atributos DECLARACION_VARS_P1 = new Atributos ();
        Linea_BE id = new Linea_BE ();

        if (preAnalisis.equals(",")) {
            emparejar(",");
            id = cmp.be.preAnalisis;
            emparejar("id");

            DECLARACION_VARS_P(DECLARACION_VARS_P1);
        } else {
            //ε->vacio
        }
    }
   
    
    //Autor: Francisco Axel Roman Cardoza - No. Control: 19130971
    //TIPO_RETORNO -> void | TIPO_DATO
    private void TIPO_RETORNO(Atributos TIPO_RETORNO) {
        Atributos TIPO_DATO = new Atributos();
        if (preAnalisis.equals("void")) //Primeros (TIPO_RETORNO) = {void, int, float, string}
        {
            emparejar(preAnalisis);
        } 
        else if (preAnalisis.equals("int") || preAnalisis.equals("float") || 
                 preAnalisis.equals("string")) 
        {
            TIPO_DATO(TIPO_DATO);
        } else {
            error ( "[TIPO_RETORNO] : Se esperaba un tipo de dato."  
            + cmp.be.preAnalisis.numLinea ); 
        }
    }
    

    //Autor: Francisco Axel Roman Cardoza - No. Control: 19130971
    // RESULTADO -> EXPRESION | void
    public void RESULTADO(Atributos RESULTADO) {
        //Primeros (RESULTADO) = {void, literal, id, num, num.num, (, opsuma, empty, opmult, (, empty}
        Atributos EXPRESION =  new Atributos();

        if  (preAnalisis.equals ( "literal" ) || preAnalisis.equals ( "id" )      ||
             preAnalisis.equals ( "num" )     || preAnalisis.equals ( "num.num" ) ||
             preAnalisis.equals ( "(" ) )  
        {
            EXPRESION(EXPRESION);

        } 
        else if (preAnalisis.equals("void")) 
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
    public void PROPOSICIONES_OPTATIVAS(Atributos PROPOSICIONES_OPTATIVAS) {
        //Primeros (PROPOSICIONES_OPTATIVAS) = {id, if, while, print, int, float, string, empty}
        Atributos PROPOSICION = new Atributos ();
        Atributos PROPOSICIONES_OPTATIVAS1 = new Atributos ();

        if ( preAnalisis.equals ( "int" )    || preAnalisis.equals ( "float" ) || 
             preAnalisis.equals ( "string" ) || preAnalisis.equals ( "id" )    || 
             preAnalisis.equals ( "if" )     || preAnalisis.equals ( "while" ) || 
             preAnalisis.equals ( "print" ) )
        {
            PROPOSICION(PROPOSICION);
            PROPOSICIONES_OPTATIVAS(PROPOSICIONES_OPTATIVAS1);

        } else {
            //ε->vacio
        }
    }

    
    //Autor: Francisco Axel Roman Cardoza - No. Control: 19130971
    //PROPOSICION -> DECLARACION_VARS | id PROPOSICION_P | if CONDICION : PROPOSICIONES_OPTATIVAS else : PROPOSICIONES_OPTATIVAS :: 
    // | while CONDICION : PROPOSICIONES_OPTATIVAS :: | print ( EXPRESION )
    public void PROPOSICION(Atributos PROPOSICION) {
        //Primeros (PROPOSICION) = {id, if, while, print, int, float, string}
        Atributos DECLARACION_VARS = new Atributos ();
        Atributos PROPOSICION_P = new Atributos ();
        Atributos CONDICION = new Atributos ();
        Atributos PROPOSICIONES_OPTATIVAS1 = new Atributos ();
        Atributos PROPOSICIONES_OPTATIVAS2 = new Atributos ();
        Atributos CONDICION1 = new Atributos ();
        Atributos PROPOSICIONES_OPTATIVAS3 = new Atributos ();
        Atributos EXPRESION = new Atributos ();
        Linea_BE id = new Linea_BE ();

        if ( preAnalisis.equals ( "int" ) || preAnalisis.equals ( "float" ) || 
             preAnalisis.equals ( "string" ) )
        {
            DECLARACION_VARS ( DECLARACION_VARS );
            
        }
        else if ( preAnalisis.equals( "id" ) )
        {
          
            id = cmp.be.preAnalisis;
            emparejar ( "id" );
            PROPOSICION_P ( PROPOSICION_P );
            emite(  id.lexema+ ":=" +PROPOSICION_P.lugar);
            cmp.cua.agregar(new Cuadruplo ( ":=", PROPOSICION_P.lugar,"",id.lexema) );
         
        }
        else if ( preAnalisis.equals ( "if" ) )
        {
            emparejar ( "if" );
            CONDICION ( CONDICION );
            emparejar ( ":" );
            PROPOSICIONES_OPTATIVAS ( PROPOSICIONES_OPTATIVAS1 );
            
            emparejar ( "else" );
            emparejar ( ":" );
            PROPOSICIONES_OPTATIVAS ( PROPOSICIONES_OPTATIVAS2 );

            emparejar ( ":" );
            emparejar ( ":" );
        }
        else if ( preAnalisis.equals ( "while" ) )
        {
            emparejar ( "while" );
            CONDICION ( CONDICION1 );
            emparejar ( ":" );
            PROPOSICIONES_OPTATIVAS ( PROPOSICIONES_OPTATIVAS3 );
            
            
            emparejar ( ":" );
            emparejar ( ":" );
        }
        else if ( preAnalisis.equals ( "print" ) )
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
        Atributos LISTA_EXPRESIONES = new Atributos ();
        Atributos FACTOR_P = new Atributos ();
        Atributos E = new Atributos ();
        Linea_BE id = new Linea_BE ();
                
        if (preAnalisis.equals("opasig")) 
        {
            emparejar("opasig");
            EXPRESION(EXPRESION);
            if(EXPRESION.tipoexpre==1){
                EXPRESION.lugar=infixToPrefix(arrExprInfijo);
                PROPOSICION_P.lugar=E.lugar;
                
            }else{
                
            }
        } 
        else if (preAnalisis.equals("(")) 
        {
            emparejar("(");
            LISTA_EXPRESIONES(LISTA_EXPRESIONES);
            emparejar(")");
        } else {
            // Manejar error sintáctico o lanzar una excepción si el preAnalisis no es válido.
            error ( "[PROPOSICION_p]: Error de expresión en operador " +
                        "No. Linea: " + cmp.be.preAnalisis.numLinea  );
        }
    }
    
    //Autor: Francisco Axel Roman Cardoza - No. Control: 19130971
    //CONDICION -> EXPERSION oprel EXPRESION
    public void CONDICION(Atributos CONDICION) 
    {
        Atributos EXPRESION = new Atributos ();
        Atributos EXPRESION1 = new Atributos ();

        if ( preAnalisis.equals ( "id" )      ||
             preAnalisis.equals ( "num" )     ||
             preAnalisis.equals ( "num.num" ) ||
             preAnalisis.equals ( "(" )       ||
             preAnalisis.equals ( "literal" ) ) 
        {
        
            EXPRESION ( EXPRESION );
            emparejar ( "oprel" );
            EXPRESION ( EXPRESION1 );
      
          
        } 
        else 
        {
            error ( "[CONDICION] Expresión inválida"
                    + " se esperaba id o num o num.num o ( o literal y se encontró "
                    + preAnalisis + cmp.be.preAnalisis.numLinea );
        }
    }


    //Autor: Braulio Esteban Gonzalez Alanis - No. Control: 20131498
    //TIPO_DATO -> int | float | string
    private void TIPO_DATO ( Atributos TIPO_DATO )
    {
        if ( preAnalisis.equals ( "int" ) )
        {
            emparejar ( "int" );
         
        }
        else if ( preAnalisis.equals ( "float" ) )
        {
            emparejar ( "float" );
        }
        else if ( preAnalisis.equals ( "string" ) )
        {
            emparejar ( "string" );
        }
        else
            error ( "[TIPO_DATO] : Tipo de dato esperado." +
                    "No. Línea: " + cmp.be.preAnalisis.numLinea );
    }
    
    
   
    //Autor: Braulio Esteban Gonzalez Alanis - No. Control: 20131498
    //ARGUMENTOS -> TIPO_DATO id ARGUMENTOS_P | ε
    private void ARGUMENTOS(Atributos ARGUMENTOS) {
        
        Atributos TIPO_DATO = new Atributos ();
        Atributos ARGUMENTOS_P = new Atributos ();
        Linea_BE id = new Linea_BE ();
        
        if (preAnalisis.equals("int") || preAnalisis.equals("float") || 
            preAnalisis.equals("string")) {
            
            TIPO_DATO( TIPO_DATO);
            id = cmp.be.preAnalisis;
            
            emparejar("id");
            
            ARGUMENTOS_P(ARGUMENTOS_P);
            
        } 
        else 
        {
            //ε->vacio
        }
    }
    
   
    //Autor: Braulio Esteban Gonzalez Alanis - No. Control: 20131498
    //ARGUEMNTOS_P -> , TIPO_DATO id ARGUEMNTOS_P | ε
    private void ARGUMENTOS_P(Atributos ARGUMENTOS_P) {
        
        Atributos TIPO_DATO = new Atributos ();
        Atributos ARGUMENTOS_P1 = new Atributos ();
        Atributos ARGUMENTOS = new Atributos ();
        Linea_BE id = new Linea_BE ();
        
        
        if (preAnalisis.equals(",")) 
        {
            emparejar(",");
            TIPO_DATO(TIPO_DATO);
            id = cmp.be.preAnalisis;
            emparejar("id");
            
                  
            ARGUMENTOS_P( ARGUMENTOS_P1);
        } else {
            //ε->vacio
        }
    }
    

    //Autor: Braulio Esteban Gonzalez Alanis - No. Control: 20131498
    //LISTA_EXPRESIONES -> EXPRESION LISTA_EXPRESIONES_P | ε
    private void LISTA_EXPRESIONES(Atributos LISTA_EXPRESIONES) 
    {
        Atributos EXPRESION = new Atributos ();
        Atributos LISTA_EXPRESIONES_P = new Atributos ();
        
        if (preAnalisis.equals("literal")
                || preAnalisis.equals("id")
                || preAnalisis.equals("num")
                || preAnalisis.equals("num.num")
                || preAnalisis.equals("(")) {
            
 // LISTA_EXPRESIONES -> EXPRESION  LISTA_EXPRESIONES'
            EXPRESION(EXPRESION);
            LISTA_EXPRESIONES_P(LISTA_EXPRESIONES_P);
    
    }
    }
    
    
    //Autor: Braulio Esteban Gonzalez Alanis - No. Control: 20131498
    //LISTA_EXPRESIONES_P -> , EXPRESION LISTA_EXPRESIONES | ε
    private void LISTA_EXPRESIONES_P(Atributos LISTA_EXPRESIONES_P) {
        
        Atributos EXPRESION = new Atributos ();
        Atributos LISTA_EXPRESIONES_P1 = new Atributos ();
        
        if (preAnalisis.equals(",")) {
            
            emparejar(",");
            EXPRESION( EXPRESION);
            LISTA_EXPRESIONES_P(LISTA_EXPRESIONES_P1);
            
        } else {
            //ε->vacio
        }
    }
    
  
    //Autor: Arturo Rosales Valdés - No. Control: 20130766
    //EXPRESION -> TERMINO EXPRESION_P | literal
    private void EXPRESION(Atributos EXPRESION) 
    {
        Atributos TERMINO = new Atributos ();
        Atributos EXPRESION_P = new Atributos ();
        Linea_BE literal = new Linea_BE ();

        if (preAnalisis.equals("id")
                || preAnalisis.equals("num")
                || preAnalisis.equals("num.num")
                || preAnalisis.equals("(")) {
            TERMINO(TERMINO);
            EXPRESION_P.her = TERMINO.tipo;
            EXPRESION_P(EXPRESION_P);
            //accion semantica 10
            EXPRESION.tipoexpre=1;
            //fin accion semantica 10
            

    
        } else if (preAnalisis.equals("literal")) {
            literal = cmp.be.preAnalisis;

            emparejar("literal");/********************************************************************************/
            cmp.ts.anadeTipo ( literal.entrada, "string" );
                    EXPRESION.tipo = "string";
            //accion semantica 11
            
            EXPRESION.tipoexpre=2;
            //fin accion semantica 11
        } else {
            error ( "[EXPRESION] Expresión no válida." + "N° Línea: " 
                    + cmp.be.preAnalisis.numLinea );    
        }
    }
    
    
    //Autor: Arturo Rosales Valdés - No. Control: 20130766
    //EXPRESION_P -> opsuma TERMINO EXPRESION_P | ε
    private void EXPRESION_P(Atributos EXPRESION_P) {
        Atributos TERMINO = new Atributos ();
        Atributos EXPRESION_P1 = new Atributos ();
        Linea_BE opsuma = new Linea_BE ();


        if (preAnalisis.equals("opsuma")) {
            emparejar("opsuma");
             for (char c : opsuma.lexema.toCharArray()) {
            arrExprInfijo.add( c );
            }
            TERMINO(TERMINO);
            EXPRESION_P(EXPRESION_P1);
        } else {
            
            //ε->vacio
        }
    }
    
    
    //Autor: Arturo Rosales Valdés - No. Control: 20130766
    //TERMINO -> FACTOR TERMINO_P
    private void TERMINO(Atributos TERMINO) {
        Atributos FACTOR = new Atributos ();
        Atributos TERMINO_P = new Atributos ();
        
        //ACCION SEMANTICO {38}

        if ( preAnalisis.equals ( "id" )      || preAnalisis.equals ( "num" ) || 
             preAnalisis.equals ( "num.num" ) || preAnalisis.equals ( "(" ) ) 
        {
            FACTOR(FACTOR);

            TERMINO_P(TERMINO_P);

       
        } else {
            error ( "[TERMINO]: se esperaba una expresion" );
        }
        //FIN ACCION {38}
    }
    
    
    //Autor: Arturo Rosales Valdés - No. Control: 20130766
    //TERMINO_P -> opmult FACTOR TERMINO_P | ε
    private void TERMINO_P(Atributos TERMINO_P) {
        Atributos FACTOR = new Atributos ();
        Atributos TERMINO_P1 = new Atributos ();
        Linea_BE opmult = new Linea_BE ();
        if (preAnalisis.equals("opmult")) {
            emparejar("opmult");
              for (char c : opmult.lexema.toCharArray()) {
            arrExprInfijo.add( c );
            }
            FACTOR(FACTOR);

            TERMINO_P(TERMINO_P1);

       
        } else {
            //ε->vacio
        }
    }
    
    
    //Autor: Arturo Rosales Valdés - No. Control: 20130766
    //FACTOR -> id FACTOR_P | num | num.num | ( EXPRESION )
    private void FACTOR(Atributos FACTOR) {
        Atributos FACTOR_P = new Atributos ();
        Atributos EXPRESION = new Atributos ();
        Linea_BE id = new Linea_BE ();
        Linea_BE num = new Linea_BE ();
        Linea_BE num_num = new Linea_BE ();

        if ( preAnalisis.equals ( "id" ) ) 
         {
            id = cmp.be.preAnalisis;
            emparejar ( "id" );
         
           for (char c : id.lexema.toCharArray()) {
            arrExprInfijo.add( c );
            }
            FACTOR_P ( FACTOR_P );
            
        } 
         else if ( preAnalisis.equals ( "num" ) ) 
         {
            num = cmp.be.preAnalisis;
            emparejar ( "num" );
            for (char c : num.lexema.toCharArray()) {
            arrExprInfijo.add( c );
            }
            
        } 
        else if ( preAnalisis.equals ( "num.num" ) ) 
        {
            num_num = cmp.be.preAnalisis;
            emparejar ( "num.num" );
            for (char c : num_num.lexema.toCharArray()) {
            arrExprInfijo.add( c );
            }
        } 
        else if ( preAnalisis.equals ( "(" ) ) 
        {
            emparejar ( "(" );
            arrExprInfijo.add( '(');
            
            EXPRESION ( EXPRESION );
            emparejar ( ")" );
             for (char c : num_num.lexema.toCharArray()) {
            arrExprInfijo.add( ')' );
            }
            
        }
        else 
        {
            error( "[FACTOR] : Se esperaba un id, num, num.num o (. No. de línea: " 
                    + cmp.be.preAnalisis.numLinea );    
        }
    }
    
    
    //Autor: Arturo Rosales Valdés - No. Control: 20130766
    //FACTOR_P -> ( LISTA_EXPRESIONES ) | ε
    private void FACTOR_P(Atributos FACTOR_P) {
        Atributos LISTA_EXPRESIONES = new Atributos ();
        Linea_BE id = new Linea_BE ();

        if ( preAnalisis.equals ( "(" ) )
            {
                emparejar ( "(" );
                LISTA_EXPRESIONES ( LISTA_EXPRESIONES );
                emparejar (")" );
                
            }
            else
            {
       
            }
    }

    //--------------------------------------------------------------------------
    public static boolean comparar ( String tipo1, String tipo2 ) 
    {
        return (
            ( tipo1.equals ( tipo2    ) )                                ||
            ( tipo1.equals ( "int"    ) && tipo2.equals ( "float"  ) )   ||
            ( tipo1.equals ( "float"  ) && tipo2.equals ( "float"  ) )   ||
            ( tipo1.equals ( "string" ) && tipo2.equals ( "string" ) )
        );

    }
    
    //--------------------------------------------------------------------------
    
    public static boolean esCompatible ( String T1, String T2 ) 
    {
        String T3 = T1.replace ( " ", "" );
        String T4 = T2.replace ( " ", "" );
        String [] tokens  = T3.split ( "[x X]" );
        String [] tokens2 = T4.split ( "[x X]" );
                
        String resp = "";
        int cont  = 0;//declaramos varible cont
        int cont2 = 0;

        for ( int j = 0; j < tokens.length; j++ )
        {
            cont++;            
        }
        
        for ( int k = 0; k < tokens2.length; k++ )
        {
            cont2++;
        }
        
        if ( cont == cont2 )
        {
            for ( int i = 0; i < tokens.length; i++ )
            {

                if ( comparar ( tokens [i],tokens2 [i] ) == true )
                {
                    resp += "true";
                }
                else
                {
                    resp += "false";
                }
            }
        }
        else
        {
            return false;
        }
        
        if ( resp.contains ( "false" ) ) 
        {
              return false;
        }
        else
            return true;
    }
    
    /*Codigo para cambio de infijo a prefijo---------------------------------------------------*/
    static boolean isalpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    static boolean isdigit(char c) {
        return c >= '0' && c <= '9';
    }

    static boolean isOperator(char c) {
        return !isalpha(c) && !isdigit(c);
    }

    static int getPriority(char C) {
       switch (C) {
            case '-':
            case '+':
                return 1;
            case '*':
            case '/':
                return 2;
            case '^':
                return 3;
            default:
                break;
        }

        return 0;
    }

    static String reverse(char str[], int start, int end) {
        char temp;
        while (start < end) {
            temp = str[start];
            str[start] = str[end];
            str[end] = temp;
            start++;
            end--;
        }
        return String.valueOf(str);
    }

    static String infixToPostfix(ArrayList<Character> infix) {
        ArrayList<Character> char_stack = new ArrayList<>();
        StringBuilder output = new StringBuilder();

        for (char c : infix) {
            if (isalpha(c) || isdigit(c))
                output.append(c);
            else if (c == '(')
                char_stack.add('(');
            else if (c == ')') {
                while (char_stack.get(char_stack.size() - 1) != '(') {
                    output.append(char_stack.remove(char_stack.size() - 1));
                }
                char_stack.remove(char_stack.size() - 1); // Remove '(' from the stack
            } else {
                while (!char_stack.isEmpty() && isOperator(char_stack.get(char_stack.size() - 1))
                        && (getPriority(c) <= getPriority(char_stack.get(char_stack.size() - 1))))
                    output.append(char_stack.remove(char_stack.size() - 1));
                char_stack.add(c);
            }
        }

        while (!char_stack.isEmpty()) {
            output.append(char_stack.remove(char_stack.size() - 1));
        }

        return output.toString();
    }

    static String infixToPrefix(ArrayList<Character> infix) {
        Collections.reverse(infix);

        for (int i = 0; i < infix.size(); i++) {
            if (infix.get(i) == '(') {
                infix.set(i, ')');
            } else if (infix.get(i) == ')') {
                infix.set(i, '(');
            }
        }

        String postfix = infixToPostfix(infix);

        StringBuilder prefix = new StringBuilder(postfix);
        prefix.reverse();

        return prefix.toString();
    }
    /*Fin del Codigo para cambio de infijo a prefijo---------------------------*/
    
    
    }
