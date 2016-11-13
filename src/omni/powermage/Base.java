package omni.powermage;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Magic;
import org.powerbot.script.rt4.Magic.MagicSpell;
import org.powerbot.script.rt4.Npc;

@Script.Manifest(description = "Casts Weaken at varrock castle", name = "PowerWeaken")

public class Base extends PollingScript<ClientContext> implements PaintListener {

	
	private int casts;
	private int startxp;
	private long timestart;
	private final Tile monk = new Tile(3214, 3476);
	
	private Image xpImage;
	private Image timeImage;
	private Image castsImage;
	private Mage magic;
	
	private ArrayList<MagicSpell> spells = new ArrayList<MagicSpell>();
	private int xpPer = 1;
	@Override
	public void repaint(Graphics g) {
		
		if(timeImage == null){
			timeImage = getImage("http://www.surroundtech.com/media/e8/1db5/e81db50438cb3f99bca2bde301577092/icon-fast-time.png");
		}
		if(xpImage == null){
			xpImage = getImage("http://static-cdn.jtvnw.net/jtv_user_pictures/mogis2007-profile_image-b318204103da8946-600x600.png");
		}
		if(castsImage == null){
			castsImage = getImage("http://vignette4.wikia.nocookie.net/runescape2/images/3/3a/Weaken_icon.png/revision/latest?cb=20130106182531");
		}
		drawXP(g);
		drawCASTS(g);
		drawTIME(g);

		
		
	}

	@Override
	public void start(){
		startxp = ctx.skills.experience(6);
		timestart = System.currentTimeMillis();
		casts = getCastsLeft();
		magic = new Mage(ctx, spells);
		log.info("Ready to cast >:3");
		ctx.game.tab(ctx.game.tab().MAGIC);
		
		spells.add(Magic.Spell.CONFUSE);
		spells.add(Magic.Spell.WEAKEN);
		spells.add(Magic.Spell.CURSE);
		spells.add(Magic.Spell.VULNERABILITY);
		spells.add(Magic.Spell.ENFEEBLE);
		spells.add(Magic.Spell.STUN);
		

		
	}
	 public void drawXP(Graphics g){
			
		 Color background = new Color(0f, 0f, 0f, .5f);
		 g.setColor(background);
		 g.fillRoundRect(395, 50, 118, 35, 5, 5);
		 g.setColor(Color.BLUE);
		 g.drawRoundRect(395, 50, 118, 35, 5, 5);
		 g.drawImage(xpImage, 400, 53, 32, 30, null);
		 g.setColor(Color.WHITE);
		 g.drawString("Experience", 443, 63);
		 g.drawString("" + (ctx.skills.experience(6) - startxp), 473 - (g.getFontMetrics().stringWidth("" + (ctx.skills.experience(6) - startxp)) / 2), 80);
		
	 }
	 public void drawCASTS(Graphics g){
			
		 Color background = new Color(0f, 0f, 0f, .5f);
		 g.setColor(background);
		 g.fillRoundRect(395, 90, 118, 35, 5, 5);
		 g.setColor(Color.PINK);
		 g.drawRoundRect(395, 90, 118, 35, 5, 5);
		 g.drawImage(castsImage, 400, 93, 32, 30, null);
		 g.setColor(Color.WHITE);
		 g.drawString("Casts", 453, 103);
		 g.drawString("" + ((ctx.skills.experience(6) - startxp) / xpPer) + "/NA", 471 - (g.getFontMetrics().stringWidth("" + ((ctx.skills.experience(6) - startxp) / xpPer) + "/NA") / 2), 120);

	 }
	 
	 
	 public int getCastsLeft(){
		 int water = ctx.inventory.select().id(555).count(true);
		 return water / 3;
		 

	 }
	 
	 public void drawTIME(Graphics g){
		 String runtime = String.format("%02d:%02d:%02d", 
				    TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - timestart),
				    TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - timestart) - 
				    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - timestart)),
				    TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - timestart) - 
				    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - timestart)));
		 Color background = new Color(0f, 0f, 0f, .5f);
		 g.setColor(background);
		 g.fillRoundRect(395, 130, 118, 35, 5, 5);
		 g.setColor(Color.red);
		 g.drawRoundRect(395, 130, 118, 35, 5, 5);
		 g.drawImage(timeImage, 400, 133, 32, 30, null);
		 g.setColor(Color.WHITE);
		 g.drawString("Run Time", 445, 143);
		 g.drawString("" + runtime, 473 - (g.getFontMetrics().stringWidth("" + runtime) / 2), 160);

	 }
		private Image getImage(String url) {
			try {
				return ImageIO.read(new URL(url)); 
			}
			catch(IOException e)
			{
				return null;
			}
		}

	@Override
	public void poll() {
		if(magic.getBestSpell() == null){
	       JOptionPane.showMessageDialog(null, "You have run out of runes...", "You need runes!", JOptionPane.ERROR_MESSAGE);
	       ctx.controller.stop();
	       return;
		}
		log.info("XP Per Cast: " + this.xpPer);
		Npc monk = ctx.npcs.select().id(2886).nearest().poll();

		state s = getState();
		switch(s){
		case walktomonk:
			if(ctx.players.local().inMotion() == false){
				ctx.movement.step(monk);
				Condition.sleep(new Random().nextInt(1000) + 250);
				ctx.camera.turnTo(monk);
				
			}
			break;
		case cast:
			Random rand = new Random();
			if(rand.nextInt(2500) <= 250){
				ctx.camera.turnTo(monk);
			}
			int currentxp = ctx.skills.experience(6);
			ctx.magic.cast(magic.getBestSpell());
			monk.interact("cast");
			int xp = ctx.skills.experience(6) - currentxp;
			if(xp != 0){
				this.xpPer = xp;
			}
		}
		
	}
	
	public state getState(){
		if(monk.distanceTo(ctx.players.local()) != 0){
			return state.walktomonk;
		}
		return state.cast;
	}
	
	
	public enum state{
		cast, walktomonk
	}

}
