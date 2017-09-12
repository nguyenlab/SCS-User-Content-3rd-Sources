package lab.docsum.misc;

import cern.colt.Arrays;

public class Test {
	public static void main(String[] args) throws InterruptedException {
//		Object[]  arr = {"a","b","c"};
//		ExecutorService es = Executors.newFixedThreadPool(3);
//		for(Object e: arr){
//			es.submit(()->{
//				try {
//					Thread.sleep(3000);
//				} catch (Exception e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				System.out.println(e);
//				
//			});
//		}
//		es.shutdown();
//		es.awaitTermination(1, TimeUnit.MINUTES);
//		
//		Annotation sent = new Annotation("Hello");
//		System.out.println(sent.get(TextAnnotation.class));
//		
//		Iterator<String> texts = Arrays.asList("a","b").iterator();
//		String txt = texts.next();
//		
		String[] cols = "task1_reference 10. Armenians keep memory of genocide alive a century on.txt".split("\\D+");
		System.out.println(Arrays.toString(cols));
	}
}
