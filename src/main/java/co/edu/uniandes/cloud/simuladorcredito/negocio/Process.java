/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.uniandes.cloud.simuladorcredito.negocio;

import co.edu.uniandes.cloud.simuladorcredito.jpa.Cuota;
import co.edu.uniandes.cloud.simuladorcredito.jpa.PlanPago;
import co.edu.uniandes.cloud.simuladorcredito.persistencia.CuotaDAO;
import co.edu.uniandes.cloud.simuladorcredito.persistencia.PlanPagoDAO;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/*import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.ErrorHandler;*/

/** 
 *
 * @author Fredy
 */

public class Process {

    private PlanPagoDAO dao=new PlanPagoDAO();
    private CuotaDAO dao2=new CuotaDAO();
    
    /*final ApplicationContext rabbitConfig = new AnnotationConfigApplicationContext(RabbitConfiguration.class);
    final ConnectionFactory rabbitConnectionFactory = rabbitConfig.getBean(ConnectionFactory.class);
    final Queue rabbitQueue = rabbitConfig.getBean(Queue.class);
    final MessageConverter messageConverter = new SimpleMessageConverter();
    final SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer();*/
    private String uri = System.getenv("CLOUDAMQP_URL");
    AmortizacionFrances aa = new AmortizacionFrances();

    public void procesar(){
        
        ConnectionFactory factory = new ConnectionFactory();
        try {
            factory.setUri(uri);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume("hello", true, consumer);
            
            
            while (true) {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                String mensaje = new String(delivery.getBody());
                System.out.println(" [x] Received '" + mensaje + "'");
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
                    for (Cuota c:pp.getCuotas()){
                        c.setIdPlan(pp.getId());
                    }
                    dao2.insertar(pp.getCuotas());
                }
                System.out.println("Finalizo "+mensaje+"-"+Calendar.getInstance());
                
            }
            
        } catch (URISyntaxException ex) {
            Logger.getLogger(Process.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Process.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(Process.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IOException ex) {
            Logger.getLogger(Process.class.getName()).log(Level.SEVERE, null, ex);
        }catch (InterruptedException ex) {
            Logger.getLogger(Process.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        

        
        //listenerContainer.setConnectionFactory(rabbitConnectionFactory);
        //listenerContainer.setQueueNames(rabbitQueue.getName());

        // set the callback for message handling
        /*listenerContainer.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                final String mensaje = (String) messageConverter.fromMessage(message);
                
                // simply printing out the operation, but expensive computation could happen here
                System.out.println("Received from RabbitMQ: " + mensaje);
                
            }
        });

        // set a simple error handler
        /*listenerContainer.setErrorHandler(new ErrorHandler() {
            public void handleError(Throwable t) {
                t.printStackTrace();
            }
        });

        // register a shutdown hook with the JVM
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Shutting down BigOperationWorker");
                listenerContainer.shutdown();
            }
        });

        // start up the listener. this will block until JVM is killed.
        listenerContainer.start();
        System.out.println("BigOperationWorker started");
                */
        
    }

    public void procesarLocal(long plan){
        System.out.println("Paso2a");
                String mensaje = "1";
                System.out.println(" [x] Received '" + mensaje + "'");
                System.out.println("Procesando..."+mensaje+"-"+Calendar.getInstance());
                PlanPago pp=dao.leer(plan);
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
                    for (Cuota c:pp.getCuotas()){
                        c.setIdPlan(pp.getId());
                    }
                    dao2.insertar(pp.getCuotas());
                }
                System.out.println("Finalizo "+mensaje+"-"+Calendar.getInstance());
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
        //
        System.out.println("Paso1");
        new Process().procesar();
        
        //new Process().procesarLocal(82);
    }
}
