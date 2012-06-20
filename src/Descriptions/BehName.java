package Descriptions;

import Defs.M_;
import Sentiens.Clan;

public class BehName {

	private static int grade(int beh) {
		switch (beh) {
		case 0: return 0;
		case 1: case 2: case 3: return 1;
		case 12: case 13: case 14: return 2;
		case 15: return 3;
		default: return 4;
		}
	}
	
	public static String describeMem(Clan clan, M_ m) {
		int grade = (clan != null ? clan.FB.getBeh(m) : 0);
		if (grade > 3) {return "";}
		String[] descs = new String[] {};
		switch (m) {
		case BIDASKSPRD: descs = new String[] {"Very tight spread when trading","Tight spread when trading","Wide spread when trading","Very wide spread when trading"}; break;
		case STMOMENTUM: descs = new String[] {}; break;
		case LTMOMENTUM: descs = new String[] {}; break;
		case DISCRATE: descs = new String[] {}; break;
		case MARGIN: descs = new String[] {}; break;
		case INVORTRD: descs = new String[] {}; break;
		case TECHNICAL: descs = new String[] {"Does not like to use price history to predict market","","","Likes to use price history to predict market"}; break;
		case FLOW: descs = new String[] {"Does not like to use supply and demand to predict market","","","Likes to use supply and demand to predict market"}; break;
		case ARM: descs = new String[] {"Finds armor useless and cumbersome","Does not like armor","Likes armor","Finds armor indispensible in a fight"}; break;
		case MIS: descs = new String[] {"Hates ranged weapons","Does not care for ranged weapons","Finds ranged weapons useful","Finds ranged weapons indispensible"}; break;
		case PRC: descs = new String[] {"Loves blunt and armor-piercing weapons","Likes blunt and armor-piercing weapons","Likes sharp, bladed weapons","Loves sharp, bladed weapons"}; break;
		case CAV: descs = new String[] {"Hates donkeys","Does not care for cavalry","Likes cavalry","Finds donkey cavalry indispensible"}; break;
		case PYRAMIDALITY: descs = new String[] {}; break;
		case LEADERSHIP: descs = new String[] {}; break;
		case MERITOCRACITY: descs = new String[] {}; break;
		case INDIVIDUALITY: descs = new String[] {}; break;
		case EMPIRICISM: descs = new String[] {}; break;
		case JOVIALITY: descs = new String[] {}; break;
		case TEMPER: descs = new String[] {"Extremely short-tempered","Bad temper","Slow to anger","Extremely calm disposition"}; break;
		case ROMANTICNESS: descs = new String[] {}; break;
		case PROFANITY: descs = new String[] {}; break;
		case RESPENV: descs = new String[] {"Finds nature disgusting","Avoids the wilderness","Enjoys being in the wilderness","Staunch defender of the environment"}; break;
		case BLOODLUST: descs = new String[] {"Respects the sanctity of life","Does not like to hurt others","Fondness for cruelty and sadism","Revels in murder and bloodshed"}; break;
		case MADNESS: descs = new String[] {"Unyielding adherance to reason and logic","Pragmatic and realistic","Exhibits unpredictable behavior","Completely insane"}; break;
		case HUMILITY: descs = new String[] {}; break;
		case GREED: descs = new String[] {}; break;
		case VANITY: descs = new String[] {}; break;
		case EXTROVERSION: descs = new String[] {"Recluse","Prefers to be alone","Enjoys the company of others","Very outgoing and social"}; break;
		case PATIENCE: descs = new String[] {"No patience whatsoever","Fairly impatient","Patient","Very patient"}; break;
		case PARANOIA: descs = new String[] {"Paranoid schizophrenic","Risk averse","Fearless","Loves to take risks"}; break;
		case STRICTNESS: descs = new String[] {"Moral relativist","Open-minded","Narrow-minded","Fundamentalist"}; break;
		case DOGMA: descs = new String[] {"Believes in the gods of others","Philosophically curious","Prefers to follow a single creed","Staunch monotheist"}; break;
		case SUPERST: descs = new String[] {"Skeptical and materialistic","Dismissive of superstition","Supersitious","Bows to the will of the heavens"}; break;
		case CONFIDENCE: descs = new String[] {"No self-confidence","Humble","Confident","Overconfident"}; break;
		case AGGR: descs = new String[] {}; break;
		case MIERTE: descs = new String[] {"Welcomes death with open arms","Not afraid to die","Fond of living","Prizes own life above all else"}; break;
		case WORKETHIC: descs = new String[] {}; break;
		case OCD: descs = new String[] {"Dirty","Unwashed","Likes things to be clean and neat","Obsessive-compulsive"}; break;
		case PROMISCUITY: descs = new String[] {"Puritanical","Prefers celibacy","Lecherous","Uncontrollable urges"}; break;
		}
		return (grade < descs.length ? descs[grade] : "");
	}
}
