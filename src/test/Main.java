package test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JFrame;

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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlBlock;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import com.lonerr.skia.core.SkMatrix;
import com.lonerr.skia.core.SkRect;
import com.lonerr.sqlitetest.R;
import com.lonerr.utils.String8;

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
import com.lonerr.androidfw.AssetManager;
import com.lonerr.bridge.graphics.BitmapBridge;
import com.lonerr.bridge.util.XmlBlockBridge;

public class Main {
	private static org.eclipse.swt.widgets.Canvas canvas;
	private static BridgeContext context;
	private static Bitmap bitmap;
	public static boolean stop;
	private static ViewRootImpl viewRootImpl;
	public static android.graphics.Canvas canvas2;
	private static Display display;
	private static Looper looper;
	private static Animation animation;

	private static void testCanvas02(Graphics g) {
		Canvas canvas = new Canvas();
		Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
		// bitmap.eraseColor(0xffff0000);
		canvas.setBitmap(bitmap);
		Bitmap src = BitmapFactory.decodeFile("activity_picker_bg_activated.9.png");
		Paint paint = new Paint();
		canvas.rotate(30);
		NinePatch patch = new NinePatch(src, src.getNinePatchChunk(), "mytest");
		paint.setAlpha(0xff);
		patch.setPaint(paint);
		patch.draw(canvas, new Rect(0, 0, 100, 100));
		patch.draw(canvas, new Rect(150, 0, 170, 60));
		g.drawImage(BitmapBridge.getBitmap(bitmap.mNativeBitmap).fImage, 0, 0, null);
	}

	private static void testBitmap() {
		Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ALPHA_8);
		System.out.println(bitmap.getRowBytes() + "" + bitmap.hasAlpha());
		bitmap.eraseColor(0x3fffff00);
		bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_4444);
		System.out.println(bitmap.getRowBytes() + "" + bitmap.hasAlpha());
		bitmap.eraseColor(0x10202020);
		bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
		System.out.println(bitmap.getRowBytes() + "" + bitmap.hasAlpha());
		bitmap.eraseColor(0x10202020);
		System.out.println("0x" + Integer.toHexString(bitmap.getPixel(0, 0)));
		bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
		System.out.println(bitmap.getRowBytes() + "" + bitmap.hasAlpha());
		bitmap.eraseColor(0x10202020);
	}

	private static final String[] icons = new String[] { "res/drawable-hdpi/friends_button_greet_icon_default.png",
			"res/drawable-hdpi/friends_button_greet_icon_noclick.png",
			"res/drawable-hdpi/friends_button_greet_icon_pressed.png",
			"res/drawable-hdpi/friends_button_reg_album_pressed.png",
			"res/drawable-hdpi/friends_button_reg_camera_pressed.png",
			"res/drawable-hdpi/friends_button_send_msg_icon_default.png",
	// "res/friends_icon_logo.png", "res/friends_icon_reg_gg.jpg",
	// "res/ic_launcher.png"
	};

	private static final String LAYOUT_LIB_JAR_OS_PATH = "/home/lonerr/Tools/worktools/android-sdk/platforms/android-17/data/layoutlib.jar";
	private static final String PROJECT_RES_FOLDER_PATH = "/home/lonerr/Works/eclipse/androidUI/res";
	private static final String FRAMEWORK_RES_FOLDER_PATH = "/home/lonerr/Tools/worktools/android-sdk/platforms/android-17/data/res/";
	private static final String FRAMEWORK_FONT_FOLDER_PATH = "/home/lonerr/Works/eclipse/androidUI/fonts";
	private static final String FRAMEWORK_ATTR_FILE_PATH = "/home/lonerr/Tools/worktools/android-sdk/platforms/android-17/data/res/values/attrs.xml";

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		FileInputStream stream = new FileInputStream("activity_main.xml");
		try {
			byte[] bs = new byte[stream.available()];
			stream.read(bs);
			XmlBlock block = new XmlBlock(bs); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
////		addFrame();
	}

	private static Matrix matrix = new Matrix();
	static {
		matrix.preTranslate(100, 100);
	}

	private static void testPath(Graphics2D g) {
		System.out.println(g.getClass());
		Path path = new Path();
		Path path2 = new Path();
		path2.reset();
		path2.addArc(new RectF(50, 50, 150, 150), 40, 290);
		path.arcTo(new RectF(75, 75, 125, 125), 40, 290);
		Path path3 = new Path();
		long start = System.currentTimeMillis();
		for (int a = 0; a < MAXSIZE; a++) {
			path3.reset();
			path3.addRoundRect(new RectF(100, 100, 200, 175), 10, 10, Path.Direction.CW);
		}
		System.out.println(System.currentTimeMillis() - start);
		path2.setFillType(FillType.EVEN_ODD);
		path2.addPath(path);
		RectF bounds = new RectF();
		path2.computeBounds(bounds, true);
//		g.fill(path2.getSkPath().getAWTPath());
	}

	private static void testCanvas01(Graphics2D g) {
		android.graphics.Canvas canvas = new android.graphics.Canvas();
		Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
		canvas.setBitmap(bitmap);
		canvas.clipRect(0, 0, 100, 100);
		System.out.println(canvas.getClipBounds());
		canvas.rotate(20);
		System.out.println(canvas.getClipBounds());
		canvas.clipRect(new RectF(20, 20, 100, 100));
		Rect rect = canvas.getClipBounds();
		g.setColor(new Color(0x7f7f00));
		// g.fill(fArea);
		g.fill(fPath1);

		System.out.println(rect);
		g.drawRect(rect.left, rect.top, rect.width(), rect.height());
	}

	public static void testRegion(Graphics2D g) {
		SkMatrix matrix = new SkMatrix();
		matrix.preRotate(20);
		g.setColor(new Color(0x7f7f00));
		GeneralPath path = new GeneralPath();
		path.append(new Rectangle(20, 20, 80, 80), false);
		AffineTransform at = matrix.getAffineTransform();
		path.transform(at);
		Area first = new Area(path);
		Area second = new Area(new Rectangle(0, 0, 100, 100));
		g.draw(first);
		g.draw(second);
		first.intersect(second);
		g.fill(first);
		g.draw(first.getBounds());
		System.out.println(first.getBounds());
		g.setColor(new Color(0xff0000));
		g.draw(second.getBounds());
		System.out.println(second.getBounds());
		SkMatrix invert = new SkMatrix();
		matrix.invert(invert);
		SkRect dst = new SkRect();
		SkRect src = new SkRect();
		src.set(0, 25, 88, 100);
		invert.mapRect(dst, src);
		System.out.println(dst);
	}

	private static void testClip() {
		android.graphics.Canvas canvas = new android.graphics.Canvas();
		Rect rect = new Rect(20, 20, 50, 50);
		canvas.clipRect(rect, Region.Op.INTERSECT);
		System.out.println(canvas.getClipBounds());
		Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
		canvas.setBitmap(bitmap);
		canvas.rotate(50, 50, 50);
		System.out.println(canvas.getClipBounds());
		canvas.save();
		canvas.clipRect(rect, Region.Op.INTERSECT);
		System.out.println(canvas.getClipBounds());
		canvas.restore();
		System.out.println(canvas.getClipBounds());
	}

	private static void testCanvas() {
		android.graphics.Canvas canvas = new android.graphics.Canvas();
		System.out.println(canvas.getMatrix());
		long current = System.currentTimeMillis();
		for (int a = 0; a < MAXSIZE; a++) {
			canvas.rotate(20);
		}
		Matrix matrix = new Matrix();
		canvas.setMatrix(matrix);
		System.out.println(canvas.getMatrix());
		matrix.preRotate(20);
		System.out.println(canvas.getMatrix());
		System.out.println(matrix);
		if (DEBUG_TIME)
			System.out.println("rotate:" + (System.currentTimeMillis() - current));
		System.out.println(canvas.getMatrix());
		current = System.currentTimeMillis();
		for (int a = 0; a < MAXSIZE; a++) {
			canvas.save(android.graphics.Canvas.CLIP_SAVE_FLAG);
		}
		if (DEBUG_TIME)
			System.out.println("save:" + (System.currentTimeMillis() - current));
		System.out.println(canvas.getMatrix());
		current = System.currentTimeMillis();
		for (int a = 0; a < MAXSIZE; a++) {
			canvas.rotate(20);
		}
		if (DEBUG_TIME)
			System.out.println("rotate:" + (System.currentTimeMillis() - current));
		System.out.println(canvas.getMatrix());
		current = System.currentTimeMillis();
		for (int a = 0; a < MAXSIZE; a++) {
			canvas.restore();
		}
		if (DEBUG_TIME)
			System.out.println("restore:" + (System.currentTimeMillis() - current));
		System.out.println(canvas.getMatrix());
	}

	private static void test01() {
		Matrix matrix = new Matrix();
		matrix.postRotate(20, 30, 30);
		System.out.println(matrix);
		matrix.postTranslate(30, 30);
		System.out.println(matrix);
		matrix.postScale(40, 30, 50, 20);
		System.out.println(matrix);
		matrix.postSkew(30, 40, 50, 60);
		System.out.println(matrix);
		matrix.preRotate(20, 30, 30);
		System.out.println(matrix);
		matrix.preTranslate(30, 30);
		System.out.println(matrix);
		matrix.preScale(40, 30, 50, 20);
		System.out.println(matrix);
		matrix.preSkew(30, 40, 50, 60);
		System.out.println(matrix);
		Matrix inverse = new Matrix();
		inverse.preTranslate(30, 40);
		matrix.invert(inverse);
		System.out.println(inverse);
	}

	private static void test02() {
		Matrix m = new Matrix();
		m.postRotate(30);
		System.out.println(m);
		m.postTranslate(100, 100);
		System.out.println(m);
		m = new Matrix();
		m.setTranslate(100, 100);
		System.out.println(m);
		m.preRotate(30);
		System.out.println(m);
	}

	private static final int MAXSIZE = 1000000;
	private static final boolean DEBUG_TIME = false;

	private static void testMatrix() {
		Matrix m = new Matrix();
		m.setRotate(10, 40, 100);

		long current = System.currentTimeMillis();
		for (int a = 0; a < MAXSIZE; a++) {
			m.postRotate(30, 50, 50);
		}
		if (DEBUG_TIME)
			System.out.println("postRotate:" + (System.currentTimeMillis() - current));
		System.out.println(m);
		current = System.currentTimeMillis();
		for (int a = 0; a < MAXSIZE; a++) {
			m.postTranslate(100, 100);
		}
		if (DEBUG_TIME)
			System.out.println("postTranslate:" + (System.currentTimeMillis() - current));
		System.out.println(m);
		current = System.currentTimeMillis();
		for (int i = 0; i < MAXSIZE; i++) {
			m.postSkew(2, 3, 30, 30);
		}
		if (DEBUG_TIME)
			System.out.println("postSkew:" + (System.currentTimeMillis() - current));
		System.out.println(m);
		current = System.currentTimeMillis();
		for (int i = 0; i < MAXSIZE; i++) {
			m.postScale(4, 5);
		}
		if (DEBUG_TIME)
			System.out.println("postScale:" + (System.currentTimeMillis() - current));
		System.out.println(m);
		current = System.currentTimeMillis();
		for (int i = 0; i < MAXSIZE; i++) {
			m.preScale(5, 6, 50, 70);
		}
		if (DEBUG_TIME)
			System.out.println("preScale:" + (System.currentTimeMillis() - current));
		System.out.println(m);
		current = System.currentTimeMillis();
		for (int i = 0; i < MAXSIZE; i++) {
			m.preRotate(5, 50, 70);
		}
		if (DEBUG_TIME)
			System.out.println("preRotate:" + (System.currentTimeMillis() - current));
		System.out.println(m);
		current = System.currentTimeMillis();
		for (int i = 0; i < MAXSIZE; i++) {
			m.preTranslate(20, 20);
		}
		if (DEBUG_TIME)
			System.out.println("preTranslate:" + (System.currentTimeMillis() - current));
		System.out.println(m);
		current = System.currentTimeMillis();
		for (int i = 0; i < MAXSIZE; i++) {
			m.preSkew(2, 3, 40, 40);
		}
		if (DEBUG_TIME)
			System.out.println("preSkew:" + (System.currentTimeMillis() - current));
		System.out.println(m);
		Matrix matrix = new Matrix();
		current = System.currentTimeMillis();
		for (int a = 0; a < MAXSIZE; a++) {
			m.invert(matrix);
		}
		if (DEBUG_TIME)
			System.out.println("invert:" + (System.currentTimeMillis() - current));
		System.out.println(matrix);

		float[] pts = new float[8];
		pts[0] = pts[1] = 0;
		pts[2] = 30;
		pts[3] = 0;
		pts[4] = 0;
		pts[5] = 30;
		pts[6] = pts[7] = 30;
		current = System.currentTimeMillis();
		for (int a = 0; a < MAXSIZE; a++) {
			m.mapPoints(pts);
		}
		if (DEBUG_TIME)
			System.out.println("mapPoints:" + (System.currentTimeMillis() - current));
		System.out.println(Arrays.toString(pts));
		RectF rect = new RectF(0, 100, 100, 0);
		current = System.currentTimeMillis();
		for (int a = 0; a < MAXSIZE; a++) {
			m.mapRect(rect);
		}
		if (DEBUG_TIME)
			System.out.println("mapRect:" + (System.currentTimeMillis() - current));
		System.out.println(rect);
		float[] vec = new float[4];
		vec[0] = 100;
		vec[1] = 0;
		vec[2] = 0;
		vec[3] = 100;
		current = System.currentTimeMillis();
		for (int a = 0; a < MAXSIZE; a++) {
			m.mapVectors(vec);
		}
		if (DEBUG_TIME)
			System.out.println("mapVectors:" + (System.currentTimeMillis() - current));
		System.out.println("Vector:" + Arrays.toString(vec));
		current = System.currentTimeMillis();
		float Radius = 0;
		for (int a = 0; a < MAXSIZE; a++) {
			Radius = m.mapRadius(100);
		}
		if (DEBUG_TIME)
			System.out.println("mapRadius:" + (System.currentTimeMillis() - current));
		System.out.println(Radius);
	}

	private static int[] createCircle(int radius, int centerX, int centerY) {
		int[] points = new int[360 * 2];
		for (int i = 0; i < 360; i++) {
			points[i * 2] = centerX + (int) (radius * Math.cos(Math.toRadians(i)));
			points[i * 2 + 1] = centerY + (int) (radius * Math.sin(Math.toRadians(i)));
		}
		return points;
	}

	private static long last, start;
	public static GeneralPath fPath1;
	public static GeneralPath fPath2;
	private static final long interval = 1000 / 60;
	protected static final boolean MirrorY = true;
	private static BufferedImage image;
	public static Area fArea;
	public static Area fRegion;
	public static float scale = 1.0f;

	private static void addFrame() {
		final JFrame frame = new JFrame();
		frame.setTitle("Test");
		frame.setSize(800, 600);
		image = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g2d = (Graphics2D) image.getGraphics();
		final java.awt.Canvas canvas = new java.awt.Canvas() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				if (MirrorY)
					g.drawImage(image, 0, 0, (int) (image.getWidth() * scale), (int) (image.getHeight() * scale), 0,
							image.getHeight(), image.getWidth(), 0, null);
				else
					g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), 0, 0, image.getWidth(),
							image.getHeight(), null);

			}
		};

		AffineTransform transform = new AffineTransform();
		transform.translate(300, 200);
		g2d.transform(transform);
		g2d.scale(scale, scale);
		// testRegion(g2d);
		// testCanvas01(g2d);
		// testClip();
		// testBitmap();

		testCanvas02(g2d);

		g2d.setColor(new Color(0x7f7f00));
		g2d.drawLine(-200, 0, 200, 0);
		g2d.drawLine(0, -200, 0, 200);
		Path arrowX = new Path();
		arrowX.moveTo(200, 0);
		arrowX.lineTo(194, 4);
		arrowX.lineTo(194, -4);
		arrowX.close();
		Path arrowY = new Path();
		arrowY.moveTo(0, 200);
		arrowY.lineTo(4, 194);
		arrowY.lineTo(-4, 194);
		arrowY.close();
//		g2d.fill(arrowX.getSkPath().getAWTPath());
//		g2d.fill(arrowY.getSkPath().getAWTPath());
		canvas.addKeyListener(new java.awt.event.KeyListener() {

			@Override
			public void keyTyped(java.awt.event.KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(java.awt.event.KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyChar() == 'f') {
					canvas.repaint();
				}
			}
		});
		canvas.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
				frame.setTitle("x:" + e.getX() + " y:" + e.getY());
			}

			@Override
			public void mouseDragged(java.awt.event.MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
		canvas.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				scale += (e.getWheelRotation() * 0.1f);
				System.out.println(scale);
				canvas.repaint();
			}
		});
		
		frame.add(canvas);
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
				System.exit(0);
			}

		});
		frame.setVisible(true);
	}

	private static void start() throws FileNotFoundException {
		System.loadLibrary("system");
		stop = false;
		display = new Display();
		System.out.println(display.getDPI());
		final Shell shell = new Shell(display);
		shell.setBounds(0, 0, 800, 480);
		shell.setLayout(new FillLayout());
		canvas = new org.eclipse.swt.widgets.Canvas(shell, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
		canvas.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
		canvas.addControlListener(new ControlListener() {

			@Override
			public void controlResized(ControlEvent e) {
				// TODO Auto-generated method stub
				if (viewRootImpl != null) {
					Rect rect = new Rect();
					org.eclipse.swt.graphics.Rectangle rectangle = canvas.getBounds();
					rect.set(rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y + rectangle.height);
					viewRootImpl.dispatchResized(rect, rect, rect, true, null);
					bitmap = Bitmap.createBitmap(rectangle.width, rectangle.height, Config.ARGB_8888);
					// ImageData data = image.getImageData();
					// data.alpha = 0xbf;
					// image = new Image(null, data);
					canvas2.setBitmap(bitmap);
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
				shell.setText((e.x - canvas.getBounds().x) + ":" + (e.y - canvas.getBounds().y));
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
				for (int startx = 0; startx < canvas.getBounds().width; startx += 10) {
					e.gc.drawLine(startx, 0, startx, canvas.getBounds().height);
				}
				for (int starty = 0; starty < canvas.getBounds().height; starty += 10) {
					e.gc.drawLine(0, starty, canvas.getBounds().width, starty);
				}
				// if (bitmap != null)
				// e.gc.drawImage(bitmap.mImage, 0, 0);
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
					if (viewRootImpl != null) {
						viewRootImpl.die(true);
						viewRootImpl = null;
					}
					break;
				}
			}
		});

		new Thread(new Runnable() {
			@Override
			public void run() {
				FrameworkResources frameworkResources = new FrameworkResources();
				ProjectResources projectResources = new ProjectResources(com.lonerr.sqlitetest.R.class);
				try {
					frameworkResources.loadResources(new FolderWrapper(FRAMEWORK_RES_FOLDER_PATH));
					frameworkResources.loadPublicResources(new FolderWrapper(FRAMEWORK_RES_FOLDER_PATH), null);
					projectResources.loadResources(new FolderWrapper(PROJECT_RES_FOLDER_PATH));
					projectResources.loadPublicResources(new FolderWrapper(PROJECT_RES_FOLDER_PATH), null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				AttrsXmlParser attrsXmlParser = new AttrsXmlParser(FRAMEWORK_ATTR_FILE_PATH, null, 1000);
				attrsXmlParser.preload();
				new Bridge().init(null, new File(FRAMEWORK_FONT_FOLDER_PATH), attrsXmlParser.getEnumFlagValues(), null);
				DisplayMetrics metrics = new DisplayMetrics();
				metrics.densityDpi = metrics.noncompatDensityDpi = DisplayMetrics.DENSITY_DEFAULT;

				metrics.density = metrics.noncompatDensity = metrics.densityDpi
						/ (float) DisplayMetrics.DENSITY_DEFAULT;

				metrics.scaledDensity = metrics.noncompatScaledDensity = metrics.density;

				metrics.widthPixels = metrics.noncompatWidthPixels = 800;
				metrics.heightPixels = metrics.noncompatHeightPixels = 480;
				context = new BridgeContext(null, metrics, ResourceResolver.create(
						projectResources.getConfiguredResources(new FolderConfiguration()),
						frameworkResources.getConfiguredResources(new FolderConfiguration()), "Theme.DeviceDefault",
						false), projectResources, null, android.os.Build.VERSION_CODES.JELLY_BEAN_MR1);
				context.setBridgeInflater(new BridgeInflater(context, projectResources));
				context.initResources();
				Looper.prepare();
				PhoneWindow phoneWindow = new PhoneWindow(context);
				phoneWindow.setContentView(com.lonerr.sqlitetest.R.layout.activity_main);
				WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
				android.view.Display display = wm.getDefaultDisplay();
				viewRootImpl = new ViewRootImpl(context, display);
				viewRootImpl.setView(phoneWindow.getDecorView(), phoneWindow.getAttributes(),
						phoneWindow.getDecorView());

				Main.display.asyncExec(new Runnable() {

					@Override
					public void run() {
						shell.open();
					}
				});

				View view = phoneWindow.findViewById(com.lonerr.sqlitetest.R.id.imageView1);
				animation = AnimationUtils.loadAnimation(context, com.android.internal.R.anim.fade_in);
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