package dmit2015.restclient;

import jakarta.enterprise.context.RequestScoped;
import jakarta.json.JsonObject;
import jakarta.ws.rs.*;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.LinkedHashMap;

/**
 * The baseUri for the web MpRestClient be set in either microprofile-config.properties (recommended)
 * or in this file using @RegisterRestClient(baseUri = "http://server/path").
 * <p>
 * To set the baseUri in microprofile-config.properties:
 * 1) Open src/main/resources/META-INF/microprofile-config.properties
 * 2) Add a key/value pair in the following format:
 * package-name.ClassName/mp-rest/url=baseUri
 * For example:
 * package-name:    dmit2015.client
 * ClassName:       TodoItemMpRestClient
 * baseUri:         http://localhost:8080/contextName
 * The key/value pair you need to add is:
 * dmit2015.restclient.TodoItemMpRestClient/mp-rest/url=http://localhost:8080/contextName
 * <p>
 * <p>
 * To use the client interface from an environment does support CDI, add @Inject and @RestClient before the field declaration such as:
 <code>
 @Inject
 @RestClient private TodoItemMpRestClient _todoitemMpRestClient;
 </code>
 * <p>
 * To use the client interface from an environment that does not support CDI, you can use the RestClientBuilder class to programmatically build an instance as follows:
 * <p>
 <code>
    URI apiURI = new URI("http://sever/contextName");
    TodoItemMpRestClient _todoitemMpRestClient = RestClientBuilder.newBuilder()
                                                  .baseUri(apiURi)
                                                  .build(TodoItemMpRestClient.class);
 </code>
 *
 * To filter data with firebase you need to add an indexOf security rule
<code>
{
    "rules": {
        "TodoItem": {
            "$uid": {
            // Allow only authenticated content owners access to their data
            ".read": "auth !== null && auth.uid === $uid",
            ".write": "auth !== null && auth.uid === $uid",
            ".indexOn": ["done"]
            }
        }
    }
}
</code>
 *
 */
@RequestScoped
@RegisterRestClient
public interface TodoItemMpRestClient {

    final String DOCUMENT_URL = "/TodoItem/{userUID}";

    @POST
    @Path(DOCUMENT_URL + ".json")
    JsonObject create(@PathParam("userUID") String userId, TodoItem newTodoItem, @QueryParam("auth") String token);

    @GET
    @Path(DOCUMENT_URL + ".json")
    LinkedHashMap<String, TodoItem> findAll(@PathParam("userUID") String userId, @QueryParam("auth") String token);

    @GET
    @Path(DOCUMENT_URL + ".json")
    LinkedHashMap<String, TodoItem> findAllByDone(@PathParam("userUID") String userId, @QueryParam("auth") String token,
                                                @QueryParam("orderBy") String orderBy, @QueryParam("equalTo") boolean done );

    @GET
    @Path(DOCUMENT_URL + "/{key}.json")
    TodoItem findById(@PathParam("userUID") String userId, @PathParam("key") String id, @QueryParam("auth") String token);

    @PUT
    @Path(DOCUMENT_URL + "/{key}.json")
    TodoItem update(@PathParam("userUID") String userId, @PathParam("key") String key, TodoItem updatedTodoItem, @QueryParam("auth") String token);

    @PATCH
    @Path(DOCUMENT_URL + "/{key}.json")
    JsonObject updateDone(@PathParam("userUID") String userId, @PathParam("key") String key, JsonObject payload, @QueryParam("auth") String token);

    @DELETE
    @Path(DOCUMENT_URL + "/{key}.json")
    void delete(@PathParam("userUID") String userId, @PathParam("key") String key, @QueryParam("auth") String token);

}