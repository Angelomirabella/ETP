import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;
import java.io.*;
import java.lang.Math;
import static java.util.stream.Collectors.*;
import static java.util.Comparator.*; 
public class TimeTable {
	private Integer tmax;
	private List<Exam> esamiPeggiori;
	private SortedMap<Integer,Exam> exams;
	private SortedMap<String,List<Exam>> students;  //per ogni matricola la lista degli esami a cui si è iscritto
	private int[][] n;
	private int E,iteration,S,count, worst,num;
	private double current_obj,best_obj;
	private boolean trovato = false, compatibile=true, continua=true;
	private SortedMap<Integer,List<Exam>> initialSolution;
	private SortedMap<Integer,List<Exam>> current_solution;
	private SortedMap<Integer,List<Exam>> best_solution;
	private Move[] tabuTimeslots;
	private Move[] tabuExams;
	private Exam [] lastExams;
//	private List<Move> moves;
	private List<Neighbor> neighborhoodExams;
	private List<Neighbor> neighborhoodTimeslots;
	private Exam incompatibile;
	private int DIM_H = 0;
	private int DIM_TS = 15;
	private int enrollments = 0;
	private double media=0;
	private double r=0.15;//0.15
	
	
	public TimeTable()
	{
		exams=new TreeMap<>();
		students=new TreeMap<>();
		current_solution=new TreeMap<>();
		tabuTimeslots=new Move[4];
		//tabuExams=new Move[DIM_TS];
		
		neighborhoodExams=new ArrayList<>();
		neighborhoodTimeslots=new ArrayList<>();
	}
	
	public void Initialize (String slo,String stu, String exm)
	{
		 try(BufferedReader in=new BufferedReader(new FileReader(slo))) //leggo tmax
		 {   
			 String line=in.readLine();

			 Scanner s;
				 s=new Scanner(line);
				 tmax=s.nextInt();
				 s.close();
			 
			 
		 }  catch (IOException e) {};
		 
		 
		 try(BufferedReader in=new BufferedReader(new FileReader(exm))) //leggo esami
		 {   
			 String line;
			 Scanner s;
			 while((line=in.readLine())!=null)
			 {
				 if(!line.equals(""))
				 {
				 s=new Scanner(line);
				 Exam e=new Exam(s.nextInt(),s.nextInt());
				 enrollments+=e.getTot_stud();
				 exams.put(e.getId(),e);
				 s.close();
				 }
			 }
			 E=exams.keySet().size();
			 n=new int[E][E];

		 }  catch (IOException e) {};
		 DIM_TS=E/9;//modified
			tabuExams=new Move[DIM_TS];

		 media = enrollments/exams.size();
		 if(E>200)
		 {	
			 
			 
			if(E>400) 
			 {
			  if(E<600)	
			  {
				  for(int j=1; j<=exams.size(); j++) {  //per 5
			 
					int nStud=exams.get(j).getTot_stud();
					if(nStud<(media/2.5))//2.5
						DIM_H++;
			   }
			  }
			  else //per 6
			  {
				  for(int j=1; j<=exams.size(); j++) {  
						 
						int nStud=exams.get(j).getTot_stud();
						if(nStud<(media/2.5))//2.5
							DIM_H++;
			     }
			  } 
			 }
			else
			 {
				for(int j=1; j<=exams.size(); j++) {  //per 4
				int nStud=exams.get(j).getTot_stud();
				if(nStud<(media/2.5))//2.5
					DIM_H++;
			 }
			}
		 }
		 else //if (E>100) //per la 1-2-3 va un po meglio e 7
		 {
			 for(int j=1; j<=exams.size(); j++) {
					int nStud=exams.get(j).getTot_stud();
					if(nStud<(media/1.5))
						DIM_H++;
				}
		 }
		
			lastExams = new Exam[DIM_H];
		 System.out.println("light--> " + DIM_H);
		 try(BufferedReader in=new BufferedReader(new FileReader(stu));
			 PrintWriter out=new PrintWriter(new FileWriter("prova.txt")))
		 {   
			
			 String line;
			 Scanner s;
			 
			 while((line=in.readLine())!=null)
			 {
				 if(!line.equals(""))
				 { s=new Scanner(line);
				 String student=s.next();
				 
				 List<Exam>l=students.get(student);
				 if(l==null)
				 {
					 l=new ArrayList<>();
					 Exam e=exams.get(s.nextInt());
					// e.addStudent(student);
					 l.add(e);
					 students.put(student, l);
				 }
				 else
				 {
					 
					 Exam e=exams.get(s.nextInt());
					 e.addStudent(student);
					 l.add(e);
				 }
				 s.close();

			  }
			 }
			
			 S=students.keySet().size();
			 //popolo n_ij
			 for (int i=0; i<S;i++)
			 {
				 String st="s"+(i+1);
				
				List<Exam> l=students.get(st);
				 for(int j=0;j<l.size();j++)
				 {
					 for(int k=0; k<l.size() ;k++) //anche k =j -> evito permutazioni
					 {
						if(j!=k)
						{
							int row=l.get(j).getId();
							int col=l.get(k).getId();
							 n[row-1][col-1]++;//esami partono da 1
							
						}
					 }
					 
				 }
			 }
			// System.out.println(Arrays.deepToString(n)); 

		 }  catch (IOException e) {};
		 
		 
	}

	
	private double Evaluate(SortedMap<Integer,List<Exam>> solution) {
		double obj=0;
		for (int i=1;i<=tmax-1;i++) {
			if(!solution.get(i).isEmpty()) {
				List<Exam>l=solution.get(i);

				Iterator<Exam> iter=l.iterator();
				while(iter.hasNext()) {
					Exam e=iter.next();
					for(int j=i+1;j<=i+5 && j<=tmax;j++) {
						if(!solution.get(j).isEmpty()) {
						List<Exam> l_int=solution.get(j);
						Iterator<Exam> iter_int=l_int.iterator();
						while(iter_int.hasNext()) {
							Exam e_int=iter_int.next();
							int distance=Math.abs(e.getTime_slot()-e_int.getTime_slot());
							
								int row=e.getId()-1;
								int col=e_int.getId()-1;
								double po=Math.pow(2,5-distance);
								
								obj+=po*n[row][col];    
						}
					}
				}
			}
		}	
	}
		return obj/S;
	}
	
	
	
	private void Print()
	{
		try(PrintWriter out=new PrintWriter(new FileWriter("C:\\Users\\angij\\Desktop\\Polito magistrale 1 anno\\Optimization methods and algoritms\\Assignment\\instancename_OMAAL_group04.sol")))
		{			
			best_solution.values().stream().flatMap(l->l.stream()).sorted(comparing(e->e.getId())).forEach(e->out.format("%d %d\n",e.getId(),e.getTime_slot()));
	 }catch (IOException e) {};
	}
	
	private SortedMap<Integer,List<Exam>> initializeInitialSolution() {
		SortedMap<Integer,List<Exam>> solution = new TreeMap<>();
		for(int i=1; i<=tmax; i++){
			solution.put(i, new ArrayList<>());
		}
		return solution;
	}

	private SortedMap<Integer,Exam> copiaEsami() {
		SortedMap<Integer,Exam> esami = new TreeMap<>();
		exams.entrySet().stream().forEach(e-> esami.put(e.getKey(), e.getValue()));
		return esami;
	}

	private Object sum(int size) {
		return num=num+size;
	}

	public int checkTimeslot(int j, int i,SortedMap<Integer,List<Exam>> solution) {
		final int idx = i;
		int incompatibilita = 0;
		if(solution.get(j).size() != 0){
			List<Exam> listOfExams = solution.get(j);
			Iterator<Exam> iter = listOfExams.iterator();
			while(iter.hasNext()) {
				Exam e = iter.next();
				if(n[e.getId()-1][idx-1] != 0) {
					incompatibilita++;
					incompatibile=e;
				}
			}
		}
		return incompatibilita;
	}

	public SortedMap <Integer,List<Exam>> Generate_Initial_Solution() {
		while(!trovato){
			initialSolution = initializeInitialSolution();  				
			SortedMap<Integer,Exam> esamiDaAssegnare = copiaEsami(); 		
			continua=true;
			
			while(!esamiDaAssegnare.isEmpty() && continua) {	
				Exam esameScelto;
				worst=tmax;													
				esamiPeggiori = new ArrayList<>();
				Iterator<Exam> iter = esamiDaAssegnare.values().iterator();
				
				while(iter.hasNext() && continua) {
					Exam e = iter.next();
					List<Integer> timeSlotsDisponibili = new ArrayList<>();
					count=0;
					for(int i=1; i<=tmax; i++){
						if(checkTimeslot(i, e.getId(),initialSolution)==0){
							count++;
							timeSlotsDisponibili.add(i);
						}
					}
					if(!timeSlotsDisponibili.isEmpty()) {
						e.setTimeSlotsDisponibili(timeSlotsDisponibili);
						if(count<worst || esamiPeggiori.isEmpty()){
							esamiPeggiori = new ArrayList<>();
							worst=count;
							esamiPeggiori.add(e);
						}
						else if(count==worst){
							esamiPeggiori.add(e);
						}
					}
					else
						continua = false;
				}
				
				if(continua) {
					esameScelto = esamiPeggiori.get(new Random().nextInt(esamiPeggiori.size()));
					int r = esameScelto.getTimeSlotsDisponibili().get(new Random().nextInt(esameScelto.getTimeSlotsDisponibili().size()));
					initialSolution.get(r).add(esameScelto);
					esameScelto.setTime_slot(r);
					esamiDaAssegnare.remove(esameScelto.getId());
				}
			}
			
			if(esamiDaAssegnare.isEmpty())
				trovato=true;
		}
		
	//	initialSolution.values().stream().forEach(t->sum(t.size()));
	//	System.out.println("Esami assegnati: " + num);
		return initialSolution;
	}

	private List<Neighbor> Generate_Neighborhood() {
		List<Neighbor> res=new ArrayList<>();

		for(int i=1;i<=tmax;i++) {
			SortedMap<Integer,List<Exam>> neighbor1=Clone_solution();
			List<Exam> first=neighbor1.get(i);
			Iterator<Exam> iter=first.iterator();
			double diff=current_obj*S;
			while(iter.hasNext()) {
				Exam e=iter.next();
				for(int k=i-5;k<=i+5 && k<=tmax ;k++) {
					if(k>0 &&k!=i) {
						List<Exam> l_int=neighbor1.get(k);
						Iterator<Exam> iter_int=l_int.iterator();
						while(iter_int.hasNext()) {
							Exam e_int=iter_int.next();
													//i
							int distance=Math.abs(e.getTime_slot()-e_int.getTime_slot());
							
							int row=e.getId()-1;
							int col=e_int.getId()-1;
							double po=Math.pow(2,5-distance);
							
							diff-=po*n[row][col]; //- devo sottrarre
						}
					}
				}
			}
			for(int j=i+1;j<=tmax;j++) { 	
				 //dopo aver generato il clone faccio modifica al ts ->genero tutti gli swap
				SortedMap<Integer,List<Exam>> neighbor=Clone_solution();
				double obj_neighbor=diff;
				if(!(neighbor.get(i).isEmpty() && neighbor.get(j).isEmpty())) {
				first=neighbor.remove(i);
	
				List<Exam> second=neighbor.remove(j);
				
				Iterator<Exam> iter_second=second.iterator();
				while(iter_second.hasNext()) {
					Exam e=iter_second.next();
					for(int k=j-5;k<=j+5 && k<=tmax ;k++) {
						if(k>0 &&k!=i &&k!=j) {
							List<Exam> l_int=neighbor.get(k);
							Iterator<Exam> iter_int=l_int.iterator();
							while(iter_int.hasNext()) {
								Exam e_int=iter_int.next();
														//i
								int distance=Math.abs(e.getTime_slot()-e_int.getTime_slot());
								
								int row=e.getId()-1;
								int col=e_int.getId()-1;
								double po=Math.pow(2,5-distance);
								
								obj_neighbor-=po*n[row][col]; //- devo sottrarre
							}
						}
					}
				}
				
				for(int k=0;k<first.size();k++)  //coerenza tra mappa e timeslot in ogni esame
			    	first.get(k).setTime_slot(j);
			
				for(int k=0;k<second.size();k++)      
					second.get(k).setTime_slot(i);
				neighbor.put(i,second);
				neighbor.put(j, first);
				
				//adesso sommmo dopo aver scambiato le liste
				first=neighbor.get(i);
				 iter=first.iterator();
				while(iter.hasNext()) {//calcolo obj del neighbor da quella della soluzione corrente
					Exam e=iter.next();
					for(int k=i-5;k<=i+5 && k<=tmax ;k++) {
						if(k>0 && k!=i) {
							List<Exam> l_int=neighbor.get(k);
							Iterator<Exam> iter_int=l_int.iterator();
							while(iter_int.hasNext()) {
								Exam e_int=iter_int.next();
														//i
								int distance=Math.abs(e.getTime_slot()-e_int.getTime_slot());
								
								int row=e.getId()-1;
								int col=e_int.getId()-1;
								double po=Math.pow(2,5-distance);
								
								obj_neighbor+=po*n[row][col]; //sommo
							}
						}
					}
				}
				
				second=neighbor.get(j);
				iter_second=second.iterator();
				while(iter_second.hasNext()) {
					Exam e=iter_second.next();
					for(int k=j-5;k<=j+5 && k<=tmax ;k++) {
						if(k>0 &&k!=i&&k!=j) {
							List<Exam> l_int=neighbor.get(k);
							Iterator<Exam> iter_int=l_int.iterator();
							while(iter_int.hasNext()) {
								Exam e_int=iter_int.next();
														//i
								int distance=Math.abs(e.getTime_slot()-e_int.getTime_slot());
								
								int row=e.getId()-1;
								int col=e_int.getId()-1;
								double po=Math.pow(2,5-distance);
								
								obj_neighbor+=po*n[row][col]; //sommo
							}
						}
					}
				}
				obj_neighbor=obj_neighbor/S;
				Move move;
				if(second.size()!=0)
				   move=new Move(second.get(0),j,i);
				else
				   move=new Move(first.get(0),i,j);
				move.setScambiato(first.get(0));
			    Neighbor n=new Neighbor(neighbor,move,obj_neighbor);
			    res.add(n);
			
			  
			 }
		}
		}
		
		return res;
	}
	
	private List<Neighbor> GenerateNeighborhoodExams(boolean reduce, int factor){
		List<Neighbor> res=new ArrayList<>();
		
		for(int i=1;i<=tmax;i++) {
			List<Exam> esami=current_solution.get(i);
			Iterator<Exam> iter = esami.iterator();
			while(iter.hasNext()) {
				Exam e = iter.next();
				int index=esami.indexOf(e);
				List<Integer> timeSlotsDisponibili = new ArrayList<>();
				for(int k=1; k<=tmax; k++){
					if(checkTimeslot(k, e.getId(), current_solution)==0 && k!=i){
						timeSlotsDisponibili.add(k);
					}
				}
				if(!timeSlotsDisponibili.isEmpty()) {
						int r=new Random().nextInt(factor);//25
						if(r==0 || reduce==false) {
					
					SortedMap<Integer,List<Exam>> vicino1=Clone_solution();
					double diff=current_obj*S;
					Exam e_int=vicino1.get(i).get(index);
					for(int k=i-5;k<=i+5 &&k<=tmax;k++) {
						if(k>0 && k!=i) {
							List<Exam> l_int=vicino1.get(k);
							Iterator<Exam> iter_int=l_int.iterator();
							while(iter_int.hasNext()) {
								Exam e2=iter_int.next();
														//i
								int distance=Math.abs(e_int.getTime_slot()-e2.getTime_slot());
								
								int row=e_int.getId()-1;
								int col=e2.getId()-1;
								double po=Math.pow(2,5-distance);
								
								diff-=po*n[row][col]; //- devo sottrarre
							}
						}
					}
					
					Iterator<Integer> iter2 = timeSlotsDisponibili.iterator();
					while(iter2.hasNext()) {
						int t=iter2.next();
						SortedMap<Integer,List<Exam>> vicino=Clone_solution();
						e_int=vicino.get(i).get(index);
						double obj_neighbor=diff;
						vicino.get(t).add(e_int);
						e_int.setTime_slot(t);
						vicino.get(i).remove(index);
						for(int k=t-5;k<=t+5 &&k<=tmax;k++) {
							if(k>0 && k!=t) {
								List<Exam> l_int=vicino.get(k);
								Iterator<Exam> iter_int=l_int.iterator();
								while(iter_int.hasNext()) {
									Exam e2=iter_int.next();
															//i
									int distance=Math.abs(e_int.getTime_slot()-e2.getTime_slot());
									
									int row=e_int.getId()-1;
									int col=e2.getId()-1;
									double po=Math.pow(2,5-distance);
									
									obj_neighbor+=po*n[row][col]; //- devo sottrarre
								}
							}
						}
						
						obj_neighbor=obj_neighbor/S;
						Neighbor nei=new Neighbor(vicino, new Move(e_int, t, i),obj_neighbor);
						res.add(nei);
						
					}
						}
				 }
				
			}
		}
		
		return res;
	}
	
	
	private SortedMap<Integer, List<Exam>> Clone_solution() {

		SortedMap<Integer,List<Exam>> clone=initializeInitialSolution();
		for(int i=1;i<=tmax;i++) {
			List<Exam> l=current_solution.get(i);
			List<Exam> l_new=clone.get(i);
			l.stream().forEach(e->{
				Exam e_new=(Exam)e.clone();
				l_new.add(e_new);
			});
		}
		
		return clone;
	}

	private Neighbor best_In_Neighborhood(Move[] tabu, List<Neighbor> neighborhood, int ts) {
		Neighbor best=null;
		double best_obj_in_neighborhood=-1;
		Iterator<Neighbor> iter=neighborhood.iterator();
		while(iter.hasNext()) {
			Neighbor neighbor=iter.next();
			double obj=neighbor.getObj();
			if(obj<best_obj_in_neighborhood|| best_obj_in_neighborhood==-1) {
				Move m=neighbor.getM();
				boolean bad=false;
				for(int i=0; i<lastExams.length; i++)
					if(lastExams[i]!= null && lastExams[i].getId()==m.getE().getId())
						bad=true;
				if(bad==false) {
					for(int i=0;i<tabu.length && tabu[i]!=null;i++) {																																								//modified
						if(tabu[i]!=null && (ts==0 && tabu[i].getE().getId()==m.getE().getId() && ((tabu[i].getTo()==m.getFrom() && tabu[i].getFrom()==m.getTo()) || (tabu[i].getTo()==m.getTo() && tabu[i].getFrom()==m.getFrom()) || tabu[i].getTo()==m.getFrom())
								|| (ts==1 && tabu[i].getScambiato()!=null && (tabu[i].getE().getId()==m.getE().getId() || m.getE().getId()==tabu[i].getScambiato().getId()) && ((tabu[i].getTo()==m.getFrom() && tabu[i].getFrom()==m.getTo()) || (tabu[i].getTo()==m.getTo() && tabu[i].getFrom()==m.getFrom())))))
							if(obj<best_obj) { //aspiration
								best=neighbor;
								best_obj_in_neighborhood=obj;
								//la mossa è già in tabu list e non la tocco
							}
							else
								bad=true;
	
					}
				}
				if(!bad) {
					best_obj_in_neighborhood=obj;
					best=neighbor;
				}
			}	
		}
		
		//if(best!=null)
		//	System.out.println(" --> Move: exam " + best.getM().getE().getId() +" moves :" + best.getM().getTo()+" -->" + best.getM().getFrom());

		return best;
	}

	
	public void Solve(long time, long limit) {

		current_solution=Generate_Initial_Solution();
		best_solution=current_solution;
		//System.out.println(current_solution);
		Print();
		current_obj=Evaluate(current_solution);
		best_obj=current_obj;
		//System.out.println("obj di partenza:"+current_obj);
	//Start Tabu search
		limit=limit*1000;
		iteration=0;
		boolean half=false,reduce=false,quart=false;
		int relax=0;
		int factor=1;
		int countExams=0;  //non serve
		int countTimeslots=0; //non serve
		int count_swap=0;
		int cattivo=0;  //contatore che conta gli swap di esami effettuati che non portano a una soluzione migliore. Quando si trova una soluzine migliore viene azzerato.
		int count_cattivo=0;  //contatore che serve per tornare allo swap di esami (cioè qundo raggiunge timeslotSwaps) dopo che è entrato in gioco lo swap di timeslot.
		int badIterationsLimit;  //se dopo questo numero di swap di esami, non si è trovata una soluzione migliore, si passa allo swap di timeslots
		int timeslotSwaps=tmax/2;  //numero di iterazioni fatte con lo swap d timeslot dopo che badIterationsLimit è raggiunto
							//3
		int counterExams=0;
	
		if(E>200)
		{	
			if(E<600)				
			{	
				
				if(E<400) //ins 04
				{
					relax=2*DIM_H;
					badIterationsLimit=3*DIM_H;
				}
					//ins05
				else				//*2
				 {
					factor=24;
					reduce=true;
					relax=DIM_H/2;//2
					badIterationsLimit=DIM_H; //1
			 }
			}
			else	
			{			//per la 6
				factor=30;//30
				reduce=true;
				relax=DIM_H/4;//4
				badIterationsLimit=DIM_H/2;//2
			}
		}//setto in base al numero di esami
		else //if(E >100)
		{
			relax=2*DIM_H;
			badIterationsLimit=3*DIM_H;
		}

		while(System.currentTimeMillis()-time<limit) {
			neighborhoodExams=GenerateNeighborhoodExams(reduce,factor);
			Neighbor bestEx=best_In_Neighborhood(tabuExams, neighborhoodExams, 0);
			Neighbor bestTs = new Neighbor(null,null,-1);
			if(iteration<30 || cattivo>badIterationsLimit) {
				neighborhoodTimeslots=Generate_Neighborhood();
				if(iteration>30&&count_cattivo==0) {
					tabuTimeslots=new Move[4];
					lastExams = new Exam[DIM_H];
					counterExams=0;
					countTimeslots=0;
				}
				bestTs=best_In_Neighborhood(tabuTimeslots, neighborhoodTimeslots, 1);
			}
			
			Neighbor best;
			double diff=(current_obj-best_obj)/best_obj;//modified ==2*DIM_H no &&
			if( (cattivo>=relax && lastExams[DIM_H-1]!=null)|| diff>r) {
				tabuExams=new Move[DIM_TS];
				lastExams = new Exam[DIM_H];
				counterExams=0;
				count_swap=0;
				System.out.println("relaxing --> " +cattivo + " == " + relax + " diff "+ diff + " --> " + r);
			}
			
			if(bestEx==null || bestTs==null)
				{
				tabuExams=new Move[DIM_TS];
				lastExams = new Exam[DIM_H];
				counterExams=0;
				System.out.println("null");
				}
			else {
			if((bestEx.getObj()>bestTs.getObj() && iteration<30 && bestTs.getObj()!=-1)|| cattivo>badIterationsLimit) {  // scambio di timeslot viene fatto nelle prime 30 iterazioni a scelta con lo swap di esami (scelgo il migliore dei due), oppure per sbloccare la situazione
				best=bestTs;
				int index=countTimeslots%tabuTimeslots.length;
				tabuTimeslots[index]=best.getM();
				countTimeslots++;
				if(cattivo>badIterationsLimit)
					count_cattivo++;
				if(count_cattivo==timeslotSwaps){
					cattivo=0;
					count_cattivo=0;
					tabuTimeslots=new Move[4];
					tabuExams=new Move[DIM_TS];
					count_swap++;
					if(count_swap==8) //aggiunta
					{
							lastExams = new Exam[DIM_H]; //modified
							count_swap=0;
							System.out.println(" count_swap>5");
					}
					System.out.println("swapped");
				}
				
			}
			else {
				best = bestEx;
				best.getM().getE().incMovements();
				int index=countExams%tabuExams.length;
				tabuExams[index]=best.getM();
				if(counterExams<lastExams.length) {
					if(best.getM().getE().getMovements()>2) {
						lastExams[counterExams]=best.getM().getE();
						counterExams++;
					}
				}
				else {
					boolean ok=false;
					for(int j=0; j<lastExams.length && ok==false; j++) {
					//	if(lastExams[j]==null)
						//	System.out.println("cazzo");
						if(lastExams[j]!=null && lastExams[j].getMovements()<best.getM().getE().getMovements()) {
							lastExams[j]=best.getM().getE();
							ok=true;
						}
					}
				}
				countExams++;
			}

			current_solution=best.getSolution();
			current_obj=best.getObj();
			if(current_obj<best_obj)
			{	
				System.out.println(current_obj + " \t" + best.getM() + " " + "\t iteration: " +iteration);
				best_obj=current_obj;
				best_solution=current_solution;
				cattivo=0;
				count_swap=0;
			}
			else
				cattivo++;
			                                         //3
			if(System.currentTimeMillis()-time>limit/4 && half==false)
			{
				r=0.1;
				System.out.println("--1/3--");
				half=true;
				if(reduce) //la 6 si
				{
					current_solution=best_solution;//intensifico partendo da best
					current_obj=best_obj;
				//reduce=false;
					factor=5;         // in ins5 cancellato
					count_swap=0;
					cattivo=0;
					tabuExams=new Move[DIM_TS];
					lastExams = new Exam[DIM_H];
				}
			}
			
			if(System.currentTimeMillis()-time>limit/2 && quart==false)
			{
				r=0.1;
				System.out.println("--2/3--");
				quart=true;
				if(reduce) //la 6 si
				{
					current_solution=best_solution;//intensifico partendo da best
					current_obj=best_obj;
					reduce=false;
					count_swap=0;				
					cattivo=0;
					tabuExams=new Move[DIM_TS];
					lastExams = new Exam[DIM_H];
				}
			}
			iteration++;
		}
		}
		Print();
		
		System.out.println(best_solution);
		System.out.println("obj di arrivo: "+best_obj);
		System.out.println(Evaluate(best_solution));
		System.out.println(("total time: "+(System.currentTimeMillis()-time))+ " iterations --> " + iteration);
		
		System.gc();
		return  ;
	}
}

