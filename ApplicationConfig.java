
package com.newgen;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * /*************************************************************************************************************

*
* Group				: SDC
* Module			: API For ChatBoot
* File Name			: ApplicationConfig.java
* Author			: Anil Kumar A
* Date written		        : 23/07/2019
* ************************************************************************************************************/
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.newgen.OmniData.class);
    }
}
