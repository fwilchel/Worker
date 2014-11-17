/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.uniandes.cloud.simuladorcredito.persistencia;


import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ParallelScanOptions;
import java.net.UnknownHostException;

import java.util.List;
import java.util.Set;

import static java.util.concurrent.TimeUnit.SECONDS;

// To directly connect to a single MongoDB server (note that this will not auto-discover the primary even
// if it's a member of a replica set:

/**
 *
 * @author Fredy
 */
public class TestMongoDB {
    public static void main(String args[]){

        try{

            MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
            DB db = mongoClient.getDB( "mydb" );
            Set<String> colls = db.getCollectionNames();
            System.out.println("Inicio");
            for (String s : colls) {
                System.out.println(s);
            }
            
            DBCollection coll = db.getCollection("testCollection");
            
            BasicDBObject doc = new BasicDBObject("name", "MongoDB")
                .append("type", "database")
                .append("count", 1)
                .append("info", new BasicDBObject("x", 203).append("y", 102));
            coll.insert(doc);

            DBObject myDoc = coll.findOne();
            System.out.println(myDoc);

            for (int i=0; i < 100; i++) {
                coll.insert(new BasicDBObject("i", i));
            }
            System.out.println(coll.getCount());

            DBCursor cursor = coll.find();
            try {
               while(cursor.hasNext()) {
                   System.out.println(cursor.next());
               }
            } finally {
               cursor.close();
            }
            
            BasicDBObject query = new BasicDBObject("i", 71);

            cursor = coll.find(query);

            try {
               while(cursor.hasNext()) {
                   System.out.println(cursor.next());
               }
            } finally {
               cursor.close();
            }

            
            System.out.println("Fin");
        }catch(UnknownHostException e){
            System.out.println(e);
        }
        
    }
}
