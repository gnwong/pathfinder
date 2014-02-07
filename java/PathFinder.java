/*
 *  `PathFinder.java'
 *    A simple program with GUI demonstrating how a simple pathfinding
 *    algorithm works given randomly placed obstacles.
 *
 *  Copyright (C)  2014 George Wong
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/.
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;

public class PathFinder {

  public static JFrame frame;
  public static WorldView wv;
  public static SideBar sb;
  public static World world;
  public static JPanel sideBar;
  public static Path p;
  
  private static int[] CLICKED_POSITION;

  public static void main (String[] args) {
  
    CLICKED_POSITION = new int[2];
    p = new Path(0,0,0,0);
  
    world = new World(200, 200);
    createGUI();
    
  }
  
  private static void createGUI () {
    frame = new JFrame();
    frame.setTitle("PathFinder | wong1275/pathfinder");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(new Dimension(650,500));
    frame.setMinimumSize(new Dimension(480,320));
    frame.setLocationRelativeTo(null);
    frame.setLayout(new BorderLayout());
    
    wv = new WorldView(world.getWidth(), world.getHeight(), world);
    frame.add(wv, BorderLayout.CENTER);
    
    sb = new SideBar();
    frame.add(sb, BorderLayout.LINE_END);
    
    frame.validate();
    frame.setVisible(true);
  }

  // Don't yell at me for putting this here, O ye
  // proper Java programmers!
  static class WorldView extends JPanel {

    BufferedImage view;
    World w;

    public WorldView (int width, int height, World world) {
      super();
      this.w = world;
      view = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed (MouseEvent e) {
          CLICKED_POSITION[0] = e.getX() * w.getWidth() / getWidth();
          CLICKED_POSITION[1] = e.getY() * w.getHeight() / getHeight();
          sb.updateCoordinate();
        }
      });
    }
    
    
    @Override
    public void paint (Graphics g) {
      int world_height = world.getHeight();
      int world_width = world.getWidth();
      
      for (int i=0; i<world_height; i++) {
        for (int j=0; j<world_width; j++) {
          Color c = new Color(0,0,0);
          if (w.getTile(j,i) == (byte)0x01) c = new Color(0,153,51);
          else if (w.getTile(j,i) == (byte)0x02) c = new Color(28,107,180);
          else if (w.getTile(j,i) == (byte)0x03) c = new Color(255,0,0);
          else if (w.getTile(j,i) == (byte)0x04) c = Color.BLACK;
          else if (w.getTile(j,i) == (byte)0x05) c = new Color(0,255,0);
          else if (w.getTile(j,i) == (byte)0x06) c = new Color(0,0,255);
          view.setRGB(j,i,c.getRGB());
        }
      }
      
      g.drawImage(view,0,0,this.getWidth(),this.getHeight(),null);
    }
    
    // Set a single pixel
    public void setPixel (int x, int y, int color) {
      view.setRGB(x,y,color);
    }
    
    // Turns r,g,b into int
    private int toRGB (int r, int g, int b) {
      return new Color(r,g,b).getRGB();
    }
    
  }


  // Don't yell at me for this either?
  static class SideBar extends JPanel {
  
    JLabel coordinate;
  
    // Constructor
    public SideBar () {
      this.setBackground(Color.WHITE);
      this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      this.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Color.BLACK), new EmptyBorder(10,10,10,10)));
      this.setLayout(new GridLayout(4,1));
      
      coordinate = new JLabel("(0,0)",SwingConstants.CENTER);
      
      this.add(coordinate);
      
      JButton set_start = new JButton("SET START");
      JButton set_end = new JButton("SET END");
      JButton trace = new JButton("TRACE");
      
      this.add(set_start);
      this.add(set_end);
      this.add(trace);
      
      set_start.addActionListener(new ActionListener () {
        @Override
        public void actionPerformed (ActionEvent e) {
          p.setStart(CLICKED_POSITION[0], CLICKED_POSITION[1]);
          world.setTile(CLICKED_POSITION[0],CLICKED_POSITION[1],0x03);
          wv.repaint();
        }
      });
      
      set_end.addActionListener(new ActionListener () {
        @Override
        public void actionPerformed (ActionEvent e) {
          p.setEnd(CLICKED_POSITION[0], CLICKED_POSITION[1]);
          world.setTile(CLICKED_POSITION[0],CLICKED_POSITION[1],0x03);
          wv.repaint();
        }
      });
      
      trace.addActionListener(new ActionListener () {
        @Override
        public void actionPerformed (ActionEvent e) {
          p.trace(world);
          wv.repaint();
        }
      });
      
    }
    
    public void updateCoordinate () {
      coordinate.setText("(" + CLICKED_POSITION[0] + "," + CLICKED_POSITION[1] + ")");
    } 
  }
}
