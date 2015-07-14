package extract.analysis;


/**
 * Class that allows you to send pairs of values
 * @author sloates
 *
 * @param <A>
 * @param <B>
 */
public class Pair<A,B> {
	 	private final A a;
	    private final B b;
	    
	    
	    public Pair(A a, B b) {         
	        this.a= a;
	        this.b= b;
	    }

		public A getA() {
			return a;
		}

		public B getB() {
			return b;
		}
}
