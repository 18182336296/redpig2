package test;

/**
 * 线程安全的懒汉式单例
 * @author Administrator
 * 出现非线程安全问题，是由于多个线程可以同时进入getInstance()方法，那么只需要对该方法进行synchronized的锁同步即可：
 */
public class Singleton {
	
//1.私有静态的方法
	private static Singleton instance=null;
	//2.私有的构照方法
	 private Singleton(){ }
	 //3.自己类里面的方法调自己,这个方法向外部提供服务是public的  synchronized
	 public synchronized static Singleton getInstance() {
		 try {
			 if(instance !=null) {
				 
			 }else {
				 instance=new Singleton();
			 }
			 
		 }catch(Exception  e) {
			 e.printStackTrace();
			 
		 }
		 return instance;
	 }
	 
	 
	
}
