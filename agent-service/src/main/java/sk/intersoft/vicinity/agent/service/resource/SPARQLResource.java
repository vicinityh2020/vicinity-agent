package sk.intersoft.vicinity.agent.service.resource;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.agent.clients.ClientResponse;
import sk.intersoft.vicinity.agent.clients.GatewayAPIClient;
import sk.intersoft.vicinity.agent.thing.ThingDescription;

public class SPARQLResource extends AgentResource {
    final static Logger logger = LoggerFactory.getLogger(SPARQLResource.class.getName());


    @Post()
    public String sparql(Representation entity) {
        try {

            logger.info("CALLING SPARQL: ");
            String query = getQueryString(getQuery());
            logger.info("QUERY: " + query);

            if(entity == null) {
                throw new Exception("Empty payload!");
            }
            String rawPayload = entity.getText();

            logger.info("PAYLOAD: " + rawPayload);

            ThingDescription thing = getCallerObject();
            logger.info("CALLER THING: " + thing.toSimpleString());

            String endpoint = GatewayAPIClient.getSPARQLEndpoint();

            logger.info("GTW API ENDPOINT: "+endpoint);

            ClientResponse gtwResponse = GatewayAPIClient.post(endpoint, rawPayload, thing.oid, thing.password, query);
            logger.info("GTW API RAW RESPONSE: \n"+gtwResponse);

            return gtwSuccess(gtwResponse);


        }
        catch (Exception e) {
            logger.error("SPARQL FAILURE! ", e);
            return gtwError(e).toString();
        }
    }
}