package org.bittle.beansmod;

public class Tester {
    
    
    public static void main(String[] args)
    {
        Connection connection = new Connection();
        connection.connect("wss://notextures.io:8086");
        
        connection.register("test_evan", "tacosaregreat");
        connection.login("test_evan", "tacosaregreat");
        connection.changePass("test_evan", "tacosaregreat", "tacosarebad");
        connection.logout();
        connection.login("test_evan", "tacosaregreat");
        
//        connection.clean();
//        connection.close();
    }
}