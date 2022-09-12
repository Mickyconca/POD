package ar.edu.itba.pod.client;

public class Utils {

    public static ServerAddress serverAddressParser(final String serverAddress) throws NumberFormatException{
        final String[] addressTokens = serverAddress.split(":",2);
        return new ServerAddress(addressTokens[0], Integer.parseInt(addressTokens[1]));
    }

    public static class ServerAddress {
        private final String ip;
        private final Integer port;

        public ServerAddress(String ip, Integer port){
            this.ip = ip;
            this.port = port;
        }

        public Integer getPort(){
            return port;
        }

        public String getIp(){
            return ip;
        }

        public String getServerAddress(){
            return ip + ":" + port;
        }
    }
}
