import java.util.*;


public class PagerankData {
	private Integer k = 0;
	private Object[] v = new Object[50];
	
	public PagerankData(Integer k, ArrayList<Integer> v) {
		this.k = k;
		this.v = v.toArray();
	}
}
