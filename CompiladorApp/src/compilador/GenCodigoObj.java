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
        cmp.iuListener.mostrarCodObj ( "" );
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
                cmp.iuListener.mostrarCodObj ( "  " + variable + " DWORD 0" );
        }
        cmp.iuListener.mostrarCodObj ( "" );
    }
    
    //--------------------------------------------------------------------------
    
    private void genSegmentoCodigo () {
        cmp.iuListener.mostrarCodObj ( ".code" );
        cmp.iuListener.mostrarCodObj ( "main PROC" );
        cmp.iuListener.mostrarCodObj ( "  ; (aqui se insertan las instrucciones ejecutables)" );
    }
    
    //--------------------------------------------------------------------------
    // Genera las lineas en Ensamblador de finalizacion del programa
    
    private void genPieASM () {
       // cmp.iuListener.mostrarCodObj ( "  exit" );
        cmp.iuListener.mostrarCodObj ( "main ENDP" );
        cmp.iuListener.mostrarCodObj ( "" );
        cmp.iuListener.mostrarCodObj ( "; (aqui se insertan los procedimientos adicionales)" );
        cmp.iuListener.mostrarCodObj ( "END main" );
    }
    
    //--------------------------------------------------------------------------
    // Algoritmo de generacion de codigo en ensamblador
    
    private void algoritmoGCO() {
        ArrayList<String> codigoIntermedio = new ArrayList<>();
    // Agrega las instrucciones del código intermedio
    codigoIntermedio.add("x:=5");
    codigoIntermedio.add("y:=8");
    codigoIntermedio.add("t1:=x+1");
    codigoIntermedio.add("t2:=2*t1");
    codigoIntermedio.add("t3:=t2+y");
    codigoIntermedio.add("x:=t3");
    codigoIntermedio.add("t4:=12*x");
    codigoIntermedio.add("t5:=t4*x");
    codigoIntermedio.add("t6:=10*y");
    codigoIntermedio.add("t7:=t5+t6");
    codigoIntermedio.add("t8:=t7+99");
    codigoIntermedio.add("y:=t8");
    codigoIntermedio.add("t9:=y+3");
    codigoIntermedio.add("t10:=t9+12");
    codigoIntermedio.add("t11:=2*t10");
    codigoIntermedio.add("t12:=t11*1");
    codigoIntermedio.add("t13:=t12+x");
    codigoIntermedio.add("z:=t13");

    for (String instruccion : codigoIntermedio) {
        String[] partes = instruccion.split(":=");
        String leftSide = partes[0].trim();
        String rightSide = partes[1].trim();

        if (rightSide.matches("\\d+")) {
            cmp.iuListener.mostrarCodObj("  mov ax, " + rightSide);
        } else {
            String[] operandos = rightSide.split("[\\+\\-\\*/]");
            String operador = rightSide.replaceAll("[^\\+\\-\\*/]", "");

            if (operandos.length == 1) {
                cmp.iuListener.mostrarCodObj("  mov ax, " + obtenerVariable(operandos[0].trim()));
            } else {
                cmp.iuListener.mostrarCodObj("  mov ax, " + obtenerVariable(operandos[0].trim()));
                cmp.iuListener.mostrarCodObj("  mov bx, " + obtenerVariable(operandos[1].trim()));

                switch (operador) {
                    case "+":
                        cmp.iuListener.mostrarCodObj("  add ax, bx");
                        break;
                    case "-":
                        cmp.iuListener.mostrarCodObj("  sub ax, bx");
                        break;
                    case "*":
                        cmp.iuListener.mostrarCodObj("  imul bx");
                        break;
                    case "/":
                        cmp.iuListener.mostrarCodObj("  cdq");
                        cmp.iuListener.mostrarCodObj("  idiv bx");
                        break;
                    default:
                        break;
                }
            }
        }

        cmp.iuListener.mostrarCodObj("  mov " + leftSide + ", ax");
        }
    }

// Método para obtener el nombre de la variable
    private String obtenerVariable(String variable) {
        // Lógica para obtener la dirección o el valor de la variable en memoria
        // Esto puede variar según la implementación de la tabla de símbolos o cómo se manejen las variables en tu compilador
        // Aquí se simula una asignación directa de valores a las variables

        switch (variable) {
            case "x":
                return "x";
            case "y":
                return "y";
            case "t1":
                return "t1";
            case "t2":
                return "t2";
            // Agrega más casos según tus necesidades
            default:
                return "";
        }

    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    
}