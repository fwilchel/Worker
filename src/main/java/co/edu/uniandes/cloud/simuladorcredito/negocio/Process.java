/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.uniandes.cloud.simuladorcredito.negocio;

import co.edu.uniandes.cloud.simuladorcredito.jpa.Cuota;
import co.edu.uniandes.cloud.simuladorcredito.jpa.PlanPago;
import co.edu.uniandes.cloud.simuladorcredito.persistencia.PlanPagoDAO;
import co.edu.uniandes.csw.simuladorcredito.utils.ColaWorkerUtil;
import java.util.Calendar;
import java.util.List;

/** 
 *
 * @author Fredy
 */
//@Stateless
public class Process {

    private PlanPagoDAO dao=new PlanPagoDAO();
    

    public void procesar(){
        AmortizacionFrances aa = new AmortizacionFrances();
        String mensaje=ColaWorkerUtil.leerMensaje();
        //dejar ciclo infinito
        while(mensaje!=null){
            System.out.println("Procesando..."+mensaje+"-"+Calendar.getInstance());
            PlanPago pp=dao.leer(new Long(mensaje));
            if(pp.getLinea()!=null){
                //generar cuota
                List<Cuota> cuotas=aa.generarCuotas(
                        pp.getValor(), 
                        pp.getLinea().getTasa(), 
                        pp.getPlazo());
                pp.setCuotas(cuotas);
                //calcular nivel de riesgo
                pp.setNivelRiesgo(calcularNivelRiesgo());
                pp.setEstado("Generado");
                //guardar cuota
                dao.actualizar(pp);
            }
            //eliminar mensaje
            ColaWorkerUtil.borrarUltimoMensaje();
            System.out.println("Finalizo "+mensaje+"-"+Calendar.getInstance());
            //leer de la cola
            mensaje=ColaWorkerUtil.leerMensaje();
        }
    }
    
    public double calcularNivelRiesgo(){
        Calendar inicio=Calendar.getInstance();
        double diferencia=0;
        do{
            Calendar ahora=Calendar.getInstance();
            diferencia = (int)((ahora.getTimeInMillis() - inicio.getTimeInMillis())/1000);
            //System.out.println(diferencia);
        }while(diferencia<25);
        
        return 1+(int)(Math.random()*10);
    }
    
    public static void main(String args[]){
        new Process().procesar();
    }
}
