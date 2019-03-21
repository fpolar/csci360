package models;

public class Hero {
	double power;
	double mastery_player;
	double mastery_opponent;
	int membership;
	
	public Hero(double power, double mastery_player, double mastery_opponent, int membership) {
		this.power = power;
		this.mastery_player = mastery_player;
		this.mastery_opponent = mastery_opponent;
		this.membership = membership;
	}
	
	public String toString() {
		return "\n\tpower = "+power+
			   "\n\tmaster_player = "+mastery_player+
			   "\n\tmastery_opponent = "+mastery_opponent+
			   "\n\tmemership = "+membership;
	}
}
