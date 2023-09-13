/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:                  SEMESTRE: ___________    HORA: ___________ HRS
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
 *:=============================================================================
 *: 22/Feb/2015 FGil                -Se mejoro errorEmparejar () para mostrar el
 *:                                 numero de linea en el codigo fuente donde 
 *:                                 ocurrio el error.
 *: 08/Sep/2015 FGil                -Se dejo lista para iniciar un nuevo analizador
 *:                                 sintactico.
 *: 20/FEB/2023 F.Gil, Oswi         -Se implementaron los procedures del parser
 *:                                  predictivo recursivo de leng BasicTec.
 *:-----------------------------------------------------------------------------
 */
package compilador;

import javax.swing.JOptionPane;

public class SintacticoSemantico {

    private Compilador cmp;
    private boolean    analizarSemantica = false;
    private String     preAnalisis;
    
    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //

    public SintacticoSemantico(Compilador c) {
        cmp = c;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // Metodo que inicia la ejecucion del analisis sintactico predictivo.
    // analizarSemantica : true = realiza el analisis semantico a la par del sintactico
    //                     false= realiza solo el analisis sintactico sin comprobacion semantica

    public void analizar(boolean analizarSemantica) {
        this.analizarSemantica = analizarSemantica;
        preAnalisis = cmp.be.preAnalisis.complex;

        // * * *   INVOCAR AQUI EL PROCEDURE DEL SIMBOLO INICIAL   * * *
        PROGRAMA();
        
    }

    //--------------------------------------------------------------------------

    private void emparejar(String t) {
        if (cmp.be.preAnalisis.complex.equals(t)) {
            cmp.be.siguiente();
            preAnalisis = cmp.be.preAnalisis.complex;            
        } else {
            errorEmparejar( t, cmp.be.preAnalisis.lexema, cmp.be.preAnalisis.numLinea );
        }
    }
    
    //--------------------------------------------------------------------------
    // Metodo para devolver un error al emparejar
    //--------------------------------------------------------------------------
 
    private void errorEmparejar(String _token, String _lexema, int numLinea ) {
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
        msjError += " se encontró " + ( _lexema.equals ( "$" )? "fin de archivo" : _lexema ) + 
                    ". Linea " + numLinea;        // FGil: Se agregó el numero de linea

        cmp.me.error(Compilador.ERR_SINTACTICO, msjError);
    }

    // Fin de ErrorEmparejar
    //--------------------------------------------------------------------------
    // Metodo para mostrar un error sintactico

    private void error(String _descripError) {
        cmp.me.error(cmp.ERR_SINTACTICO, _descripError);
    }

    // Fin de error
    //--------------------------------------------------------------------------
    //  *  *   *   *    PEGAR AQUI EL CODIGO DE LOS PROCEDURES  *  *  *  *
    //--------------------------------------------------------------------------

 //Autor: Julian Rodolfo Villa Cruz
    //PROGRAMA
    private void PROGRAMA (){
    if(preAnalisis.equals("def") //pro->funcion-> def
            ||preAnalisis.equals("int")||preAnalisis.equals("float")//pro->proposicion-> esto...
            ||preAnalisis.equals("id")||preAnalisis.equals("if")||preAnalisis.equals("while")||preAnalisis.equals("print")||preAnalisis.equals("string")){
        INSTRUCCION();
        PROGRAMA();
    }else{ 
        //Programa->vacio
    }
}
    private void INSTRUCCION (){
    if(preAnalisis.equals("def")){
        FUNCION();
    }else if(preAnalisis.equals("int")||preAnalisis.equals("float")||preAnalisis.equals("string")
            ||preAnalisis.equals("id")||preAnalisis.equals("if")||preAnalisis.equals("while")||preAnalisis.equals("print")){
       PROPOSICION();
    }else{
        error("Error en instruccion");
    }
   }
    
    private void FUNCION (){
    if(preAnalisis.equals("def")){
        emparejar("def");
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
    }else{
    }
   }
    private void DECLARACION_VARS (){
    if(preAnalisis.equals("int")||preAnalisis.equals("float")||preAnalisis.equals("string")){
        TIPO_DATO();
        emparejar("id");
        DECLARACION_VARS_P();
    }else{
        error("Error en 'declaracion VARS' ");
    }
   }
    
    private void DECLARACION_VARS_P (){
    if(preAnalisis.equals(",")){
        
        emparejar(",");
        emparejar("id");
        DECLARACION_VARS_P();
    }else {
    //a vacio
    }
   }
   
    //------------------------------------------------------------------------------
    //------------------------------------------------------------------------------
    
    //Autor: Francisco Axel Roman Cardoza 19130971
    //Primeros (TIPO_RETORNO) = {void, int, float, string}
    private void TIPO_RETORNO(){
        if(preAnalisis.equals("void") || preAnalisis.equals("int") || preAnalisis.equals("float") || preAnalisis.equals("string"))
            //TIPO_RETORNO -> void | TIPO_DATO
            emparejar(preAnalisis);
        else
            error("Error sintáctico: se esperaba 'void', 'int', 'float' o 'string'" + cmp.be.preAnalisis.getNumLinea());
    }
    
    //------------------------------------------------------------------------------
    
    //Primeros (RESULTADO) = {void, literal, id, num, num.num, (, opsuma, empty, opmult, (, empty}
    public void RESULTADO(){
        if (preAnalisis.equals("id") || preAnalisis.equals("num") || preAnalisis.equals("num.num") || preAnalisis.equals("literal")) {
            // RESULTADO -> EXPRESION
            EXPRESION();
        } else if (preAnalisis.equals("void")) {
            // RESULTADO -> void
            emparejar("void");
        } else {
            // Manejar error sintáctico o lanzar una excepción si el preAnalisis no es válido.
            error("Error sintáctico: se esperaba 'id', 'num', 'num.num', 'literal' o 'void'"+cmp.be.preAnalisis.getNumLinea());
        }
    }

    
    //------------------------------------------------------------------------------
    
    //Primeros (PROPOSICIONES_OPTATIVAS) = {id, if, while, print, int, float, string, empty}
    public void PROPOSICIONES_OPTATIVAS(){
        if (preAnalisis.equals("def") || preAnalisis.equals("int") || preAnalisis.equals("float") || preAnalisis.equals("string")
                || preAnalisis.equals("void") || preAnalisis.equals("id") || preAnalisis.equals("if") || preAnalisis.equals("while")
                || preAnalisis.equals("print")) {
            // PROPOSICIONES_OPTATIVAS -> PROPOSICION PROPOSICIONES_OPTATIVAS
            PROPOSICION();
            PROPOSICIONES_OPTATIVAS();
        } else {
            // PROPOSICIONES_OPTATIVAS -> ε
            // No se realiza ninguna acción en este caso, ya que ε representa la producción nula.
        }
    }

    //------------------------------------------------------------------------------
    
    //Primeros (PROPOSICION) = {id, if, while, print, int, float, string}
    public void PROPOSICION() {
        
        if (preAnalisis.equals("id")) {
            // PROPOSICION -> id PROPOSICION'
            emparejar("id");
            PROPOSICION_P();
        } else if (preAnalisis.equals("if")) {
            // PROPOSICION -> if CONDICION : PROPOSICIONES_OPTATIVAS else : PROPOSICIONES_OPTATIVAS ::
            emparejar("if");
            CONDICION();
            emparejar(":");
            PROPOSICIONES_OPTATIVAS();
            emparejar("else");
            emparejar(":");
            PROPOSICIONES_OPTATIVAS();
            emparejar(":");
            emparejar(":");
        } else if (preAnalisis.equals("while")) {
            // PROPOSICION -> while CONDICION : PROPOSICIONES_OPTATIVAS ::
            emparejar("while");
            CONDICION();
            emparejar(":");
            PROPOSICIONES_OPTATIVAS();
            emparejar(":");
            emparejar(":");
        } else if (preAnalisis.equals("print")) {
            // PROPOSICION -> print ( EXPRESION )
            emparejar("print");
            emparejar("(");
            EXPRESION();
            emparejar(")");
        } else if (preAnalisis.equals("int") || preAnalisis.equals("float") || preAnalisis.equals("string")) {
            // PROPOSICION -> DECLARACION_VARS
            DECLARACION_VARS();
        } else {
            // Manejar error sintáctico o lanzar una excepción si el preAnalisis no es válido.
            error("Error sintáctico: se esperaba 'id', 'if', 'while', 'print' o declaración de variables."+cmp.be.preAnalisis.getNumLinea());
        }
    }
    
    //------------------------------------------------------------------------------
    
    //Primeros (PROPOSICION'_p) = {opasig, (}
    public void PROPOSICION_P() {
        if (preAnalisis.equals("opasig")) {
            // PROPOSICION' -> opasig EXPRESION
            emparejar("opasig");
            EXPRESION();
        } else if (preAnalisis.equals("(")) {
            // PROPOSICION' -> ( LISTA_EXPRESIONES )
            emparejar("(");
            LISTA_EXPRESIONES();
            emparejar(")");
        } else {
            // Manejar error sintáctico o lanzar una excepción si el preAnalisis no es válido.
            error("Error sintáctico: se esperaba 'opasig' o '('"+cmp.be.preAnalisis.getNumLinea());
        }
    }

    //------------------------------------------------------------------------------
    
    //Primeros (CONDICION) = {oprel, literal}
    public void CONDICION(){
        EXPRESION();
        emparejar("oprel");
        EXPRESION();
    }

    //------------------------------------------------------------------------------
    //------------------------------------------------------------------------------
    //------------------------------------------------------------------------------
    //Autor: Braulio Esteban Gonzalez Alanis (20131498)
    //PRIMEROS ( TIPO_DATO ) = {int, float, string }
    
    private void TIPO_DATO (){
        
        if (preAnalisis.equals("int"))
        {
            // TIPO_DATO -> int
            emparejar("int");
        } 
        else if (preAnalisis.equals("float"))
        {
            //TIPO_DATO -> float
            emparejar("float");
        } 
        else if (preAnalisis.equals("string"))
        {
            //TIPO_DATO -> string
            emparejar("string");            
        } else 
        {
         error( "TIPO_DATO: Tipo de dato incorrecto, se espera (int, float, string) NO. Linea " + cmp.be.preAnalisis.getNumLinea());
        }
         
    }
    
    //--------------------------------------------------------------------------
    //Autor: Braulio Esteban Gonzalez Alanis (20131498)
    //PRIMEROS ( ARGUMENTOS ) = {PRIMEROS(TIPO_DATO), ϵ} 
    private void ARGUMENTOS(){
        if(preAnalisis.equals("int") || preAnalisis.equals("float") || preAnalisis.equals("string")){
            //ARGUMENTOS -> TIPO_DATO id ARGUMENTOS_P
            TIPO_DATO();
            emparejar("id");
            ARGUMENTOS_P();
            
        } else
        {
            // ARGUMENTOS -> empty
        }
        // error( "ARGUMENTOS: Argumento no valido / se espera un id, argumentos validos (int, float, string) NO. Linea " + cmp.be.preAnalisis.getNumLinea());
        
    }
    
    
    //--------------------------------------------------------------------------
    //Autor: Braulio Esteban Gonzalez Alanis (20131498)
    //PRIMEROS ( ARGUMENTOS_P ) = {, , ϵ }
    
    private void ARGUMENTOS_P() {
    
    if(preAnalisis.equals(",")){
        emparejar(",");
        TIPO_DATO();
        emparejar("id");
        ARGUMENTOS_P();
    } else {
        //ARGUMENTOS_P -> ϵ
            //error( "ARGUMENTOS: Argumento no valido / se espera un id, argumentos validos (int, float, string) / se espera una( , )   NO. Linea " + cmp.be.preAnalisis.getNumLinea());

    }
    
        
    
    
    }
    
       //--------------------------------------------------------------------------
    //Autor: Braulio Esteban Gonzalez Alanis (20131498)
    //PRIMEROS ( LISTA_EXPRESIONES) = {EXPRESION ,  ϵ }
   // PRIMEROS(EXPRESION) = {TERMINO, literal}
   // PRIMEROS (TERMINO)  = {FACTOR}
   // PRIMEROS (FACTOR)   = {id, num, num.num, ( }
    
    private void LISTA_EXPRESIONES(){
  
        
            if(preAnalisis.equals("literal") || 
                    preAnalisis.equals("id") || 
                    preAnalisis.equals("num")|| 
                    preAnalisis.equals("num.num") || 
                    preAnalisis.equals("(")){
                
            // LISTA_EXPRESIONES -> id
            EXPRESION();
            LISTA_EXPRESIONES_P();
            
            } else {
            
            }
                
     
       //  error( "EXPRESION: Expresion no valida / Expresiones validas ({id, num, num.num, literal) / se espera ( , )   NO. Linea " + cmp.be.preAnalisis.getNumLinea());
        
    
    }
    
    
     //--------------------------------------------------------------------------
    //Autor: Braulio Esteban Gonzalez Alanis (20131498)
    //PRIMEROS ( LISTA_EXPRESIONES_P) = { ,  ,  ϵ }
    
    private void LISTA_EXPRESIONES_P(){
        if(preAnalisis.equals(",")){
            emparejar(",");
            EXPRESION();
            LISTA_EXPRESIONES_P();
        }else {
            //LISTA_EXPRESIONES_P -> ϵ
        }
        
     //   error( "EXPRESION: Expresion no valida / Expresiones validas ({id, num, num.num, literal) / se espera ( , )  NO. Linea " + cmp.be.preAnalisis.getNumLinea());
        
        
    }
    
    
    //------------------------------------------------------------------------------
    private void EXPRESION(){
        if(preAnalisis.equals("id")
                || preAnalisis.equals("num")
                || preAnalisis.equals("num.num")
                || preAnalisis.equals("(")){
            TERMINO();
            EXPRESION_P();
        }else if(preAnalisis.equals("literal")){
            emparejar("literal");
        }else if (preAnalisis.equals("num.num")){
            error(""+cmp.be.preAnalisis.getNumLinea());
        }
         
    }
    private void EXPRESION_P(){
        //AUTOR: ADRIAN TORRES DE ALBA
        //EXPREISON' -> opsuma TERMINO EXPRESION' |e

        if(preAnalisis.equals("opsuma")){

            emparejar("opsuma");
             //TERMINO -> {id,num,num.num,(,literal}
            TERMINO();
            EXPRESION_P();

        }else{
            //e->vacio
        }
    }
    private void TERMINO(){
        //AUTOR: ADRIAN TORRES DE ALBA
        //TERMINO-> FACTOR TERMINO_P
        //FACTOR->{id,num,num.num,(,literal}
        if(preAnalisis.equals("id")){
            FACTOR();
            TERMINO_P();

        }else if(preAnalisis.equals("num")){
            FACTOR();
            TERMINO_P();
        }else if(preAnalisis.equals("num.num")){
            FACTOR();
            TERMINO_P();
        }else if(preAnalisis.equals("(")){
            FACTOR();
            TERMINO_P();
        }else if(preAnalisis.equals("literal")){
            FACTOR();
            TERMINO_P();
        }
        else{
            error(""+cmp.be.preAnalisis.getNumLinea());
        }
    }
    private void TERMINO_P(){
        //AUTOR: ADRIAN TORRES DE ALBA
        //TERMINO'-> opmult FACTOR TERMINO' | e

        if(preAnalisis.equals("opmult")){
            //termino_p->opmult factor termino_p
            emparejar("opmult");
            FACTOR();
            TERMINO_P();

        }else{
            //e->vacio
        }
    }
    private void FACTOR(){
        //AUTOR: ADRIAN TORRES DE ALBA
        //FACTOR-> id FACTOR' | num | num.num | (EXPRESION)

        if(preAnalisis.equals("id")){

            emparejar("id");
            FACTOR_P();


        }else if(preAnalisis.equals("num")){
            emparejar("num");
        }else if(preAnalisis.equals("num.num")){
            emparejar("num.num");
        }else if(preAnalisis.equals("(")){
            emparejar("(");
            EXPRESION();
            emparejar(")");
        }
        else{
            error(""+cmp.be.preAnalisis.getNumLinea());
        }
    }
    private void FACTOR_P(){
        //AUTOR: ADRIAN TORRES DE ALBA
        //FACTOR'-> (LISTA_EXPRESIONES) | e
        if(preAnalisis.equals("(")){

            emparejar("(");
            LISTA_EXPRESIONES();
            emparejar(")");


        }else{
        //e->vacio
        }
    }
    
    
}
//------------------------------------------------------------------------------
//::