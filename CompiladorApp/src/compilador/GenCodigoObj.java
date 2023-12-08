/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:        SEMESTRE: ______________            HORA: ______________ HRS
 *:                                   
 *:               
 *:    # Clase con la funcionalidad del Generador de COdigo Objeto
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
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *: 24/May/2023 F.Gil              -Generar la plantilla de programa Ensamblador
 *:-----------------------------------------------------------------------------
 */


package compilador;

import general.Linea_TS;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GenCodigoObj {
 
    private Compilador cmp;

    
    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //
    public GenCodigoObj ( Compilador c ) {
        cmp = c;
    }
    // Fin del Constructor
    //--------------------------------------------------------------------------
	
    public void generar () {
        genEncabezadoASM ();
        genDeclaraVarsASM();
        genSegmentoCodigo();
        algoritmoGCO     ();
        genPieASM        ();
    }    

    //--------------------------------------------------------------------------
    // Genera las primeras lineas del programa Ensamblador hasta antes de la 
    // declaracion de variables.
    
    private void genEncabezadoASM () {
        cmp.iuListener.mostrarCodObj ( "TITLE CodigoObjeto ( codigoObjeto.asm )"  );
        cmp.iuListener.mostrarCodObj ( "; Descripción del programa: Automatas II" );
        cmp.iuListener.mostrarCodObj ( "; Fecha de creacion: Ene-Jun/2023"        );
        cmp.iuListener.mostrarCodObj ( "; Revisiones:" );
        cmp.iuListener.mostrarCodObj ( "; Fecha de ult. modificacion:" );
        cmp.iuListener.mostrarCodObj ( "" );
        cmp.iuListener.mostrarCodObj ( "; INCLUDE Irvine32.inc" );
        cmp.iuListener.mostrarCodObj ( "; (aqui se insertan las definiciones de simbolos)" );
        cmp.iuListener.mostrarCodObj ( ".MODEL SMALL" );
        cmp.iuListener.mostrarCodObj ( ".STACK 4096h" );
        cmp.iuListener.mostrarCodObj ( ".data" );
        cmp.iuListener.mostrarCodObj ( "  ; (aqui se insertan las variables)" );       
        
    }
    
    //--------------------------------------------------------------------------
    // Genera las lineas en Ensamblador de Declaracion de variables.
    // Todas las variables serán DWORD ya que por simplificacion solo se genera
    // codigo objeto de programas fuente que usaran solo variables enteras.
    
    private void genDeclaraVarsASM () {
        for ( int i = 1; i < cmp.ts.getTamaño (); i++ ) {
            // Por cada entrada en la Tabla de Simbolos...
            Linea_TS elemento = cmp.ts.obt_elemento( i );
            String variable = elemento.getLexema();
            
            // Genera una declaracion de variable solo si se trata de un id
            if ( elemento.getComplex().equals ( "id" ) ) 
                cmp.iuListener.mostrarCodObj ( "  " + variable + " DW 0" );
        }
        cmp.iuListener.mostrarCodObj ( "" );
        
        
        ArrayList<Cuadruplo> cuadruplos = cmp.cua.getCuadruplos(); // Suponiendo que getCuadruplos() devuelve los cuádruplos

    for (Cuadruplo cuadruplo : cuadruplos) {
        String operacion = cuadruplo.op;
        String operando1 = cuadruplo.arg1;
        String operando2 = cuadruplo.arg2;
        String resultado = cuadruplo.resultado;
        if(operando1.charAt(0)=='t')
            cmp.iuListener.mostrarCodObj("  "+operando1+ " DW 0");
    }
        
        
    }
    
    //--------------------------------------------------------------------------
    
    private void genSegmentoCodigo () {
        cmp.iuListener.mostrarCodObj ( ".code" );
        cmp.iuListener.mostrarCodObj ( "main PROC" );
        cmp.iuListener.mostrarCodObj ( "  ; (aqui se insertan las instrucciones ejecutables)" );
        cmp.iuListener.mostrarCodObj ( "inicio:\n" +
"MOV ax, @Data \n" +
"MOV ds, ax" );
        //cmp.iuListener.mostrarCodObj ( "" );
    }
    
    //--------------------------------------------------------------------------
    // Genera las lineas en Ensamblador de finalizacion del programa
    
    private void genPieASM () {
       // cmp.iuListener.mostrarCodObj ( "  exit" );
        cmp.iuListener.mostrarCodObj ( "MOV ax,4c00h\n" +
"INT 21h" );
        cmp.iuListener.mostrarCodObj ( "main ENDP" );
        cmp.iuListener.mostrarCodObj ( "" );
        cmp.iuListener.mostrarCodObj ( "; (aqui se insertan los procedimientos adicionales)" );
        cmp.iuListener.mostrarCodObj ( "END main" );
    }
    
    //--------------------------------------------------------------------------
    // Algoritmo de generacion de codigo en ensamblador
    public void algoritmoGCO() {
    ArrayList<Cuadruplo> cuadruplos = cmp.cua.getCuadruplos(); // Suponiendo que getCuadruplos() devuelve los cuádruplos

    for (Cuadruplo cuadruplo : cuadruplos) {
        String operacion = cuadruplo.op;
        String operando1 = cuadruplo.arg1;
        String operando2 = cuadruplo.arg2;
        String resultado = cuadruplo.resultado;

        switch (operacion) {
            case "+":
                generarSuma(operando1, operando2, resultado);
                break;
            case "-":
                generarResta(operando1, operando2, resultado);
                break;
            case "*":
                generarMultiplicacion(operando1, operando2, resultado);
                break;
            case "/":
                generarDivision(operando1, operando2, resultado);
                break;
            case "=":
                generarIgual(operando1, resultado);
                break;
            // Agrega más casos según las operaciones que manejes en tus cuádruplos
            default:
                break;
        }
    }
}
    
private void generarSuma(String operando1, String operando2, String resultado) {
    cmp.iuListener.mostrarCodObj("  ; Suma");
    cmp.iuListener.mostrarCodObj("  mov ax, " + operando1);
    cmp.iuListener.mostrarCodObj("  add ax, " + (operando2));
    cmp.iuListener.mostrarCodObj("  mov " + resultado + ", ax");
}

private void generarResta(String operando1, String operando2, String resultado) {
    cmp.iuListener.mostrarCodObj("  ; Resta");
    cmp.iuListener.mostrarCodObj("  mov ax, " + (operando1));
    cmp.iuListener.mostrarCodObj("  sub ax, " + (operando2));
    cmp.iuListener.mostrarCodObj("  mov " + resultado + ", ax");
}

private void generarMultiplicacion(String operando1, String operando2, String resultado) {
    cmp.iuListener.mostrarCodObj("  ; Multiplicación");
    cmp.iuListener.mostrarCodObj("  mov ax, " + (operando1));
    cmp.iuListener.mostrarCodObj("  mul " + (operando2));
    cmp.iuListener.mostrarCodObj("  mov " + resultado + ", ax");
}

private void generarDivision(String operando1, String operando2, String resultado) {
    cmp.iuListener.mostrarCodObj("  ; División");
    cmp.iuListener.mostrarCodObj("  mov ax, " + (operando1));
    cmp.iuListener.mostrarCodObj("  cwd");
    cmp.iuListener.mostrarCodObj("  div " + (operando2));
    cmp.iuListener.mostrarCodObj("  mov " + resultado + ", ax");
}
private void generarIgual(String operando1, String resultado) {
    cmp.iuListener.mostrarCodObj("  ; igualacion");
    cmp.iuListener.mostrarCodObj("  mov ax, " + (operando1));
    cmp.iuListener.mostrarCodObj("  mov " + resultado + ", ax");
}



    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    
}