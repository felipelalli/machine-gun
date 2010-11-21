package br.fml.eti.machinegun;

/**
 * <p>
 * A {@link Capsule} is a way to keep the
 * "bullet" (data) intact through the way to
 * the {@link Target target}.
 * </p>
 * <p>
 * In a less abstract, an instance of this class is able to convert
 * the data <code>T</code> to an array of bytes and an array of bytes to
 * a <code>T</code> type again.
 * </p>
 * <small><small><small>
 * <pre>

                      ,ooo+=+=-                                                             
                    $¥°,       .Ø+                                                          
                  +#ØøøØ$Ø¥¥¥#¥Ø®##                                                         
                  #$ø$$$$®######®®¥#                                                        
                 #¥0oo$Ø$®#¥¥###Ø®$o#                                                       
                 #0=-+øØØ¥#¥¥¥##¥®ø-#                                                       
                o#°,,+ø$Ø¥#®®¥¥#®Ø$-$-                                                      
                #Ø+=+o$Ø®¥¥®®®¥#®Ø$+o0                                                      
                #0°++o$$®#¥¥®¥¥#®Ø$+-®                                                      
                #°+-=+0$Ø¥¥¥¥¥¥#¥Ø$+.#                                                      
                #°=.=oØ®#########Ø$+ #                                                      
                #°°+o°o- ==+oo+oø$$°,#                                                      
               +#$=,,°Ø®########¥$0-.®                                                      
               #     =00Ø¥Øø00øØ#$ø,  =                                                     
               #,    °$ø®¥Ø$$$$®#øø+  +                                                     
               ¥,    o$ø¥¥ØØ$$$®#0ø=  =                                                     
               ®,   .o$ø¥¥Ø$$$$Ø#0ø=  -                                                     
               ®,    +øø¥¥Ø$$$$Ø#øø=  -                                                     
               Ø,   ,o$ø¥®Ø$$$$Ø#øø=  -                                                     
               Ø,   ,o$ø¥®Ø$$$$Ø#0ø-  ,                                                     
               $,   ,o$ø¥®$$$$$Ø#øø=  ,                                                     
               $,   -oøø¥ØØ$$ø$Ø¥00=  .                                                     
               0.   =o$$¥Ø$øøø$$¥00-                                                        
               o,   -oøø¥Ø$$øøø$¥00=                                                        
               o.  .+oø$¥Ø$øø$ø$¥0o=                                                        
               o,  ,+oø$®$$øø00ø®o°-                                                        
               o, .-+oø$®$øøø00®#®®-         +¥########Ø=                                   
               o.  -+oøØ®$$ø00o°®  ø¥,    o###¥Ø$0oooø®####®oo                .=            
               ø,-=+o0$ØØ$øøø0°0$o®##0  o#¥$øo+=---=++-,=0Ø®#0$$                $$          
               $===+o0$®Ø$$0ø0+######$ $¥øo0+,=+°°°++°°°o-.oøØ¥ #                ØØ$        
               $oo0ø$$Ø®Ø$$ø$ø-#¥##ØØ$°$°o$=++o0o-+°==+ooo0 o0$® #                ##        
               o+°oo0ø$Ø$$$ø$ø.####®o-°°+®0o0oø, +oo##°oooø$ øo¥ #    -++o00øøø0+ ##        
               o°oo0ø$$Ø$$$$$ø.####$= =++#°ø$$Ø,oØ®+ o$oooø¥ ø°$ø#Ø$$øo+=,       -#0        
               -+°oo0ø$$$$$$$$ #####Øø +.#o°0ø$$+=,-o0$ø$$®# øo$ Ø             °##          
                .-+++°°ooooo0o ##¥#### $=0#++o0$ØØ®®ØØ$$$®# ,0°ø °    =ø®######$            
                   .,--=+++==  #######  Ø=°#o.-+ooo00ø$Ø¥$  $+ .-ø######################®o- 
                $®®Ø®®®®Ø®¥¥¥######®     øo°$®0-.=°ooo=   ø0. Ø###################¥$+       
     .-=+oøØ¥###Ø00$Ø¥##################0.-+,-+o+      +Øo. $#################$°.           
      ..,,.,,.,-°ø$$$Ø$$$$$$$0+-      °Ø###############¥¥################®ø=                
                                             ,°$Ø®®#################Øo.                   
                                                                                                  
</pre></small></small></small>
 *
 * @see br.fml.eti.machinegun.util.GenericCapsuleForLazyPeople
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 6:51:35 AM
 */
public interface Capsule<T> {
    T restoreFromBytes(byte[] data) throws WrongCapsuleException;
    byte[] convertToBytes(T data) throws WrongCapsuleException;
}
