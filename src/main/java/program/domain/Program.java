package program.domain;

import java.sql.Date;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "program")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
public class Program {
	@XmlJavaTypeAdapter(SqlDateAdapter.class)
	Date date;
	int count;
	int id_program;
	int fk_userid_user;

	public Program(Date date, int count, int fk_userid_user) {
		this.date = date;
		this.count = count;
		this.fk_userid_user = fk_userid_user;
	}
}
