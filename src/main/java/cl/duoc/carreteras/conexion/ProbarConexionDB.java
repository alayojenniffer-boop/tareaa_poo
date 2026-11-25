package cl.duoc.carreteras.conexion;

import java.sql.Connection;

public class ProbarConexionDB{
    public static void main(String[]args){
        try(Connection con = ConexionBD.getConnection()){
            System.out.println("Connexion ok" +con.getMetaData().getURL());

        }catch (Exception e){
            System.out.println("Error al conectar"+e.getMessage());
        }
    }

}