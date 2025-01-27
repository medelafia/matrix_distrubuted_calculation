package ressources;

import utils.SlaveServerInfo;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Config {
    private String rmiServerHostName ;
    private int rmiServerPort ;
    private Map<String , SlaveServerInfo> slaveServerInfoMap = new HashMap<>() ;

    public void loadConfig(String filePath) {
        if(filePath.endsWith(".properties")) {
            try(FileReader fileReader= new FileReader(filePath)) {
                Properties properties = new Properties() ;
                properties.load(fileReader);
                this.rmiServerPort = Integer.parseInt(properties.getProperty("RmiServer.port"))  ;
                this.rmiServerHostName = properties.getProperty("RmiServer.host") ;

                String[] slaveServersHostNames = new String[4];
                int[] slaveServersPorts = new int[4];
                String[] slaveServerStatus = new String[ 4];

                Enumeration<Object> keys = properties.keys() ;
                while(keys.hasMoreElements()) {
                    String element = (String)keys.nextElement();
                    if(element.contains("SlaveServer")) {
                        int i = Integer.parseInt(element.substring("SlaveServer".length(), "SlaveServer".length() + 1)) ;
                        if(element.contains("host")) {
                            slaveServersHostNames[i] = properties.getProperty(element);
                        }else if(element.contains("port")){
                            slaveServersPorts[i] = Integer.parseInt(properties.getProperty(element));
                        }
                    }
                }
                for(int i = 0 ; i < 4 ; i ++ ) {
                    slaveServerInfoMap.put("server"+ i ,
                            new SlaveServerInfo(slaveServersHostNames[i] , slaveServersPorts[i] ) ) ;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            System.out.println("the file should be .properties");
        }
    }

    public int getRmiServerPort() {
        return rmiServerPort;
    }


    public String getRmiServerHostName() {
        return rmiServerHostName;
    }

    public Map<String, SlaveServerInfo> getSlaveServerInfoMap() {
        return slaveServerInfoMap;
    }

}
