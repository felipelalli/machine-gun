/*
 * Copyright (c) 2010.
 * CC-by Felipe Micaroni Lalli
 */

package br.eti.fml.machinegun;

import br.eti.fml.behavior.BuildingException;
import br.eti.fml.behavior.Factory;
import br.eti.fml.machinegun.auditorship.ArmyAudit;

/**
 * <p>
 * A {@link Target} groups all the information needed to a bullet (data)
 * reaches your right destination (be processed in the right way).
 * </p>
 <small><small><pre>
                   0############Ø+                                          
               #######¥®®®®®®¥#######0                                      
            ####¥®®®®®®¥#####¥®®®®®®####ø                                   
          ###¥®®®®################¥®®®®###®                                 
        ###®®®®####o            .####¥®®®###=                               
       ##¥®®®###°                   ###®®®®###                              
      ##®®®®##o                       ##¥®®®¥##                             
     ##®®®®##           oØ®$,          ##¥®®®¥##                            
    ##®®®®##         ##########®        ##®®®®¥#                            
    #¥®®®®#-       ###®®®®®®®®###        #¥®®®®##                           
   +#®®®®®#       +#¥®®®®®®®®®®®##       ##®®®®##                           
   ø#®®®®®#       ¥#®®®®®®®®®®®®##       Ø#®®®®®#                           
   -#®®®®®#        ##®®®®®®®®®®¥##       ##®®®®##                           
    #¥®®®®#Ø        ###¥®®®®®###¥        #¥®®®®#Ø                           
    ##®®®®¥#         =########Ø         ##®®®®##                            
     ##®®®®##,                         ##®®®®##°                            
      ##®®®®###                      o##®®®®##$                             
       ###®®®¥###                  Ø###®®®¥##=                              
        $##¥®®®¥####$          +#####®®®®###                                
          ####®®®®¥###############®®®®¥###                                  
            °####¥®®®®®®®®®®®®®®®®¥####®                                    
               +####################Ø   ,Ø¥ø                                
                    =$¥#######Ø°.       0ø$+                                
                                    ...                                     
                ..                ..                                        
                                                                                  
</pre></small></small>                                                                                   
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 1:51:29 PM
 */
public class Target<T> {
    private String queueName;
    private Factory<DirtyWork<T>> dirtyWorkFactory;

    /**
     *
     * @param queueName The name of the persisted internal queue used to this
     *                  kind of data.
     * 
     * @param dirtyWorkFactory a factory to get {@link DirtyWork}. 
     */
    public Target(String queueName, Factory<DirtyWork<T>> dirtyWorkFactory) {
        this.queueName = queueName;
        this.dirtyWorkFactory = dirtyWorkFactory;
    }

    /**
     * 
     * @return The name of the persisted internal queue used to this
     *                  kind of data.
     */
    public String getQueueName() {
        return queueName;
    }

    /**
     * Creates a {@link DirtyWork} and delegate the action to it.
     * 
     * @param idJob a unique number to identify this job
     * @param consumerName A consumer name, like "Consumer 2 of 8"
     * @param audit To inspect the internal execution
     * @param data The data to be processed
     * @throws br.eti.fml.behavior.BuildingException if something went wrong on
     *          dirty work factory production.
     */
    public void workOnIt(long idJob, String consumerName, ArmyAudit audit, T data)
            throws BuildingException {

        dirtyWorkFactory.buildANewInstance().workOnIt(idJob, consumerName, audit, data);
    }
}

