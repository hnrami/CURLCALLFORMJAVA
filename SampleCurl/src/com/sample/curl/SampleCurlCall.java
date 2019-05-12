package com.sample.curl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class SampleCurlCall {

	static FileOutputStream fos;
	 static CountDownLatch latch ;
	 static ExecutorService executor;
	 static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
//	 static String inputFile= "/Users/harry/input.csv";
//	 static String outputFile="/Users/harry/output.csv";
	public static void main(String[] args) {
		try {
			  String inputFile= args[0];
			  String outputFile=args[1];
			  int numberthread=Integer.parseInt(args[2]);
			  List<String> resultCurl = processInputFile(inputFile);
			  System.out.println("size of CURLS statements is "+resultCurl.size());
			Scanner myObj = new Scanner(System.in); 
			System.out.println("are you sure you want start Thread? please enter your answer yes/no/y/n");

			String result = myObj.nextLine(); 
			System.out.println("your Answer is : " + result);
			if(result.equalsIgnoreCase("yes") || result.equalsIgnoreCase("y") ) {
				int thread=0;
				//Enter Read File where we read CURL STATMENT
				
				fos = new FileOutputStream(outputFile, true);
				executor = Executors.newFixedThreadPool(numberthread);
				latch= new CountDownLatch(resultCurl.size());
				System.out.println("we start Thread time is ::"+formatter.format(new Date()));  
				for (String curlURL : resultCurl) {
					executor.submit(() -> {
						try {
							callCurl(curlURL);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});

				}
			}else {
				System.out.println("we exist for program");
				fos.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				latch.await();
				fos.close();
				
				System.out.println("complate CSV Write Time is "+formatter.format(new Date()));
				 executor.shutdownNow();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static void callCurl(String url) throws IOException {
//		String command = "curl -X GET http://localhost:9090/api/test";
		Process process = Runtime.getRuntime().exec(url);
		BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		while ((line = r.readLine()) != null) {
			fos.write(line.getBytes());
		}
		r.close();
		latch.countDown();
	}

	private static List<String> processInputFile(String inputFilePath) throws IOException {
		List<String> inputList = new ArrayList<String>();

		File inputF = new File(inputFilePath);
		InputStream inputFS = new FileInputStream(inputF);
		BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
		inputList = br.lines().collect(Collectors.toList());
		br.close();

		return inputList;
	}

}
