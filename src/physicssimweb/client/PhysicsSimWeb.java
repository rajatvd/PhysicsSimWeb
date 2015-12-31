package physicssimweb.client;



import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PhysicsSimWeb implements EntryPoint {
	
	final static int DELAY = 10;
	
	private PhysicsEngine peng;
	private GraphicsEngine geng;
	private UIEngine ueng;
	private PhysicsAnimator anim;
	private Canvas mainCanvas, topLayer, botLayer;
	private int width = 1300, height = 700;
	private Vec creationPos = new Vec(), creationVel = new Vec();
	
	private Vec start, end, 
			oldPan = new Vec(), pan;

	private HandlerRegistration mouseMove;
	
	private boolean leftDragging = false, rightDragging = false
			;//,paused = false, temp;
	
	
	
	public void onModuleLoad() {
		mainCanvas = Canvas.createIfSupported();
		topLayer = Canvas.createIfSupported();
		botLayer = Canvas.createIfSupported();
		if (mainCanvas == null) {
            RootPanel.get().add(new Label("Sorry, your browser doesn't "+
            					"support the HTML5 Canvas element"));
            return;
        }
		mainCanvas.setCoordinateSpaceWidth(width);
		mainCanvas.setCoordinateSpaceHeight(height);
		mainCanvas.setPixelSize(width, height);
		topLayer.setCoordinateSpaceWidth(width);
		topLayer.setCoordinateSpaceHeight(height);
		topLayer.setPixelSize(width, height);
		botLayer.setCoordinateSpaceWidth(width);
		botLayer.setCoordinateSpaceHeight(height);
		botLayer.setPixelSize(width, height);
		
		peng = new PhysicsEngine();
		
		geng = new GraphicsEngine(peng, mainCanvas, topLayer, botLayer);
		pan = geng.pan;
		
		anim = new PhysicsAnimator(geng,peng);
		
		ueng = new UIEngine(mainCanvas, peng, geng, anim);
		ueng.createUI();
		
//		geng.draw(c2);
//		c2.fillRect(150, 150, 20, 20);
//		Timer t = new Timer(){
//			public void run(){
//				c2.drawImage(cTop.getCanvas(), 0, 0);
//			}
//		};
//		t.schedule(2000);
		

//		Timer timer = new Timer(){
//			public void run(){
//				if(paused)return;
//				peng.update();
//				geng.draw();
//				c2.drawImage(cTop.getCanvas(), 0, 0);
//				new Vec(500,500).draw(c2, Vec.ZERO, CssColor.make("red"));
//			}
//		};
//		timer.scheduleRepeating(DELAY);
		
		
		
		anim.run(10000000);
		
		
		
		mainCanvas.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
//				paused = true;
				if(event.getNativeButton() == NativeEvent.BUTTON_LEFT){
					leftDragging = true;
					
					if(ueng.creatingBall){
						creationPos.x = event.getX();
						creationPos.y = event.getY();
						creationVel.x = 0;
						creationVel.y = 0;
						geng.creationPos = creationPos;
						geng.creationVec = creationVel;
					}else if(ueng.lockingCamera){
						geng.trackBall(new Vec(event.getX(),event.getY()));
					}
				}else if(event.getNativeButton() == NativeEvent.BUTTON_RIGHT){
					rightDragging = true;
					geng.trackedBall = null;
					oldPan.x = pan.x;
					oldPan.y = pan.y;
					start = new Vec(event.getX(),event.getY());
					end = new Vec(start.x,start.y);
				}
				
				
				
				mouseMove = mainCanvas.addMouseMoveHandler(new MouseMoveHandler() {
					@Override
					public void onMouseMove(MouseMoveEvent event) {
						if(leftDragging){
							creationVel.x = event.getX()-creationPos.x;
							creationVel.y = event.getY()-creationPos.y;
							
						}else if(rightDragging){
							end.x=event.getX();
							end.y=event.getY();
							Vec p = oldPan.plus(end.minus(start));
							pan.x = p.x;
							pan.y = p.y;
						}
					}
				});
				
			}
		});
		mainCanvas.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				if(event.getNativeButton() == NativeEvent.BUTTON_LEFT){
					leftDragging = false;
					mouseMove.removeHandler();
					if(ueng.creatingBall){
						creationVel.x = event.getX()-creationPos.x;
						creationVel.y = event.getY()-creationPos.y;
						
						Ball b = new Ball();
						b.pos = geng.invTransform(creationPos);
						b.vel = creationVel.scaleV(geng.sensitivity/geng.zoom);
						b.invMass = 1/peng.mass;
						b.r = peng.radius;
						
						peng.addBody(b);
						geng.creationVec = null;
					}
				}else if(event.getNativeButton() == NativeEvent.BUTTON_RIGHT){
					rightDragging = false;
					mouseMove.removeHandler();
					
					
				}
			}
		});
		
		mainCanvas.addMouseWheelHandler(new MouseWheelHandler() {
			
			@Override
			public void onMouseWheel(MouseWheelEvent event) {
				double newZoom = geng.zoom*(1-event.getDeltaY()/50.0);
				geng.zoom(newZoom, new Vec(event.getX(),event.getY()));
			}
		});
				
	}
		
}
