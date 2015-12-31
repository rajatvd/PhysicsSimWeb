package physicssimweb.client;


import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

public class RigidBody {

	//coordinates of centre of mass
	public Vec pos, vel, acc, newAcc;
	//mass of body
	public double invMass;
	
	//color of body when drawn
	public CssColor c;
	
	public void drawBody(Context2d g){
		
	}
	
	public void update(){
		
	}

	public void verletUpdate() {
		
	}

	public void velUpdate() {
		
	}
	
}
