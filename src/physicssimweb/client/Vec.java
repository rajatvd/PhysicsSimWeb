package physicssimweb.client;


import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.FillStrokeStyle;


public class Vec {
	
	public double x,y,z=0;
	public static final Vec ZERO = new Vec(0,0);
	
	public Vec(){
		x=0;y=0;z=0;
	}
	
	public Vec(String s){
		s = s.replace("(", "").replace(")", "");
		String[] nums = s.split(",");
		if(nums.length==2){
			x=Double.parseDouble(nums[0]);
			y=Double.parseDouble(nums[1]);
		}else if(nums.length==3){
			x=Double.parseDouble(nums[0]);
			y=Double.parseDouble(nums[1]);
			z=Double.parseDouble(nums[2]);
		}
	}
	
	public Vec(double xx, double yy, double zz){
		x=xx;y=yy;z=zz;
	}
	public Vec(double xx, double yy){
		x=xx;y=yy;z=0;
	}
	
	public Vec(Vec v) {
		x=v.x;
		y=v.y;
		z=v.z;
	}
	
	
	public Vec set(Vec v){
		x=v.x;
		y=v.y;
		z=v.z;
		return this;
	}
	
	public Vec add(Vec v){
		x+=v.x;
		y+=v.y;
		z+=v.z;
		return this;
	}
	
	public Vec add(double xx, double yy, double zz){
		x+=xx;
		y+=yy;
		z+=zz;
		return this;
	}
	
	public Vec add(double xx, double yy){
		x+=xx;
		y+=yy;
		return this;
	}
	
	public Vec plus(Vec v){
		return new Vec(x+v.x,y+v.y,z+v.z);
	}
	
	public Vec plus(double xx, double yy){
		return new Vec(x+xx,y+yy,z);
	}
	
	public Vec subtract(Vec v){
		x-=v.x;
		y-=v.y;
		z-=v.z;
		return this;
	}
	
	public Vec minus(Vec v){
		return new Vec(x-v.x,y-v.y,z-v.z);
	}
	
	public Vec minus(double xx, double yy){
		return new Vec(x-xx,y-yy,z);
	}
	
	public double dot(Vec v){
		return x*v.x + y*v.y + z*v.z;
	}
	
	public Vec scale(double f){
		x*=f;y*=f;z*=f;
		return this;
	}
	public Vec scaleV(double f){
		return new Vec(x*f,y*f,z*f);
	}
	
	public double mag(){
		return Math.sqrt(x*x+y*y+z*z);
	}
	
	public Vec cross(Vec v){
		return new Vec(y*v.z - z*v.y,
					   z*v.x - x*v.z,
					   x*v.y - y*v.x);
	}
	
	/**
	 * Draws a Vec using a Graphics object.
	 * @param g - Context2d object to draw the Vec
	 * @param origin - Starting point of the Vec
	 * @param c - CSS color to draw with
	 */
	public void draw(Context2d g, Vec origin, CssColor c){
		int mag = (int) this.mag();
		FillStrokeStyle cc = g.getFillStyle();
		//transform
		g.translate(origin.x, origin.y);
		g.rotate(Math.atan2(y,x));
		g.setFillStyle(c);
		g.setStrokeStyle(c);
		
		//draw vector
		g.beginPath();
		g.lineTo(0,0);
		g.lineTo(mag-10,0);
		g.stroke();
		g.closePath();
		g.beginPath();
		g.lineTo(mag-10, 0);
		g.lineTo(mag-10, 5);
		g.lineTo(mag, 0);
		g.lineTo(mag-10, -5);
		g.lineTo(mag-10, 0);
		g.fill();
		g.closePath();
		
		//undo transform
		g.rotate(-Math.atan2(y,x));
		g.translate(-origin.x, -origin.y);
		g.setFillStyle(cc);
		g.setStrokeStyle(cc);
	}
	
	/**
	 * Draws a Vec using a Context2d object with a default color of orange.
	 * @param g - Context2d object to draw the Vec
	 * @param origin - Starting point of the Vec
	 */
	public void draw(Context2d g, Vec origin){
		draw(g,origin,CssColor.make("orange"));
	}
	
	/**
	 * Format: (x,y,z)
	 */
	public String toString(){
		return "("+ x +","+ y +","+ z +")";
	}
	
}
