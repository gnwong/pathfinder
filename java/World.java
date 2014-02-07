/*
 *  Copyright (C)  2014 George Wong
 *  GNU General Public License
 */
 
import java.util.Random;

public class World {

  // Kind of lame, but I guess we'll set these definitions here
  // 0x00 - null / empty
  // 0x01 - grass (default)
  // 0x02 - water
  // 0x03 - path (for our purposes)
  // 0x04 - waypoint (for our purposes)
  // 0x05 - path v2 (for our purposes)
  // 0x06 - final path (for our purposes)

  // We use the convention standard in printing arrays, namely that
  // MAP[a][b] represents (b,a) on the coordinate axes.
  private byte[][] MAP;
  private int WIDTH, HEIGHT;
  
  // Public constructors
  public World (int width, int height) {
    init_me(width,height,(byte)0x01);
  }
  public World (int width, int height, int type) {
    init_me(width,height,(byte)type);
  }
  public World (int width, int height, byte type) {
    init_me(width,height,type);
  }
  
  public boolean isValid (int x, int y) {
    if (x<0 || x >= this.WIDTH) return false;
    if (y<0 || y > this.HEIGHT) return false;
    return true;
  }
  
  // True constructor
  private void init_me (int width, int height, byte type) {
    this.MAP = new byte[height][width];
    this.WIDTH = width;
    this.HEIGHT = height;
    
    for (int i=0; i<height; i++) {
      for (int j=0; j<width; j++) {
        this.MAP[i][j] = type;
      }
    }
    
    // Always place obstacles here
    this.circleBrush(70,70,8,(byte)0x02);
    this.circleBrush(85,43,5,(byte)0x02);
    this.circleBrush(164,42,27,(byte)0x02);
    
    // Randomly place other obstacles
    Random r = new Random(System.nanoTime());
    for (int i=0; i<20; i++) {
      this.circleBrush((int)(r.nextInt()%this.WIDTH),
                       (int)(r.nextInt()%this.HEIGHT),
                       r.nextInt()%20+2,(byte)0x02);
    }
  }
  
  // Dimensions
  public int getHeight () {
    return this.HEIGHT;
  }
  public int getWidth () {
    return this.WIDTH;
  }
  
  // Returns true if the given square is passable
  public boolean passable (int x, int y) {
    if (this.MAP[y][x] == 0x00) return false;
    if (this.MAP[y][x] == 0x02) return false;
    return true;
  }
  
  // Sets the type of the given square
  public void setTile (int x, int y, byte type) {
    this.MAP[y][x] = type;
  }
  public void setTile (int x, int y, int type) {
    this.MAP[y][x] = (byte)type;
  }
  
  // Returns the type of the given square
  public byte getTile (int x, int y) {
    return this.MAP[y][x];
  }
  
  // Obstacle brush
  private void circleBrush (int x, int y, int r, byte type) {
    x = Math.abs(x);
    y = Math.abs(y);
    r = Math.abs(r);
    for (int i=x-r; i<x+r; i++) {
      for (int j=y-r; j<y+r; j++) {
        if (j<0 || j>HEIGHT-1 || i<0 || i>WIDTH-1) continue;
        if ((y-j)*(y-j) + (x-i)*(x-i) > r*r) continue;
        this.MAP[j][i] = type;
      }
    }
  }
  
}
