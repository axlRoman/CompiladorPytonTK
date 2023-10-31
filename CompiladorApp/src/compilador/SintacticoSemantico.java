/*:--------------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:                  SEMESTRE: AgoDic2023    HORA: __7-8__ HRS
 *:                                   
 *:               
 *:         Clase con la funcionalidad del Analizador Sintactico
 *                 
 *:                           
 *: Archivo       : SintacticoSemantico.java
 *: Autor         : Fernando Gil  ( Estructura general de la clase  )
 *:                 Grupo de Lenguajes y Automatas II ( Procedures  )
 *: Fecha         : 03/SEP/2014
 *: Compilador    : Java JDK 7
 *: Descripción   : Esta clase implementa un parser descendente del tipo 
 *:                 Predictivo Recursivo. Se forma por un metodo por cada simbolo
 *:                 No-Terminal de la gramatica mas el metodo emparejar ().
 *:                 El analisis empieza invocando al metodo del simbolo inicial.
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:================================================================================
 *: 22/Feb/2015 FGil                -Se mejoro errorEmparejar () para mostrar el
 *:                                 numero de linea en el codigo fuente donde 
 *:                                 ocurrio el error.
 *: 08/Sep/2015 FGil                -Se dejo lista para iniciar un nuevo analizador
 *:                                 sintactico.
 *: 20/FEB/2023 F.Gil, Oswi         -Se implementaron los procedures del parser
 *:                                  predictivo recursivo de leng BasicTec.
 *: 15/SEP/2023 Julian Rodolfo Villa Cruz, 
 *:             Arturo Rosales Valdez,              -Se implementaron los procedures del parser 
 *:             Francisco Axel Roman Cardoza,        predictivo del lenguaje PaytonTK.
 *:             Braulio Esteban Gonzales Alanis                     
                                     
ULTIMA CAPTURA ACERCA DE 
 *:--------------------------------------------------------------------------------
 */
package compilador;

import javax.swing.JOptionPane;
import general.Linea_BE;

public class SintacticoSemantico {

    public static final String VACIO = "vacio";
    public static final String ERROR_TIPO = "error_tipo";
    public static final String  NIL = "";
    public static final String VOID = "";
    
    private Compilador cmp;
    private boolean analizarSemantica = false;
    private String preAnalisis;
    
    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.

    public SintacticoSemantico(Compilador c) {
        cmp = c;
    }

    //--------------------------------------------------------------------------
    // Metodo que inicia la ejecucion del analisis sintactico predictivo.
    // analizarSemantica : true = realiza el analisis semantico a la par del sintactico
    //                     false= realiza solo el analisis sintactico sin comprobacion semantica

    public void analizar(boolean analizarSemantica) {
        this.analizarSemantica = analizarSemantica;
        preAnalisis = cmp.be.preAnalisis.complex;
        // * * *   INVOCAR AQUI EL PROCEDURE DEL SIMBOLO INICIAL   * * *
        Atributos PROGRAMA = new Atributos();
        PROGRAMA(PROGRAMA);
    }

    
    private void emparejar(String t) {
        if (cmp.be.preAnalisis.complex.equals(t)) {
            cmp.be.siguiente();
            preAnalisis = cmp.be.preAnalisis.complex;
        } else {
            errorEmparejar(t, cmp.be.preAnalisis.lexema, cmp.be.preAnalisis.numLinea);
        }
    }
    
    
    // Metodo para devolver un error al emparejar
    private void errorEmparejar(String _token, String _lexema, int numLinea) {
        String msjError = "";

        if (_token.equals("id")) {
            msjError += "Se esperaba un identificador";
        } else if (_token.equals("num")) {
            msjError += "Se esperaba una constante entera";
        } else if (_token.equals("num.num")) {
            msjError += "Se esperaba una constante real";
        } else if (_token.equals("literal")) {
            msjError += "Se esperaba una literal";
        } else if (_token.equals("oparit")) {
            msjError += "Se esperaba un operador aritmetico";
        } else if (_token.equals("oprel")) {
            msjError += "Se esperaba un operador relacional";
        } else if (_token.equals("opasig")) {
            msjError += "Se esperaba operador de asignacion";
        } else {
            msjError += "Se esperaba " + _token;
        }
        msjError += " se encontró " + (_lexema.equals("$") ? "fin de archivo" : _lexema)
                + ". Linea " + numLinea;        // FGil: Se agregó el numero de linea

        cmp.me.error(Compilador.ERR_SINTACTICO, msjError);
    }
    // Fin de ErrorEmparejar
    

    // Metodo para mostrar un error sintactico
    private void error(String _descripError) {
        cmp.me.error ( cmp.ERR_SINTACTICO, 
                       _descripError + 
                       "Linea: " + cmp.be.preAnalisis.numLinea );
    }
    // Fin de error
    
    
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
            PROGRAMA(PROGRAMA);
            
            if ( analizarSemantica )
            {
                if ( INSTRUCCION.tipo.equals ( VACIO ) && PROGRAMA1.tipo.equals ( VACIO ) )
                    PROGRAMA.tipo = VACIO;
                else
                {
                    PROGRAMA.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, 
                        "[PROGRAMA] Error de inicio del Programa.");
                }
            }
        } else {
            //ε->vacio
                PROGRAMA.tipo=VACIO;
            if(!preAnalisis.equals("$")){
                error("[PROGRAMA]: Se esperaba fin del archivo."+
                        " Se encontro: "+cmp.be.preAnalisis.lexema+
                        "\nNo Linea: "+cmp.be.preAnalisis.getNumLinea());
            }
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

            if ( analizarSemantica )
            {
                if ( cmp.ts.buscaTipo ( id.entrada ).equals ( NIL ) && ARGUMENTOS.tipo.equals ( VACIO ) &&
                     TIPO_RETORNO.tipo != ERROR_TIPO )
                {
                    cmp.ts.anadeTipo ( id.entrada, ARGUMENTOS.her + " -> " + TIPO_RETORNO.tipo );
                    FUNCION.tipo = VACIO;
                }
                else
                {
                    FUNCION.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, 
                        "[FUNCIÓN]: Expresión inválida, identificador redeclarado o argumentos inválidos");
                }
            }
            
            PROPOSICIONES_OPTATIVAS(PROPOSICIONES_OPTATIVAS);

            if ( analizarSemantica )
            {
                if ( PROPOSICIONES_OPTATIVAS.tipo.equals ( VACIO ) && FUNCION.tipo.equals ( VACIO ) )
                    FUNCION.tipo = VACIO;
                else
                {
                    FUNCION.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, 
                        "[FUNCIÓN]: Expresión inválida, error de tipo en la función declarada");
                }
            }

            emparejar("return");
            RESULTADO(RESULTADO);

            if ( analizarSemantica )
            {
                if ( RESULTADO.tipo .equals ( TIPO_RETORNO.tipo ) && FUNCION.tipo.equals ( VACIO ) )
                    FUNCION.tipo = VACIO;
                else
                {
                    FUNCION.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, 
                        "[FUNCIÓN]: Expresión inválida tipo de dato incompatible o void esperado");
                }
            }

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
            if ( analizarSemantica )
            {
                if ( cmp.ts.buscaTipo ( id.entrada ).equals ( NIL ) )
                {
                    cmp.ts.anadeTipo ( id.entrada, TIPO_DATO.tipo );
                    DECLARACION_VARS_P.her = TIPO_DATO.tipo;
                    DECLARACION_VARS.tipo = VACIO;
                }
                else
                {
                    DECLARACION_VARS.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, 
                        "[DECLARACIÓN_VARS]: Tipo de dato de identificadores no válido");
                }
            }

            DECLARACION_VARS_P(DECLARACION_VARS_P);

             if ( analizarSemantica )
            {
                if ( DECLARACION_VARS.tipo.equals ( VACIO ) && DECLARACION_VARS_P.tipo.equals ( VACIO ) )
                    DECLARACION_VARS.tipo = VACIO;
                else
                {
                    DECLARACION_VARS.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, 
                        "[DECLARACIÓN_VARS]: Declaracion de variables inválida, se esperaba una palabra reservada");
                }
            }
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

            if ( analizarSemantica )
            {
                if ( cmp.ts.buscaTipo ( id.entrada ).equals ( NIL ) )
                {
                    cmp.ts.anadeTipo ( id.entrada, DECLARACION_VARS_P.her );
                    DECLARACION_VARS_P1.her = DECLARACION_VARS_P.her;
                    DECLARACION_VARS_P.tipo = VACIO;
                }
                else
                {
                    DECLARACION_VARS_P.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, 
                        "[DECLARACION_VARS'] Tipo de dato en identificadores no válido");
                }
                
            }

            DECLARACION_VARS_P(DECLARACION_VARS_P1);
            if ( analizarSemantica )
            {
                if ( DECLARACION_VARS_P.tipo.equals ( VACIO ) && DECLARACION_VARS_P1.tipo.equals ( VACIO ) )
                    DECLARACION_VARS_P.tipo = VACIO;
                else
                {
                    DECLARACION_VARS_P.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, 
                        "[DECLARACION_VARS'] Declaración de variables inválida, se esperaba una palabra reservada");
                }
            }
        } else {
            //ε->vacio
            DECLARACION_VARS_P.tipo = VACIO;
        }
    }
   
    
    //Autor: Francisco Axel Roman Cardoza - No. Control: 19130971
    //TIPO_RETORNO -> void | TIPO_DATO
    private void TIPO_RETORNO(Atributos TIPO_RETORNO) {
        Atributos TIPO_DATO = new Atributos();
        if (preAnalisis.equals("void")) //Primeros (TIPO_RETORNO) = {void, int, float, string}
        {
            emparejar(preAnalisis);
            
            TIPO_RETORNO.tipo = VOID;
        } 
        else if (preAnalisis.equals("int") || preAnalisis.equals("float") || 
                 preAnalisis.equals("string")) 
        {
            TIPO_DATO(TIPO_DATO);

            TIPO_RETORNO.tipo = TIPO_DATO.tipo;
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

            RESULTADO.tipo = EXPRESION.tipo;
        } 
        else if (preAnalisis.equals("void")) 
        {
            emparejar("void");

            RESULTADO.tipo = VOID;
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

            if ( analizarSemantica )
            {
                if ( PROPOSICION.tipo.equals ( VACIO ) && PROPOSICIONES_OPTATIVAS1.tipo.equals ( VACIO ) )
                    PROPOSICIONES_OPTATIVAS.tipo = VACIO;
                else
                {
                    PROPOSICIONES_OPTATIVAS.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, 
                        "[PROPOSICIONES OPTATIVAS]: Palabra reservada esperada");
                }
            }
        } else {
            //ε->vacio
            PROPOSICIONES_OPTATIVAS.tipo = VACIO;
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
            
            PROPOSICION.tipo = DECLARACION_VARS.tipo;

        }
        else if ( preAnalisis.equals( "id" ) )
        {
          
            id = cmp.be.preAnalisis;
            emparejar ( "id" );
            PROPOSICION_P ( PROPOSICION_P );
            
        
            if ( analizarSemantica )
            {
                if ( cmp.ts.buscaTipo ( id.entrada ).equals ( PROPOSICION_P.tipo ) )
                    PROPOSICION.tipo = VACIO;
                else
                {
                    PROPOSICION.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, 
                        "[PROPOSICION]: identificador redeclarado o tipo incompatible");
                }
            }
         
        }
        else if ( preAnalisis.equals ( "if" ) )
        {
            emparejar ( "if" );
            CONDICION ( CONDICION );
            emparejar ( ":" );
            PROPOSICIONES_OPTATIVAS ( PROPOSICIONES_OPTATIVAS1 );
            

            if ( analizarSemantica )
            {
                if ( CONDICION.tipo.equals ( "boolean" ) && PROPOSICIONES_OPTATIVAS1.tipo.equals ( VACIO ) )
                    PROPOSICION.tipoaux = VACIO;
                else
                {
                    PROPOSICION.tipoaux = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, 
                        "[PROPOSICION]: Expresión de condición if inválida");
                }
            }

            
            emparejar ( "else" );
            emparejar ( ":" );
            PROPOSICIONES_OPTATIVAS ( PROPOSICIONES_OPTATIVAS2 );

            if ( analizarSemantica )
            {
            if ( PROPOSICION.tipoaux.equals ( VACIO ) && PROPOSICIONES_OPTATIVAS2.tipo.equals ( VACIO ) )
                PROPOSICION.tipo = VACIO;
            else
            {
                PROPOSICION.tipo = ERROR_TIPO;
                cmp.me.error(Compilador.ERR_SEMANTICO, 
                        "[PROPOSICION]: Expresión de condición else inválida");
            }
            }
         
            
            emparejar ( ":" );
            emparejar ( ":" );
        }
        else if ( preAnalisis.equals ( "while" ) )
        {
            emparejar ( "while" );
            CONDICION ( CONDICION1 );
            emparejar ( ":" );
            PROPOSICIONES_OPTATIVAS ( PROPOSICIONES_OPTATIVAS3 );
            
      
            if ( analizarSemantica )
            {
                if ( CONDICION1.tipo.equals ( "boolean" ) && PROPOSICIONES_OPTATIVAS3.tipo.equals ( VACIO ) )
                    PROPOSICION.tipo = VACIO;
                else
                {
                    PROPOSICION.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, 
                        "[PROPOSICION]: Expresión de condición while inválida");
                }
            }
            
            
            emparejar ( ":" );
            emparejar ( ":" );
        }
        else if ( preAnalisis.equals ( "print" ) )
        {
         
            emparejar ( "print" );
            emparejar ( "(" );
            EXPRESION ( EXPRESION );
            emparejar ( ")" );
            
           
            if ( analizarSemantica )
            {
                if ( EXPRESION.tipo.equals ( "int" ) || EXPRESION.tipo.equals ( "float" ) ||
                     EXPRESION.tipo.equals ( "string" ) )
                    PROPOSICION.tipo = VACIO;
                else
                {
                    PROPOSICION.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, 
                        "[PROPOSICION]: Declaración de expresión inválida");
                }
            }
            
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

        if (preAnalisis.equals("opasig")) 
        {
            emparejar("opasig");
            EXPRESION(EXPRESION);
            PROPOSICION_P.tipo = EXPRESION.tipo;

        } 
        else if (preAnalisis.equals("(")) 
        {
            emparejar("(");
            LISTA_EXPRESIONES(LISTA_EXPRESIONES);
            emparejar(")");
            if( analizarSemantica )
            {
                String tipoid = cmp.ts.buscaTipo ( Integer.parseInt ( FACTOR_P.her ) );
                if ( tipoid.contains ( "->" ) )
                {
                    int indice = tipoid.indexOf ( "->" );
                    String aux = tipoid.substring ( indice );
                    if ( esCompatible ( aux, LISTA_EXPRESIONES.tipo ))
                       PROPOSICION_P.tipo = LISTA_EXPRESIONES.tipo;
                    else
                    {
                       PROPOSICION_P.tipo = ERROR_TIPO;
                       cmp.me.error(Compilador.ERR_SEMANTICO,
                               "[PROPOSICION']: Expresión inválida");
                    }
                }
                else if ( tipoid.equals ( "int"    ) && LISTA_EXPRESIONES.tipo.equals ( VACIO ) || 
                          tipoid.equals ( "float"  ) && LISTA_EXPRESIONES.tipo.equals ( VACIO ) || 
                          tipoid.equals ( "string" ) && LISTA_EXPRESIONES.tipo.equals ( VACIO ) )
                    PROPOSICION_P.tipo = tipoid;
                else
                {
                    PROPOSICION_P.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, 
                            "[PROPOSICION']: Expresión de tipo de dato inválida");
                }
                }
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
      
            if ( analizarSemantica )
            {
                if ( ( EXPRESION.tipo.equals ( "int"   ) && EXPRESION1.tipo.equals ( "float" ) ) ||
                     ( EXPRESION.tipo.equals ( "float" ) && EXPRESION1.tipo.equals ( "int"   ) ) ||
                     ( EXPRESION.tipo.equals ( EXPRESION1.tipo ) ) )
                    CONDICION.tipo = "boolean";
                else
                {
                    CONDICION.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, 
                        "[CONDICION]: Tipos incompatibles en la comparación");
                }
            }
          
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
            
            TIPO_DATO.tipo = "int";
         
        }
        else if ( preAnalisis.equals ( "float" ) )
        {
            emparejar ( "float" );
            
            TIPO_DATO.tipo = "float";
        }
        else if ( preAnalisis.equals ( "string" ) )
        {
            emparejar ( "string" );
            
            TIPO_DATO.tipo = "string";
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
                
             if ( analizarSemantica ) 
            {
                if ( cmp.ts.buscaTipo ( id.entrada ).equals ( NIL ) )
                {
                    cmp.ts.anadeTipo( id.entrada, TIPO_DATO.tipo );
                    ARGUMENTOS.tipo = VACIO;
                }
                else
                {
                    ARGUMENTOS.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, 
                        "[ARGUMENTOS]: Identificador redeclarado o tipo de dato incompatible");
                }
            }

            ARGUMENTOS_P(ARGUMENTOS_P);
            
            if ( analizarSemantica )
            {
                if ( ARGUMENTOS.tipo.equals ( VACIO ) && ARGUMENTOS_P.tipo.equals ( VACIO ) )
                {
                    ARGUMENTOS.tipo = VACIO;
                    ARGUMENTOS.her = TIPO_DATO.tipo + " X " + ARGUMENTOS_P.her;
                }
                else
                {
                    ARGUMENTOS.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, 
                        "[ARGUMENTOS]: Argumento no valido");
                }
            }
        } 
        else 
        {
            //ε->vacio
            ARGUMENTOS.tipo = VACIO;
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
            
            if ( analizarSemantica )
            {
                if ( cmp.ts.buscaTipo ( id.entrada ).equals ( NIL ) )
                {
                    cmp.ts.anadeTipo ( id.entrada, TIPO_DATO.tipo );
                    ARGUMENTOS_P.tipo = VACIO;
                }
                else
                {
                    ARGUMENTOS_P.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, 
                    "[ARGUMENTOS']: Identificador redeclarado o tipo de dato incompatible");
                }
            }
                
                  
            ARGUMENTOS_P( ARGUMENTOS_P1);

            if (analizarSemantica) {
                if (ARGUMENTOS_P.tipo.equals(VACIO) && ARGUMENTOS_P1.tipo.equals(VACIO)) {
                    ARGUMENTOS_P.her = TIPO_DATO.tipo + " X " + ARGUMENTOS_P1.her;
                    ARGUMENTOS_P.tipo = VACIO;
                } else {
                    ARGUMENTOS_P.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO,
                            "[ARGUMENTOS']: Argumento no valido");
                }
            }
                       
        } else {
            //ε->vacio
            //INICIO ACCION SEMANTICA {33}
            ARGUMENTOS_P.tipo = VACIO;
            //FIN ACCION SEMANTICA  {33}
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
          
            if ( analizarSemantica )
            {
                if ( EXPRESION.tipo != ERROR_TIPO && LISTA_EXPRESIONES_P.tipo != ERROR_TIPO )
                    LISTA_EXPRESIONES.tipo = EXPRESION.tipo + "X" + LISTA_EXPRESIONES_P.tipo;
                else
                {
                    LISTA_EXPRESIONES.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, 
                        "[LISTA_EXPRESIONES]: Declaración de expresión inválida");
                }
            }
        } else {
            //ε->vacio
            //INCIO ACCION SEMANTICA {35}
            LISTA_EXPRESIONES.tipo = VACIO;
            //FIN ACCION SEMANTICA {35}
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
            
              // ACCIÓN SEMÁNTICA  {36}
            if ( analizarSemantica )
            {
                if ( EXPRESION.tipo != ERROR_TIPO && LISTA_EXPRESIONES_P1.tipo != ERROR_TIPO )
                     LISTA_EXPRESIONES_P.tipo = EXPRESION.tipo + "X" + LISTA_EXPRESIONES_P1.tipo;
                else
                {
                    LISTA_EXPRESIONES_P.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, 
                    "[LISTA DE EXPRESIONES] Declaración de expresiones inválida");
                }
            }
                // FIN ACCIÓN SEMÁNTICA {36}
            
        } else {
            //ε->vacio
            //INICIO ACCION SEMANTICA {37}
            LISTA_EXPRESIONES_P.tipo = VACIO;
            //FIN ACCION SEMANTICA {37}
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

            if ( analizarSemantica )
            {
                if ( EXPRESION_P.tipo.equals ( VACIO ) )
                    EXPRESION.tipo = TERMINO.tipo;
                else
                    EXPRESION.tipo = EXPRESION_P.tipo;
            }

        } else if (preAnalisis.equals("literal")) {
            literal = cmp.be.preAnalisis;
            emparejar("literal");
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

        if (preAnalisis.equals("opsuma")) {
            emparejar("opsuma");
            TERMINO(TERMINO);
            if ( analizarSemantica )
            {
                if ( EXPRESION_P.her.equals ( "int"   ) && TERMINO.tipo.equals ( "float" ) ||
                     EXPRESION_P.her.equals ( "float" ) && TERMINO.tipo.equals ( "int"   ) )
                    EXPRESION_P1.her = "float";
                else if (EXPRESION_P.her.equals ( TERMINO.tipo ) )
                    EXPRESION_P1.her = TERMINO.tipo;
                else
                {
                    EXPRESION_P1.her = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, 
                        "[EXPRESION']: Operación de suma de tipos incompatible");
                }
            }
            EXPRESION_P(EXPRESION_P1);
            if ( EXPRESION_P1.tipo.equals ( VACIO ) )
                EXPRESION_P.tipo = EXPRESION_P1.her;
            else
                EXPRESION_P.tipo = EXPRESION_P1.tipo;
        } else {
            //ε->vacio
            EXPRESION_P.tipo = VACIO;
        }
    }
    
    
    //Autor: Arturo Rosales Valdés - No. Control: 20130766
    //TERMINO -> FACTOR TERMINO_P
    private void TERMINO(Atributos TERMINO) {
        Atributos FACTOR = new Atributos ();
        Atributos TERMINO_P = new Atributos ();

        if ( preAnalisis.equals ( "id" )      || preAnalisis.equals ( "num" ) || 
             preAnalisis.equals ( "num.num" ) || preAnalisis.equals ( "(" ) ) 
        {
            FACTOR(FACTOR);

            TERMINO_P.her = FACTOR.tipo;
            TERMINO_P(TERMINO_P);

            if ( TERMINO_P.tipo.equals ( VACIO ) )
                TERMINO.tipo = FACTOR.tipo;
            else
                TERMINO.tipo = TERMINO_P.tipo;

        } else {
            error ( "[TERMINO]: se esperaba una expresion" );
        }
    }
    
    
    //Autor: Arturo Rosales Valdés - No. Control: 20130766
    //TERMINO_P -> opmult FACTOR TERMINO_P | ε
    private void TERMINO_P(Atributos TERMINO_P) {
        Atributos FACTOR = new Atributos ();
        Atributos TERMINO_P1 = new Atributos ();
        if (preAnalisis.equals("opmult")) {
            emparejar("opmult");
            FACTOR(FACTOR);

            if ( analizarSemantica )
            {
                if ( ( TERMINO_P.her.equals ( "int"   ) && FACTOR.tipo.equals ( "float") ) ||
                     ( TERMINO_P.her.equals ( "float" ) && FACTOR.tipo.equals ( "int"  ) ) )
                    TERMINO_P1.her = "float";
                else if ( TERMINO_P.her.equals ( FACTOR.tipo ) )
                        TERMINO_P1.her = FACTOR.tipo;
                else
                {
                    TERMINO_P1.her = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, 
                        "[TERMINO']: Tipos de datos incompatibles en la operación de multiplicación");
                }         
            }

            TERMINO_P(TERMINO_P1);

            if ( analizarSemantica )
            {
                if ( TERMINO_P1.tipo.equals ( VACIO ) )
                    TERMINO_P.tipo = TERMINO_P1.her;
                else
                    TERMINO_P.tipo = TERMINO_P1.tipo;
            }

        } else {
            //ε->vacio
            TERMINO_P.tipo = VACIO;
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
            FACTOR_P ( FACTOR_P );
            
            if ( analizarSemantica )
            {
                String tipoid = cmp.ts.buscaTipo ( id.entrada );
                if ( tipoid.contains ( "->" ) )
                {
                    int indice = tipoid.indexOf ( "->" );
                    String aux = tipoid.substring ( indice );
                    String aux2 = tipoid.substring ( 0, indice );
                    if ( esCompatible ( aux, FACTOR_P.tipo ) )
                        FACTOR.tipo = aux2;
                    else
                    {
                        FACTOR.tipo = ERROR_TIPO;
                        cmp.me.error(Compilador.ERR_SEMANTICO, 
                        "[FACTOR]: Expresión inválida");
                    }
                }
                else if ( tipoid.equals ( "int"    ) && FACTOR_P.tipo.equals ( VACIO ) || 
                              tipoid.equals ( "float"  ) && FACTOR_P.tipo.equals ( VACIO ) ||
                              tipoid.equals ( "string" ) && FACTOR_P.tipo.equals ( VACIO ))
                        FACTOR.tipo = tipoid;
                else
                    {
                        FACTOR.tipo = ERROR_TIPO;
                        cmp.me.error(Compilador.ERR_SEMANTICO, 
                        "[FACTOR]: Expresión de tipo de dato inválida");
                    }
            }
        } 
         else if ( preAnalisis.equals ( "num" ) ) 
         {
            num = cmp.be.preAnalisis;
            emparejar ( "num" );
            
            if ( analizarSemantica )
            {
                cmp.ts.anadeTipo ( num.entrada, "int" );
                FACTOR.tipo = cmp.ts.buscaTipo ( num.entrada );
            }
        } 
        else if ( preAnalisis.equals ( "num.num" ) ) 
        {
            num_num = cmp.be.preAnalisis;
            emparejar ( "num.num" );
            if ( analizarSemantica )
            {
                cmp.ts.anadeTipo ( num_num.entrada, "float" );
                FACTOR.tipo = cmp.ts.buscaTipo ( num_num.entrada );
            }
        } 
        else if ( preAnalisis.equals ( "(" ) ) 
        {
            emparejar ( "(" );
            EXPRESION ( EXPRESION );
            emparejar ( ")" );
            
            if ( analizarSemantica )
            {
                if ( EXPRESION.tipo != ERROR_TIPO && EXPRESION.tipo != "string" )
                    FACTOR.tipo = EXPRESION.tipo;
                else
                {
                    FACTOR.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, 
                        "[FACTOR] Expresión de operación en paréntesis inválida");
                }
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

        if ( preAnalisis.equals ( "(" ) )
            {
                emparejar ( "(" );
                LISTA_EXPRESIONES ( LISTA_EXPRESIONES );
                emparejar (")" );
                
                if( analizarSemantica )
                {
                    String tipoid = cmp.ts.buscaTipo ( Integer.parseInt ( FACTOR_P.her ) );
                    if ( tipoid.contains ( "->" ) )
                    {
                        int indice = tipoid.indexOf ( "->" );
                        String aux = tipoid.substring ( indice );
                        if ( esCompatible ( aux, LISTA_EXPRESIONES.tipo ))
                           FACTOR_P.tipo = VACIO;
                        else
                        {
                           FACTOR_P.tipo = ERROR_TIPO;
                           cmp.me.error(Compilador.ERR_SEMANTICO,
                                   "[FACTOR']: Expresión inválida");
                        }
                    }
                    else if ( tipoid.equals ( "int"    ) && LISTA_EXPRESIONES.tipo.equals ( VACIO ) || 
                              tipoid.equals ( "float"  ) && LISTA_EXPRESIONES.tipo.equals ( VACIO ) || 
                              tipoid.equals ( "string" ) && LISTA_EXPRESIONES.tipo.equals ( VACIO ) )
                        FACTOR_P.tipo = tipoid;
                    else
                    {
                        FACTOR_P.tipo = ERROR_TIPO;
                        cmp.me.error(Compilador.ERR_SEMANTICO, 
                                "[FACTOR']: Expresión de tipo de dato inválida ");
                    }
                }
            }
            else
            {
                FACTOR_P.tipo = VACIO;
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
    
    
}