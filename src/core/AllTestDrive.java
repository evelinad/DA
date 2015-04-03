package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

import node.Node;

public class AllTestDrive {
	public static final int peers = 3;
	public static ArrayList<Thread> CBCASTThreads;
	public static ArrayList<Thread> ABCASTThreads;
	public static ArrayList<Thread> threePhaseThreads;
	public static ArrayList<String> CBCASTAnswers;
	public static ArrayList<String> ABCASTAnswers;
	public static ArrayList<String> threePhaseAnswers;
	public static ArrayList<Node> CBCASTNodes;
	public static ArrayList<Node> ABCASTNodes;
	public static ArrayList<Node> threePhaseNodes;
    public static final long  MAX_MSG = 176 * peers;
	
	static void startCBCAST() {
		Integer[] port = {1234, 1235, 1236};
		String[] IP = {"localhost", "localhost", "localhost"};
		ArrayList<String> ipArray = new ArrayList<String>(Arrays.asList(IP));
		ArrayList<Integer> portArray = new ArrayList<Integer>(Arrays.asList(port));
		
		CBCASTThreads = new ArrayList<Thread>();
		CBCASTNodes = new ArrayList<Node>();
		
		for (int i = 0; i < peers; i++) {
			Node node = new Node(i, peers, ipArray, portArray, "cbcast");
			CBCASTNodes.add(node);
			CBCASTThreads.add(new Thread(node));
		}
		for (Thread thread: CBCASTThreads) {
			thread.start();
		}		
	}
	
	static void startABCAST() {
		Integer[] port = {1237, 1238, 1239};
		String[] IP = {"localhost", "localhost", "localhost"};
		ArrayList<String> ipArray = new ArrayList<String>(Arrays.asList(IP));
		ArrayList<Integer> portArray = new ArrayList<Integer>(Arrays.asList(port));
		
		ABCASTThreads = new ArrayList<Thread>();
		ABCASTNodes = new ArrayList<Node>();
		
		for (int i = 0; i < peers; i++) {
			Node node = new Node(i, peers, ipArray, portArray, "abcast 0");
			ABCASTNodes.add(node);
			ABCASTThreads.add(new Thread(node));
		}
		for (Thread thread: ABCASTThreads) {
			thread.start();
		}	
	}
	
	static void startThreephase() {
		Integer[] port = {1240, 1241, 1242};
		String[] IP = {"localhost", "localhost", "localhost"};
		ArrayList<String> ipArray = new ArrayList<String>(Arrays.asList(IP));
		ArrayList<Integer> portArray = new ArrayList<Integer>(Arrays.asList(port));
		
		threePhaseThreads = new ArrayList<Thread>();
		threePhaseNodes = new ArrayList<Node>();
		
		for (int i = 0; i < peers; i++) {
			Node node = new Node(i, peers, ipArray, portArray, "threephase");
			threePhaseNodes.add(node);
			threePhaseThreads.add(new Thread(node));
		}
		for (Thread thread: threePhaseThreads) {
			thread.start();
		}
	}

	static void getCBCASTAnswers() {
		CBCASTAnswers = new ArrayList<String>();
		for (int i = peers - 1; i >= 0; i--) {
			CBCASTAnswers.add(CBCASTNodes.get(i).getText());
		}
	}
	
	static void getABCASTAnswers() {
		ABCASTAnswers = new ArrayList<String>();
		for (int i = peers - 1; i >= 0; i--) {
			ABCASTAnswers.add(ABCASTNodes.get(i).getText());
		}
	}
	
	static void getThreePhaseAnswers() {
		threePhaseAnswers = new ArrayList<String>();
		for (int i = peers - 1; i >= 0; i--) {
			threePhaseAnswers.add(threePhaseNodes.get(i).getText());
		}
	}

	static void stopCBCAST() {
		for (int i = peers - 1; i >= 0; i--) {
			try {
				CBCASTNodes.get(i).finish();
				CBCASTThreads.get(i).join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	static void stopABCAST() {
		for (int i = peers - 1; i >= 0; i--) {
			try {
				ABCASTNodes.get(i).finish();
				ABCASTThreads.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	static void stopThreephase() {
		for (int i = peers - 1; i >= 0; i--) {
			try {
				threePhaseNodes.get(i).finish();
				threePhaseThreads.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	static boolean CBCASTFinished() {
		for (Node node: CBCASTNodes) {
			System.out.println(node.deliveredMessages());
			if (node.deliveredMessages() < MAX_MSG) {
				return false;
			}
		}
		return true;
	}
	
	static boolean ABCASTFinished() {
		for (Node node: ABCASTNodes) {
			System.out.println(node.deliveredMessages());
			if (node.deliveredMessages() < MAX_MSG) {
				return false;
			}
		}
		return true;
	}
	
	static boolean threePhaseFinished() {
		for (Node node: threePhaseNodes) {
			System.out.println(node.deliveredMessages());
			if (node.deliveredMessages() < MAX_MSG) {
				return false;
			}
		}
		return true;
	}
	
	static boolean checkAnswers(ArrayList<String> answers) {
		for (int i = 1; i< answers.size(); i++) {
			if (answers.get(0).equals(answers.get(i)) == false) {
				return false;
			}
		}
		
		return true;
	}
	
	public static void main(String[] args) {
	    try {
	    	startABCAST();
	    	startCBCAST();
	    	startThreephase();
	    	
			while ((CBCASTFinished() && ABCASTFinished() && threePhaseFinished())== false) {
				Thread.sleep(1000);
			}
			
			Thread.sleep(3000);
			
			getABCASTAnswers();
			getCBCASTAnswers();
			getThreePhaseAnswers();
			
			BufferedWriter bw = new BufferedWriter(new FileWriter("/home/evelina/workspace/DA/src/answers", true));
			bw.write("ABCAST answers: " + ((checkAnswers(ABCASTAnswers) == false) ? "differ\n" : "are the same\n"));
			bw.write(ABCASTAnswers.toString() + "\n");
			bw.write("CBCAST answers: " + ((checkAnswers(CBCASTAnswers) == false) ? "differ\n" : "are the same\n"));
			bw.write(CBCASTAnswers.toString() + "\n");
			bw.write("ThreePhase answers: " + ((checkAnswers(threePhaseAnswers) == false) ? "differ\n" : "are the same\n"));
			bw.write(threePhaseAnswers.toString() + "\n");
			System.out.println(ABCASTAnswers.toString());
			System.out.println(CBCASTAnswers.toString());
			System.out.println(threePhaseAnswers.toString());
			bw.close();
			
			stopABCAST();
			stopCBCAST();
			stopThreephase();
			
	    } catch (Exception e) {
			e.printStackTrace();
		}
	}

}
