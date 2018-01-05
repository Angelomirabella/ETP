import static java.util.Comparator.comparing;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*; 
public class Tester implements Runnable {
private static Neighbor[] neighbors;
private static Neighbor best;
private static String exm;
private static String slo;
private static String stu;
private static String instance;
private static long limit;
private static long time;
private static double min=-1;
private String name;

	public Tester (String name)
	{
	this.name=name;	
	}



	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		;
		  time=System.currentTimeMillis();
		 neighbors=new Neighbor[4];
		//TimeTable t= new TimeTable();
		instance=args[0];
		slo=".\\"+instance+".slo";
		stu= ".\\"+instance+".stu";
		exm=".\\"+instance+".exm";
		limit=Long.parseLong(args[2]);
		Thread t1,t2,t3,t4;
		Runnable r1,r2,r3,r4;
		r1=new Tester("0");
		r2=new Tester("1");
		r3=new Tester("2");
		r4=new Tester("3");
		
		t1= new Thread(r1);
		t2= new Thread(r2);
		t3= new Thread(r3);
		t4= new Thread(r4);
		
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		
		Thread.sleep(limit*1000+2000);
		for(int i=0;i<4;i++)
		{
			if(neighbors[i].getObj()<min|| min==-1)
			{
				best=neighbors[i];
				min=best.getObj();
			}
		System.out.println("obj "+i + " " + neighbors[i].getObj());
		}
		
		
		System.out.println("best obj -->" + best.getObj());
		System.out.println("total time--> " + (System.currentTimeMillis()-time));
		Print(best.getSolution(),instance);
		
	}
	
	public void run()
	{
		TimeTable t=new TimeTable();
		int id = Integer.parseInt(getName());
		t.Initialize(slo, stu, exm);
		neighbors[id]=t.Solve(time,limit);
	}



	public String getName() {
		return name;
	}
	
	private static void Print(SortedMap <Integer,List<Exam>> best, String instance)
	{
		try(PrintWriter out=new PrintWriter(new FileWriter(".\\"+instance+"_OMAAL_group04.sol")))
		{			
			best.values().stream().flatMap(l->l.stream()).sorted(comparing(e->e.getId())).forEach(e->out.format("%d %d\n",e.getId(),e.getTime_slot()));
	 }catch (IOException e) {};
	}
}
