package io.vertx.starter;

public class SvrDTO {

    private String clientId, clientSecret, svrInfo, clientIdtf;

    public String getClientIdtf() {
        return clientIdtf;
    }

    public void setClientIdtf(String clientIdtf) {
        this.clientIdtf = clientIdtf;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getSvrInfo() {
        return svrInfo;
    }

    public void setSvrInfo(String svrInfo) {
        this.svrInfo = svrInfo;
    }

    public SvrDTO(String clientId, String clientSecret, String svrInfo, String clientIdtf) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.svrInfo = svrInfo;
        this.clientIdtf = clientIdtf;
    }

    @Override
    public String toString() {
        return "SvrDTO [clientId=" + clientId + ", clientSecret=" + clientSecret + ", svrInfo=" + svrInfo + ", clientIdtf=" + clientIdtf + "]";
    }

  

    

}