import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class Neighbor {
	private SortedMap<Integer,List<Exam>> solution=new TreeMap<>();
	private Move m;
	private double obj;
	
	public Neighbor(SortedMap<Integer, List<Exam>> solution, Move m,double obj) {
		this.solution = solution;
		this.m = m;
		this.obj=obj;
	}


	public SortedMap<Integer, List<Exam>> getSolution() {
		return solution;
	}


	public double getObj() {
		return obj;
	}


	public Move getM() {
		return m;
	}
}
