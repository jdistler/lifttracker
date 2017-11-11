package program.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import program.domain.Exercise;
import program.domain.Group;
import program.domain.Program;
import program.domain.User;
import program.domain.Workout;
import program.domain.WorkoutExercise;

public class WorkoutServiceImpl implements WorkoutService {

	String dbURL = "";
	String dbUsername = "";
	String dbPassword = "";
	Properties props;

	// DB connection information would typically be read from a config file.
	public WorkoutServiceImpl(String dbUrl, String username, String password) throws SQLException {
		this.dbURL = dbUrl;
		this.dbUsername = username;
		this.dbPassword = password;

		props = setupDataSource();
	}

	public Properties setupDataSource() throws SQLException {
		Properties props = new Properties();
		props.setProperty("user", this.dbUsername);
		props.setProperty("password", this.dbPassword);

		try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
			e.printStackTrace();
		}
		System.out.println("PostgreSQL JDBC Driver Registered!");

		return props;
	}

	@Override
	public Optional<Program> getLatestProgram(int userId) throws Exception {
		String query = "SELECT * FROM LiftTracker.Program WHERE fk_userid_user=" + userId + " ORDER BY date;";
		Connection conn = DriverManager.getConnection(this.dbURL, props);
		PreparedStatement s = conn.prepareStatement(query);
		ResultSet r = s.executeQuery();

		if (!r.next()) {
			return Optional.empty();
		}

		Program p = new Program(r.getDate("date"), r.getInt("count"), r.getInt("id_program"), r.getInt("fk_userid_user"));
		return Optional.ofNullable(p);
	}
	
	@Override
	public List<Program> getAllPrograms() throws Exception {
		String query = "SELECT * FROM LiftTracker.Program;";
		Connection conn = DriverManager.getConnection(this.dbURL, props);
		PreparedStatement s = conn.prepareStatement(query);
		ResultSet r = s.executeQuery();
		List<Program> programs = new ArrayList<>();
		while (r.next()) {
			Program p = new Program(r.getDate("date"), r.getInt("count"), r.getInt("id_program"), r.getInt("fk_userid_user"));
			programs.add(p);
		}
		return programs;
	}

	@Override
	public List<Workout> getWorkouts(int programId) throws Exception {
		String query = "SELECT * FROM LiftTracker.Workout WHERE fk_programid_program=" + programId + ";";
		Connection conn = DriverManager.getConnection(this.dbURL, props);
		PreparedStatement s = conn.prepareStatement(query);
		ResultSet r = s.executeQuery();
		List<Workout> workouts = new ArrayList<>();
		while (r.next()) {
			Workout w = new Workout(r.getDouble("current_1rm"), r.getBoolean("failure"), r.getInt("id_workout"), r.getInt("fk_programid_program"));
			workouts.add(w);
		}
		return workouts;
	}

	@Override
	public List<Exercise> getExercises(int workoutId) throws Exception {
		String query = "SELECT * FROM LiftTracker.Exercise, LiftTracker.Workout_Exercise WHERE fk_exerciseid_exercise=id_exercise AND fk_workoutid_workout="
				+ workoutId + ";";
		Connection conn = DriverManager.getConnection(this.dbURL, props);
		PreparedStatement s = conn.prepareStatement(query);
		ResultSet r = s.executeQuery();
		List<Exercise> exercises = new ArrayList<>();
		while (r.next()) {
			Exercise e = new Exercise(r.getString("name"), r.getDouble("progression"),r.getInt("id_exercise"));
			exercises.add(e);
		}
		return exercises;
	}
	
	@Override
	public List<Group> getGroups(int programId) throws Exception {
		String query = "SELECT * FROM LiftTracker.Group WHERE fk_programid_program="
				+ programId + ";";
		Connection conn = DriverManager.getConnection(this.dbURL, props);
		PreparedStatement s = conn.prepareStatement(query);
		ResultSet r = s.executeQuery();
		List<Group> groups = new ArrayList<>();
		while (r.next()) {
			Group g = new Group(r.getString("name"), r.getInt("id_group"), r.getInt("fk_programid_program"));
			groups.add(g);
		}
		return groups;
	}
	
	@Override
	public List<Group> getAllGroups() throws Exception {
		String query = "SELECT * FROM LiftTracker.Group;";
		Connection conn = DriverManager.getConnection(this.dbURL, props);
		PreparedStatement s = conn.prepareStatement(query);
		ResultSet r = s.executeQuery();
		List<Group> groups = new ArrayList<>();
		while (r.next()) {
			Group g = new Group(r.getString("name"), r.getInt("id_group"), r.getInt("fk_programid_program"));
			groups.add(g);
		}
		return groups;
	}

	@Override
	public List<User> getAllUsers() throws Exception {
		String query = "SELECT * FROM LiftTracker.User;";
		Connection conn = DriverManager.getConnection(this.dbURL, props);
		PreparedStatement s = conn.prepareStatement(query);
		ResultSet r = s.executeQuery();
		List<User> users = new ArrayList<>();
		while (r.next()) {
			User u = new User(r.getString("username"), r.getString("password_hash"), r.getString("salt"), r.getInt("id_user"));
			users.add(u);
		}
		return users;
	}
	
	@Override
	public Optional<User> getUser(int userId) throws Exception {
		String query = "SELECT * FROM LiftTracker.User WHERE id_User="+userId+";";
		Connection conn = DriverManager.getConnection(this.dbURL, props);
		PreparedStatement s = conn.prepareStatement(query);
		ResultSet r = s.executeQuery();
		if (!r.next()) {
			return Optional.empty();
		}

		User u = new User(r.getString("username"), r.getString("password_hash"), r.getString("salt"), r.getInt("id_user"));
		return Optional.ofNullable(u);
	}

	@Override
	public boolean createUser(User user) throws Exception {
		String query = "INSERT INTO LiftTracker.User (username, password_hash, salt) VALUES(?,?,?) RETURNING id_user";
		Connection conn = DriverManager.getConnection(this.dbURL, props);
		PreparedStatement s = conn.prepareStatement(query);
		
		s.setString(1, user.getUsername());
		s.setString(2, user.getPassword_hash());
		s.setString(3, user.getSalt());

		ResultSet r = s.executeQuery();
		
		if (r.getFetchSize() == 0) {
			return false;
		}
        if (r.next()) {
        	user.setId_user(r.getInt(1));
        }
		return true;
	}
	
	@Override
	public boolean createProgram(Program program) throws Exception {
		String query = "INSERT INTO LiftTracker.Program (date, count, fk_userid_user) VALUES(?,?,?)";
		Connection conn = DriverManager.getConnection(this.dbURL, props);
		PreparedStatement s = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		
		s.setDate(1, program.getDate());
		s.setInt(2, program.getCount());
		s.setInt(3, program.getFk_userid_user());
		
		int affectedRows = s.executeUpdate();
		if (affectedRows == 0) {
			return false;
		}
		ResultSet generatedKeys = s.getGeneratedKeys();
        if (generatedKeys.next()) {
        	program.setId_program(generatedKeys.getInt(1));
        }
		return true;
	}
	
	@Override
	public boolean createWorkout(Workout workout) throws Exception {
		String query = "INSERT INTO LiftTracker.Workout (goal, failed, fk_programid_program) VALUES(?,?,?)";
		Connection conn = DriverManager.getConnection(this.dbURL, props);
		PreparedStatement s = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		
		s.setDouble(1, workout.getGoal());
		s.setBoolean(2, workout.getFailed());
		s.setInt(3, workout.getFk_progamid_program());
		
		int affectedRows = s.executeUpdate();
		if (affectedRows == 0) {
			return false;
		}
		ResultSet generatedKeys = s.getGeneratedKeys();
        if (generatedKeys.next()) {
        	workout.setId_workout(generatedKeys.getInt(1));
        }
		return true;
	}
	
	@Override
	public boolean createGroup(Group group) throws Exception {
		String query = "INSERT INTO LiftTracker.Group (name, fk_programid_program) VALUES(?,?)";
		Connection conn = DriverManager.getConnection(this.dbURL, props);
		PreparedStatement s = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		
		s.setString(1, group.getName());
		s.setInt(2, group.getFk_programid_program());
		
		int affectedRows = s.executeUpdate();
		if (affectedRows == 0) {
			return false;
		}
		ResultSet generatedKeys = s.getGeneratedKeys();
        if (generatedKeys.next()) {
        	group.setId_group(generatedKeys.getInt(1));
        }
		return true;
	}
	
	@Override
	public boolean createExercise(Exercise exercise) throws Exception {
		String query = "INSERT INTO LiftTracker.Exericse (name, progression) VALUES(?,?)";
		Connection conn = DriverManager.getConnection(this.dbURL, props);
		PreparedStatement s = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		
		s.setString(1, exercise.getName());
		s.setDouble(2, exercise.getProgression());
		
		int affectedRows = s.executeUpdate();
		if (affectedRows == 0) {
			return false;
		}
		ResultSet generatedKeys = s.getGeneratedKeys();
        if (generatedKeys.next()) {
        	exercise.setId_exercise(generatedKeys.getInt(1));
        }
		return true;
	}
	
	@Override
	public boolean createWorkoutExercise(WorkoutExercise workoutExercise) throws Exception {
		String query = "INSERT INTO LiftTracker.Exericse (fk_workoutid_workout, fk_exerciseid_exercise) VALUES(?,?)";
		Connection conn = DriverManager.getConnection(this.dbURL, props);
		PreparedStatement s = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		
		s.setInt(1, workoutExercise.getFk_workoutid_workout());
		s.setInt(2, workoutExercise.getFk_exerciseid_exercise());
		
		int affectedRows = s.executeUpdate();
		if (affectedRows == 0) {
			return false;
		}
		return true;
	}

}
