package org.wildfly.examples.swarm.jaxrs;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/freelancers")

public interface FreelancerService {

	@GET
	JsonObject getAllFreelancers();

	@GET
	@Path("/{freelancerId}")
	JsonObject getFreelancerById(@PathParam("freelancerId") String freelancerId);

}
