package models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

public class DraftState {
	double advantage;
	double minimax_val;
	boolean player_turn;
	//HashMap<Integer, Integer> hero_states;
	HashMap<Integer, Hero> player_heroes;
	HashMap<Integer, Hero> opponent_heroes;
	SortedMap<Double, DraftState> successors;

	//The id of the most recent hero drafted
	int last_draft = -1;
	
	public DraftState(HashMap<Integer, Hero> heroes) {

		player_turn = true;
		player_heroes = new HashMap<Integer, Hero>();
		opponent_heroes = new HashMap<Integer, Hero>();

	    Iterator<Entry<Integer, Hero>> it = heroes.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Integer, Hero> pair = (Map.Entry<Integer, Hero>)it.next();
//	    	System.out.println("Draft State Constructor: "+pair.getKey()+" - Mem: "+pair.getValue().membership);
	        if(pair.getValue().membership == 1) {
	        	player_heroes.put(pair.getKey(), pair.getValue());
	        }
	        if(pair.getValue().membership == 2) {
	        	opponent_heroes.put(pair.getKey(), pair.getValue());
	        }
//		    System.out.println(player_heroes.size());
//		    System.out.println(opponent_heroes.size());
	        //it.remove();
	    }
	}

	// constructor for cloning
	public DraftState() {
		player_heroes = new HashMap<Integer, Hero>();
		opponent_heroes = new HashMap<Integer, Hero>();
		successors = new TreeMap<Double, DraftState>();
	}

	public String toString() {
		String out = "Draft State: advantage = "+advantage+", my turn = "+player_turn;
		out+= "\n\tPlayer Heroes: \n";
	    Iterator<Entry<Integer, Hero>> it = player_heroes.entrySet().iterator();
//	    System.out.println(player_heroes.size());
	    while (it.hasNext()) {
	        Map.Entry<Integer, Hero> pair = (Map.Entry<Integer, Hero>)it.next();
	        out+="\n\tHero "+pair.getKey()+" "+pair.getValue();
	    }
		out+= "\n\n\tOpponent Heroes: \n";
		Iterator<Entry<Integer, Hero>> it2 = opponent_heroes.entrySet().iterator();
//	    System.out.println(opponent_heroes.size());
	    while (it2.hasNext()) {
	        Map.Entry<Integer, Hero> pair = (Map.Entry<Integer, Hero>)it2.next();
	        out+="\n\tHero "+pair.getKey()+" "+pair.getValue();
	    }
	    return out;
	}

	public DraftState cloneState() {
		DraftState tempDraftState = new DraftState();
		tempDraftState.advantage = advantage;
		tempDraftState.player_turn = player_turn;
		tempDraftState.player_heroes.putAll(player_heroes);
		tempDraftState.opponent_heroes.putAll(opponent_heroes);
//	    System.out.println("Cloning:\n"+tempDraftState.player_heroes.size());
//	    System.out.println(tempDraftState.opponent_heroes.size());
		return tempDraftState;
	}
	
	//may be necessary later
	public boolean equals(DraftState d) {
		return false;
	}
	
	public void calcAdvantage() {
		double player_advantage = 0;
		double opponent_advantage = 0;

		boolean[] synergy = new boolean[5];
	    Iterator<Entry<Integer, Hero>> it = player_heroes.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Integer, Hero> pair = (Map.Entry<Integer, Hero>)it.next();
	        int id = pair.getKey();
	        Hero hero = pair.getValue();
	        player_advantage += hero.power*hero.mastery_player;
	        synergy[id%10-1] = true;
	        //it.remove();
	    }
	    if(synergy[0]&&synergy[1]&&synergy[2]&&synergy[3]&&synergy[4]) {
	    	player_advantage+=120;
	    }

		synergy = new boolean[5];
	    it = opponent_heroes.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Integer, Hero> pair = (Map.Entry<Integer, Hero>)it.next();
	        int id = pair.getKey();
	        Hero hero = pair.getValue();
	        opponent_advantage += hero.power*hero.mastery_player;
	        synergy[id%10-1] = true;
	        //it.remove();
	    }
	    if(synergy[0]&&synergy[1]&&synergy[2]&&synergy[3]&&synergy[4]) {
	    	opponent_advantage+=120;
	    }
	    
	    advantage = player_advantage - opponent_advantage;
	}
	
	//TODO: may want to store synergy as a field to avoid calcAverage at the end of every draft round
	public boolean draftHero(int h, Hero hero) {
		if(player_turn) {
			if(player_heroes.size()==5 || player_heroes.containsKey(h)) {
				return false;
			}
			player_heroes.put(h, hero);
		}else {
			if(opponent_heroes.size()==5 || opponent_heroes.containsKey(h)) {
				return false;
			}
			opponent_heroes.put(h, hero);
		}
//	    System.out.println("Cloning:\n"+tempDraftState.player_heroes.size());
//	    System.out.println(tempDraftState.opponent_heroes.size());
		player_turn = !player_turn;
		last_draft = h;
		calcAdvantage();
		return true;
	}
	
	public void createSuccessors(HashMap<Integer, Hero> heroes) {
		
		successors = new TreeMap<Double, DraftState>();

	    Iterator<Entry<Integer, Hero>> it = heroes.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Integer, Hero> pair = (Map.Entry<Integer, Hero>)it.next();
	        int id = pair.getKey();
	        Hero hero = pair.getValue();
	        
        	DraftState tempDraftState = cloneState();
        	if(tempDraftState.draftHero(id, hero)) {
        		tempDraftState.setLast_draft(id);
        		successors.put(tempDraftState.advantage, tempDraftState);
        	}
	        //it.remove();
	    }
	}


    public boolean draftOver() {
    	if (player_heroes.size() == 5 && opponent_heroes.size() == 5) {
    		return true;
    	}
    	return false;
    }
       
	public double getAdvantage() {
		return advantage;
	}


	public void setAdvantage(double advantage) {
		this.advantage = advantage;
	}


	public boolean isPlayer_turn() {
		return player_turn;
	}


	public void setPlayer_turn(boolean player_turn) {
		this.player_turn = player_turn;
	}


	public HashMap<Integer, Hero> getPlayer_heroes() {
		return player_heroes;
	}


	public void setPlayer_heroes(HashMap<Integer, Hero> player_heroes) {
		this.player_heroes = player_heroes;
	}


	public HashMap<Integer, Hero> getOpponent_heroes() {
		return opponent_heroes;
	}


	public void setOpponent_heroes(HashMap<Integer, Hero> opponent_heroes) {
		this.opponent_heroes = opponent_heroes;
	}


	public SortedMap<Double, DraftState> getSuccessors() {
		return successors;
	}


	public void setSuccessors(SortedMap<Double, DraftState> successors) {
		this.successors = successors;
	}

	public int getLast_draft() {
		return last_draft;
	}

	public void setLast_draft(int last_draft) {
		this.last_draft = last_draft;
	}

	public double getMinimax_val() {
		return minimax_val;
	}

	public void setMinimax_val(double minimax_val) {
		this.minimax_val = minimax_val;
	}
	
}
