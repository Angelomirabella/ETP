import java.util.ArrayList;
import java.util.List;

public class Exam implements Cloneable{
	private int id;//nemesi,conflict;
	private int tot_stud;
	private Integer time_slot;
	private List<String> students;
	private List<Integer> timeSlotsDisponibili;

	public Exam(int id,int tot_stud)
	{
		this.id=id;
		this.tot_stud=tot_stud;
		time_slot=-1;
		students=new ArrayList<>();
		timeSlotsDisponibili = new ArrayList<>();
	}
//x

	public void setTime_slot(Integer time_slot) {
		this.time_slot = time_slot;
	}
	public List<String> getStudents() {
		return students;
	}


	public int getId() {
		return id;
	}


	/*public int getNemesi() {
		return nemesi;
	}

	public int getConflict() {
		return conflict;
	}
*/
	public int getTot_stud() {
		return tot_stud;
	}

/*	public void setNemesi(int nemesi,int conflict)
	{
		this.nemesi=nemesi;
		this.conflict=conflict;
		return;
	}*/
	public Integer getTime_slot() {
		return time_slot;
	}
	public void addStudent(String s)
	{
		students.add(s);
		return;
	}
	
	public String toString(){
		return "esame " + id;
	}
	
	public List<Integer> getTimeSlotsDisponibili() {
		return timeSlotsDisponibili;
	}

	
	public void setTimeSlotsDisponibili(List<Integer> timeSlotsDisponibili) {
		this.timeSlotsDisponibili = timeSlotsDisponibili;
	}
	public Object clone(){
		try {Exam e = (Exam) super.clone(); 
				return e;}    
	catch (CloneNotSupportedException e) {return null;}     } 
}
