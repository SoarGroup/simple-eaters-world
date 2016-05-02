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
	
	final protected double SIZE_WALL = 0.48;
	final protected double SIZE_EATER = 0.40;
	final protected double SIZE_FOOD = 0.20;
	final protected Color COLOR_EATER_OUTER = Color.BLACK;
	final protected Color COLOR_EATER_INNER = Color.decode("#FFC65D");

	final protected CountDownLatch done;
	final protected MapObject[][] m;
	final protected MapObject[][] backupM;
	final protected int foodCount;
	final protected int height;
	final protected int width;

	protected Orientation o;
	protected int x;
	protected int y;
	protected Orientation backupO;
	protected int backupX;
	protected int backupY;
	protected boolean moving;

	protected Integer lastScore = null;
	protected int score = 0;
	protected int steps = 0;
	
	protected Map<MapObject, Integer> points = new HashMap<MapObject, Integer>();
	protected int timePenalty = 0;
	protected int wallPenalty = 0;

	protected Map<MapObject, Integer> eatenCounts = new HashMap<MapObject, Integer>();
	protected int eaten = 0;
	
	protected Identifier inputLink = null;
	protected List<WMElement> wmes = new LinkedList<>();
	
	protected final int sleepTime;
	protected final Draw d = new Draw("SimpleEater");

	public SimpleEatersWorld(CountDownLatch latchDone, Agent agent, MapObject[][] map, Orientation initialOrientation, int initialX, int initialY, int sleepMsec) {
		agent.RegisterForRunEvent(smlRunEventId.smlEVENT_BEFORE_INPUT_PHASE, this, null);
		agent.AddOutputHandler(CMD_ROTATE, this, null); // by 90 degree clockwise, no argument
		agent.AddOutputHandler(CMD_FORWARD, this, null); // no argument

		done = latchDone;
		
		height = map.length;
		width = map[0].length;
		m = new MapObject[height][width];
		backupM = new MapObject[height][width];
		int foods = 0;
		for (int row=0; row<height; row++) {
			for (int col=0; col<width; col++) {
				final MapObject o = backupM[row][col] = m[row][col] = map[height-row-1][col];
				if (o!=null && o!=MapObject.wall) {
					foods++;
				}
			}
		}
		foodCount = foods;
		
		d.setXscale(0, width+3);
		d.setYscale(0, height+3);
		sleepTime = sleepMsec;
		
		o = backupO = initialOrientation;
		x = backupX = initialX;
		y = backupY = initialY;
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
	
	public void setTimePenalty(int points) {
		timePenalty = points;
	}
	
	public int getTimePenalty() {
		return timePenalty;
	}
	
	public void setWallPenalty(int points) {
		wallPenalty = points;
	}
	
	public int getWallPenalty() {
		return wallPenalty;
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
		if (o == Orientation.west) {
			return x-1;
		} else if (o == Orientation.east) {
			return x+1;
		} else {
			return x;
		}
	}
	
	protected int _nextY() {
		if (o == Orientation.north) {
			return y+1;
		} else if (o == Orientation.south) {
			return y-1;
		} else {
			return y;
		}
	}
	
	private void _updateState() {
		lastScore = score;
		
		if (moving) {
			final int nextX = _nextX();
			final int nextY = _nextY();
			final MapObject nextO = getCellContents(nextX, nextY);
			
			if (nextO != MapObject.wall) {
				x = nextX;
				y = nextY;
				
				if (nextO!=null) {
					removeCellContents(x, y);
					
					score += getPoints(nextO);
					eaten++;
					if (!eatenCounts.containsKey(o)) {
						eatenCounts.put(nextO, 1);
					} else {
						eatenCounts.put(nextO, eatenCounts.get(o)+1);
					}
				}
			} else {
				score -= wallPenalty;
			}			
		}
		
		score -= timePenalty;
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
		if (lastScore==null) {
			_createWME(inputLink, "score-diff", "nil");
		} else {
			_createWME(inputLink, "score-diff", score-lastScore);
		}
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
	
	private void _wall(double x, double y) {
		d.setPenColor(MapObject.wall.color);
		d.filledSquare(x, y, SIZE_WALL);
		
		if (wallPenalty!=0) {
			d.setPenColor(Color.WHITE);
			d.text(x, y, String.valueOf(-wallPenalty));
		}
	}
	
	private void _visualizeState() {		
		d.clear();
		{
			for (int row=1; row<=height; row++) {
				_wall(1, row+1);
				_wall(width+2, row+1);
			}
			
			for (int col=1; col<=width; col++) {
				_wall(col+1, 1);
				_wall(col+1, height+2);
			}
			
			for (int row=0; row<height; row++) {
				for (int col=0; col<width; col++) {
					final MapObject o = getCellContents(col, row);
					if (o != null) {
						d.setPenColor(o.color);
						if (o == MapObject.wall) {
							_wall(col+2, row+2);
						} else {
							d.filledCircle(col+2, row+2, SIZE_FOOD);
							d.setPenColor(Color.WHITE);
							d.text(col+2, row+2, String.valueOf(getPoints(o)));
						}
					}
				}
			}
			
			d.setPenColor(COLOR_EATER_OUTER);
			d.filledCircle(x+2, y+2, SIZE_EATER);
			d.setPenColor(COLOR_EATER_INNER);
			d.filledCircle(x+2, y+2, SIZE_EATER*.98);
			d.setPenColor(Color.BLACK);
			d.line(x+2, y+2, _nextX()+2, _nextY()+2);
			
			d.setPenColor(Color.BLACK);
			d.textLeft(1, 0, "Score: " + score);
			d.textLeft(1, height+3, String.format("Time Penalty: %d", timePenalty));
		}
		d.show();
		d.show(sleepTime);
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
