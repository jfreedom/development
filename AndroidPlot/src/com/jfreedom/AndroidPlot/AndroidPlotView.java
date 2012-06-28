package com.jfreedom.AndroidPlot;

import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * @author joe
 * 
 */
public class AndroidPlotView extends View {
	
	private static final String TAG = "AndroidPlotView";

	List<AndroidPlot> apList;
	private double xMin;
	private double xMax;
	private double yMin;
	private double yMax;
	ScaleGestureDetector sgd;

	private float lastX;

	private float lastY;

	private long lastEventTime;

	private long lastActionDown;

	private boolean wasScaling;

	static final int textSize = 15;

	static final int nDiv = 10;

	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public AndroidPlotView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param attrs
	 */
	public AndroidPlotView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public AndroidPlotView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	/**
	 * Adds an AndroidPlot to the list of plots.
	 * 
	 * @param ap
	 *            the AndroidPlot that will be plotted on this view
	 */
	public void addPlot(AndroidPlot ap) {
		apList.add(ap);
	}
	/**
	 * Draws a grid and X and Y axes based on the x and y min and max values.
	 * 
	 * @param canvas
	 *            the Canvas that is being drawn to.
	 */
	private void drawGrid(Canvas canvas) {

		// Create the paint to draw the grid lines
		Paint gridPaint = new Paint();

		gridPaint.setColor(Color.BLACK);
		gridPaint.setStyle(Style.STROKE);

		// Ran into some issues draw dash path effects. This seemed to work fine
		// in the simulator but not on HW
		// gridPaint.setPathEffect(new DashPathEffect(new float[] {10,40},1));
		gridPaint.setStrokeWidth(0);

		// Create the paint for the axes
		Paint labelPaint = new Paint();
		labelPaint.setColor(Color.BLACK);
		labelPaint.setStyle(Style.FILL);
		labelPaint.setTextSize(textSize);
		labelPaint.setTextAlign(Align.LEFT);

		int width = canvas.getWidth();
		int height = canvas.getHeight();

		// Draw the minimum X value since it is drawn special (above the y value
		// in th ebottom left)
		canvas.drawText(String.format("x = %.3f,", xMin), 0, height - textSize * 2, labelPaint);

		// Draw the remaining X values
		Double gridHorizSpace = Double.valueOf(Double.valueOf(width) / nDiv);
		Double gridVertSpace = Double.valueOf(Double.valueOf(height) / nDiv);
		int i = 0;
		for (int iX = gridHorizSpace.intValue(); iX < width; iX += gridHorizSpace.intValue()) {
			canvas.drawLine(iX, 0, iX, height, gridPaint);
			canvas.drawText(String.format("x = %.3f", xMin + (i + 1) * (xMax - xMin) / nDiv), iX, height - textSize,
					labelPaint);
			i++;
		}

		// Draw all the Y values.
		i = 0;
		for (int iY = gridVertSpace.intValue(); iY < width; iY += gridVertSpace.intValue()) {
			canvas.drawLine(0, iY, width, iY, gridPaint);
			canvas.drawText(String.format("y = %.3f ", yMax - (i + 1) * (yMax - yMin) / nDiv), 0, iY, labelPaint);
			i++;
		}
	}

	/**
	 * Called by all three constructors.
	 * 
	 * @param context
	 */
	private void init(Context context) {
		sgd = new ScaleGestureDetector(context, new ScaleList());
		setBackgroundColor(Color.WHITE);
		apList = new Vector<AndroidPlot>();
		xMin = -10;
		xMax = 10;
		yMin = -10;
		yMax = 10;
		lastX = Float.NaN;
		lastY = Float.NaN;
		lastActionDown = 0;
	}

	/*
	 * Does actual drawing when the view is invalidated. (non-Javadoc)
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	public void onDraw(Canvas canvas) {
		
		drawGrid(canvas);
		long startDraw = SystemClock.uptimeMillis();
		for (AndroidPlot ap : apList)
			ap.draw(canvas, xMin, xMax, yMin, yMax);
		long drawDone = SystemClock.uptimeMillis();
		Log.d(TAG, "Draw Time: " + Long.valueOf(drawDone - startDraw).toString());
	}

	/*
	 * onTouchEvent captures all of the Views touches, and for now makes it so
	 * that the super class doesn't process them. There may be better ways to do
	 * that.
	 * 
	 * The motions we're looking for are motions to scale and shift the plot.
	 * Scaling is simply passed on to the ScaleGestureDetector. (non-Javadoc)
	 * 
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Pass the event to the ScaleGestureDetector
		sgd.onTouchEvent(event);

		// If the ScaleGestureDetector isn't currently processing a gesture do
		// our own work
		if (!sgd.isInProgress()) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// For a down action save the X and Y values and the time of the
				// down
				lastX = event.getX();
				lastY = event.getY();
				lastEventTime = event.getEventTime();

				// Was scaling is a parameter to make sure the plot doesn't
				// shift after doing a scale gesture
				wasScaling = false;

				// If it was a double tap go to the filled zoom.
				if ((event.getEventTime() - lastActionDown) < ViewConfiguration.getDoubleTapTimeout())
					zoomFill();
				lastActionDown = event.getEventTime();
				break;

			case MotionEvent.ACTION_MOVE:
				// It was a move make sure the user wasn't just scaling the
				// view, or it will shift when it is likely that isn't
				// desireable.
				if (!wasScaling) {

					float cX = event.getX();
					float cY = event.getY();
					// lastX and lastY are set to NaN on an action up. I don't
					// think it would be possible to get here with out them
					// being set, but they are checked any way
					if (!Float.valueOf(lastX).isNaN() && !Float.valueOf(lastY).isNaN()) {

						// Compute the pixels to shift
						float sX = cX - lastX;
						float sY = cY - lastY;

						// Compute how many cartesian coordinates to shift
						int widthPixels = this.getWidth();
						int heightPixels = this.getHeight();
						double widthCartesian = xMax - xMin;
						double heightCartesian = yMax - yMin;
						double shiftXCartesian = sX * widthCartesian / widthPixels;
						double shiftYCartesian = sY * heightCartesian / heightPixels;

						// Perform the shift
						xMin -= shiftXCartesian;
						xMax -= shiftXCartesian;
						yMin += shiftYCartesian;
						yMax += shiftYCartesian;

						// Force a redraw
						invalidate();
					}
					lastX = cX;
					lastY = cY;
					lastEventTime = event.getEventTime();
				}
				break;
			case MotionEvent.ACTION_UP:
				// The user lifted their finger of the screen.
				if (!wasScaling) {
					// Only allow for long presses if the user wasn't scaling.
					if ((event.getEventTime() - lastEventTime) > ViewConfiguration.getLongPressTimeout())
						zoomFill();
					lastX = Float.NaN;
					lastY = Float.NaN;
				}
				break;
			default:
				super.onTouchEvent(event);
			}
		} else {
			// The gesture detector is scaling so it was scaling in the recent
			// past.
			wasScaling = true;
		}

		// I ran into some issues calling the super classes on touch event.
		// There is probably a way to keep it working, but for now I'll just
		// make the super class starve and consume all the MotionEvents here
		return true;// super.onTouchEvent(event);
	}

	/**
	 * @param xPixel
	 *            the X pixel on the view
	 * @return the X cartisan coordinate on the plot
	 */
	double pixelToCartX(double xPixel) {
		xPixel = xPixel / getWidth();
		double xCart = xPixel * (xMax - xMin);
		xCart += xMin;
		return xCart;
	}

	/**
	 * @param yPixel
	 *            the Y pixel on the view
	 * @return the Y cartisan coordinate on the plot
	 */
	double pixelToCartY(double yPixel) {
		yPixel = yPixel / getHeight();
		double yCart = -yPixel * (yMax - yMin);
		yCart += yMax;
		return yCart;
	}

	/**
	 * Forces the plot to zoom to the maxiumum and minimum of all of the plotted
	 * data.
	 */
	public void zoomFill() {
		// Set the base min and max values
		double xMinTmp = Double.POSITIVE_INFINITY;
		double xMaxTmp = Double.NEGATIVE_INFINITY;
		double yMinTmp = Double.POSITIVE_INFINITY;
		double yMaxTmp = Double.NEGATIVE_INFINITY;
		// Loop over the plots
		for (AndroidPlot ap : apList) {
			// Loop over the X and Y vectors finding the maximum and minimums.
			for (Double x : ap.getxList()) {
				if (x < xMinTmp)
					xMinTmp = x;
				if (x > xMaxTmp)
					xMaxTmp = x;
			}
			for (Double y : ap.getyList()) {
				if (y < yMinTmp)
					yMinTmp = y;
				if (y > yMaxTmp)
					yMaxTmp = y;
			}
		}
		xMin = xMinTmp;
		xMax = xMaxTmp;
		yMin = yMinTmp;
		yMax = yMaxTmp;

		// Force a redrawing of the plot.
		invalidate();

	}

	/**
	 * @author joe This inner class listens for scale information and modifies
	 *         the x and y min/max values of the AndroidPlotView when a pinch
	 *         zoom is performed.
	 */
	class ScaleList implements ScaleGestureDetector.OnScaleGestureListener {
		@SuppressWarnings("unused")
		private static final String TAG = "ScaleList";

		// The center of the plot that is being zoomed in on
		double xCent;
		double yCent;
		static final float spanThreshold = 40; // This threshold was determined
												// empirically based on trying
												// to zoom only in one dimension
												// I would see the span
												// somewhere in the range of
												// 0-23

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.view.ScaleGestureDetector.OnScaleGestureListener#onScale(
		 * android.view.ScaleGestureDetector)
		 */
		public boolean onScale(ScaleGestureDetector detector) {

			float xScale;
			float yScale;

			if (Math.abs(detector.getCurrentSpanX()) < spanThreshold)
				xScale = 1;
			else
				xScale = detector.getPreviousSpanX() / detector.getCurrentSpanX();

			if (Math.abs(detector.getCurrentSpanY()) < spanThreshold)
				yScale = 1;
			else
				yScale = detector.getPreviousSpanY() / detector.getCurrentSpanY();

			if (xScale > 0 && yScale > 0 && xScale < 2 && yScale < 2) {

				scalePlot(xScale, yScale);
			}
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.view.ScaleGestureDetector.OnScaleGestureListener#onScaleBegin
		 * (android.view.ScaleGestureDetector)
		 */
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			xCent = pixelToCartX(detector.getFocusX());
			yCent = pixelToCartY(detector.getFocusY());
			return true;
		}

		public void onScaleEnd(ScaleGestureDetector detector) {

		}

		/**
		 * @param xScale
		 * @param yScale
		 */
		public void scalePlot(float xScale, float yScale) {
			// Compute how to adjust the x and y Max/Min values based on a scale
			// factor
			double xSpan = xMax - xMin;
			double ySpan = yMax - yMin;
			xSpan *= xScale;
			ySpan *= yScale;
			xMin = xCent - xSpan / 2;
			xMax = xCent + xSpan / 2;
			yMax = yCent + ySpan / 2;
			yMin = yCent - ySpan / 2;

			// Force the plots to be redrawn
			invalidate();
		}

	}
}
