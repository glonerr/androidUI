import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ContextImpl;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Looper;
import android.util.Xml;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AnalogClock;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class Main {
	private static Canvas canvas;
	private static final String[] icons = new String[] {
			"res/friends_button_greet_icon_default.png",
			"res/friends_button_greet_icon_noclick.png",
			"res/friends_button_greet_icon_pressed.png",
			"res/friends_button_reg_album_pressed.png",
			"res/friends_button_reg_camera_pressed.png",
			"res/friends_button_send_msg_icon_default.png",
	// "res/friends_icon_logo.png", "res/friends_icon_reg_gg.jpg",
	// "res/ic_launcher.png"
	};

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		System.loadLibrary("system");
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setBounds(0, 0, 640, 480);
		shell.setLayout(new FillLayout());
		canvas = new Canvas(shell, SWT.NULL);
		canvas.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
		canvas.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent e) {
				shell.setText((e.x - canvas.getBounds().x) + ":"
						+ (e.y - canvas.getBounds().y));
			}
		});
		canvas.addDragDetectListener(new DragDetectListener() {

			@Override
			public void dragDetected(DragDetectEvent e) {
				System.out.println(e);
			}
		});

		MyContext context = new MyContext();
		context.startLoop();
		final LinearLayout layout = new LinearLayout(context);
		layout.setGravity(Gravity.CENTER);
		// layout.setOrientation(LinearLayout.VERTICAL);
		ImageView imageView = null;
		for (String path : icons) {
			imageView = new ImageView(context);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT, 1);
			imageView.setLayoutParams(params);
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			imageView.setImageDrawable(new BitmapDrawable(new FileInputStream(
					path)));
			layout.addView(imageView);
		}
		Looper.prepare();
		AnalogClock analogClock = new AnalogClock(context);
		layout.addView(analogClock);
		analogClock.onAttachedToWindow();
		
		RelativeLayout frameLayout = new RelativeLayout(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.FILL_PARENT, 1);
		frameLayout.setLayoutParams(params);
		for (String path : icons) {
			imageView = new ImageView(context);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			layoutParams.addRule(RelativeLayout.ALIGN_BASELINE);
			imageView.setLayoutParams(layoutParams);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setImageDrawable(new BitmapDrawable(new FileInputStream(
					path)));
			frameLayout.addView(imageView);
		}
		layout.addView(frameLayout);

		canvas.addControlListener(new ControlListener() {

			@Override
			public void controlResized(ControlEvent e) {
				layout.measure(View.MeasureSpec.makeMeasureSpec(
						canvas.getBounds().width, View.MeasureSpec.EXACTLY),
						View.MeasureSpec.makeMeasureSpec(
								canvas.getBounds().height,
								View.MeasureSpec.EXACTLY));
				layout.layout(canvas.getBounds().x, canvas.getBounds().y,
						canvas.getBounds().width + canvas.getBounds().x,
						canvas.getBounds().y + canvas.getBounds().height);
			}

			@Override
			public void controlMoved(ControlEvent e) {
				// TODO Auto-generated method stub

			}
		});
		
		// Animation animation;
		// try {
		// XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		// factory.setNamespaceAware(true);
		// XmlPullParser xpp = factory.newPullParser();
		// xpp.setInput(new InputStreamReader(new FileInputStream(
		// "res/slide_in_left.xml")));
		// animation = AnimationUtils.createAnimationFromXmlImpl(context, xpp,
		// null, Xml.asAttributeSet(xpp));
		// imageView.setAnimation(animation);
		// animation.start();
		// } catch (XmlPullParserException e1) {
		// } catch (IOException e1) {
		// }

		final android.graphics.Canvas canvas2 = new android.graphics.Canvas();
		canvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				canvas2.setGC(e.gc);
				e.gc.setForeground(e.gc.getDevice().getSystemColor(
						SWT.COLOR_BLACK));
				for (int startx = 0; startx < canvas.getBounds().width; startx += 10) {
					e.gc.drawLine(startx, 0, startx, canvas.getBounds().height);
				}
				for (int starty = 0; starty < canvas.getBounds().height; starty += 10) {
					e.gc.drawLine(0, starty, canvas.getBounds().width, starty);
				}
				layout.draw(canvas2);
			}
		});
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(!canvas.isDisposed()){
					display.asyncExec(new Runnable() {
						
						@Override
						public void run() {
							canvas.redraw();
						}
					});
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		canvas.redraw();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
			else {
			}
		}
		context.stopLoop();
		display.dispose();
	}

	public static void printClass(Class<?> class1) {
		Method[] methods = class1.getDeclaredMethods();
		Arrays.sort(methods, new MyComparator());
		for (Method method : methods) {
			System.out.println(method.toString());
		}
	}

	private static class MyComparator implements Comparator<Method> {

		@Override
		public int compare(Method o1, Method o2) {
			// TODO Auto-generated method stub
			return ((Method) o1).toString().compareTo(((Method) o2).toString());
		}

	}

}