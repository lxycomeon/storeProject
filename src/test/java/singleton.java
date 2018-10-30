/**
 * Created with IntelliJ IDEA By lxy on 2018/10/22
 */
public class singleton {

	private static singleton unsingleton = new singleton();
	static {
		System.out.println("22");
	}
	private singleton(){}

	public static singleton getInstance(){
		System.out.println("232");
		return unsingleton;
	}

}
