package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import android.content.Context;
import android.graphics.Rect;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.BridgeInflater;
import android.view.View;
import android.view.ViewRootImpl;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.android.ide.common.resources.FrameworkResources;
import com.android.ide.common.resources.ProjectResources;
import com.android.ide.common.resources.ResourceResolver;
import com.android.ide.common.resources.configuration.FolderConfiguration;
import com.android.ide.common.resources.platform.AttrsXmlParser;
import com.android.internal.policy.impl.PhoneWindow;
import com.android.io.FolderWrapper;
import com.android.layoutlib.bridge.Bridge;
import com.android.layoutlib.bridge.android.BridgeContext;

public class Main {
	private static Canvas canvas;
	private static BridgeContext context;
	private static int index = 0;
	private static Image image;
	private static GC gc;
	public static boolean stop;
	private static ViewRootImpl viewRootImpl;
	public static android.graphics.Canvas canvas2;
	private static Display display;
	private static Looper looper;
	private static Animation animation;

	private static final String[] icons = new String[] {
			"res/drawable-hdpi/friends_button_greet_icon_default.png",
			"res/drawable-hdpi/friends_button_greet_icon_noclick.png",
			"res/drawable-hdpi/friends_button_greet_icon_pressed.png",
			"res/drawable-hdpi/friends_button_reg_album_pressed.png",
			"res/drawable-hdpi/friends_button_reg_camera_pressed.png",
			"res/drawable-hdpi/friends_button_send_msg_icon_default.png",
	// "res/friends_icon_logo.png", "res/friends_icon_reg_gg.jpg",
	// "res/ic_launcher.png"
	};

	private static final String LAYOUT_LIB_JAR_OS_PATH = "/home/lonerr/Tools/worktools/android-sdk-linux/platforms/android-17/data/layoutlib.jar";
	private static final String PROJECT_RES_FOLDER_PATH = "/home/lonerr/Works/eclipse/androidUI/res";
	private static final String FRAMEWORK_RES_FOLDER_PATH = "/home/lonerr/Tools/worktools/android-sdk-linux/platforms/android-17/data/res/";
	private static final String FRAMEWORK_FONT_FOLDER_PATH = "/home/lonerr/Works/eclipse/androidUI/fonts";
	private static final String FRAMEWORK_ATTR_FILE_PATH = "/home/lonerr/Tools/worktools/android-sdk-linux/platforms/android-17/data/res/values/attrs.xml";

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		start();
	}

	private static void start() throws FileNotFoundException {
		System.loadLibrary("system");
		stop = false;
		display = new Display();
		System.out.println(display.getDPI());
		final Shell shell = new Shell(display);
		shell.setBounds(0, 0, 800, 480);
		shell.setLayout(new FillLayout());
		canvas = new Canvas(shell, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
		canvas.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
		canvas.addControlListener(new ControlListener() {

			@Override
			public void controlResized(ControlEvent e) {
				// TODO Auto-generated method stub
				if (viewRootImpl != null) {
					Rect rect = new Rect();
					Rectangle rectangle = canvas.getBounds();
					rect.set(rectangle.x, rectangle.y, rectangle.x
							+ rectangle.width, rectangle.y + rectangle.height);
					viewRootImpl.dispatchResized(rect, rect, rect, true, null);
					if (image != null) {
						image.dispose();
					}
					image = new Image(display, rectangle.width,
							rectangle.height);
					if (gc != null) {
						gc.dispose();
					}
					gc = new GC(image);
					canvas2.setGC(gc);
				}
			}

			@Override
			public void controlMoved(ControlEvent e) {
				// TODO Auto-generated method stub

			}
		});
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

		canvas2 = new android.graphics.Canvas();
		canvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				if (image != null)
					e.gc.drawImage(image, 0, 0);
				// for (int startx = 0; startx < canvas.getBounds().width;
				// startx += 10) {
				// e.gc.drawLine(startx, 0, startx, canvas.getBounds().height);
				// }
				// for (int starty = 0; starty < canvas.getBounds().height;
				// starty += 10) {
				// e.gc.drawLine(0, starty, canvas.getBounds().width, starty);
				// }
			}
		});
		shell.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.character) {
				case 'r':
					canvas.redraw();
					break;
				case 'q':
					viewRootImpl.die(true);
					break;
				}
			}
		});

		new Thread(new Runnable() {
			@Override
			public void run() {
				FrameworkResources frameworkResources = new FrameworkResources();
				ProjectResources projectResources = new ProjectResources(
						com.lonerr.sqlitetest.R.class);
				try {
					frameworkResources.loadResources(new FolderWrapper(
							FRAMEWORK_RES_FOLDER_PATH));
					frameworkResources.loadPublicResources(new FolderWrapper(
							FRAMEWORK_RES_FOLDER_PATH), null);
					projectResources.loadResources(new FolderWrapper(
							PROJECT_RES_FOLDER_PATH));
					projectResources.loadPublicResources(new FolderWrapper(
							PROJECT_RES_FOLDER_PATH), null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				AttrsXmlParser attrsXmlParser = new AttrsXmlParser(
						FRAMEWORK_ATTR_FILE_PATH, null, 1000);
				attrsXmlParser.preload();
				new Bridge().init(null, new File(FRAMEWORK_FONT_FOLDER_PATH),
						attrsXmlParser.getEnumFlagValues(), null);
				DisplayMetrics metrics = new DisplayMetrics();
				metrics.densityDpi = metrics.noncompatDensityDpi = DisplayMetrics.DENSITY_DEFAULT;

				metrics.density = metrics.noncompatDensity = metrics.densityDpi
						/ (float) DisplayMetrics.DENSITY_DEFAULT;

				metrics.scaledDensity = metrics.noncompatScaledDensity = metrics.density;

				metrics.widthPixels = metrics.noncompatWidthPixels = 800;
				metrics.heightPixels = metrics.noncompatHeightPixels = 480;
				context = new BridgeContext(
						null,
						metrics,
						ResourceResolver.create(
								projectResources
										.getConfiguredResources(new FolderConfiguration()),
								frameworkResources
										.getConfiguredResources(new FolderConfiguration()),
								"Theme.DeviceDefault", false),
						projectResources, null,
						android.os.Build.VERSION_CODES.JELLY_BEAN_MR1);
				context.setBridgeInflater(new BridgeInflater(context,
						projectResources));
				context.initResources();

				Looper.prepare();
				PhoneWindow phoneWindow = new PhoneWindow(context);
				phoneWindow
						.setContentView(com.lonerr.sqlitetest.R.layout.activity_main);
				WindowManager wm = (WindowManager) context
						.getSystemService(Context.WINDOW_SERVICE);
				android.view.Display display = wm.getDefaultDisplay();
				viewRootImpl = new ViewRootImpl(context, display);
				viewRootImpl.setView(phoneWindow.getDecorView(),
						phoneWindow.getAttributes(), phoneWindow.getDecorView());

				Main.display.asyncExec(new Runnable() {

					@Override
					public void run() {
						shell.open();
					}
				});

				View view = phoneWindow
						.findViewById(com.lonerr.sqlitetest.R.id.imageView1);
				animation = AnimationUtils.loadAnimation(context,
						com.android.internal.R.anim.slide_in_left);
				view.setAnimation(animation);
				animation.setRepeatCount(Animation.INFINITE);
				animation.start();
				looper = Looper.myLooper();
				Looper.loop();
			}
		}).start();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
			else {
			}
		}

		looper.quit();
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

	public static void refresh() {
		if (display != null) {
			display.syncExec(new Runnable() {

				@Override
				public void run() {
					canvas.redraw();
				}
			});
		}
	}
}