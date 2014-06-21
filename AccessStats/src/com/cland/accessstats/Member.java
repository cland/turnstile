package com.cland.accessstats;

public class Member {
	public int idno;
	public String gender;
	public String homeclub;
	public Member(int i, String gender, String homeclub){
		this.idno = i;
		this.gender = gender;
		this.homeclub = homeclub;
	}
	@Override
	public String toString() {
		return "Member [idno=" + idno + ", gender=" + gender + ", homeclub="
				+ homeclub + "]";
	}
	
}
