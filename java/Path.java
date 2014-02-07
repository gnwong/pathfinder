/*
 *  Copyright (C)  2014 George Wong
 *  GNU General Public License
 */

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class Path {

  ipos beg;
  ipos end;
  double dp = 0.2;
  ArrayList<ipos> waypoints = new ArrayList<ipos>();

  public Path (int x1, int y1, int x2, int y2) {
    beg = new ipos(x1,y1);
    end = new ipos(x2,y2);
  }
  
  public void setStart (int x, int y) {
    beg.x = x;
    beg.y = y;
  }
  
  public void setEnd (int x, int y) {
    end.x = x;
    end.y = y;
  }
  
  // This will not guarantee a shortest path, but it should guarantee a
  // greedy shortest path.
  public boolean trace (World w) {
    
    waypoints.clear();
    ArrayList<ipos> toadd = new ArrayList<ipos>();
    ArrayList<ipos> tiles = new ArrayList<ipos>();
    ArrayList<ipos> unpass1 = new ArrayList<ipos>();
    ArrayList<ipos> unpass2 = new ArrayList<ipos>();
    ArrayList<ipos> unpassable = new ArrayList<ipos>();
    
    double dx = end.x - beg.x;
    double dy = end.y - beg.y;
    double dr = Math.sqrt(dx*dx+dy*dy);
    
    // This isn't an ipos because we need it to have double precision
    double[] position = {beg.x, beg.y};
    
    // If it's possible to just go straight from beg to end...
    if (passable(new ipos(beg.x, beg.y), new ipos(end.x, end.y), w)) {
      waypoints.add(new ipos(beg.x, beg.y));
      waypoints.add(new ipos(end.x, end.y));
      drawWaypoints(w);
      return true;
    }
    
    // First we just go through each square
    while ( (Math.abs(position[0] - end.x) > dp) || (Math.abs(position[1] - end.y) > dp) ) {
      position[0] += dx * dp / dr;
      position[1] += dy * dp / dr;
      if (! toadd.contains(new ipos((int)Math.floor(position[0]),(int)Math.floor(position[1])))) {
        toadd.add(new ipos((int)Math.floor(position[0]),(int)Math.floor(position[1])));
      }
    }
    
    // Breaking everything apart like this is one way to do it, but not necessarily the best...
    
    // First pass (find obstacles)
    boolean lastpassable = false;
    for (ipos p: toadd) {
      if (w.passable(p.x,p.y)) {
        if (!lastpassable) {
          lastpassable = true;
          waypoints.add(p);
          tiles.add(p);
        }
      }
      else {
        if (lastpassable) {
          lastpassable = false;
          waypoints.add(toadd.get(toadd.indexOf(p)-1));
          unpass1.add(p);
          unpassable.add(p);
        }
      }
    }
    toadd.clear();
    
    // Find routes around obstacles
    while (unpass1.size() != 0 || unpass2.size() != 0) {
      for (ipos p: unpass1) {
        for (int i=-1; i<2; i++) {
          for (int j=-1; j<2; j++) {
            if (!w.isValid(p.x+i,p.y+j)) continue;
            if (w.passable(p.x+i,p.y+j)) {
              if (!tiles.contains(new ipos(p.x+i,p.y+j))) tiles.add(new ipos(p.x+i,p.y+j));
            }
            else {
              if (!unpassable.contains(new ipos(p.x+i,p.y+j))) {
                unpass2.add(new ipos(p.x+i,p.y+j));
                unpassable.add(new ipos(p.x+i,p.y+j));
              }
            }
          }
        }
      }
      unpass1.clear();
      for (ipos p: unpass2) {
        for (int i=-1; i<2; i++) {
          for (int j=-1; j<2; j++) {
            if (!w.isValid(p.x+i,p.y+j)) continue;
            if (w.passable(p.x+i,p.y+j)) {
              if (!tiles.contains(new ipos(p.x+i,p.y+j))) tiles.add(new ipos(p.x+i,p.y+j));
            }
            else {
              if (!unpassable.contains(new ipos(p.x+i,p.y+j))) {
                unpass1.add(new ipos(p.x+i,p.y+j));
                unpassable.add(new ipos(p.x+i,p.y+j));
              }
            }
          }
        }
      }
      unpass2.clear();
    }
    waypoints.add(new ipos(end.x, end.y));
    
    
    // The following line isn't really necessary--it's just here so that we outline the
    // obstacles we encounter.
    for (ipos p: tiles) w.setTile(p.x,p.y,0x03);
    
    
    // Find shortest path around obstacles
    ArrayList<ipos> newWaypoints = new ArrayList<ipos>();
    newWaypoints.add(waypoints.get(0));
    for (int i=1; i<waypoints.size()-1; i+=2) {
      newWaypoints.add(waypoints.get(i));
      ArrayList<ipos> temp = dijkstra(tiles, waypoints.get(i), waypoints.get(i+1));
      for (int j=temp.size()-1; j>=0; j--) {
        newWaypoints.add(temp.get(j));
      }
      newWaypoints.add(waypoints.get(i+1));
    }
    newWaypoints.add(waypoints.get(waypoints.size()-1));

    
    // Again, the following line isn't strictly necessary but it helps to point out
    // the best paths around obstacles.
    for (ipos p: newWaypoints) w.setTile(p.x,p.y,0x05);
    
    
    waypoints.clear();
    waypoints.add(newWaypoints.get(newWaypoints.size()-1));
    while (waypoints.get(waypoints.size()-1)!=newWaypoints.get(0)) {
      ipos end = waypoints.get(waypoints.size()-1);
      for (ipos p: newWaypoints) {
        if (passable(p, end, w)) {
          if (!waypoints.contains(p)) waypoints.add(p);
          else waypoints.add(newWaypoints.get(newWaypoints.indexOf(p)-1));
          break;
        }
      }
    }
    
    drawWaypoints(w);
    
    return true;
  }
  
  // Draws the final path from the waypoints
  public void drawWaypoints (World w) {
    for (int i=0; i<waypoints.size()-1; i++) {
      drawLine(waypoints.get(i),waypoints.get(i+1),w);
    }
  }
  
  // Draws a line
  public void drawLine (ipos begin, ipos end, World w) {
    double dx = end.x - begin.x;
    double dy = end.y - begin.y;
    double dr = Math.sqrt(dx*dx+dy*dy);
    double[] position = {begin.x, begin.y};
    while (Math.abs(position[0] - end.x) > dp) {
      position[0] += dx * dp / dr;
      position[1] += dy * dp / dr;
      w.setTile((int)Math.floor(position[0]),(int)Math.floor(position[1]),0x04);
    }
  }
  
  // Only returns true when there's a clear cut between the two points
  private boolean passable (ipos begin, ipos end, World w) {
    
    double dx = end.x - begin.x;
    double dy = end.y - begin.y;
    double dr = Math.sqrt(dx*dx+dy*dy);
    double[] position = {begin.x, begin.y};
    
    while ( (Math.abs(position[0] - end.x) > dp) || (Math.abs(position[1] - end.y) > dp) ) {
      position[0] += dx * dp / dr;
      position[1] += dy * dp / dr;
      if (! w.passable((int)Math.floor(position[0]), (int)Math.floor(position[1]))) return false;
    }
    
    return true;
  }
  
  // Dijkstra's algorithm for finding the shortest path
  static ArrayList<ipos> dijkstra (ArrayList<ipos> g, ipos src, ipos target) {
    double inf = Double.POSITIVE_INFINITY;
    Map<ipos,Double> distance = new HashMap<ipos,Double>();
    Map<ipos,ipos> previous = new HashMap<ipos,ipos>();
    ArrayList<ipos> queue = new ArrayList<ipos>();
    ipos u;
    
    for (ipos v: g) {
      distance.put(v,inf);
      previous.put(v,null);
      queue.add(v);
    }
    
    distance.put(src,(double)0);
    
    while (queue.size() > 0) {
      u = minDistance(distance,queue);
      if (u.equals(target)) break;
      queue.remove(u);
      if (distance.get(u)==inf) break;
      for (ipos v: queue) {
        double temp = distance.get(u) + u.dist(v);
        if (temp < distance.get(v)) {
          distance.put(v,temp);
          previous.put(v,u);
        }
      }
    }
    
    // Reverse the order of the nodes
    queue.clear();
    u = target;
    queue.add(u);
    while (!u.equals(src)) {
      u = previous.get(u);
      queue.add(u);
    }
  
    return queue;
  }
  
  // Finds the ipos in queue whose distance mapping is minimal
  private static ipos minDistance (Map<ipos,Double> distance, ArrayList<ipos> queue) {
    ipos vert = null;
    double dist = Double.POSITIVE_INFINITY;
    for (ipos v: queue) {
      if (distance.get(v) < dist) {
        dist = distance.get(v);
        vert = v;
      }
    }
    return vert;
  }

  // I have no idea why I named this ipos, and even more
  // confusingly, why I didn't name it Ipos or IPos. 
  class ipos {
  
    int x,y;
    
    public ipos(int X, int Y) {
      this.x = X;
      this.y = Y;
    }
    
    // Distance from this ipos to ipos v
    public double dist (ipos v) {
      if ((Math.abs(this.x-v.x) > 1) || (Math.abs(this.y-v.y) > 1)) return Double.POSITIVE_INFINITY;
      return Math.sqrt((this.x-v.x)*(this.x-v.x)+(this.y-v.y)*(this.y-v.y));
    }
    
    @Override
    public String toString () {
      return "(" + this.x + "," + this.y + ")";
    }
    @Override
    public boolean equals(Object other) {
      if (other == null) return false;
      if (getClass() != other.getClass()) return false;
      if (((ipos)other).x != this.x) return false;
      if (((ipos)other).y != this.y) return false;
      return true;
    }
    @Override
    public int hashCode() {
      return ((this.x * 0xf0f0f0f0) ^ this.y); // This is a pseudo-valid way to do this.
    }
  }
  
}
