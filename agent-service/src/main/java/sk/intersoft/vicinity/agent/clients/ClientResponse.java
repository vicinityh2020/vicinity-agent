package sk.intersoft.vicinity.agent.clients;

public class ClientResponse {
    public int statusCode;
    public String statusCodeReason;
    public String data;

    public ClientResponse(int statusCode, String statusCodeReason, String data){
        this.statusCode = statusCode;
        this.statusCodeReason = statusCodeReason;
        this.data = data;
    }

    public boolean isOK(){
        return (statusCode == 200);
    }

    public String toString(){
        return "[ClientResponse ("+statusCode+" / "+statusCodeReason+"): "+data+" ]";
    }
}
