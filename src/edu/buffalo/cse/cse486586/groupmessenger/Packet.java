package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.Serializable;

public class Packet implements Serializable{
	//int msgID;
	private static final long serialVersionUID = 123456543L;
	int sendersPort;
	String message;
	int msgNo;

	Packet(int sndrsPort, String msg, int msgNum){
		//msgID=id;
		this.sendersPort=sndrsPort;
		this.message=msg;
		this.msgNo=msgNum;
	}

}