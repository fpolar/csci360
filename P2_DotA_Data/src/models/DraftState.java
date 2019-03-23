package models;

import java.util.ArrayList;
import java.util.Collections;
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
//	HashMap<Integer, Hero> player_heroes;
//	HashMap<Integer, Hero> opponent_heroes;
	ArrayList<Integer> player_heroes;
	ArrayList<Integer> opponent_heroes;
	//SortedMap<Double, DraftState> successors;

	//The id of the most recent hero drafted
	int last_draft = -1;
	//The id of the hero to draft next for best result
	int next_draft = -1;
	
	public DraftState(Map<Integer, Hero> heroes) {

		player_turn = true;
		player_heroes = new ArrayList<Integer>();
		opponent_heroes = new ArrayList<Integer>();

	    Iterator<Entry<Integer, Hero>> it = heroes.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Integer, Hero> pair = (Map.Entry<Integer, Hero>)it.next();
	        if(pair.getValue().membership == 1) {
	        	player_heroes.add(pair.getKey());
	        }
	        if(pair.getValue().membership == 2) {
	        	opponent_heroes.add(pair.getKey());
	        }
	    }
	}

	// constructor for cloning
	public DraftState() {
		player_heroes = new ArrayList<Integer>();
		opponent_heroes = new ArrayList<Integer>();
		//successors = new TreeMap<Double, DraftState>();
	}

	public String toString() {
		String out = "Draft State: advantage = "+advantage+", my turn = "+player_turn;
		out += "\n             last draft: "+last_draft+", next best draft: "+next_draft;
		out+= "\n\tPlayer Heroes:";
		for(int h:player_heroes) {
			out+=" "+h;
		}
		out+= "\n\tOpponent Heroes: ";
		for(int h:opponent_heroes) {
			out+=" "+h;
		}
	    return out;
	}

	public DraftState cloneState() {
		DraftState tempDraftState = new DraftState();
		tempDraftState.advantage = advantage;
		tempDraftState.player_turn = player_turn;
		tempDraftState.player_heroes.addAll(player_heroes);
		tempDraftState.opponent_heroes.addAll(opponent_heroes);
		return tempDraftState;
	}
	
	//may be necessary later
	public boolean equals(DraftState d) {
		return false;
	}
	
	public void calcAdvantage(Map<Integer, Hero> heroes) {
		double player_advantage = 0;
		double opponent_advantage = 0;

		boolean[] synergy = new boolean[5];

		for(int h:player_heroes) {
	        Hero hero = heroes.get(h);
	        player_advantage += hero.power*hero.mastery_player;
	        synergy[h%10-1] = true;
	    }
	    if(synergy[0]&&synergy[1]&&synergy[2]&&synergy[3]&&synergy[4]) {
	    	player_advantage+=120;
	    }

		synergy = new boolean[5];
		for(int h:opponent_heroes) {
	        Hero hero = heroes.get(h);
	        opponent_advantage += hero.power*hero.mastery_opponent;
	        synergy[h%10-1] = true;
	    }
	    if(synergy[0]&&synergy[1]&&synergy[2]&&synergy[3]&&synergy[4]) {
	    	opponent_advantage+=120;
	    }
	    
	    advantage = player_advantage - opponent_advantage;
	}
	
	//TODO: may want to store synergy as a field to avoid calcAverage at the end of every draft round
	public boolean draftHero(int h, Hero hero) {
		if(player_turn) {
			if(player_heroes.size()>=5 || player_heroes.contains(h) || opponent_heroes.contains(h) ) {
				return false;
			}else {
				player_heroes.add(h);
			}
		}else {
			if(opponent_heroes.size()>=5 || opponent_heroes.contains(h) || player_heroes.contains(h)) {
				return false;
			}else {
				opponent_heroes.add(h);
			}
		}
		player_turn = !player_turn;
		last_draft = h;
		return true;
	}
	
	public ArrayList<DraftState> createSuccessors(Map<Integer, Hero> heroes) {
		
		ArrayList<DraftState> successors;
		if(player_turn) {
			successors= new ArrayList<DraftState>();
		}else {
			successors = new ArrayList<DraftState>();
		}

	    Iterator<Entry<Integer, Hero>> it = heroes.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Integer, Hero> pair = (Map.Entry<Integer, Hero>)it.next();
	        int id = pair.getKey();
	        Hero hero = pair.getValue();
	        
        	DraftState tempDraftState = cloneState();
        	if(tempDraftState.draftHero(id, hero)) {
        		tempDraftState.setLast_draft(id);
        		tempDraftState.calcAdvantage(heroes);
        		successors.add(tempDraftState);
        	}
	    }
	    return successors;
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


	public ArrayList<Integer> getPlayer_heroes() {
		return player_heroes;
	}

	public void setPlayer_heroes(ArrayList<Integer> player_heroes) {
		this.player_heroes = player_heroes;
	}

	public ArrayList<Integer> getOpponent_heroes() {
		return opponent_heroes;
	}

	public void setOpponent_heroes(ArrayList<Integer> opponent_heroes) {
		this.opponent_heroes = opponent_heroes;
	}

	public int getLast_draft() {
		return last_draft;
	}

	public void setLast_draft(int last_draft) {
		this.last_draft = last_draft;
	}

	public int getNext_draft() {
		return next_draft;
	}

	public void setNext_draft(int next_draft) {
		this.next_draft = next_draft;
	}

	public double getMinimax_val() {
		return minimax_val;
	}

	public void setMinimax_val(double minimax_val) {
		this.minimax_val = minimax_val;
	}
	
}
