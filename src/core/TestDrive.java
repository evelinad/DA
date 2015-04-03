package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import node.Node;

public class TestDrive {

	public static void main(String[] args) {
	    try {
			BufferedReader br = new BufferedReader(new FileReader("/home/evelina/workspace/DA/src/config"));
			int peers = Integer.parseInt(br.readLine());
			String protocol =  br.readLine();
			ArrayList<String> ipArray = new ArrayList<String>();
			ArrayList<Integer> portArray = new ArrayList<Integer>();
			
			for (int i = 0; i < peers; i++) {
				String[] tokens = br.readLine().split(" ");
				String IP = tokens[0];
				int port = Integer.parseInt(tokens[1]);
				ipArray.add(IP);
				portArray.add(port);
			}
			
			ArrayList<Thread> nodeThreads = new ArrayList<Thread>();
			ArrayList<Node> nodes = new ArrayList<Node>();
			for (int i = 0; i < peers; i++) {
				Node node = new Node(i, peers, ipArray, portArray, protocol);
				nodes.add(node);
				nodeThreads.add(new Thread(node));
			}
			for (Thread thread: nodeThreads) {
				thread.start();
			}
			
			boolean finished = false;
			while (finished == false) {
				finished = true;
				for (Node node: nodes) {
					System.out.println(node.deliveredMessages());
					if (node.deliveredMessages() < 176 * 4) {
						finished = false;
					}
				}
				
				Thread.sleep(1000);
			}
			
			Thread.sleep(3000);
			
			ArrayList<String> answers = new ArrayList<String>();
			for (int i = peers - 1; i >= 0; i--) {
				answers.add(nodes.get(i).getText());
				//nodes.get(i).finish();
				//nodeThreads.get(i).join();
			}
			
			BufferedWriter bw = new BufferedWriter(new FileWriter("/home/evelina/workspace/DA/src/answers", true));
			
			for (int i = 1; i< answers.size(); i++) {
				if (answers.get(0).equals(answers.get(i)) == false) {
					bw.write("Answers differ\n");
				} else {
					bw.write("Answers are the same\n");
				}
			}
			
			System.out.println(answers.toString());
			bw.write(answers.toString());
			bw.write("\n");
			bw.close();
			br.close();
	    	
			for (int i = peers - 1; i >= 0; i--) {
				nodes.get(i).finish();
				nodeThreads.get(i).join();
			}
			
	    } catch (Exception e) {
			e.printStackTrace();
		}
	}

}
