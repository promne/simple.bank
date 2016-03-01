package org.simple.bank.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("client")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface ClientBankApi {
    
    public static final String HEADER_AUTH_TOKEN = "Session-Authentication-Token";

    @POST
    public String login(@HeaderParam("username") String username, @HeaderParam("password") String password);
    
    @GET
    @Path("accounts")
    public List<Account> listAccounts(@HeaderParam(HEADER_AUTH_TOKEN) String authToken);

    @GET
    @Path("accounts/{account}/transactions")
    public List<Transaction> listTransactions(@HeaderParam(HEADER_AUTH_TOKEN) String authToken, @PathParam("account") String accountId);

    @POST
    @Path("accounts/{account}/transactions")
    public Transaction pay(@HeaderParam(HEADER_AUTH_TOKEN) String authToken, @PathParam("account") String accountId, Transaction transaction);

    @DELETE
    public void logout(@HeaderParam(HEADER_AUTH_TOKEN) String authToken);
 
    
}
