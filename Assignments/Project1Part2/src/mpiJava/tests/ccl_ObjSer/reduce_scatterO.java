import mpi.*;
import java.io.*;
class complexNum implements Serializable{
  float realPart;
  float imaginPart;
}

class complexAdd extends User_function{
  public void Call(Object invec, int inoffset, Object outvec, int outoffset,
                   int count, Datatype datatype){
    Object [] in_array = (Object[])invec;
    Object [] out_array = (Object[])outvec;

    for (int i = 0; i < count; i++){
      complexNum ocd = (complexNum)out_array[outoffset + i];
      complexNum icd = (complexNum)in_array[inoffset + i];

      ocd.realPart += icd.realPart;
      ocd.imaginPart += icd.imaginPart;
    }
  }
}


class reduce_scatterO {
  static public void main(String[] args) throws MPIException {
    final int MAXLEN = 100;
 
    complexNum out[] = new complexNum[900];
    complexNum in[]  = new complexNum[900];

    int i,j,k;
    int myself,tasks;
    int recvcounts[] = new int[128];
    boolean bool=false;

    MPI.Init(args);
    myself = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();
 
    for(j=MAXLEN*tasks;j<=MAXLEN*tasks;j*=10)  {
      for(i=0;i<tasks;i++)  recvcounts[i] = j;
      for(i=0;i<j*tasks;i++){
        in[i] = new complexNum();
        out[i] = new complexNum();
        out[i].realPart = i;
        out[i].imaginPart = i;
      }

      complexAdd cadd = new complexAdd();
      Op op = new Op(cadd, bool);
      MPI.COMM_WORLD.Reduce_scatter(out,0,in,0,recvcounts,MPI.OBJECT,op);

      for(k=0;k<j;k++) {
	if(in[k].realPart != tasks*(myself*j+k)) {  
	  System.out.println
	    ("bad answer ("+in[k].realPart+") at index "+k+" of "+j+
	     "(should be "+tasks*(myself*j+k)+")"); 
	  break; 
	}
      }
    }

    MPI.COMM_WORLD.Barrier();
    if(myself == 0)  System.out.println("Reduce_scatter TEST COMPLETE\n");
    MPI.Finalize();
  }
}
