package Game;

import Defs.P_;
import Sentiens.Stressor;
import Shirage.Shire;

public class Job implements Defs, Stressor.Causable {
	private Act[] acts;
	private String desc;
	
	public Job(String N, Act... A) {desc = N; acts = A;}

	public Act[] getActs() {return acts;}

	private static Logic AND(Logic... L) {return new And(L);}
	private static Logic AND(int... L) {return new And(Logic.L(L));}
	private static Logic OR(Logic... L) {return new Or(L);}
	private static Logic OR(int... L) {return new Or(Logic.L(L));}
	private static Logic M(int n, int L) {return new Mult(n, L);}
	private static Logic I(int i) {return new Node(i);}
	private static Logic N() {return new Nada();}
	
	public final static Act NullAct = new Act("Nothing",						N(),						N(),			null,								null,		-1);
	
	public final static Act Lobotomize = Act.newCraft("Lobotomize Donkey",		I(donkey),					I(lobodonkey),	P_.LOBOTOMY,						null,		-1);
	public static final Job LOBOTOMIST = new Job("Lobotomist", Lobotomize);
	
	public final static Act MakeBow = Act.newCraft("Craft Crossbow",			I(timber),					I(bow),			P_.CARPENTRY,						null,		-1);
	public final static Act ConstructW = Act.newCraft("Build Construction",		I(timber),					I(wconstr),		P_.CARPENTRY,						null,		-1);
	public static final Job CARPENTER = new Job("Carpenter", MakeBow, ConstructW);
	
	public final static Act ConstructS = Act.newCraft("Build Construction",		I(stone),					I(sconstr),		P_.MASONRY,							null,		-1);
	public static final Job MASON = new Job("Stonemason", ConstructS);
	
	public final static Act Build = Act.newCraft("Construct Building",			OR(wconstr, sconstr),		I(constr),		P_.STRENGTH,						null,		-1);
	public static final Job BUILDER = new Job("Builder", Build);

	public final static Act TradeA = Act.newTrade("Trade Animals",				OR(bovad, donkey, lobodonkey, poop),											null,		-1);
	public final static Act TradeC = Act.newTrade("Trade Commodities",			OR(timber, stone, iron, silver, constr),										null,		-1);
	public final static Act TradeW = Act.newTrade("Trade Arms",					OR(sword, mace, bow, gun, armor),												null,		-1);
	public static final Job TRADER = new Job("Merchant", TradeA, TradeC, TradeW);
	
	public final static Act ForgeGun = Act.newCraft("Smith Arquebus",			AND(I(iron), M(2, timber)),	I(gun),			P_.SMITHING,						null,		-1);
	public static final Job GUNSMITH = new Job("Gunsmith", ForgeGun);
	
	public final static Act ForgeWeapon = Act.newCraft("Smith Weapon",			AND(iron, timber),			OR(sword, mace),P_.SMITHING,						null,		-1);
	public final static Act ForgeArmor = Act.newCraft("Smith Armor",			AND(M(3, iron), I(timber)),	I(armor),		P_.SMITHING,						null,		-1);
	public static final Job SMITHY = new Job("Blacksmith", ForgeWeapon, ForgeArmor);
	
	public final static Act MakeJewel = Act.newCraft("Silvercrafting",			AND(silver, timber),		I(jewelry),		P_.ARTISTRY,						SMITHY,		1000);
	public static final Job JEWELER = new Job("Silversmith", MakeJewel);
	
	public final static Act Quarry = Act.newReapR("Quarry Stone",				N(),						I(stone),		P_.STRENGTH,	Shire.STONES,		BUILDER,	500);
	public final static Act MineIron = Act.newReapR("Iron Mining",				N(),						I(iron),		P_.STRENGTH,	Shire.IORE,			null,			-1);
	public final static Act MineSilver = Act.newReapR("Silver Mining",			N(),						I(silver),		P_.STRENGTH,	Shire.SORE,			JEWELER,	1000);
	public static final Job MINER = new Job("Miner", Quarry, MineIron, MineSilver);
	
	public final static Act Lumberjacking = Act.newReapR("Fell Trees",			N(),						I(timber),		P_.STRENGTH,	Shire.TREES,		CARPENTER,	500);
	public static final Job LUMBERJACK = new Job("Lumberjack", Lumberjacking);
	
	public final static Act Settle = Act.newReapR("Settle Land",				N(),						I(land),		null,			Shire.WILDERNESS,	LUMBERJACK, 100);
	public final static Act Farm = Act.newReapC("Minimal Farming",				I(rentland),				M(3, millet),	null,			Shire.FERTILITY,	MINER,		100);
	public final static Act FarmF = Act.newReapC("Fertilized Farming",			AND(rentland, poop),		M(3, millet),	null,			-1,					MINER,		100);
	public final static Act FarmA = Act.newReapC("Assisted Farming",			AND(M(2, rentland),
																				I(rentanimal)),				M(6, millet),	null,			Shire.FERTILITY,	MINER,		100);
	public final static Act FarmAF = Act.newReapC("Complete Farming",			AND(M(2, rentland),
			AND(rentanimal, poop)),		M(6, millet),	null,			-1,					MINER,		100);
	public static final Job FARMER = new Job("Farmer", Settle, Farm, FarmF, FarmA, FarmAF);
	
	public final static Act HerdB = Act.newReapR("Bovad Herding",				N(),						I(bovad),		null,			Shire.WBOVADS,		TRADER,		100);
	public final static Act HerdD = Act.newReapR("Donkey Herding",				N(),						I(donkey),		null,	 		Shire.WDONKEYS,		TRADER,		100);
	public final static Act Butcher = Act.newCraft("Butcher Animal",			OR(I(bovad), I(donkey),
																				I(lobodonkey)),				M(3, meat),		null,								LOBOTOMIST,	1000);
	public final static Act DungCollecting = Act.newCraft("Collect Dung",		I(rentanimal),				I(poop),		null,								null,			-1);
	public static final Job HERDER = new Job("Herder", HerdB, HerdD, DungCollecting, Butcher);
	
	public final static Act GatherMillet = Act.newReapC("Gather Edibles",		N(),						I(millet),		null,			Shire.FERTILITY,	FARMER,		1000); //vegetation
	public final static Act HuntSloth = Act.newReapC("Hunt Sloth",				N(),						I(meat),		P_.MARKSMANSHIP,Shire.WILDERNESS,	HERDER,		1000);
	public static final Job HUNTERGATHERER = new Job("Savage", GatherMillet, HuntSloth);
	

	
		
	
	public String getDesc() {return desc;}
}