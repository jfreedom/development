package com.jfreedom.AndroidPlot;

import java.util.Iterator;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * @author joe this is a class to be plotted by the AndroidPlotView
 */
public class AndroidPlot {

	private List<Double> xList;

	private List<Double> yList;

	private Paint paint;

	/**
	 * Default constructor for a plot
	 * 
	 * @param x
	 * @param y
	 */
	public AndroidPlot(List<Double> x, List<Double> y) {
		this.xList = x;
		this.yList = y;
		paint = new Paint();
		paint.setColor(Color.BLUE);
		paint.setStrokeWidth(0);
	}

	/**
	 * Adds an X, Y pair to be plotted.
	 * 
	 * @param x
	 * @param y
	 */
	public void addPoint(Double x, Double y) {
		xList.add(x);
		yList.add(y);
	}

	/**
	 * @param canvas
	 *            The canvas to be drawn to
	 * @param xMin
	 *            the minimum X value to be plotted
	 * @param xMax
	 *            the maximum X value to be plotted
	 * @param yMin
	 *            the minimum Y value to be plotted
	 * @param yMax
	 *            the maximum Y value to be plotted
	 */
	public void draw(Canvas canvas, double xMin, double xMax, double yMin, double yMax) {
		int w = canvas.getWidth();
		int h = canvas.getHeight();

		double xScale = w / (xMax - xMin);
		double yScale = h / (yMax - yMin);

		double xShift = -xMin;
		double yShift = yMax;

		drawScaled(canvas, xScale, yScale, xShift, yShift);
	}

	/**
	 * Actually draws the plot to the canvas.
	 * 
	 * @param canvas
	 *            the canvas that is going to be drawn to
	 * @param xScale
	 *            how to scale the X coordinates for the plot
	 * @param yScale
	 *            how to scale the Y coordinates for the plot
	 * @param xShift
	 *            how to shift the X coordinates for the plot
	 * @param yShift
	 *            how to shift the Y coordinates for the plot
	 */
	private void drawScaled(Canvas canvas, double xScale, double yScale, double xShift, double yShift) {
		Iterator<Double> xIt = xList.iterator();
		Iterator<Double> yIt = yList.iterator();
		float lastX = Float.NaN;
		float lastY = Float.NaN;
		while (xIt.hasNext() && yIt.hasNext()) {
			double xpt = xIt.next() + xShift;
			double ypt = (-1 * yIt.next()) + yShift;
			float xPntCnv = (float) (xpt * xScale);
			float yPntCnv = (float) (ypt * yScale);
			canvas.drawPoint(xPntCnv, yPntCnv, paint);
			if (!Double.valueOf(lastX).isNaN() && !Double.valueOf(lastY).isNaN()) {
				//canvas.drawLine(lastX, lastY, xPntCnv, yPntCnv, paint);
			}
			lastX = xPntCnv;
			lastY = yPntCnv;
		}

	}

	public List<Double> getxList() {
		return xList;
	}

	public List<Double> getyList() {
		return yList;
	}

}
