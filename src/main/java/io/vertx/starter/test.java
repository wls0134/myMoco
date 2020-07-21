package io.vertx.starter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.net.util.SubnetUtils.SubnetInfo;

public class test {

    private static Connection db;

    public static void main(String[] args) {
        
        Connection conn = null ;
        PreparedStatement stat = null;
        ResultSet rs = null;
        
        String clientIp = "10.10.2.220";

        ArrayList list = new ArrayList<>();
        
        String clientId = null;
        String clientSecret = null;

        try {
        conn = connectToDB();
        String sql = "select clnt_id, clnt_scrt, svr_info from clients";
        stat = conn.prepareStatement(sql);
        System.out.println(stat);

        rs = stat.executeQuery();
        
        {
            SvrDTO info = null;
            if(rs.next()){
                String clntId = rs.getString("clnt_id");
                String clntSecret = rs.getString("clnt_scrt");
                String svrInfo = rs.getString("svr_info");
                info  = new SvrDTO(clntId,clntSecret, svrInfo,"" );
                list.add(info);
            }
        }
        

        System.out.println("list.toString"+list.toString());
        System.out.println("list.size()"+ list.size());
        if(list.size() > 0 ){
            for(int i = 0 ; i < list.size(); i++){
                SvrDTO info = (SvrDTO) list.get(i);
                String svrInfos = info.getSvrInfo();    

                if(svrInfos.contains(",")){
                    System.out.println("multi ::::::::::::::");
                    String[] svrInfo = svrInfos.split(",");
                    for(int j = 0 ; j < svrInfo.length ; j++){
                        svrInfo[j] = svrInfo[j].trim();
                        
                        if(svrInfo[j].contains("/")){
                        System.out.println(svrInfo[j]+" is subnet Mask");

                        if( subnetMaskCheck(1, clientIp, svrInfo[j]) ){
                            //리턴값 셋팅하고 
                            System.out.println("CHECK 1" + info.toString());
                            break;
                        };
                        }else{
                            System.out.println(svrInfo[j]+" is NOT subnet Mask");

                            if( subnetMaskCheck(0, clientIp, svrInfo[j]) ){
                                //리턴값 셋팅하고 
                                System.out.println("CHECK 2" +info.toString());
                                break;
                            };
                        }
                    }
                }else{
                    System.out.println("single ::::::::::::::");
                    if(svrInfos.contains("/")){
                        System.out.println(svrInfos+"is subnet Mask");
                        if( subnetMaskCheck(1, clientIp, svrInfos ) ){
                            //리턴값 셋팅하고 
                            System.out.println("CHECK 3" + info.toString());
                            break;
                        };                        
                    }else{
                        System.out.println(svrInfos+"is NOT subnet Mask");
                        
                        if( subnetMaskCheck(0, clientIp, svrInfos ) ){
                            //리턴값 셋팅하고 
                            System.out.println("CHECK 4" +info.toString());
                            break;
                        };
                    }
    
                }
            }
            
        }
        

        } catch (Exception e) {
        e.printStackTrace();
        } finally{
            if(rs != null){
            try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        if(stat != null){ 
            try { stat.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        if(conn != null){
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        }
        
    }
    
    private static boolean subnetMaskCheck(int type, String clientIp , String ipInfo) {

        boolean rslt = false;
        if(type == 0){ //서브넷 없음
            if(clientIp.equals(ipInfo)){
                rslt = true;
            }
        }else{  //서브넷 있음
            String ip = ipInfo.split("/")[0];
            String subnet = ipInfo.split("/")[1];
            System.out.println("subnet" +subnet);
            if("31".equals(subnet) || "32".equals(subnet)){
                if(clientIp.equals(ip)){
                    rslt = true;
                } 
            }else{
                SubnetUtils sb = new SubnetUtils(ipInfo);
                SubnetInfo info = sb.getInfo(); 
                rslt = info.isInRange(clientIp);
            }
        }   
        return rslt;
    }


    private static Connection connectToDB() {
        try {
          // Class.forName("org.mariadb.jdbc.Driver");
          Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        }
    
        String jdbcUrl = "jdbc:mariadb://10.10.2.250:3306/mydata";
        String userId = "mydata";
        String userPass = "mocomsys1$";
    
        try {
          Connection connection = DriverManager.getConnection(jdbcUrl, userId, userPass);
          System.out.println("DB connection success");
          return connection;
        } catch (SQLException e) {
          System.out.println("DB connection failed");
          e.printStackTrace();
        }
        return null;
      }
 
}