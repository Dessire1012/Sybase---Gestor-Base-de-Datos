package sybasejconnect;

import java.io.*;
import java.sql.*;

public class SybaseJConnect
{
    public static void main( String args[] )
    {
        try
        {
            Connection con;
            con = DriverManager.getConnection("jdbc:sybase:Tds:localhost:2638", "Dessi", "dessi");
            
            System.out.println("Using Jconnect driver");
            
            ResultSet MyResultset; 
            Statement MyStatement;
            MyStatement = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            
            /*String action = "SELECT *\n" + "FROM Cliente;";
            MyResultset = MyStatement.executeQuery(action);
            
            while (MyResultset.next()){
                System.out.println(MyResultset.getString(1) + " - " + MyResultset.getString(2)+
                                   " - " + MyResultset.getString(3)+ " - " + MyResultset.getString(4)+
                                    " - " + MyResultset.getString(5)+"\n");
            }*/
            
            ResultSet rs; 
            try {

                DatabaseMetaData dbmd = con.getMetaData();
                ResultSet result = dbmd.getCatalogs();
                while (result.next()) {
                    String aDBName = result.getString(1);
                    System.out.println(aDBName);
                } 
                String[] types = {"TABLE"};
                rs = dbmd.getTables(null, null, "%", types);
                while (rs.next()) {
                    System.out.println(rs.getString("TABLE_NAME"));
            }
            } 
                catch (SQLException e) {
                e.printStackTrace();
            }
            
            MyStatement.close();
            con.close();

        }
        catch (SQLException sqe)
        {
            System.out.println("Unexpected exception : " +
                                sqe.toString() + ", sqlstate = " +
                                sqe.getSQLState());
            System.exit(1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        System.exit(0);
    }
}