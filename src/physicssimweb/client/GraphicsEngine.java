package physicssimweb.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.FillStrokeStyle;
import com.google.gwt.user.client.ui.VerticalPanel;

//import physicsengine.client.PhysicsEngine;
//import physicsengine.client.Vec;
//import physicsengine.client.Ball;
//import physicsengine.client.RigidBody;


public class GraphicsEngine {
	
	
	PhysicsEngine peng;
	
	private int crosshairW = 30, crosshairH = 30, trail = 0;
	
	//pan amount and zooming scale
	public Vec pan = new Vec();
	public double zoom=1, sensitivity = 0.03;
	
	//Vecs to interact with other engines
	public Vec creationPos,creationVec,mouseCHPos;
	
	//graphics fields
	public Canvas mainCanvas, topLayer, botLayer;
	private Context2d g, top, bot;
	
	//colors
	public CssColor crosshairColor = CssColor.make("purple"), 
			bgColor = CssColor.make("rgba(0,0,0,1)"), 
			rawBG = CssColor.make("black"), 
			vecColor = CssColor.make("yellow");
	
	//temporary variable
	private FillStrokeStyle temp;
	
	public Ball trackedBall;
	
	public boolean drawMomentum = false;
	
	//vertical panel associated with the main canvas, used to get centre of view.
	public VerticalPanel vp;
	
	public GraphicsEngine(PhysicsEngine e, Canvas main, Canvas topp, Canvas bott){
		peng = e;
		mainCanvas = main;
		topLayer = topp;
		botLayer = bott;
		g = mainCanvas.getContext2d();
		top = topLayer.getContext2d();
		bot = botLayer.getContext2d();
		g.setFillStyle(rawBG);
		g.fillRect(0, 0, mainCanvas.getCoordinateSpaceWidth()
				, mainCanvas.getCoordinateSpaceHeight());
	}
	
	
	/**
	 * Perform all the functions when the zoom changes
	 * @param newzoom - new zoom amount
	 * @param centre - the centre of the zooming(in the apparent view)
	 */
	public void zoom(double newzoom, Vec centre){
		//centre is the position vector of zooming origin. Subtracting the pan 
		//away yields the position vector of an imaginary object at the zooming origin.
		//Dividing by zoom yields the actual position vector of an imaginary object
		//as if it were at the mouse. Multiplying by newzoom gets the new 
		//apparent position vector with respect to zooming origin. The change in 
		//position of the object is centre*(newzoom/zoom) - centre. Subtracting this 
		//from pan causes the object to be unmoved.
		
		//centre is now apparent position vector of an imaginary object at the zoom origin
		centre.subtract(pan);
		
		//(centre*(newzoom/zoom) - centre) + (change in pan) = 
		//net change in apparent position of imaginary object.
		//for the object to be unmoved, net change = 0,
		//change in pan = -(centre*(newzoom/zoom) - centre)
		pan.subtract(centre.scaleV(newzoom/zoom).minus(centre));
		
		zoom=newzoom;
	}
	
	/**
	 * Draws the corsshair assuming a ball is tracked.
	 * @param g Context2d to draw with
	 */
	public void drawCrosshair(Context2d g){
		Vec centre = getCentre();
		if(centre == null)return;
		int w = (int)Math.max((crosshairW/2+2*trackedBall.r)*zoom, crosshairW),
			h = (int)Math.max((crosshairH/2+2*trackedBall.r)*zoom, crosshairH);
		drawCrosshair(g,centre,w,h);
	}
	
	/**
	 * Draws a crosshair.
	 * @param g - Context2d
	 * @param centre - Centre of crosshair
	 * @param w - width of crosshair
	 * @param h - height of crosshair
	 */
	public void drawCrosshair(Context2d g, Vec centre, int w, int h){
		
		temp = top.getStrokeStyle();
		double t = top.getLineWidth();
		
		g.setStrokeStyle(crosshairColor);
		g.setLineWidth(2);
		
		g.beginPath();
		g.arc(centre.x,centre.y, (w+h)/4,0,Math.PI*2);
		g.stroke();
		g.closePath();
		
		g.setLineWidth(t);
		g.setStrokeStyle(temp);
	}
	
	/**
	 * Adjust the pan so that the view is centred on the centre of mass, 
	 * or the centre of the walls.
	 */
	public void centreView(){
		Vec centre = getCentre();
		if(centre == null)return;
		if(peng.bodies.size()==0){
			pan.x=centre.x - peng.wallx*zoom/2;
			pan.y=centre.y - peng.wally*zoom/2;
		}else{
			Vec c = (invTransform(centerOM()).scale(-1*zoom))
					.plus(new Vec(centre.x,centre.y));
			pan.set(c);
		}
	}
	
	public CssColor invert(CssColor c){
//		return CssColor.make(255-c..getRed(), 255-c.getGreen(), 255-c.getBlue());
		return c;
	}
	
	/**
	 * Clear the main canvas layer with the bgColor, and completely clear the top layer.
	 */
	public void clearImage(){
		temp = bot.getFillStyle();
		bot.setFillStyle(bgColor);
		bot.fillRect(0, 0, mainCanvas.getCoordinateSpaceWidth()
				, mainCanvas.getCoordinateSpaceHeight());
		bot.setFillStyle(temp);
		top.clearRect(0, 0, topLayer.getCoordinateSpaceWidth(),
				topLayer.getCoordinateSpaceHeight());
	}
	
	/**
	 * Draw the walls.
	 * @param g Context2d to draw with.
	 */
	public void drawWalls(Context2d g){
		temp = g.getStrokeStyle();
		g.setStrokeStyle(CssColor.make("white"));
		g.beginPath();
		g.rect(0, 0, peng.wallx, peng.wally);
		g.stroke();
		g.closePath();
		g.setStrokeStyle(temp);
	}
	
	/**
	 * Draw all eng.bodies
	 * @param g - Graphics
	 */
	public void drawBodies(Context2d g){	
		for(int i=0;i<peng.bodies.size();i++){
			peng.bodies.elementAt(i).drawBody(g);
		}		
	}
		
		
	/**
	 * Draw all vectors
	 * @param g - Graphics
	 */
	public void drawVecs(Context2d g){
		if(drawMomentum)drawMomentum(g);
		if(creationVec!=null)drawCreationVec(g);
	}
	
	
	/**
	 * To draw total momentum vector from the center of mass
	 * @param g - Graphics to draw with
	 */
	public void drawMomentum(Context2d g){
		RigidBody b;
		Vec s = new Vec();
		for(int i=0;i<peng.bodies.size();i++){
			b=peng.bodies.get(i);
			s.add(b.vel.scaleV(1/b.invMass)); 
		}
		s.scale(zoom/sensitivity).draw(g, centerOM(), vecColor);
	}
	
	/**
	 * Draws the creation vector.
	 * @param g - Context2d to draw with.
	 */
	public void drawCreationVec(Context2d g){
		creationVec.draw(g, creationPos, vecColor);
	}
	
	/**
	 * 
	 * @return The center of mass of the collection of balls
	 */
	public Vec centerOM(){
		Vec s = new Vec(0,0);
		double m=0;
		RigidBody b;
		for(int i=0;i<peng.bodies.size();i++){
			b=peng.bodies.get(i);
			s.add(b.pos.scaleV(1/b.invMass));
			m+=1/b.invMass;
		}
		return transform(s.scale(1/m));
	}
	
	
	//zoom and pan transforms of Context2d
	/**
	 * Translate then scale
	 * @param g - Context2d to transform
	 */
	public void transform(Context2d g){
		g.translate(pan.x, pan.y);
		g.scale(zoom, zoom);
	}
	
	/**
	 * Inverse scale then inverse translate
	 * @param g - Context2d to transform
	 */
	public void invTransform(Context2d g){
		g.scale(1/zoom, 1/zoom);	
		g.translate(-pan.x, -pan.y);
	}
	
	//zoom and pan transforms of vectors
	/**
	 * Inverse scale then inverse translate
	 * @param v - Vec to transform
	 */
	public Vec transform(Vec v){
		return v.scaleV(zoom).plus(pan.x, pan.y);
	}
	
	/**
	 * Inverse translate then inverse scale
	 * @param v - Vec to transform
	 */
	public Vec invTransform(Vec v){
		return v.plus(-pan.x, -pan.y).scaleV(1/zoom);
	}
	
	/**
	 * Update the pan if a ball is being tracked.
	 */
	public void updatePan() {
		
		if(trackedBall!=null){
			pan.set(trackedBall.pos.scaleV(-zoom).plus(getCentre()));
		}
		
	}
	
	
	/**
	 * Tracks the first ball found which contains the point given by Vec v.
	 * If no such ball is found, no tracking is done.
	 * @param v - Defines the point which is used to find a ball to track
	 */
	public void trackBall(Vec v) {
		trackedBall = null;
		for(int i=0;i<peng.bodies.size();i++){
			Ball a = (Ball) peng.bodies.elementAt(i);
			Vec c = transform(a.pos);
			if(v.minus(c).mag()<a.r*zoom){
				System.out.println(a.r*zoom);
				trackedBall = a;
				return;
			}
		}
		System.out.println(trackedBall);
	}
	
	/**
	 * 
	 * @return The centre of the main canvas view.
	 */
	public Vec getCentre(){
		if(vp==null)return null;
		double width = vp.getAbsoluteLeft();
		double height = vp.getOffsetHeight();
		return  new Vec(width/2,height/2);
	}
	
	public void setTrail(int t){
		trail = t;
		bgColor = CssColor.make("rgba(0,0,0,"+((100.0-trail)/100)+")");
	}
	
	public int getTrail(){
		return trail;
	}
	
	/**
	 * Perform all the draw operations on the main canvas associated with this
	 * GraphicsEngine.
	 */
	public void draw(){
		updatePan();
		clearImage();
		
		transform(bot);
		drawBodies(bot);
		invTransform(bot);
		
		transform(top);
		if(peng.walls)drawWalls(top);
		invTransform(top);
		drawVecs(top);
		if(trackedBall!=null)drawCrosshair(top);
		if(mouseCHPos!=null)drawCrosshair(top,mouseCHPos,crosshairW,crosshairH);
		
		g.drawImage(bot.getCanvas(), 0, 0);
		g.drawImage(top.getCanvas(), 0, 0);
		
	}
	
}
