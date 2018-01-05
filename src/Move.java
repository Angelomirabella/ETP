
public class Move {
	private Exam e;
	private int from,to;
	private Exam exhanged;
	
	
	
	public Exam getE() {
		return e;
	}



	public int getFrom() {
		return from;
	}



	public int getTo() {
		return to;
	}



	public Exam getExhanged() {
		return exhanged;
	}



	public void setExhanged(Exam exhanged) {
		this.exhanged = exhanged;
	}



	public Move(Exam e, int from, int to) {
		this.e = e;
		this.from = from;
		this.to = to;
		exhanged=null;
	}
	public boolean Equals(Move m)
	{
		if(m.getE()==null)
			if(e==null)
                 return  from==m.getFrom() && to==m.getTo();
			else
				return false;
		else
			if(e==null)
				return false;
		return e.getId()==m.getE().getId() && from==m.getFrom() && to==m.getTo();
	}
	
	public String toString(){
		return "mossa: " + "e" + e.getId() + " " + to + " -> "+ from;
	}

}
