package robocup.model.enums;

import java.awt.Polygon;

import robocup.model.FieldPoint;
import robocup.model.World;

/**
 * An enumeration that describes zones on the field. The plan of the zones is
 * shown in the image. The points a-j are only shown in the second quadrant, but used 
 * in the other quadrants too (but than mirrored in the center spot). There are 
 * 2 extra zones for each field half (west and east).
 * <br><br>
 * <img src="../../../../images/fieldzones.jpg" />
 */
public enum FieldZone {

	WEST_NORTH_CORNER(
			FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.a),
			FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.b),
			FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.e),
			FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.d)),
	WEST_NORTH_SECONDPOST(
			FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.e),
			FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.b),
			FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.g),
			FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.f)),
	WEST_NORTH_FRONT(
			FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.b),
			FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.c),
			FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.h),
			FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.g)),
	WEST_NORTH_GOAL(
			FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.d),
			FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.e),
			FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.f),
			FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.j),
			FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.i)),

	WEST_CENTER(
			FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.f),
			FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.g),
			FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.g),
			FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.f)),
	WEST_MIDDLE(
			FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.g),
			FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.h),
			FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.h),
			FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.g)),

	WEST_SOUTH_CORNER(
			FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.a),
			FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.b),
			FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.e),
			FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.d)),
	WEST_SOUTH_SECONDPOST(
			FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.b),
			FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.g),
			FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.f),
			FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.e)),
	WEST_SOUTH_FRONT(
			FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.b),
			FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.c),
			FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.h),
			FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.g)),
	WEST_SOUTH_GOAL(
			FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.i),
			FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.j),
			FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.f),
			FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.e),
			FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.d)),

	EAST_NORTH_CORNER(
			FieldPointPaletteZones.a,
			FieldPointPaletteZones.b,
			FieldPointPaletteZones.e,
			FieldPointPaletteZones.d),
	EAST_NORTH_SECONDPOST(
			FieldPointPaletteZones.e,
			FieldPointPaletteZones.b,
			FieldPointPaletteZones.g,
			FieldPointPaletteZones.f),
	EAST_NORTH_FRONT(
			FieldPointPaletteZones.b,
			FieldPointPaletteZones.c,
			FieldPointPaletteZones.h,
			FieldPointPaletteZones.g),
	EAST_NORTH_GOAL(
			FieldPointPaletteZones.d,
			FieldPointPaletteZones.e,
			FieldPointPaletteZones.f,
			FieldPointPaletteZones.j,
			FieldPointPaletteZones.i),

	EAST_CENTER(
			FieldPointPaletteZones.f,
			FieldPointPaletteZones.g,
			FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.g),
			FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.f)),
	EAST_MIDDLE(
			FieldPointPaletteZones.g,
			FieldPointPaletteZones.h,
			FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.h),
			FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.g)),

	EAST_SOUTH_CORNER(
			FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.a),
			FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.b),
			FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.e),
			FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.d)),
	EAST_SOUTH_SECONDPOST(
			FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.b),
			FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.g),
			FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.f),
			FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.e)),
	EAST_SOUTH_FRONT(
			FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.b),
			FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.c),
			FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.h),
			FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.g)),
	EAST_SOUTH_GOAL(
			FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.i),
			FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.j),
			FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.f),
			FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.e),
			FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.d)),

	EAST(
			FieldPointPaletteZones.a,
			FieldPointPaletteZones.c,
			FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.c),
			FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.a)),
	WEST(
			FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.a),
			FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.c),
			FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.c),
			FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.a));
	
	public static void update() {
		WEST_NORTH_CORNER.setVertices(
				FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.a),
				FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.b),
				FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.e),
				FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.d));
		WEST_NORTH_SECONDPOST.setVertices(
				FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.e),
				FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.b),
				FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.g),
				FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.f));
		WEST_NORTH_FRONT.setVertices(
				FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.b),
				FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.c),
				FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.h),
				FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.g));
		WEST_NORTH_GOAL.setVertices(
				FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.d),
				FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.e),
				FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.f),
				FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.j),
				FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.i));

		WEST_CENTER.setVertices(
				FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.f),
				FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.g),
				FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.g),
				FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.f));
		WEST_MIDDLE.setVertices(
				FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.g),
				FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.h),
				FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.h),
				FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.g));

		WEST_SOUTH_CORNER.setVertices(
				FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.a),
				FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.b),
				FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.e),
				FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.d));
		WEST_SOUTH_SECONDPOST.setVertices(
				FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.b),
				FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.g),
				FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.f),
				FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.e));
		WEST_SOUTH_FRONT.setVertices(
				FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.b),
				FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.c),
				FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.h),
				FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.g));
		WEST_SOUTH_GOAL.setVertices(
				FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.i),
				FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.j),
				FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.f),
				FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.e),
				FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.d));

		EAST_NORTH_CORNER.setVertices(
				FieldPointPaletteZones.a,
				FieldPointPaletteZones.b,
				FieldPointPaletteZones.e,
				FieldPointPaletteZones.d);
		EAST_NORTH_SECONDPOST.setVertices(
				FieldPointPaletteZones.e,
				FieldPointPaletteZones.b,
				FieldPointPaletteZones.g,
				FieldPointPaletteZones.f);
		EAST_NORTH_FRONT.setVertices(
				FieldPointPaletteZones.b,
				FieldPointPaletteZones.c,
				FieldPointPaletteZones.h,
				FieldPointPaletteZones.g);
		EAST_NORTH_GOAL.setVertices(
				FieldPointPaletteZones.d,
				FieldPointPaletteZones.e,
				FieldPointPaletteZones.f,
				FieldPointPaletteZones.j,
				FieldPointPaletteZones.i);

		EAST_CENTER.setVertices(
				FieldPointPaletteZones.f,
				FieldPointPaletteZones.g,
				FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.g),
				FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.f));
		EAST_MIDDLE.setVertices(
				FieldPointPaletteZones.g,
				FieldPointPaletteZones.h,
				FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.h),
				FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.g));

		EAST_SOUTH_CORNER.setVertices(
				FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.a),
				FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.b),
				FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.e),
				FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.d));
		EAST_SOUTH_SECONDPOST.setVertices(
				FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.b),
				FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.g),
				FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.f),
				FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.e));
		EAST_SOUTH_FRONT.setVertices(
				FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.b),
				FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.c),
				FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.h),
				FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.g));
		EAST_SOUTH_GOAL.setVertices(
				FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.i),
				FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.j),
				FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.f),
				FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.e),
				FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.d));

		EAST.setVertices(
				FieldPointPaletteZones.a,
				FieldPointPaletteZones.c,
				FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.c),
				FieldPointPaletteZones.getFourthQuadrant(FieldPointPaletteZones.a));
		WEST.setVertices(
				FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.a),
				FieldPointPaletteZones.getSecondQuadrant(FieldPointPaletteZones.c),
				FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.c),
				FieldPointPaletteZones.getThirdQuadrant(FieldPointPaletteZones.a));
	}

	private FieldPoint[] vertices;

	/**
	 * Constructor for {@link FieldZone}. The zone is created on basis of an array of {@link FieldZone}s
	 * that indicate the vertices.
	 * @param points An array of {@link FieldPoint}s that indicate the vertices of the {@link FieldZone}.
	 */
	FieldZone(FieldPoint... points) {
		vertices = points;
	}

	public void setVertices(FieldPoint... vertices) {
		this.vertices = vertices;
	}

	/**
	 * @return An array of {@link FieldPoint}s that indicate the vertices of the {@link FieldZone}.
	 */
	public FieldPoint[] getVertices() {
		return vertices;
	}

	/**
	 * @return The number of vertices of the {@link FieldZone} polygon.
	 */
	public int getNumberOfVertices() {
		return vertices.length;
	}

	/**
	 * Calculates the center in x and in y coordinates and creates a {@link FieldPoint} with
	 * these calculated x and y.
	 * @return {@link FieldPoint} that indicates the center of the {@link FieldZone}.
	 */
	public FieldPoint getCenterPoint() {
		double maxX = -Integer.MAX_VALUE, minX = Integer.MAX_VALUE, maxY = -Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		for(FieldPoint vertex: vertices) {
			minX = Math.min(minX, vertex.getX());
			maxX = Math.max(maxX, vertex.getX());
			minY = Math.min(minY, vertex.getY());
			maxY = Math.max(maxY, vertex.getY());
		}
		return new FieldPoint((maxX+minX)/2, (maxY+minY)/2);
	}

	/**
	 * @param point The {@link FieldPoint} for which the distance to the center point is to be calculated.
	 * @return The distance between the given {@link FieldPoint} and the center of the {@link FieldZone} polygon.
	 */
	public double getDistanceFromCenter(FieldPoint point) {
		return point.getDeltaDistance(getCenterPoint());
	}

	/**
	 * @return The width of the {@link FieldZone} polygon.
	 */
	public double getWidth() {
		double max = 0, min = 0;
		for(FieldPoint vertex: vertices) {
			max = (max > vertex.getX() ? max : vertex.getX());
			min = (min < vertex.getX() ? min : vertex.getX());
		}
		return max - min;
	}

	/**
	 * @return The height of the {@link FieldZone} polygon.
	 */
	public double getHeight() {
		double max = 0, min = 0;
		for(FieldPoint vertex: vertices){
			max = (max > vertex.getY() ? max : vertex.getY());
			min = (min < vertex.getY() ? min : vertex.getY());
		}
		return max - min;
	}

	/**
	 * @param point The {@link FieldPoint} for which is to be checked whether it is inside the {@link FiedZone} polygon.
	 * @return Whether the given point is inside the {@link FieldZone} polygon.
	 */
	public boolean contains(FieldPoint point) {
		boolean result = false;
		for (int i = 0, j = vertices.length - 1; i < vertices.length; j = i++) {
			if ((vertices[i].getY() > point.getY()) != (vertices[j].getY() > point.getY())
					&& (point.getX() < (vertices[j].getX() - vertices[i].getX()) * (point.getY() - vertices[i].getY())
							/ (vertices[j].getY() - vertices[i].getY()) + vertices[i].getX())) {
				result = !result;
			}
		}
		return result;
	}

	/**
	 * @param point The {@link FieldPoint} to find the closest vertex to.
	 * @return A {@link FieldPoint} indicating the closest vertex to the given {@link FieldPoint}.
	 */
	public FieldPoint getClosestVertex(FieldPoint point) {
		FieldPoint closestVertex = new FieldPoint(0, 0);
		double shortestDistance = Double.MAX_VALUE;

		for (int i = 0; i < vertices.length; i++) {
			FieldPoint vertexPoint = vertices[i];
			double calcDistance = point.getDeltaDistance(vertexPoint);
			if (calcDistance < shortestDistance) {
				shortestDistance = calcDistance;
				closestVertex = vertexPoint;
			}
		}
		return closestVertex;
	}
	
	/**
	 * @returns a {@link Polygon} object of this FieldZone.
	 */
	public Polygon getPolygon(){
		int xpoints[] = new int[vertices.length];
		int ypoints[] = new int[vertices.length];
 		for(int i = 0; i < vertices.length; ++i){
			xpoints[i] = (int)vertices[i].getX();
			ypoints[i] = (int)vertices[i].getY();
		}
		return new Polygon(xpoints, ypoints, vertices.length);
	}

	/**
	 * A palatte containing {@link FieldPoint}s that are used for creating zones.
	 * The coordinates are in the first quadrant.<br>
	 * For the second and third quadrant are x coordinates negative.<br>
	 * For the third and fourth quadrant are y coordinates negative.
	 * <br><br>
	 * <img src="../../../../images/zones.jpg" />
	 */
	public static class FieldPointPaletteZones {
		private static int length = World.getInstance().getField().getLength();
		private static int width = World.getInstance().getField().getWidth();
		private static int defenceRadius = World.getInstance().getField().getDefenceRadius();
		private static int defenceStretch = World.getInstance().getField().getDefenceStretch();

		public static FieldPoint a = new FieldPoint(length / 2, width / 2);
		public static FieldPoint b = new FieldPoint((length / 2 - defenceRadius) * 0.68, width / 2);
		public static FieldPoint c = new FieldPoint(0, width / 2);
		public static FieldPoint d = new FieldPoint(length / 2, defenceRadius + defenceStretch / 2);
		public static FieldPoint e = new FieldPoint(length / 2 - defenceRadius / 2, defenceRadius + defenceStretch / 2);
		public static FieldPoint f = new FieldPoint(length / 2 - defenceRadius, defenceStretch / 2 + defenceRadius * 0.4);
		public static FieldPoint g = new FieldPoint((length / 2 - defenceRadius) * 0.68, defenceStretch / 2 + defenceRadius * 0.4);
		public static FieldPoint h = new FieldPoint(0, defenceStretch / 2 + defenceRadius * 0.4);
		public static FieldPoint i = new FieldPoint(length / 2, 0);
		public static FieldPoint j = new FieldPoint(length / 2 - defenceRadius, 0);
		
		public static void update() {
			length = World.getInstance().getField().getLength();
			width = World.getInstance().getField().getWidth();
			defenceRadius = World.getInstance().getField().getDefenceRadius();
			defenceStretch = World.getInstance().getField().getDefenceStretch();

			a = new FieldPoint(length / 2, width / 2);
			b = new FieldPoint((length / 2 - defenceRadius) * 0.68, width / 2);
			c = new FieldPoint(0, width / 2);
			d = new FieldPoint(length / 2, defenceRadius + defenceStretch / 2);
			e = new FieldPoint(length / 2 - defenceRadius / 2, defenceRadius + defenceStretch / 2);
			f = new FieldPoint(length / 2 - defenceRadius, defenceStretch / 2 + defenceRadius * 0.4);
			g = new FieldPoint((length / 2 - defenceRadius) * 0.68, defenceStretch / 2 + defenceRadius * 0.4);
			h = new FieldPoint(0, defenceStretch / 2 + defenceRadius * 0.4);
			i = new FieldPoint(length / 2, 0);
			j = new FieldPoint(length / 2 - defenceRadius, 0);
		}

		/**
		 * @param point The {@link FieldPoint} that represents the point in the first quadrant.
		 * @return The given {@link FieldPoint}, but then in the second quadrant.
		 */
		public static FieldPoint getSecondQuadrant(FieldPoint point) {
			FieldPoint newPoint = new FieldPoint(-1*point.getX(), point.getY());
			return newPoint;
		}

		/**
		 * @param point The {@link FieldPoint} that represents the point in the first quadrant.
		 * @return The given {@link FieldPoint}, but then in the third quadrant.
		 */
		public static FieldPoint getThirdQuadrant(FieldPoint point) {
			FieldPoint newPoint = new FieldPoint(-1*point.getX(), -1*point.getY());
			return newPoint;
		}

		/**
		 * @param point The {@link FieldPoint} that represents the point in the first quadrant.
		 * @return The given {@link FieldPoint}, but then in the fourth quadrant.
		 */
		public static FieldPoint getFourthQuadrant(FieldPoint point) {
			FieldPoint newPoint = new FieldPoint(point.getX(), -1*point.getY());
			return newPoint;
		}
	}
}