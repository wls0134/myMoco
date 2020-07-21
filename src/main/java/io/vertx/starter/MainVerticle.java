package io.vertx.starter;

import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.net.util.SubnetUtils.SubnetInfo;

import java.io.UnsupportedEncodingException;

public class MainVerticle extends AbstractVerticle {

  public static String orgUrl = "";
  public static Logger log;

  public MainVerticle() {
    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
    String logFile = "/var/log/my-app.log";

    log = LoggerFactory.getLogger(getClass());
  }
  
  @Override
  public void start() throws Exception {
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    /** oauth check */
    router.get("/url").handler(this::vewIndex);
    router.get("/formSubmit").handler(this::formSubmit);
    router.post("/oauthCheck").handler(this::oauthCheck);

    /** service api */
    router.get("/client-ip/:clientIp").handler(this::whiteListCheck);
    router.get("/check-scope").handler(this::scopeCheck);
    router.post("/token").handler(this::insertToken);
    
    vertx.createHttpServer().requestHandler(router).listen(8080);
  }

  private static Connection connectToDB() {
    try {
      Class.forName("org.mariadb.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    String jdbcUrl = "jdbc:mariadb://10.10.2.250:3306/mydata";
    String userId = "mydata";
    String userPass = "mocomsys1$";

    try {
      Connection connection = DriverManager.getConnection(jdbcUrl, userId, userPass);
      log.info("DB connection success");
      return connection;
    } catch (SQLException e) {
      log.info("DB connection failed");
      e.printStackTrace();
    }
    return null;
  }

  private void vewIndex( RoutingContext routingContext) {
    log.info("##################[vewIndex]##################");
  
    HttpServerRequest request = routingContext.request();
    for ( String name : request.headers().names()) {
      log.info(name + ": " + request.headers().get(name));
    }

    log.info("BODY :" + routingContext.getBodyAsString("utf-8"));
    log.info("Query :" + request.query());

    if(request.query() == null){
        request.response().setStatusCode(200).end(); //test
    }else{
      orgUrl = request.query();
  
      try {
        orgUrl =  URLDecoder.decode(orgUrl, "UTF-8");
        orgUrl = orgUrl.replace("original-url=", "");
        orgUrl = orgUrl.replace("https://localhost:8513", "https://acoapig01.mocomsys.com");
        log.info(orgUrl);
      } catch ( UnsupportedEncodingException e) {
        e.printStackTrace();
      }

      String indexHtml = "<html lang=\"en\" xml:lang=\"en\"> <head><meta content=\"text/html; charset=UTF-8\" http-equiv=\"Content-Type\"><title>인증화면테스트</title><meta http-equiv=\"Cache-Control\" content=\"no-cache\"><meta http-equiv=\"Pragma\" content=\"no-cache\"><meta http-equiv=\"Expires\" content=\"-1\"><meta http-equiv=\"Content-type\" content=\"text/html;charset=UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><style media=\"screen\" type=\"text/css\">body {background-color: #cccccc; padding: 0; margin: 0;}.page { width: 100%; height: 100%; padding: 0; margin: 0;} h1 {font-family: Helvetica, Arial !important; font-weight: 200 !important; font-size: 22px;  color: #767676;  margin-bottom: 30px;}.white-box { background: #ffffff; box-shadow: 0 0 5px #333; position: relative; top: 50%;left: 50%;margin-left: -200px;-webkit-transform: translateY(-50%);transform: translateY(-50%);width: 400px;padding: 10px 40px;font-family: Arial, Tahoma, Verdana;font-size: 13px;font-weight: 200 !important;box-sizing: border-box;    }    .legacy .white-box {margin-top: -125px;    }    .inputContainer {margin-bottom: 30px;position: relative;    }   .inputContainer label {display: block;position: absolute;top: 8px;left: 0;color: #cccccc;font-style: italic;transition: top 0.3s, color 0.3s;    }    .inputContainer input {padding:4px0;border-width: 0 0 1px 0;border-style: solid;border-color: #dddddd;width: 100%;outline: none;    }    .inputContainer input:focus + label,    .inputContainer input:valid +label {top: -14px;color: #767676;font-style: normal;    }    .legacy .inputContainer label {top: -14px;color: #767676;font-style: normal;    }    .inputContainer input:focus + label {color: #0080e9;    }    .internalError {      color: red;    }    .inputWrapper p {color: #767676;    }    button {border: 1px solid #0080e9;cursor: pointer;font-family: Arial,Verdana, Helvetica, sans-serif;font-size: 14px;font-weight: bold;padding: 5px 11px;color: #0080e9;background-color: #ffffff;transition: background-color 0.2s;    }   button:disabled {border-color: #cccccc;cursor: not-allowed;color: #767676;    }    button:hover:not(:disabled),    button:focus {background-color: #0080e9;border-color: #0080e9;color:#ffffff;    }    @media all and (max-width: 400px) {.white-box {    width: 100%;    left: 0;    margin-left: 0;}    }    @media all and (min-width: 2000px) {.page {   zoom: 2;}    }    </style>    <script type=\"text/javascript\">        var startup = function() {var userInput = document.getElementById(\"username\"); legacy = false;if (userInput.style[\"transform\"] === undefined) {    document.body.className = \"login legacy\";    legacy = true;}if (!legacy) {    var passInput =document.getElementById(\"password\");    var loginButton = document.getElementById(\"login_button\");    loginButton.disabled = true;    var validate = function(evt) {if(userInput.value && passInput.value) {    loginButton.disabled = false;} else {    loginButton.disabled = true;}    };    if (window.addEventListener) {userInput.addEventListener(\"input\", validate);passInput.addEventListener(\"input\", validate);    } else {userInput.attachEvent(\"oninput\", validate);passInput.attachEvent(\"oninput\", validate);    }}setTimeout(function() {    userInput.focus();},0);    };    if (window.addEventListener) {window.addEventListener(\"load\",startup);    } else {window.attachEvent(\"onload\", startup);    }  "
        + "</script>  <body class=\"login\"><div class=\"page\"><div id=\"box\" class=\"white-box\">"
        + "<form name='oauthCheckForm' method='get' action ='http://10.10.2.220:8080/formSubmit'>"
        + "<h1>API Connect - AuthorizationServerLogin</h1><div class=\"inputWrapper\"><div class=\"inputContainer\">"
        + "<input type=\"text\" name=\"username\" id=\"username\" autofocus=\"autofocus\"required=\"required\" autocomplete=\"off\"> <label for=\"input_username\">User name</label></div><div class=\"inputContainer\">"
        + "<input type=\"password\"name=\"password\" id=\"password\" required=\"required\" autocomplete=\"off\"> <label for=\"input_password\">Password</label></div>"
        + "<button id=\"login_button\"type=\"submit\" name=\"login\" value=\"true\">Log in</button>"
        + "</div><div class=\"internalError\"></div></form></div></div></body></html> ";

      log.info("index");
      request.response().setStatusCode(200).end(indexHtml);
    }
    
  }

  private void oauthCheck( RoutingContext routingContext) {
    log.info("##################[oauthCheck]##################");
    HttpServerRequest request = routingContext.request();

    if (request.headers().get("X-URI-in") == null) {
      request.response().setStatusCode(200).end();
    }

    String uriIn = request.headers().get("X-URI-in");
    String[] s = uriIn.split("\\?");
    String[] ss = s[1].split("\\&");

    Map headerMap = new HashMap<>();
    for (int i = 0; i < ss.length; i++) {
      String[] sss = ss[i].split("=");
      headerMap.put(sss[0], sss[1]);
    }

    String confirmation = headerMap.get("confirmation").toString();

    if ("true".equals(confirmation)) {
      log.info("success");
      routingContext.response().putHeader("Content-Type", "application/json").setStatusCode(200).end("success");
    } else {
      log.info("fail");
      routingContext.response().putHeader("Content-Type", "application/json").setStatusCode(401).end("fail");
    }
  }

  private void formSubmit( RoutingContext routingContext) {
    log.info("##################[formSubmit]##################");
    HttpServerRequest request = routingContext.request();

    String query = request.query();
    Map queryMap = new HashMap<>();

    try {
      query = URLDecoder.decode(query, "UTF-8");
      String[] ss = query.split("\\&");
      for (int i = 0; i < ss.length; i++) {
         String[] sss = ss[i].split("=");
         queryMap.put(sss[0], sss[1]);
      }

    } catch ( UnsupportedEncodingException e) {
      log.info(e.getMessage());
    }

    String username = (String) queryMap.get("username");
    String confirmation = "false";
    if(username.equals("jihye") || username.equals("admin") || username.equals("apicadm")){
      confirmation = "true";
    }

    try {
          log.info("orgUrl : "+ orgUrl);
          request.response().setStatusCode(302).putHeader(HttpHeaders.LOCATION, orgUrl+"&username="+queryMap.get("username")+"&confirmation="+confirmation).end();
    } catch ( Exception e) {
      e.printStackTrace();
    }
  }


  private void whiteListCheck( RoutingContext routingContext) {
    log.info("##################[whiteListCheck]##################");
    HttpServerRequest request = routingContext.request();
    String clientIp = request.getParam("clientIp");
    log.info("path Param [clientIp] : " + clientIp);
    
    String clientId = null;
    String clientSecret = null;
    String clientIdtf = null;

    ArrayList list = new ArrayList<>();

    Connection conn = null ;
    PreparedStatement stat = null;
    ResultSet rs = null;

    try {
      conn = connectToDB();
      String sql = "select clnt_idtf, clnt_id, clnt_scrt, svr_info from clients";
      stat = conn.prepareStatement(sql);
      log.info(stat);

      rs = stat.executeQuery();
      SvrDTO rsltInfo = null;
      {
          SvrDTO info = null;
          if(rs.next()){
              String clntIdtf = rs.getString("clnt_idtf");
              String clntId = rs.getString("clnt_id");
              String clntSecret = rs.getString("clnt_scrt");
              String svrInfo = rs.getString("svr_info");
              info  = new SvrDTO(clntId, clntSecret, svrInfo, clntIdtf );
              list.add(info);
          }
      }
      
      if(list.size() > 0 ){
          for(int i = 0 ; i < list.size(); i++){
              SvrDTO info = (SvrDTO) list.get(i);
              String svrInfos = info.getSvrInfo();    

              if(svrInfos.contains(",")){
                  log.info("multi ::::::::::::::");
                  String[] svrInfo = svrInfos.split(",");
                  for(int j = 0 ; j < svrInfo.length ; j++){
                      svrInfo[j] = svrInfo[j].trim();
                      
                      if(svrInfo[j].contains("/")){
                      log.info(svrInfo[j]+" is subnet Mask");

                        if( subnetMaskCheck(1, clientIp, svrInfo[j]) ){
                            //리턴값 셋팅하고 
                            rsltInfo = info;
                            log.info("CHECK 1" + info.toString());

                        }
                      }else{
                          log.info(svrInfo[j]+" is NOT subnet Mask");

                          if( subnetMaskCheck(0, clientIp, svrInfo[j]) ){
                              //리턴값 셋팅하고 
                              rsltInfo = info;
                              log.info("CHECK 2" +info.toString());
                              break;
                          }
                      }
                  }
              }else{
                  log.info("single ::::::::::::::");
                  if(svrInfos.contains("/")){
                      log.info(svrInfos+"is subnet Mask");
                      if( subnetMaskCheck(1, clientIp, svrInfos ) ){
                          //리턴값 셋팅하고 
                          rsltInfo = info;
                          log.info("CHECK 3" + info.toString());
                          break;
                      };                        
                  }else{
                      log.info(svrInfos+"is NOT subnet Mask");
                      if( subnetMaskCheck(0, clientIp, svrInfos ) ){
                          //리턴값 셋팅하고 
                          rsltInfo = info;
                          log.info("CHECK 4" +info.toString());
                          break;
                      };
                  }

              }
          }
        }//if
        if(rsltInfo != null){
          clientId = rsltInfo.getClientId();
          clientSecret = rsltInfo.getClientSecret();
          clientIdtf = rsltInfo.getClientIdtf();
          request.response()
            .setStatusCode(200)
            .putHeader("X-Client-Id", clientId)
            .putHeader("X-Client-Secret", clientSecret)
            .putHeader("X-Client-Aud", clientIdtf).end();
        }else{
          request.response()
            .setStatusCode(401)
            .end("client ip is Unauthorized");
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
    if(type == 0){
        if(clientIp.equals(ipInfo)){
            rslt = true;
        }
    }else{ 
        String ip = ipInfo.split("/")[0];
        String subnet = ipInfo.split("/")[1];
        log.info("subnet" +subnet);
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

  
  //scope check
  private void scopeCheck( RoutingContext routingContext) {
    //select ast_type , ast_idtf from assets where identifier_type ='CI' and identifier_value='DJiefjeiLIEK4DJ8HDUSGGA39077';
    HttpServerRequest request = routingContext.request();
    String query = request.query();
    HashMap queryMap = new HashMap<>();
    Connection conn = null ;
    PreparedStatement stat = null;
    ResultSet rs = null;

    try {
      query = URLDecoder.decode(query, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    log.info("###########query >> " + query);
    String[] ss = query.split("\\&");

    for (int i = 0; i < ss.length; i++) {
      String[] sss = ss[i].split("=");
      queryMap.put(sss[0].toUpperCase(), sss[1]);
    }

    log.info("client_id" + queryMap.get("CLIENT_ID"));
    log.info("X-Client-Aud" + queryMap.get("X-CLIENT-AUD"));
    log.info("scope"+ queryMap.get("SCOPE"));

    if(queryMap.get("SCOPE") == null){
      request.response().setStatusCode(400).end("bedRequest");
    }

    String sql = "select count('X') as row_cnt from assets where clnt_idtf = (select clnt_idtf  from clients where clnt_id = ? and clnt_idtf = ?)";
    String[] scopes = ((String) queryMap.get("SCOPE")).split(" ");
    for( int i = 0; i < scopes.length ; i++){
      if(i == 0){
        sql += "and (ast_type = '"+scopes[i].split(":")[0]+"' and ast_idtf='"+scopes[i].split(":")[1]+"')";
      }else{
        sql += "or (ast_type = '"+scopes[i].split(":")[0]+"' and ast_idtf='"+scopes[i].split(":")[1]+"')";
      }
    }

    try {
      conn = connectToDB();
      stat = conn.prepareStatement(sql);
      stat.setString(1, (String)queryMap.get("CLIENT_ID"));
      stat.setString(2, (String)queryMap.get("X-CLIENT-AUD"));

      log.info(stat);
      rs = stat.executeQuery();
      if(rs.next()){
        if(rs.getInt("row_cnt") == scopes.length){
          request.response().setStatusCode(200).end();
        }else{
          request.response().setStatusCode(401).end();
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

  private void insertToken( RoutingContext routingContext) {
    log.info("########insertToken###########");
    HttpServerRequest request = routingContext.request();

    log.info("BODY :" + routingContext.getBodyAsString("utf-8"));
    JsonObject obj = routingContext.getBodyAsJson();
    JsonObject resultObj = new JsonObject();
    log.info(obj);

    //INSERT INTO mydata.access_token  (client_id, access_token, issue_datetime, expiry_time, disposal_time, delete_yn, tx_guid, gubun) VALUES('', '', NULL, NULL, NULL, 'N', NULL, NULL);
    log.info(obj.getString("access_token"));

    String accToken = "INSERT INTO mydata.access_token (client_id, access_token, issue_datetime, expiry_time, disposal_time, delete_yn, tx_guid, gubun, status) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    String refToken = "INSERT INTO mydata.refresh_token (client_id, refresh_token, issue_datetime, expiry_time, disposal_time, delete_yn, tx_guid, gubun) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
    
    Connection conn = null ;
    PreparedStatement stat = null;
    ResultSet rs = null;

    try {
      conn = connectToDB();
      stat = conn.prepareStatement(accToken);
      stat.setString(1, obj.getString("client_id"));
      stat.setString(2, obj.getString("access_token"));
      stat.setString(3, obj.getString("datetime"));
      stat.setString(4, obj.getString("acc_expires_in"));
      stat.setString(5, null);
      stat.setString(6, "N");
      stat.setString(7, obj.getString("tx_guid"));
      stat.setString(8, obj.getString("gubun"));
      stat.setString(9, obj.getString("status"));

      log.info(stat);
      if( stat.executeUpdate() <= 0){
        //failed
        request.response().setStatusCode(500).end("access token insert fail");
      }

      stat = conn.prepareStatement(refToken);
      stat.setString(1, obj.getString("client_id"));
      stat.setString(2, obj.getString("refresh_token"));
      stat.setString(3, obj.getString("datetime"));
      stat.setString(4, obj.getString("ref_expires_in"));
      stat.setString(5, null);
      stat.setString(6, "N");
      stat.setString(7, obj.getString("tx_guid"));
      stat.setString(8, obj.getString("gubun"));
      log.info(stat);

      if( stat.executeUpdate() <= 0){
        //failed
        request.response().setStatusCode(500).end("refresh token insert fail");
      }
      resultObj.put("result", "OK");
      request.response()
        .putHeader("Content-Type", "application/json")
        .setStatusCode(200)
        .end(resultObj.toString());

    } catch (Exception e) {
        e.printStackTrace();
        resultObj.put("result", "token insert error");
        request.response()
          .putHeader("Content-Type", "application/json")
          .setStatusCode(500)
          .end(resultObj.toString());
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

  //response
  private void resResult(HttpServerRequest request, int statusCode) {

  }
  
}//end

