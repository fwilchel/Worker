/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.uniandes.cloud.simuladorcredito.persistencia;

import co.edu.uniandes.cloud.simuladorcredito.jpa.SuperPojo;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoURI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Fredy
 */
public class SuperDAO <T extends SuperPojo> {
    //private static MongoClient mongoClient ;
    protected static DB db ;
    static{
        try {
            //mongoClient = new MongoClient( "localhost" , 27017 );
            //db = mongoClient.getDB( "Simulador" );

            MongoURI mongoURI = new MongoURI(System.getenv("MONGOHQ_URL"));
            db = mongoURI.connectDB();
            db.authenticate(mongoURI.getUsername(), mongoURI.getPassword());
            
        } catch (UnknownHostException ex) {
            Logger.getLogger(SuperDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch(Exception ex){
            Logger.getLogger(SuperDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    protected DBCollection col;
    
    protected DBObject leerBD(String campo, Object valor){
        BasicDBObject query = new BasicDBObject(campo, valor);
        DBCursor cursor = col.find(query);
        try {
           if(cursor.hasNext()) {
               return cursor.next();
           }else{
               return null;
           }
        } finally {
           cursor.close();
        }        
    }
    
    protected List<DBObject> leerVariosBD(String campo, Object valor){
        List<DBObject> lista = new ArrayList();
        BasicDBObject query = new BasicDBObject(campo, valor);
        DBCursor cursor = col.find(query);
        try {
           while(cursor.hasNext()) {
               lista.add(cursor.next());
           }
        } finally {
           cursor.close();
        }   
        return lista;
    }
    
}
