/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socket;

/**
 *
 * @author willyam
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
//import javafx.application.Application;
//import javafx.stage.Stage;

//class Client extends Application {
class Client {

    String textAddress = "192.168.1.2";
    Integer textPort = 4242;
    String strRetorno = "";
    
    public String SendMsg(String msg) throws InterruptedException
    {
        strRetorno = "";
        Client.RunnableClient runnableClient = new Client.RunnableClient(textAddress, textPort, msg);
        Thread threadClient = new Thread(runnableClient);
        threadClient.start();
        
        //Aguarda Retorno da Menssagem
        synchronized(threadClient){
            threadClient.wait();
        }
//        while(strRetorno.equals("")) 
//        {strRetorno.wait();}
               
        return strRetorno;
    }   
    /*
    @Override
    public void start(Stage primaryStage) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    */
    
    class RunnableClient implements Runnable {

        String dstAddress;
        int dstPort;
        String msgToServer;

        public RunnableClient(String addr, int port, String msgTo) {
            dstAddress = addr;
            dstPort = port;
            msgToServer = msgTo;
            
            System.out.println("DSTAddress: " + dstAddress);
            System.out.println("DSTPort: " + dstPort);
            System.out.println("Menssagem: " + msgToServer);
        }
        
        @Override
        public void run() {
            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;

            try {
                socket = new Socket(dstAddress, dstPort);
                dataOutputStream = new DataOutputStream(
                        socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                if (msgToServer != null) {
                    dataOutputStream.writeUTF(msgToServer);
                }

                strRetorno = dataInputStream.readUTF();
                System.out.println("Retorno: " + strRetorno);

            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } finally {

                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        }

    }

}
