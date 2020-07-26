package test;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.SpringApplication;



public class Test {
	public static void main(String[] args) throws InterruptedException {
	//1.
	/**
	 * sleep是线程类(Thread)的方法，导致此线程暂停执行指定时间，给执行机会给其他线程，但是监控状态依然保持，到时后会自动恢复。调用sleep不会释放对象锁。
	 */
//	Thread s=new Thread();
	
//	s.sleep(5);
	/**
	 * 
	 */
//	Object e=new Object();
//	e.wait();
	
	//2.
//	test2();
		
		//3.
//		Point p1=new Point(0,0);
//		Point p2=new Point(0,0);
//		modifyPoint(p1,p2);
//		System.out.println("["+p1.x+","+p1.y+"].["+p2.x+","+p2.y+"]");
		
		
		//4.
//		List list =new ArrayList();
//		list.add("one");
//		list.add("two");
//		list.add("two");
//		list.add("two");
//		
//		deleteElement(list);
		
		
//		String s = UUID.randomUUID().toString();
//		
//		System.out.println(s);
		
		
	Child c=new Child();
	Parent p=(Parent)c;
	p.method();
	p.smethod();
		
	}
	
	
	//3.
	private static void deleteElement(List list){
		
	}
	
	//2.   结果是true,false
	public static void test2() {
		Integer a=10;
		Integer b=10000;
		Integer a1=10;
		Integer b1=10000;
		 //要注意的是字符串不能包含非数字字符，否则会抛出NumberFormatException
		System.out.println((a==a1)+","+(b==b1));
	}
	
	//3.
	private static void modifyPoint(Point p1, Point p2) {
		Point tmpoint=p1;
		p1=p2;
		p2=tmpoint;
		// 将点的位置设为指定位置
		p1.setLocation(5, 5);
		p2=new Point(5,5);
		
		
		
		
	}
	
	
}
