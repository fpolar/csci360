package models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.Map.Entry;

public class DraftState {
	double advantage;
	boolean player_turn;
	//hero states may only need to store chosen heroes
	HashMap<Integer, Integer> heroStates;
	SortedMap<Double, DraftState> successors;
	
	public DraftState(HashMap<Integer, Hero> heroes) {

	    Iterator<Entry<Integer, Hero>> it = heroes.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Integer, Hero> pair = (Map.Entry<Integer, Hero>)it.next();
	        System.out.println(pair.getKey() +""+ pair.getValue());
	        it.remove();
	    }
	}
	
	//may be necessary later
	public boolean equals(DraftState d) {
		return false;
	}
	
	public void calcAdvantage(HashMap<Integer, Hero> heroes) {
		double player_advantage = 0;
		double opponent_advantage = 0;
	}
	
	public void draftHero(int h) {
		
	}
	
}
