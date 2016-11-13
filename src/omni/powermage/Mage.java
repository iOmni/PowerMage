package omni.powermage;

import java.util.ArrayList;

import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Magic.MagicSpell;

public class Mage {
	
	
	private ClientContext ctx;
	private ArrayList<MagicSpell> spells;
	public Mage(ClientContext ctx, ArrayList<MagicSpell> spells){
		this.ctx = ctx;	
		this.spells = spells;
		
		
	}
	
	public boolean spellAvailable(MagicSpell e){
		return ctx.magic.ready(e);
	}
	
	
	public MagicSpell getBestSpell(){
		MagicSpell best = null;
		for(MagicSpell spell : spells){
			if(ctx.magic.ready(spell)){
				if(best == null){
					best = spell;
				}else{
					if(spell.level() > best.level()){
						best = spell;
					}
				}
			}	
		}
		return best;
	}
}
