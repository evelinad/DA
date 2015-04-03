package core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import cbcast.CBCASTProtocol;
import threephase.ThreePhaseProtocol;
import network.ConsistencyProtocol;
import node.Node;

public class Main {

	public static void main(String[] args) {
	    try {
			BufferedReader br = new BufferedReader(new FileReader("/home/evelina/workspace/DA/src/config"));
			int peers = Integer.parseInt(br.readLine());
			String protocol =  br.readLine();
			int id = Integer.parseInt(args[0]);
			ArrayList<String> ipArray = new ArrayList<String>();
			ArrayList<Integer> portArray = new ArrayList<Integer>();
			
			for (int i = 0; i < peers; i++) {
				String[] tokens = br.readLine().split(" ");
				String IP = tokens[0];
				int port = Integer.parseInt(tokens[1]);
				ipArray.add(IP);
				portArray.add(port);
			}
			
			new Thread(new Node(id, peers, ipArray, portArray, protocol)).start();
			br.close();
	    } catch (Exception e) {
			e.printStackTrace();
		}
	    

	}

}
