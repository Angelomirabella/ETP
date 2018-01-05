import java.util.ArrayList;
import java.util.List;

public class Exam implements Cloneable{
	private int id;
	private int tot_stud;
	private Integer time_slot;
	private List<String> students;
	private List<Integer> availableTimeSlots;
	private int movements=0;

	public Exam(int id,int tot_stud)
	{
		this.id=id;
		this.tot_stud=tot_stud;
		time_slot=-1;
		students=new ArrayList<>();
		availableTimeSlots = new ArrayList<>();
	}
//x

	public void setTime_slot(Integer time_slot) {
		this.time_slot = time_slot;
	}
	public List<String> getStudents() {
		return students;
	}
	
	public void incMovements() {
		movements++;
	}

	public int getMovements() {
		return movements;
	}


	public int getId() {
		return id;
	}


	public int getTot_stud() {
		return tot_stud;
	}


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
	
	public List<Integer> getAvailableTimeSlots() {
		return availableTimeSlots;
	}

	public void setAvailableTimeSlots(List<Integer> availableTimeSlots) {
		this.availableTimeSlots = availableTimeSlots;
	}
	public Object clone(){
		try {Exam e = (Exam) super.clone(); 
				return e;}    
	catch (CloneNotSupportedException e) {return null;}     } 
}
