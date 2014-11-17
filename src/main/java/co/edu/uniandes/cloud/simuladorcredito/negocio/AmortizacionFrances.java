/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.uniandes.cloud.simuladorcredito.negocio;

import co.edu.uniandes.cloud.simuladorcredito.jpa.Cuota;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Fredy
 */
public class AmortizacionFrances {
    public List<Cuota> generarCuotas(Integer valor, Double tasa, Integer plazo){
        Double cuota= valor / ((1-Math.pow(1+tasa/100, -plazo))/(tasa/100));
        System.out.println(cuota);
        List<Cuota> cuotas =new ArrayList<Cuota>();
        Double valor2 = valor.doubleValue();
        for (int i=0; i<plazo; i++){
            Double interes = valor2 * tasa/100;
            Double amortizacion=cuota-interes;
            valor2-=amortizacion;
            
            Cuota c=new Cuota();
            c.setNumeroCuota(i+1);
            c.setIntereses(interes);
            c.setCapital(amortizacion);
            c.setTotal(cuota);
            c.setSaldo(Math.round(valor2*100)/100.0);
            
            cuotas.add(c);
            
        }
        
        return cuotas;
    }
    
    /*public static void main(String args[]){
        List<Cuota> cuotas=new AmortizacionFrances().generarCuotas(500000, 5.0, 10, 1);
        for (Cuota c:cuotas)
            System.out.println(c);
    }*/
    
}
