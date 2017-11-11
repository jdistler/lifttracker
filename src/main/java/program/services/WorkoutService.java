package program.services;

import java.util.List;
import java.util.Optional;

import program.domain.Exercise;
import program.domain.Group;
import program.domain.Program;
import program.domain.User;
import program.domain.Workout;
import program.domain.WorkoutExercise;

public interface WorkoutService {
	// Look through program DB for most recent date of program
	// Based on last program, generate list of work outs
	public Optional<Program> getLatestProgram(int userId) throws Exception;

	public List<Workout> getWorkouts(int programId) throws Exception;

	List<Exercise> getExercises(int workoutId) throws Exception;

	List<User> getAllUsers() throws Exception;

	boolean createUser(User user) throws Exception;

	List<Group> getGroups(int programId) throws Exception;

	boolean createProgram(Program program) throws Exception;

	boolean createWorkout(Workout workout) throws Exception;

	boolean createGroup(Group group) throws Exception;

	boolean createExercise(Exercise exercise) throws Exception;

	boolean createWorkoutExercise(WorkoutExercise workoutExercise) throws Exception;

	List<Group> getAllGroups() throws Exception;

	List<Program> getAllPrograms() throws Exception;

	Optional<User> getUser(int userId) throws Exception;
}
