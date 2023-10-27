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

public class SintacticoSemantico {

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
        cmp.me.error(cmp.ERR_SINTACTICO, _descripError);
    }
    // Fin de error
    
    
    //--------------------------------------------------------------------------
    //  *  *   *   *            CODIGO DE LOS PROCEDURES          *  *  *  *
    //--------------------------------------------------------------------------


    //Autor: Julian Rodolfo Villa Cruz - No. Control: 20130764
    //PROGRAMA -> INSTRUCCION PROGRAMA |  ε
    private void PROGRAMA(Atributos PROGRAMA) {
        Atributos INSTRUCCION = new Atributos();
        if (preAnalisis.equals("def") //pro->funcion-> def
                || preAnalisis.equals("int") || preAnalisis.equals("float")//pro->proposicion-> esto...
                || preAnalisis.equals("id") || preAnalisis.equals("if") || preAnalisis.equals("while") || preAnalisis.equals("print") || preAnalisis.equals("string")) {
            INSTRUCCION();
            PROGRAMA(PROGRAMA);
        } else {
            //ε->vacio
            if(!preAnalisis.equals("$")){
                error("[PROGRAMA]: Se esperaba fin del archivo."+
                        " Se encontro: "+cmp.be.preAnalisis.lexema+
                        "\nNo Linea: "+cmp.be.preAnalisis.getNumLinea());
            }
        }
    }

     //Autor: Julian Rodolfo Villa Cruz - No. Control: 20130764
    //INSTRUCCION -> FUNCION | PROPSICION
    private void INSTRUCCION(Atributos INSTRUCCION) {
        if (preAnalisis.equals("def")) {
            FUNCION();
        } else if (preAnalisis.equals("int") || preAnalisis.equals("float") || preAnalisis.equals("string")
                || preAnalisis.equals("id") || preAnalisis.equals("if") || preAnalisis.equals("while") || preAnalisis.equals("print")) {
            PROPOSICION();
        } else {
            error("[instruccion]Error en instruccion");
        }
    }
    
    
    //Autor: Julian Rodolfo Villa Cruz - No. Control: 20130764
    //FUNCION -> def id ( ARGUMENTOS ) : TIPO_RETORNO PROPOSICIONES_OPTATIVAS return RESULTADO ::
    private void FUNCION(Atributos FUNCION) {
        if (preAnalisis.equals("def")) {
            emparejar("def");
            emparejar("id");
            emparejar("(");
            ARGUMENTOS();
            emparejar(")");
            emparejar(":");
            TIPO_RETORNO(TIPO_RETORNO);
            PROPOSICIONES_OPTATIVAS();
            emparejar("return");
            RESULTADO();
            emparejar(":");
            emparejar(":");
        } else {
            //ε->vacio
        }
    }
    
    
    //Autor: Julian Rodolfo Villa Cruz - No. Control: 20130764
    //DECLARACION_VARS -> TIPO_DATO id DECLARACION_VARS_P
    private void DECLARACION_VARS(Atributos DECLARACION_VARS ) {
        if (preAnalisis.equals("int") || preAnalisis.equals("float") || preAnalisis.equals("string")) {
            TIPO_DATO();
            emparejar("id");
            DECLARACION_VARS_P();
        } else {
            error("[declaracion_vars]: Se esperaba un tipo de dato 'int', 'float', 'string'");
        }
    }
    
    
    //Autor: Julian Rodolfo Villa Cruz - No. Control: 20130764
    //DECLARACION_VARS_P -> , id DECLARACION_P | ε
    private void DECLARACION_VARS_P(Atributos DECLARACION_VARS_P) {
        if (preAnalisis.equals(",")) {
            emparejar(",");
            emparejar("id");
            DECLARACION_VARS_P();
        } else {
            //ε->vacio
        }
    }
   
    
    //Autor: Francisco Axel Roman Cardoza - No. Control: 19130971
    //TIPO_RETORNO -> void | TIPO_DATO
    private void TIPO_RETORNO(Atributos TIPO_RETORNO) {
        Atributos TIPO_DATO(Atributos TIPO_RETORNO)
        if (preAnalisis.equals("void")) //Primeros (TIPO_RETORNO) = {void, int, float, string}
        {
            emparejar(preAnalisis);

            TIPO_RETORNO.tipo = VOID;
        } else if (preAnalisis.equals("int") || preAnalisis.equals("float") || preAnalisis.equals("string")) {
            TIPO_DATO(TIPO_DATO)

            TIPO_RETORNO.tipo = TIPO_DATO.tipo;
        } else {
            error("[tipo_retorno]Error sintáctico: se esperaba 'void', 'int', 'float' o 'string'" + cmp.be.preAnalisis.getNumLinea());
        }
    }
    

    //Autor: Francisco Axel Roman Cardoza - No. Control: 19130971
    // RESULTADO -> EXPRESION | void
    public void RESULTADO(Atributos RESULTADO) {
        //Primeros (RESULTADO) = {void, literal, id, num, num.num, (, opsuma, empty, opmult, (, empty}
        Atributos EXPRESION =  new Atributos();
        if (preAnalisis.equals("id") || preAnalisis.equals("num") || preAnalisis.equals("num.num") || preAnalisis.equals("literal")) {
            EXPRESION(EXPRESION);

            RESULTADO.tipo = EXPRESION.tipo;
        } else if (preAnalisis.equals("void")) {
            emparejar("void");

            RESULTADO.tipo = VOID;
        } else {
            // Manejar error sintáctico o lanzar una excepción si el preAnalisis no es válido.
            error("[resultado]Error sintáctico: se esperaba 'id', 'num', 'num.num', 'literal' o 'void'" + cmp.be.preAnalisis.getNumLinea());
        }
    }


    //Autor: Francisco Axel Roman Cardoza - No. Control: 19130971
    // PROPOSICIONES_OPTATIVAS -> PROPOSICION PROPOSICIONES_OPTATIVAS | ε
    public void PROPOSICIONES_OPTATIVAS(Atributos PROPOSICIONES_OPTATIVAS) {
        //Primeros (PROPOSICIONES_OPTATIVAS) = {id, if, while, print, int, float, string, empty}
        Atributos PROPOSICION = new Atributos ();
        Atributos PROPOSICIONES_OPTATIVAS1 = new Atributos ();
        if (preAnalisis.equals("def") || preAnalisis.equals("int") || preAnalisis.equals("float") || preAnalisis.equals("string")
                || preAnalisis.equals("void") || preAnalisis.equals("id") || preAnalisis.equals("if") || preAnalisis.equals("while")
                || preAnalisis.equals("print")) {
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
        if (preAnalisis.equals("id")) {
            emparejar("id");
            PROPOSICION_P(PROPOSICION_P);

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
        } else if (preAnalisis.equals("if")) {
            emparejar("if");
            CONDICION(CONDICION);
            emparejar(":");
            PROPOSICIONES_OPTATIVAS(PROPOSICIONES_OPTATIVAS1);
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
            emparejar("else");
            emparejar(":");
            PROPOSICIONES_OPTATIVAS(PROPOSICIONES_OPTATIVAS2);
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
            emparejar(":");
            emparejar(":");
        } else if (preAnalisis.equals("while")) {
            emparejar("while");
            CONDICION(CONDICION1);
            emparejar(":");
            PROPOSICIONES_OPTATIVAS(PROPOSICIONES_OPTATIVAS3);

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

            emparejar(":");
            emparejar(":");
        } else if (preAnalisis.equals("print")) {
            emparejar("print");
            emparejar("(");
            EXPRESION(EXPRESION);
            emparejar(")");
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
        } else if (preAnalisis.equals("int") || preAnalisis.equals("float") || preAnalisis.equals("string")) {
            DECLARACION_VARS(DECLARACION_VARS);

            PROPOSICION.tipo = DECLARACION_VARS.tipo;
        } else {
            // Manejar error sintáctico o lanzar una excepción si el preAnalisis no es válido.
            error("[proposicion]Error sintáctico: se esperaba 'id', 'if', 'while', 'print' o declaración de variables." + cmp.be.preAnalisis.getNumLinea());
        }
    }
    
    
    //Autor: Francisco Axel Roman Cardoza - No. Control: 19130971
    //PROPOSICION_P -> opasig EXPRESION | ( LISTA_EXPRESIONES )
    public void PROPOSICION_P(Atributos PROPOSICION_P) {
        Atributos EXPRESION = new Atributos ();
        Atributos LISTA_EXPRESIONES = new Atributos ();
        Atributos FACTOR_P = new Atributos ();
        if (preAnalisis.equals("opasig")) {
            emparejar("opasig");
            EXPRESION(EXPRESION);
            PROPOSICION_P.tipo = EXPRESION.tipo;
        } else if (preAnalisis.equals("(")) {
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
            error("[proposicion_p]Error sintáctico: se esperaba '=' o '('" + cmp.be.preAnalisis.getNumLinea());
        }
    }

    
    //Autor: Francisco Axel Roman Cardoza - No. Control: 19130971
    //CONDICION -> EXPERSION oprel EXPRESION
    public void CONDICION(Atributos CONDICION) {
        Atributos EXPRESION = new Atributos ();
        Atributos EXPRESION1 = new Atributos ();
        EXPRESION(EXPRESION);
        emparejar("oprel");
        EXPRESION(EXPRESION1);
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


    //Autor: Braulio Esteban Gonzalez Alanis - No. Control: 20131498
    //TIPO_DATO -> int | float | string
    private void TIPO_DATO(Atributos TIPO_DATO) {
        if (preAnalisis.equals("int")) {
            emparejar("int");
        } else if (preAnalisis.equals("float")) {
            emparejar("float");
        } else if (preAnalisis.equals("string")) {
            emparejar("string");
        } else {
            error("[TIPO_DATO]: Tipo de dato incorrecto, se espera (int, float, string) NO. Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }
    
   
    //Autor: Braulio Esteban Gonzalez Alanis - No. Control: 20131498
    //ARGUMENTOS -> TIPO_DATO id ARGUMENTOS_P | ε
    private void ARGUMENTOS(Atributos ARGUMENTOS) {
        if (preAnalisis.equals("int") || preAnalisis.equals("float") || preAnalisis.equals("string")) {
            TIPO_DATO();
            emparejar("id");
            ARGUMENTOS_P();
        } else {
            //ε->vacio
        }
    }
    
   
    //Autor: Braulio Esteban Gonzalez Alanis - No. Control: 20131498
    //ARGUEMNTOS_P -> , TIPO_DATO id ARGUEMNTOS_P | ε
    private void ARGUMENTOS_P(Atributos ARGUMENTOS_P) {
        if (preAnalisis.equals(",")) {
            emparejar(",");
            TIPO_DATO();
            emparejar("id");
            ARGUMENTOS_P();
        } else {
            //ε->vacio
        }
    }
    

    //Autor: Braulio Esteban Gonzalez Alanis - No. Control: 20131498
    //LISTA_EXPRESIONES -> EXPRESION LISTA_EXPRESIONES_P | ε
    private void LISTA_EXPRESIONES(Atributos LISTA_EXPRESIONES) {
        if (preAnalisis.equals("literal")
                || preAnalisis.equals("id")
                || preAnalisis.equals("num")
                || preAnalisis.equals("num.num")
                || preAnalisis.equals("(")) {
            EXPRESION();
            LISTA_EXPRESIONES_P();
        } else {
            //ε->vacio
        }
    }
    
    
    //Autor: Braulio Esteban Gonzalez Alanis - No. Control: 20131498
    //LISTA_EXPRESIONES_P -> , EXPRESION LISTA_EXPRESIONES | ε
    private void LISTA_EXPRESIONES_P(Atributos LISTA_EXPRESIONES_P) {
        if (preAnalisis.equals(",")) {
            emparejar(",");
            EXPRESION();
            LISTA_EXPRESIONES_P();
        } else {
            //ε->vacio
        }
    }
    
    
    //Autor: Arturo Rosales Valdés - No. Control: 20130766
    //EXPRESION -> TERMINO EXPRESION_P | literal
    private void EXPRESION(Atributos EXPRESION) {
        if (preAnalisis.equals("id")
                || preAnalisis.equals("num")
                || preAnalisis.equals("num.num")
                || preAnalisis.equals("(")) {
            TERMINO();
            EXPRESION_P();
        } else if (preAnalisis.equals("literal")) {
            emparejar("literal");
        } else {
            error("[expresion]: Se esperaba 'id', 'numero entero(num)', 'numero decimal(num.num)', '(', 'literal'" + cmp.be.preAnalisis.getNumLinea());
        }
    }
    
    
    //Autor: Arturo Rosales Valdés - No. Control: 20130766
    //EXPRESION_P -> opsuma TERMINO EXPRESION_P | ε
    private void EXPRESION_P(Atributos EXPRESION_P) {
        if (preAnalisis.equals("opsuma")) {
            emparejar("opsuma");
            TERMINO();
            EXPRESION_P();
        } else {
            //ε->vacio
        }
    }
    
    
    //Autor: Arturo Rosales Valdés - No. Control: 20130766
    //TERMINO -> FACTOR TERMINO_P
    private void TERMINO(Atributos TERMINO) {
        if (preAnalisis.equals("id")) {
            FACTOR();
            TERMINO_P();
        } else if (preAnalisis.equals("num")) {
            FACTOR();
            TERMINO_P();
        } else if (preAnalisis.equals("num.num")) {
            FACTOR();
            TERMINO_P();
        } else if (preAnalisis.equals("(")) {
            FACTOR();
            TERMINO_P();
        } else if (preAnalisis.equals("literal")) {
            FACTOR();
            TERMINO_P();
        } else {
            error("[termino]: Se esperaba 'id', 'numero entero(num)', 'numero decimal(num.num)', '(', 'literal'" + cmp.be.preAnalisis.getNumLinea());
        }
    }
    
    
    //Autor: Arturo Rosales Valdés - No. Control: 20130766
    //TERMINO_P -> opmult FACTOR TERMINO_P | ε
    private void TERMINO_P(Atributos TERMINO_P) {
        if (preAnalisis.equals("opmult")) {
            emparejar("opmult");
            FACTOR();
            TERMINO_P();
        } else {
            //ε->vacio
        }
    }
    
    
    //Autor: Arturo Rosales Valdés - No. Control: 20130766
    //FACTOR -> id FACTOR_P | num | num.num | ( EXPRESION )
    private void FACTOR(Atributos FACTOR) {
        if (preAnalisis.equals("id")) {
            emparejar("id");
            FACTOR_P();
        } else if (preAnalisis.equals("num")) {
            emparejar("num");
        } else if (preAnalisis.equals("num.num")) {
            emparejar("num.num");
        } else if (preAnalisis.equals("(")) {
            emparejar("(");
            EXPRESION();
            emparejar(")");
        } else {
            error("[factor]: Se esperaba 'id', 'numero entero(num)', 'numero decimal(num.num)', '(', 'literal'" + cmp.be.preAnalisis.getNumLinea());
        }
    }
    
    
    //Autor: Arturo Rosales Valdés - No. Control: 20130766
    //FACTOR_P -> ( LISTA_EXPRESIONES ) | ε
    private void FACTOR_P(Atributos FACTOR_P) {
        if (preAnalisis.equals("(")) {
            emparejar("(");
            LISTA_EXPRESIONES();
            emparejar(")");
        } else {
            //ε->vacio
        }
    }
    
    
}