/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import sun.misc.BASE64Decoder;

/**
 *
 * @author AN36130T
 */
@Provider
@PreMatching
public class SecurityFilter implements ContainerRequestFilter {

    public static final String AUTHENTICATION_HEADER_KEY = "Authorization";
    public static final String AUTHENTICATION_HEADER_PREFIX = "Base ";
    GenerateLog log = new GenerateLog();

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        log.genlog("Inside Servlet filter::::::::::::::::");
        List<String> authHeader = requestContext.getHeaders().get(AUTHENTICATION_HEADER_KEY);
        log.genlog("AUTHENTICATION_HEADER_KEY::::" + AUTHENTICATION_HEADER_KEY);
        log.genlog("AUTHENTICATION_HEADER_PREFIX::::" + AUTHENTICATION_HEADER_PREFIX);
        log.genlog("authHeader::::" + authHeader);

        System.out.println("inside cabinet");
        if (authHeader != null && authHeader.size() > 0) {
            log.genlog("Inside Authorization Header -----------------------" + authHeader);
            String authToken = authHeader.get(0);
            log.genlog(authToken);
            authToken = authToken.replaceFirst(AUTHENTICATION_HEADER_PREFIX, "");
            log.genlog(authToken);
            String[] authParts = authToken.split("\\s+");
            String authInfo = authParts[1];
            log.genlog(authInfo);
            byte[] bytes = null;
            try {
                bytes = new BASE64Decoder().decodeBuffer(authInfo);
            } catch (IOException e) {
            }
            String decodedAuth = "";
            decodedAuth = new String(bytes);
            log.genlog("decodedAuth::::" + decodedAuth);
            StringTokenizer tokenizer = new StringTokenizer(decodedAuth, ":");
            String username = tokenizer.nextToken();
            String password = tokenizer.nextToken();
            log.genlog("username::::" + username + "password:::" + password);
            if (username.equalsIgnoreCase("Omnidata") && password.equalsIgnoreCase("omnidata")) {
                System.out.println("username::" + username + "password::" + password);
                log.genlog("Inside Returning the Servlets option::::::::::");
                log.genlog("username::::" + username + "password:::" + password);
                return;
            }

            /* byte[] decodedBytes = Base64.getDecoder().decode(authToken);
            String decodeString = new String(decodedBytes, "UTF-8");
            log.genlog("decodeString:" + decodeString);
            StringTokenizer tokenizer = new StringTokenizer(decodeString, ":");
            String username = tokenizer.nextToken();
            String password = tokenizer.nextToken();
            log.genlog("username::::" + username + "password:::" + password);
            if (username.equalsIgnoreCase("Omnidata") && password.equalsIgnoreCase("omnidata")) {
                System.out.println("username::" + username + "password::" + password);
                return;
            }*/
        }
        Response unauthorizedStatus = Response.status(Response.Status.UNAUTHORIZED).entity("User Cannot access the resource.").build();
        requestContext.abortWith(unauthorizedStatus);
    }

}
