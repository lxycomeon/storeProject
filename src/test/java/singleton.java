/**
 * Created with IntelliJ IDEA By lxy on 2018/10/22
 * 单例模式
 * 利用静态内部类实现
 * 应用场景：数据库连接池，多线程的线程池，Windows的任务管理器等都是典型的单例模式
 */
public class singleton {

	private singleton(){}
	//--------------实现方式1------------------
	private static class T{	//静态内部类在使用的时候才会加载，且只加载一次
		//实现了一个延迟加载的单例模式
		private static singleton t = new singleton();
	}

	public static singleton getInstance1(){
		return T.t;
	}

	//-------------实现方式2--------------------
	//线程安全的双重检查加锁
	private static volatile singleton instance = null;
	//使用volatile修饰instance，是为了防止指令重排序。因为new singleton();这个语句不是原子操作。
	//可以分为三条JVM指令：1.memory= allocate（）分配内存；2.初始化对象，3.设置instance指向刚才分配内存的地址。若允许指令重排序，则3和2可能反过来的顺序执行。
	//这样若线程A执行完3后异常退出了，此时对象还没有被初始化。但是其他线程检测到instance不为null就将其返回使用将会导致错误。这里就是volatile关键字的作用

	public static singleton getInstance2(){
		if (instance == null){
			synchronized (singleton.class){
				if (instance == null){
					instance = new singleton();
				}
			}
		}
		return instance;
	}

	//-------------实现方式3----------------------
	//线程安全的懒加载
	private static singleton resource;
	public synchronized static singleton getInstance3(){
		if (resource == null)
			resource = new singleton();
		return resource;
	}

}
