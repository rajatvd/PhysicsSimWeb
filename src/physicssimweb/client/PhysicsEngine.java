package physicssimweb.client;

import java.util.Vector;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Label;

public class PhysicsEngine {
	
	//ball collection
	public Vector<RigidBody> bodies = new Vector<RigidBody>();	
	
	//coefficient of restitution, gravity contant, ball creation mass
	public double res=1, grav=5, mass=1;
	
	//magnitude of normal momentum transfer to walls per timestep,
	//number of timesteps since last ball creation
	public double wallMomentum = 0, timesteps = 0;
	
	//wall width and height, ball creation radius
	public int wallx=1100, wally=600, radius=6;
//		counter=0;
	
	public boolean walls = false;
	
	public Label keLabel;
	
	
	//Physics functions:
	
	/**
	 * Add a rigid body.
	 * @param a - body to add
	 */
	public void addBody(RigidBody a){
		bodies.add(a);
		timesteps = 0;
		wallMomentum = 0;
	}
	
	/**
	 * Main update function
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public void update() throws ArrayIndexOutOfBoundsException{
//		wallMomentum = 0;
		timesteps++;
		Ball a,b;
		for(int i=0;i<bodies.size();i++){
			for(int j=0;j<bodies.size();j++){
				if(i==j)continue;
				a = (Ball) bodies.elementAt(i);
				b = (Ball) bodies.elementAt(j);
				//gravity
				if(j>i)gravitate(a,b);
				if(checkCollision(a,b)){
					collide(a,b,res);
				}
			}
			if(walls)checkWall((Ball) bodies.elementAt(i));
			bodies.elementAt(i).update();
		}
		System.out.println(wallMomentum/timesteps);
//		ke = String.format("Kinetic Energy: %.8g", kineticEnergy());
		
		keLabel.setText("Kinetic Energy: "+NumberFormat.getDecimalFormat().format(kineticEnergy()));
	}
	
	/**
	 * Check if two balls are intersecting.
	 * @param a
	 * @param b
	 * @return
	 */
	public boolean checkCollision(Ball a, Ball b){
		return a.pos.minus(b.pos).mag()<a.r+b.r;
	}
	
	/**
	 * Check and perform wall collision
	 * @param a - The ball to check
	 */
	public void checkWall(Ball a){
		if(a.pos.x<=a.r){
			a.vel.x = -a.vel.x;
			a.pos.x=a.r;
			wallMomentum += Math.abs(a.vel.x)/a.invMass;
		}
		if(a.pos.x>=wallx-a.r){
			a.vel.x = -a.vel.x;
			a.pos.x=wallx-a.r;
			wallMomentum += Math.abs(a.vel.x)/a.invMass;
		}
		if(a.pos.y<=a.r){
			a.vel.y = -a.vel.y;
			a.pos.y=a.r;
			wallMomentum += Math.abs(a.vel.y)/a.invMass;
		}
		if(a.pos.y>=wally-a.r){
			a.vel.y = -a.vel.y;
			a.pos.y=wally-a.r;
			wallMomentum += Math.abs(a.vel.y)/a.invMass;
		}
	}
	
	/**
	 * Perform gravity update
	 * @param a - first ball to gravitate
	 * @param b - second ball to gravitate
	 */
	public void gravitate(Ball a, Ball b){
		Vec r = b.pos.minus(a.pos);
		//to prevent penetration gravity
		if(r.mag()<a.r+b.r)return;
		
		Vec i = r.scaleV(grav/(Math.pow(r.mag(),3)*b.invMass*a.invMass));
		
//		a.vel.add(r.scaleV(grav/(Math.pow(r.mag(),3)*b.invMass)));
//		b.vel.add(r.scaleV(-grav/(Math.pow(r.mag(),3)*a.invMass)));
		
//		a.setAcc(a.newAcc.plus(i.scaleV(a.invMass)));
//		b.setAcc(b.newAcc.plus(i.scaleV(-b.invMass)));
		
		a.impulse(i);
		b.impulse(i.scaleV(-1));
	}
	
	/*
	 *  OLD CALCULATIONS
	 *  va - vb = rest(ub - ua)
	 *  va = vb + rest(ub - ua)
	 *  p = mava + mbvb
	 *  p = mavb + marest(ub - ua) + mbvb
	 *  p - marest(ub - ua) = vb(ma + mb)
	 *  vb = [p - marest(ub - ua)]/(ma + mb)
	 *  va = [p - marest(ub - ua) + marest(ub - ua) + mbrest(ub - ua)]/(ma + mb)
	 *  va = [p + mbrest(ub - ua)]/(ma + mb)
	 *  
	 */
				
	/*
	 * NEW CALCULATIONS:(BETTER)
	 * 
	 * ma, mb - masses
	 * ua, ub - initial velocities
	 * va, vb - final velocities
	 * 
	 * Relative velocities:
	 * U = ub - ua
	 * V = vb - va
	 * 
	 * Newton's Law of Restitution:
	 * V.n = -rest*U.n
	 * (n is unit vector normal to collision)
	 * 
	 * Conservation of Momentum:
	 * maua + mbub = mava + mbvb
	 * ma(ua - va) = mb(vb - ub)
	 * ia = -ib = i
	 * 
	 * where 'i' is the impulse
	 * 
	 *  (va = ua - i/ma)
	 * -(vb = ub + i/mb)
	 * 
	 * va - vb = ua - ub + i(1/ma + 1/mb)
	 * i = (-V + U)/(1/ma + 1/mb)
	 * i = U(1+rest)/(1/ma + 1/mb)
	 * 
	 */
	
	/**
	 * Collides a and b
	 * @param a - first ball
	 * @param b - second ball
	 * @param rest - coefficient of restitution for the collision
	 */
	public void collide(Ball a, Ball b, double rest){
		//get vectors
		Vec ua = a.vel, ub = b.vel;
		Vec U = ub.minus(ua);
		Vec n = b.pos.minus(a.pos);
		if(n.mag()==0)return;
		n.scale(1/n.mag());
		
		//check if collision is proper
		if(U.dot(n)>=0){
			separate(a,b);
			return;
		}
		
		
		//find impulse
		Vec Un = n.scaleV(U.dot(n));
		Vec i = Un.scaleV((1+res)/(a.invMass+b.invMass));
		
		//execute impulse
		a.impulse(i);
		b.impulse(i.scale(-1));
		
//		System.out.println(energy());
		
	}
	
	/**
	 * Calculate kinetic energy
	 * @return Kinetic energy of the system
	 */
	public double kineticEnergy(){
		RigidBody b;
		double s=0;
		for(int i=0;i<bodies.size();i++){
			b=bodies.get(i);
			s+= 0.5 / b.invMass * b.vel.dot(b.vel); 
		}
		return s;
	}
	
	/**
	 * Calculate potential energy
	 * @return Potential energy of the system
	 */
	public double potentialEnergy(){
		RigidBody a,b;
		double s=0;
		for(int i=0;i<bodies.size();i++){
			a=bodies.get(i);
			for(int j=i+1;j<bodies.size();j++){
				b=bodies.get(j);
				s += -1*grav/(a.invMass*b.invMass*a.pos.minus(b.pos).mag());
			}
		}
		return s;
	}
	
	/**
	 * Separates two balls if they are colliding. It moves both balls away from each other
	 * an equal distance along the line joining their centres, until they do not intersect.
	 * @param a - First Ball
	 * @param b - Second Ball
	 */
	public void separate(Ball a, Ball b){
		while(checkCollision(a,b)){
			Vec r = b.pos.minus(a.pos);
			r.scale(0.01/r.mag());
			a.pos.subtract(r);
			b.pos.add(r);
		}
	}
	
	/**
	 * Separates two balls if they are colliding. It moves one ball along its velocity until
	 * they do not intersect. This separation conserves angular momentum, unlike the previous one.
	 * @param a - First Ball
	 * @param b - Second Ball
	 */
	public void separate2(Ball a, Ball b){
		while(checkCollision(a,b)){
			Vec ra = a.vel.scaleV(1/a.vel.mag());
			Vec rb = b.vel.scaleV(1/b.vel.mag());
			Vec r = b.pos.minus(a.pos);
			if(ra.dot(r)>rb.dot(r)){
				a.pos.add(ra);
			}else{
				b.pos.add(rb);
			}
		}
	}
	
}

