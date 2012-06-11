package Defs;


public enum M_ {
	NULL,

	BIDASKSPRD,  //also how far away to initially price when haggling
	STMOMENTUM, // 0-7 reversion 8-15 momentum (also how quickly to converge when haggling)
	LTMOMENTUM, // 0-7 reversion 8-15 momentum
	DISCRATE,
	MARGIN,
	INVORTRD,
	TECHNICAL,
	FLOW,

	CONST1,
	CONST2,
	CONST3,
	CONST4,
	CONST5,
	CONST6,

	ARM,
	MIS,
	PRC,
	CAV,
	PYRAMIDALITY,
	LEADERSHIP,
	MERITOCRACITY,



	INDIVIDUALITY,
	EMPIRICISM,
	JOVIALITY,
	TEMPER,   //used to decide when to explode in amygdala
	ROMANTICNESS,
	PROFANITY,
	RESPENV,
	BLOODLUST,
	MADNESS, //innovation, creativity, randomness in behavior
	HUMILITY,
	GREED,
	VANITY,
	EXTROVERSION,
	PATIENCE,
	PARANOIA,
	STRICTNESS,
	DOGMA,
	SUPERST, // add/subtract this amount to secular prestige amt if ELE sanc prest >/< EU's
	CONFIDENCE,
	AGGR,
	MIERTE,
	WORKETHIC,
	OCD, //neatness, like short vs long hair...
	PROMISCUITY,

	


	NOSERX,   //beauty calc
	NOSERY,
	NOSELX,   //beauty calc
	NOSELY,
	NOSELW,
	NOSEMW,
	NOSERW,
	MOUTHBX,
	MOUTHBY,
	MOUTHP,
	MOUTHC,
	MOUTHLH,
	MOUTHLW,
	MOUTHJH,
	MOUTHJW,    //beauty calc
	EYERP,
	EYELP,
	EYERW,
	EYELW,     //beauty calc (LEFT AND RIGHT ARE POV OF GOBLIN!)
	EYEHGT,
	EYESPRD,
	EARH, 
	EARW,
	EART,
	EARD,
	SKINR,
	SKING,
	SKINB,
	HAIRL,   //beauty calc
	HAIRC,
	HAIRW,
	HAIRR,
	HAIRG,
	HAIRB,
	HAIRX,
	HAIRS,
	
	//SANC MEMES
	V_WEALTH_POWER,   //(your highness)
	S_MONEY,
	S_POPULARITY,
	S_NVASSALS,
	S_BVASSALS,
	S_COMBAT, //combo of martial prowess, strength, strategy, tactics
	S_THREAT, //combo of bravery, proximity, bloodlust...
	//S_TOT_N_MINIONS
	//S_DIRECT_VASSAL_WEALTH
	
	V_PLEASURE_BEAUTY,   //(your majesty)
	S_SILVER,
	S_MEAT,
	S_NOSELEN,
	S_EYESIZE,
	S_JAWWIDTH,
	S_HAIRLEN,
	S_ART,
	
	V_HONOR_GLORY,	//(your excellency)
	S_PATRIOTISM, //includes #fights/victories for homeland
	S_LOYALTY, //includes #fights/victories for king
	S_ZEAL,
	S_PROMISCUITY,
	S_GREED,
	S_BLOODLUST,
	S_MONUMENTS,
	
	V_KNOWLEDGE_WISDOM,	//(your eminence)
	S_KNOWLEDGE,
	S_SACRIFICE,
	S_HEALING,
	S_SORCERY,
	S_AGE,
	S_SKILL;
	//All the above
	private static M_[] smems = new M_[] {
			S_MONEY,
			S_POPULARITY,
			S_NVASSALS,
			S_BVASSALS,
			S_COMBAT,
			S_THREAT,
			S_SILVER,
			S_MEAT,
			S_NOSELEN,
			S_EYESIZE,
			S_JAWWIDTH,
			S_HAIRLEN,
			S_ART,
			S_PATRIOTISM,
			S_LOYALTY,
			S_ZEAL,
			S_PROMISCUITY,
			S_GREED,
			S_BLOODLUST,
			S_MONUMENTS,
			S_KNOWLEDGE,
			S_SACRIFICE,
			S_HEALING,
			S_SORCERY,
			S_AGE,
			S_SKILL
	};
	public static M_[] SMems() {return smems;}
}
