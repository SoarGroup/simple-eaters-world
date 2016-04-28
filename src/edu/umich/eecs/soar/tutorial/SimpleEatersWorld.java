package edu.umich.eecs.soar.tutorial;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import edu.princeton.cs.introcs.Draw;
import sml.Agent;
import sml.Agent.OutputEventInterface;
import sml.Agent.RunEventInterface;
import sml.Identifier;
import sml.IntElement;
import sml.StringElement;
import sml.WMElement;
import sml.smlRunEventId;

public abstract class SimpleEatersWorld implements RunEventInterface, OutputEventInterface {
	final protected String CMD_ROTATE = "rotate";
	final protected String CMD_FORWARD = "forward";

	final protected CountDownLatch done;
	final protected MapObject[][] m;
	final protected MapObject[][] backup;
	final protected int foodCount;
	final protected int height;
	final protected int width;

	protected Orientation o;
	protected int x;
	protected int y;
	protected boolean moving;

	protected int score = 0;
	protected int steps = 0;
	
	protected Map<MapObject, Integer> points = new HashMap<MapObject, Integer>();
	protected int timePoints = 0;

	protected Map<MapObject, Integer> eatenCounts = new HashMap<MapObject, Integer>();
	protected int eaten = 0;
	
	protected Identifier inputLink = null;
	protected List<WMElement> wmes = new LinkedList<>();
	
	protected final int sleepTime;
	protected final Draw d = new Draw();

	public SimpleEatersWorld(CountDownLatch latchDone, Agent agent, MapObject[][] map, Orientation initialOrientation, int initialX, int initialY, int sleepMsec) {
		agent.RegisterForRunEvent(smlRunEventId.smlEVENT_BEFORE_INPUT_PHASE, this, null);
		agent.AddOutputHandler(CMD_ROTATE, this, null); // by 90 degree clockwise, no argument
		agent.AddOutputHandler(CMD_FORWARD, this, null); // no argument

		done = latchDone;
		
		height = map.length;
		width = map[0].length;
		m = new MapObject[height][width];
		backup = new MapObject[height][width];
		int foods = 0;
		for (int row=0; row<height; row++) {
			for (int col=0; col<width; col++) {
				final MapObject o = backup[row][col] = m[row][col] = map[height-row-1][col];
				if (o!=null && o!=MapObject.wall) {
					foods++;
				}
			}
		}
		foodCount = foods;
		
		d.setXscale(0, width+1);
		d.setYscale(0, height+1);
		
		sleepTime = sleepMsec;
		
		o = initialOrientation;
		x = initialX;
		y = initialY;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setPoints(MapObject o, int oPoints) {
		if (o!=MapObject.wall && o!=null) {
			points.put(o, oPoints);
		}
	}
	
	public int getPoints(MapObject o) {
		if (points.containsKey(o)) {
			return points.get(o);
		} else {
			return 1;
		}
	}
	
	public void setTimePoints(int points) {
		timePoints = points;
	}
	
	public int getTimePoints() {
		return timePoints;
	}
	
	protected abstract boolean isDone();
	
	protected MapObject getCellContents(int x, int y) {
		if (x<0 || x>=width || y<0 || y>=height) {
			return MapObject.wall;
		} else {
			return m[y][x];
		}
	}
	
	private void removeCellContents(int x, int y) {
		final MapObject o = getCellContents(x, y);
		if (o!=MapObject.wall && o!=null) {
			m[y][x] = null;
		}
	}

	@Override
	public void outputEventHandler(Object data, String agentName, String attributeName, WMElement pWmeAdded) {
		final Identifier id = pWmeAdded.ConvertToIdentifier();
		if (id != null) {
			boolean good = false;

			if (attributeName.compareTo(CMD_ROTATE) == 0) {
				o = Orientation.getOrientation(o.ordinal()+1);
				good = true;
			} else if (attributeName.compareTo(CMD_FORWARD) == 0) {
				moving = true;
				good = true;
			}

			if (good) {
				id.AddStatusComplete();
			} else {
				id.AddStatusError();
			}
		}
	}
	
	protected int _nextX() {
		if (o == Orientation.left) {
			return x-1;
		} else if (o == Orientation.right) {
			return x+1;
		} else {
			return x;
		}
	}
	
	protected int _nextY() {
		if (o == Orientation.up) {
			return y+1;
		} else if (o == Orientation.down) {
			return y-1;
		} else {
			return y;
		}
	}
	
	private void _updateState() {
		if (moving) {
			boolean changed = false;
			final int nextX = _nextX();
			final int nextY = _nextY();
			final MapObject nextO = getCellContents(nextX, nextY);
			
			if (nextO != MapObject.wall) {
				x = nextX;
				y = nextY;
				changed = true;
			}
			
			if (changed && nextO!=null) {
				removeCellContents(x, y);
				
				score += getPoints(nextO);
				eaten++;
				if (!eatenCounts.containsKey(o)) {
					eatenCounts.put(nextO, 1);
				} else {
					eatenCounts.put(nextO, eatenCounts.get(o)+1);
				}
			}
		}
		
		score += timePoints;
		steps++;
		moving = false;
	}
	
	protected IntElement _createWME(Identifier id, String attribute, int value) {
		IntElement wme = id.CreateIntWME(attribute, value);
		wmes.add(wme);
		return wme;
	}
	
	protected StringElement _createWME(Identifier id, String attribute, String value) {
		StringElement wme = id.CreateStringWME(attribute, value);
		wmes.add(wme);
		return wme;
	}
	
	private void _updateSoar(Agent agent) {
		if (inputLink == null) {
			inputLink = agent.GetInputLink();
		}
		
		for (WMElement wme : wmes) {
			wme.DestroyWME();
		}
		wmes.clear();
		
		_createWME(inputLink, "time", steps);
		_createWME(inputLink, "score", score);
		_createWME(inputLink, "x", x);
		_createWME(inputLink, "y", y);
		_createWME(inputLink, "orientation", o.name());
		
		final MapObject nextO = getCellContents(_nextX(), _nextY());
		if (nextO != null) {
			_createWME(inputLink, "sense", nextO.name());
		} else {
			_createWME(inputLink, "sense", "empty");
		}
	}
	
	private void _visualizeState() {
		final double boxSize = 0.48;
		final double circleSize = 0.14;
		
		d.clear();
		{
			d.setPenColor(MapObject.wall.color);
			
			for (int row=1; row<=height; row++) {
				d.filledSquare(0, row, boxSize);
				d.filledSquare(width+1, row, boxSize);
			}
			
			for (int col=1; col<=width; col++) {
				d.filledSquare(col, 0, boxSize);
				d.filledSquare(col, height+1, boxSize);
			}
			
			for (int row=0; row<height; row++) {
				for (int col=0; col<width; col++) {
					final MapObject o = getCellContents(col, row);
					if (o != null) {
						d.setPenColor(o.color);
						if (o == MapObject.wall) {
							d.filledSquare(col+1, row+1, boxSize);
						} else {
							d.filledCircle(col+1, row+1, circleSize);
							d.setPenColor(Color.WHITE);
							d.text(col+1, row+1, String.valueOf(getPoints(o)));
						}
					}
				}
			}
			
			d.setPenColor(Color.BLACK);
			d.filledCircle(x+1, y+1, boxSize);
			d.setPenColor(Color.YELLOW);
			d.filledCircle(x+1, y+1, boxSize*.98);
			d.setPenColor(Color.BLACK);
			d.line(x+1, y+1, _nextX()+1, _nextY()+1);
		}
		d.show();
		d.show( sleepTime );
	}

	@Override
	public void runEventHandler(int eventID, Object data, Agent agent, int phase) {
		_updateState();
		_updateSoar(agent);
		_visualizeState();
		
		if (isDone()) {
			agent.StopSelf();
			done.countDown();
		}
	}

}
