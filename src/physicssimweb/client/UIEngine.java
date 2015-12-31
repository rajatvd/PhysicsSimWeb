package physicssimweb.client;

//import gwtquery.plugins.enhance.client.colorpicker.ColorPicker;
//import gwtquery.plugins.enhance.client.colorpicker.ColorPickerFactory.ColorPickerType;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
//import com.google.gwt.widgetideas.client.SliderBar;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
//import com.google.gwt.query.client.GQuery;
//import com.google.gwt.query.client.Function;
//import com.google.gwt.query.client.Selector;
//import com.google.gwt.query.client.Selectors;
//import com.kiouri.sliderbar.client.solution.kde.KDEHorizontalLeftBW;
//
//import static com.google.gwt.query.client.GQuery.*;
//import static com.google.gwt.query.client.css.CSS.*;
//import static gwtquery.plugins.enhance.client.Enhance.Enhance;

public class UIEngine {
	
	private Canvas mainCanvas;
	
	private VerticalPanel vertPanel;
	private HorizontalPanel resetCentre;
	private DockLayoutPanel dockPanel;
	
	private PhysicsEngine peng;
	private GraphicsEngine geng;
	private PhysicsAnimator anim;
	
	private Vec pos;
	
	private Button reset, centreView, lockCamera, playPause;
	private CheckBox momentum, walls;
//	ColorPicker c;
	
//	private SliderBar resti;
	
	public boolean creatingBall = true, lockingCamera = false;
	private HandlerRegistration mouseMove;
	
	
	private IntegerBox grav, resti, trail, radius, mass;
	private Label gravL, restiL, trailL, radiusL, massL;
	private int oldgrav, oldresti, oldtrail, oldradius, oldmass;
	private HorizontalPanel gravP, restiP, trailP, radiusP, massP;
	
	private Canvas previewCanvas;
	private int prevW = 100, prevH = 100;
	
	private Label keLabel;
	
	
	public UIEngine(Canvas mainC, PhysicsEngine pee, GraphicsEngine gee, PhysicsAnimator an){
		mainCanvas = mainC;
		peng = pee;
		geng = gee;
		anim = an;
	}
	
	public void createUI(){
		
		
		dockPanel = new DockLayoutPanel(Unit.EM);
		vertPanel = new VerticalPanel();
		
		dockPanel.addEast(vertPanel, 20.5);
		dockPanel.add(mainCanvas);
		dockPanel.setHeight("100%");
		
		vertPanel.setHeight("100%");
		vertPanel.setWidth("100%");
		geng.vp = vertPanel;

		vertPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		
//		resti = new SliderBar(0,100);
//		resti.setWidth("100%");
//		resti.setNumLabels(10);
//		resti.setNumTicks(10);
//		resti.setEnabled(true);
//		vertPanel.add(resti);
//		
//		KDEHorizontalLeftBW sliderBar = new KDEHorizontalLeftBW(100, "100px");
//		sliderBar.drawMarks("white",6);
//		sliderBar.setMinMarkStep(3);
//		vertPanel.add(sliderBar);
//		IntegerBox i = new IntegerBox();
//		i.setName("testtest");
//		vertPanel.add(i);
//		$(i).as(Enhance).slider(0,2);
		
		makeButtons();
		makeLabels();
		makeBoxes();
		makeCheckBoxes();
		makeHorizontalPanels();
		makePreview();
		
		vertPanel.add(resetCentre);
		
		vertPanel.add(gravP);
		vertPanel.add(restiP);
		vertPanel.add(trailP);
		
		vertPanel.add(walls);
		vertPanel.add(momentum);
		vertPanel.add(keLabel);
		
		vertPanel.add(previewCanvas);
		updatePreview();
		
		vertPanel.add(radiusP);
		vertPanel.add(massP);
		
		vertPanel.add(lockCamera);
		vertPanel.add(playPause);
		
		addClickHandlers();
		addChangeHandlers();
		
		RootLayoutPanel rp = RootLayoutPanel.get();
		rp.add(dockPanel);
		
	}
	
	private void updatePreview(){
		Context2d g = previewCanvas.getContext2d();
		g.setFillStyle(geng.rawBG);
		g.fillRect(0, 0, prevW, prevH);
		g.setFillStyle(CssColor.make("white"));
		
		g.beginPath();
		g.arc(prevW/2, prevH/2, peng.radius, 0, 2*Math.PI);
		g.fill();
		
	}
	
	private void makePreview(){
		previewCanvas = Canvas.createIfSupported();
		previewCanvas.setCoordinateSpaceHeight(prevW);
		previewCanvas.setCoordinateSpaceWidth(prevH);
		previewCanvas.setPixelSize(prevW, prevH);
	}
	
	private void makeCheckBoxes(){
		momentum = new CheckBox("Show momentum vector");
		walls = new CheckBox("Walls");
	}
	
	/**
	 * Make the horixontal panels.
	 */
	private void makeHorizontalPanels(){
		int spacing = 15;
		
		resetCentre = new HorizontalPanel();
		resetCentre.add(reset);
		resetCentre.add(centreView);
		resetCentre.setSpacing(spacing);
		
		gravP = new HorizontalPanel();
		gravP.add(gravL);
		gravP.add(grav);
		gravP.setSpacing(spacing);
		
		restiP = new HorizontalPanel();
		restiP.add(restiL);
		restiP.add(resti);
		restiP.setSpacing(spacing);
		
		trailP = new HorizontalPanel();
		trailP.add(trailL);
		trailP.add(trail);
		trailP.setSpacing(spacing);
		
		radiusP = new HorizontalPanel();
		radiusP.add(radiusL);
		radiusP.add(radius);
		radiusP.setSpacing(spacing);
		
		massP = new HorizontalPanel();
		massP.add(massL);
		massP.add(mass);
		massP.setSpacing(spacing);
	}
	
	/**
	 * Make all the input boxes.
	 */
	private void makeBoxes(){
		String width = "100px";
		
		grav = new IntegerBox();
		resti = new IntegerBox();
		trail = new IntegerBox();
		radius = new IntegerBox();
		mass = new IntegerBox();
		
		grav.setValue((int)peng.grav);
		resti.setValue((int)(peng.res*100));
		trail.setValue(geng.getTrail());
		radius.setValue(peng.radius);
		mass.setValue((int)peng.mass);
		
		grav.setWidth(width);
		resti.setWidth(width);
		trail.setWidth(width);
		radius.setWidth(width);
		mass.setWidth(width);
	}
	
	/**
	 * Make the labels
	 */
	private void makeLabels(){
		gravL = new Label("Gravity strength:");
		restiL = new Label("Restitution percent:");
		trailL = new Label("Trail percent:");
		radiusL = new Label("Radius:");
		massL = new Label("Mass:");
		
		keLabel = new Label();
		peng.keLabel = keLabel;
	}
	
	/**
	 * Make all the buttons
	 */
	private void makeButtons(){
		reset = new Button("Reset");
		centreView = new Button("Centre View");
		lockCamera = new Button("Lock Camera");
		playPause = new Button("Pause");
	}
	
	/**
	 * Add click handlers for all the buttons.
	 */
	private void addClickHandlers(){
		reset.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				peng.bodies.clear();
				geng.zoom=1;
				geng.centreView();
				geng.trackedBall = null;
				
			}
		});
		
		centreView.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				geng.centreView();
			}		
		});
		
		lockCamera.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				if(lockCamera.getText().equals("Lock Camera")){
					lockingCamera = true;
					creatingBall = false;
					lockCamera.setText("Create ball");
					pos = new Vec();
					geng.mouseCHPos = pos;
					mouseMove = geng.mainCanvas.addMouseMoveHandler(new MouseMoveHandler(){
						@Override
						public void onMouseMove(MouseMoveEvent event) {
							pos.x = event.getX();
							pos.y = event.getY();
						}
						
					});
				}else if(lockCamera.getText().equals("Create ball")){
					lockingCamera = false;
					creatingBall = true;
					lockCamera.setText("Lock Camera");
					mouseMove.removeHandler();
					geng.mouseCHPos = null;
				}
			}
		});
		
		playPause.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				if(playPause.getText().equals("Play")){
					anim.running = true;
					playPause.setText("Pause");
				}else if(playPause.getText().equals("Pause")){
					anim.running = false;
					playPause.setText("Play");
				}
			}
		});
	}
	
	/**
	 * Add handlers for inputs
	 */
	private void addChangeHandlers(){
		//Numeric input fields
		grav.addKeyUpHandler(new KeyUpHandler(){

			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(validate(grav,-1000,1000)){
					oldgrav = grav.getValue();
					peng.grav = oldgrav;
				}else{
					grav.setValue(oldgrav);
				}
			}
			
		});
		
		resti.addKeyUpHandler(new KeyUpHandler(){

			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(validate(resti,0,100)){
					oldresti = resti.getValue();
					peng.res = oldresti/100.0;
				}else{
					resti.setValue(oldresti);
				}
			}
			
		});
		
		trail.addKeyUpHandler(new KeyUpHandler(){

			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(validate(trail,0,100)){
					oldtrail = trail.getValue();
					geng.setTrail(oldtrail);
				}else{
					trail.setValue(oldtrail);
				}
			}
			
		});
		
		radius.addKeyUpHandler(new KeyUpHandler(){

			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(validate(radius,0,50)){
					oldradius = radius.getValue();
					peng.radius = oldradius;
					updatePreview();
				}else{
					radius.setValue(oldradius);
				}
			}
			
		});
		
		mass.addKeyUpHandler(new KeyUpHandler(){

			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(validate(mass,1,1000)){
					oldmass = mass.getValue();
					peng.mass = oldmass;
				}else{
					mass.setValue(oldmass);
				}
			}
			
		});
		
		//Checkboxes
		momentum.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				geng.drawMomentum = momentum.getValue();
			}
		});
		
		walls.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				peng.walls = walls.getValue();
			}
		});
		
	}
	
	/**
	 * Check if value in box is between low and high inclusive.
	 * @param box - Box to check
	 * @param low - lower bound
	 * @param high - upper bound
	 * @return - whether box's value lies between low and high
	 */
	private boolean validate(IntegerBox box, int low, int high){
		String text = box.getText();
		if(text.equals("")){
			box.setValue(0);
			return true;
		}
		if(low<0 && text.equals("-"))return true;
		int in;
		try{
			in = Integer.parseInt(text);
			return in>=low && in <=high;
		}catch(NumberFormatException nfe){
			return false;
		}
	}
	
}
