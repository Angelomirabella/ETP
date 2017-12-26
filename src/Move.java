
public class Move {
	private Exam e;
	private int from,to;
	private Exam scambiato;
	
	
	
	public Exam getE() {
		return e;
	}



	public int getFrom() {
		return from;
	}



	public int getTo() {
		return to;
	}



	public Exam getScambiato() {
		return scambiato;
	}



	public void setScambiato(Exam scambiato) {
		this.scambiato = scambiato;
	}



	public Move(Exam e, int from, int to) {
		this.e = e;
		this.from = from;
		this.to = to;
		scambiato=null;
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
