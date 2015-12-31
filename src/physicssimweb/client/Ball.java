package physicssimweb.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.FillStrokeStyle;

public class Ball extends RigidBody{
	
	public double r;
	
	public Ball(RigidBody a){
		pos = new Vec(a.pos.x, a.pos.y);
		vel = new Vec(a.vel.x, a.vel.y);
		c = a.c;
		invMass = a.invMass;
		r = 1;
	}
	
	public Ball(){
		pos = new Vec(0,0);
		vel = new Vec(0,0);
		acc = new Vec(0,0);
		newAcc = new Vec(0,0);
		r = 1;
		c = CssColor.make("white");
		invMass = 1;
	}
	
	public Ball(double xx, double yy, double rr){
		this();
		pos = new Vec(xx,yy);
		r = rr;
	}
	
	public Ball(Vec p, double rr){
		this();
		pos = new Vec(p.x,p.y);
		r = rr;
	}
	
	public Ball(Vec p, double rr, double mass){
		this(p,rr);
		if(mass==0)invMass=1;
		else invMass = 1.0/mass;
	}
	
	public Ball(Vec p, Vec vell, double rr){
		this(p,rr);
		vel = new Vec(vell.x,vell.y);
	}
	
	public Ball(Vec p, Vec vell, double rr, double mass){
		this(p,vell,rr);
		if(mass==0)invMass=1;
		else invMass = 1.0/mass;
	}
	
	public Ball(Vec p, Vec vell, double rr, double masss, CssColor cc){
		this(p,vell,rr,masss);
		c=cc;	
	}
	
	public void drawBody(Context2d g){
		FillStrokeStyle cc = g.getFillStyle();
		g.setFillStyle(c);
		g.beginPath();
		g.arc(pos.x, pos.y, r, 0, 2*Math.PI);
		g.fill();
		g.setFillStyle(cc);
	}
	
	public void update(){
		pos.add(vel);
	}
	
	public void impulse(Vec p){
		vel.add(p.scaleV(invMass));
	}
	
	public void verletUpdate(){
		pos.add(vel);//.plus(acc.scale(0.5)));
		vel.add((acc.plus(newAcc)).scale(0.5));
//		vel.add(newAcc);
	}
	
	public void setAcc(Vec a){
		acc.set(newAcc);
		newAcc.set(a);
	}
	
	public void velUpdate(){
		vel.add((acc.plus(newAcc)).scale(0.5));
	}
	
}
