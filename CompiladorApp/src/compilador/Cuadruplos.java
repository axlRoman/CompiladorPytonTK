package compilador;

import java.util.ArrayList;


public class Cuadruplos {
    ArrayList<Cuadruplo> cuadruplos;
    Compilador cmp;
    
    
    public Cuadruplos ( Compilador c ) {
        cmp = c;
        cuadruplos = new ArrayList<> ();
    }
          
    public void inicializar () {
        vaciar ();
    }
    
    public void agregar ( Cuadruplo cuadruplo ) {
        cuadruplos.add ( cuadruplo );
    }
    
    public void vaciar () {
        cuadruplos.clear ();
    }
    
    public int getTamano () {
        return cuadruplos.size();
    }
    
    public ArrayList<Cuadruplo> getCuadruplos () {
        return cuadruplos;
    }
    
}
