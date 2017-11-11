package program.resources;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import program.domain.Exercise;
import program.domain.Group;
import program.domain.Program;
import program.domain.User;
import program.domain.Workout;
import program.services.WorkoutService;
import program.services.WorkoutServiceImpl;

@Path("/")
public class WorkoutResource {

	// CourseStudentService courseStudentService;
	WorkoutService workoutService;
	String password;
	String username;
	String dburl;

	public WorkoutResource(@Context ServletContext servletContext) throws SQLException {
		dburl = System.getProperty("DBURL");
		username = System.getProperty("DBUSERNAME");
		password = System.getProperty("DBPASSWORD");
		this.workoutService = new WorkoutServiceImpl(dburl, username, password);
	}
	
	@POST
	@Path("/program")
	public void createProgram(@FormParam("date") Date date, @FormParam("count") int count,
			@FormParam("fk_userid_user") int fk_userid_user) throws Exception {
		Program program = new Program(date, count, fk_userid_user);
		workoutService.createProgram(program);
	}

	@GET
	@Path("/program")
	@Produces("application/json")
	public List<Program> latestProgram() throws Exception {
		List<Program> programs = workoutService.getAllPrograms();
		return programs;
	}
	
	@GET
	@Path("/program/{userId}")
	@Produces("application/json")
	public Program programsForUser(@PathParam("userId") int userId) throws Exception {
		Optional<Program> p = workoutService.getLatestProgram(userId);
		return p.get();
	}

	@POST
	@Path("/users")
	public void createUser(@FormParam("username") String username, @FormParam("password_hash") String password_hash,
			@FormParam("salt") String salt) throws Exception {
		User user = new User(username, password_hash, salt);
		workoutService.createUser(user);
	}
	
	@GET
	@Path("/users/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public User getUser(@PathParam("userId") int userId) throws Exception {
		Optional<User> u = workoutService.getUser(userId);
		return u.get();
	}

	@GET
	@Path("/users")
	@Produces("text/html")
	public String users() throws Exception {
		List<User> users = workoutService.getAllUsers();
		return "<html><body><h1>Users</h1>" + users.toString()
				+ "<h2>Create a user</h2><form action=\"/tracker/users\" method=\"post\"> <div> <label for=\"username\">Username</label> <input name=\"username\" id=\"username\"></input> </div><div> <label for=\"password_hash\">Password</label> <input name=\"password_hash\" id=\"password_hash\" value=\"\"> </div><div> <label for=\"salt\">Salt</label> <input name=\"salt\" id=\"salt\" value=\"\"> </div><input type=\"submit\" value=\"Submit\" onClick=\"history.go(0)\"></form></body></html>";
	}

	@GET
	@Path("/exercises/{userId}")
	@Produces("application/json")
	public List<Exercise> exercises(@FormParam("userId") int userId) throws Exception {
		List<Exercise> exercises = workoutService.getExercises(userId);
		return exercises;
	}
	
	@POST
	@Path("/exercises")
	public void createExercise(@FormParam("name") String name, @FormParam("progression") Double progression) throws Exception {
		Exercise exericise = new Exercise(name, progression);
		workoutService.createExercise(exericise);
	}

	@GET
	@Path("/workouts/{userId}")
	@Produces("application/json")
	public List<Workout> workouts(@FormParam("userId") int userId) throws Exception {
		List<Workout> workouts = workoutService.getWorkouts(userId);
		return workouts;
	}
	
	@POST
	@Path("/workouts")
	public void createWorkout(@FormParam("goal") Double goal, @FormParam("failed") Boolean failed, @FormParam("fk_progamid_program") int fk_progamid_program) throws Exception {
		Workout workout = new Workout(goal, failed ,fk_progamid_program);
		workoutService.createWorkout(workout);
	}
	
	@GET
	@Path("/groups}")
	@Produces("application/json")
	public List<Group> allGroups() throws Exception {
		List<Group> groups = workoutService.getAllGroups();
		return groups;
	}
	
	@GET
	@Path("/groups/{programId}")
	@Produces("application/json")
	public List<Group> groups(@FormParam("programId") int programId) throws Exception {
		List<Group> groups = workoutService.getGroups(programId);
		return groups;
	}
	
	@POST
	@Path("/workouts")
	public void createGroup(@FormParam("name") String name, @FormParam("fk_programid_program") int fk_programid_program) throws Exception {
		Group group = new Group(name,fk_programid_program);
		workoutService.createGroup(group);
	}
}
