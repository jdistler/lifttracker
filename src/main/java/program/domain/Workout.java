package program.domain;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "workout")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
public class Workout {
	Double goal;
	Boolean failed;
	int id_workout;
	int fk_progamid_program;
	
	public Workout(	Double goal,Boolean failed,int fk_progamid_program) {
		this.goal = goal;
		this.failed = failed;
		this.fk_progamid_program = fk_progamid_program;
	}
}
