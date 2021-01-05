package net.gondr.domain;

public class ScoreVO {
	private int id;
	private String name;
	private int score;
	
	@Override
	public String toString() {
		return name + "(" + score +"점)";
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	
	
	
}
