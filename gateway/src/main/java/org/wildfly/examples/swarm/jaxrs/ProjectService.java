package org.wildfly.examples.swarm.jaxrs;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/projects")
public interface ProjectService {

	@GET
	JsonArray getAllProjects();

	@GET
	@Path("/{projectId}")
	JsonObject getProjectById(@PathParam("projectId") String projectId);

	@GET
	@Path("/status/{theStatus}")
	JsonArray getProjectsByStatus(@PathParam("theStatus") String projectStatus);

}
