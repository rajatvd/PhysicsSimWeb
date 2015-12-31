package physicssimweb.client;

import com.google.gwt.animation.client.Animation;

public class PhysicsAnimator extends Animation{
	
	private GraphicsEngine gen;
	private PhysicsEngine pen;
	public boolean running = true;
	
	public PhysicsAnimator(GraphicsEngine g, PhysicsEngine p){
		gen = g;
		pen = p;
	}
	
	@Override
	protected void onUpdate(double progress) {
		if(running)pen.update();
		gen.draw();
	}
	
	@Override
	protected void onComplete(){
		this.run(1000000);
	}
	
	public void pause(){
		running = false;
	}
	
	public void unpause(){
		running = true;
	}

}
