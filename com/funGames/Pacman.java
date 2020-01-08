package com.funGames;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JPanel;
public class Pacman extends JPanel implements ActionListener, KeyListener{
	
	javax.swing.Timer t = new javax.swing.Timer(15, this);
	java.util.Timer c5 = new java.util.Timer();//timer for the animation

	double[] pacCor = {240, 240};//the coordinates of pacman
	double[] pacSpe = {0, 0};//pacman's speed
	int[][] maze;//maze structure
	double[][] ghostCor = {
			{220, 200},//red
			{240, 200},//pink
			{260, 200},//cyan
			{240, 200}//orange
	};//coordinates of each ghost
	double[] ghostSpe = {.85, .7, .83, .8};//speed of ghost
	int[] direction = {3, 3, 1, 1};//ghost's direction
	int open = 0;//whether the gate is open
	int ready = 1;//how many ghost are in play
	int chase = 0;//chase mode?
	int face = 0;//direction pacman is facing
	int phase = 0;//counts the amount of phases gone by
	int energizer = 0;//makes ghost move backwards once "big coin" is eaten
	int[] scared = {0, 0, 0, 0};//which ghost is scared
	int[] counter = {0, 0, 0, 0};//complicated ... but prevents ghost from mocing backwards when ghost speed is a decimal 
	int points = 0;//how many points
	int combo = 1;//combos the points with how many ghost eaten so far
	int pump = 0;//gives pacman the speed
	int eaten = 0;//did you eat a ghost?
	int ate = 0;//did the ghost eat you?
	int fade = 0;//fade animations
	int lives = 3;//number of lives
	int beginTime = 0;//begining to open the gates
	int gateTime = 0;//durations of gate opening
	int fadeTime = 0;//durations of fade animations
	int phaseTime = 0;//durations of the ghost phases
	int coinCount = 276;
	
	public Pacman(int[][] maze, JFrame frame){
		t.start();
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		this.maze = maze;
		beginTime = 1;
		phaseTime = 1;
		open = 1;
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		setBackground(Color.black);
		Rectangle wall = new Rectangle(0, 0, 20, 20);
		Rectangle gate = new Rectangle(0, 0, 20, 10);
		Ellipse2D.Double coin = new Ellipse2D.Double(3, 3, 14, 14);
		for(int i=0; i<25; i++){//maze row length
			for(int j=0; j<25; j++){//maze colunm length
				if(maze[i][j] == 1){
					coin.setFrame(i*20+5, j*20+5, 10, 10);
					coinForMe(pacCor, new double[]{i*20+5, j*20+5}, new double[]{20, 20}, new double[]{10, 10});
					g2.setColor(Color.YELLOW);
					g2.fill(coin);
				}
				if(maze[i][j] == 2){
					wall.setLocation(i*20, j*20);
					pacCor = checkHit(pacCor, new double[]{i*20, j*20}, new double[]{20, 20}, new double[]{20, 20}, pacSpe);
					g2.setColor(Color.BLUE);
					g2.fill(wall);
				}
				if(maze[i][j] == 3){
					gate.setLocation(i*20, j*20);
					if(open == 1){
						gate.setSize(10, 20);
						pacCor = checkHit(pacCor, new double[]{i*20, j*20}, new double[]{20, 20}, new double[]{20, 20}, pacSpe);
					}else{
						gate.setSize(20, 10);
						pacCor = checkHit(pacCor, new double[]{i*20, j*20}, new double[]{20, 20}, new double[]{20, 20}, pacSpe);
					}
					g2.setColor(Color.WHITE);
					g2.fill(gate);
				}
				if(maze[i][j] == 9){
					coin.setFrame(i*20+2, j*20+2, 16, 16);
					coinForMe(pacCor, new double[]{i*20+2, j*20+2}, new double[]{20, 20}, new double[]{16, 16});
					g2.setColor(Color.YELLOW);
					g2.fill(coin);
				}
			}
		}
		Rectangle[] ghost = new Rectangle[4];
		for(int i=3; i>=0; i--){
			ghost[i] = new Rectangle((int)ghostCor[i][0], (int)ghostCor[i][1], 20, 20);
			if(scared[i] == 1 && fade == 1){
				g2.setColor(Color.DARK_GRAY);
			}else if(i == 0){
				g2.setColor(Color.RED);
			}else if(i == 1){
				g2.setColor(Color.PINK);
			}else if(i == 2){
				g2.setColor(Color.CYAN);
			}else if(i == 3){
				g2.setColor(Color.ORANGE);
			}
			if(ate == 0){
				eat(pacCor, ghostCor[i], new double[]{20, 20}, new double[]{20, 20}, i);
			}
			g2.fill(ghost[i]);
		}
		Rectangle pac = new Rectangle((int)pacCor[0], (int)pacCor[1], 20, 20);
		g2.setColor(Color.YELLOW);
		g2.fill(pac);
		Rectangle livesPac = new Rectangle(400, 0, 14, 14);
		for(int i=0; i<lives; i++){
			livesPac.setLocation(400+i*25, 3);
			g2.fill(livesPac);
		}
		g2.setColor(Color.CYAN);
		g2.drawString("points: "+points, 10, 10);
		if(eaten == 1){
			g2.drawString(""+combo*100, (int)pacCor[0], (int)pacCor[1]-5);
			c5.schedule(new TimerTask() {
				public void run(){
					eaten = 0;
				}
			}, 1000);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(eaten == 0 && ate == 0){
			if(gateTime >= 1 && pump == 1){
				gateTime++;
				if(gateTime == 200){
					open = 0;
					gateTime = 0;
				}
			}
			if(beginTime >= 1 && ready < 5){
				beginTime++;
				if(beginTime == 130){
					open = 0;
					if(ready != 4){
						ready++;
					}else{
						beginTime = 0;
					}
				}else if(beginTime == 260){
					open = 1;
					beginTime = 1;
				}
			}
			if(phaseTime >= 1 && phase < 4 && pump == 0){
				phaseTime++;
				if(phaseTime == 400){
					chase = 1;
				}
				else if(phaseTime == 1000){
					chase = 0;
					phaseTime = 1;
					phase++;
					if(phase == 4){
						chase = 1;
						phaseTime = 0;
					}
				}
			}
			if(pump == 1){
				fadeTime++;
				if(fadeTime > 600){
					pump = 0;
					fadeTime = 0;
					combo = 1;
					for(int i=0; i<ready; i++){
						scared[i] = 0;
						if(i == 0)
							ghostSpe[i] = .85;
						if(i == 1)
							ghostSpe[i] = .7;
						if(i == 2)
							ghostSpe[i] = .83;
						if(i == 3)
							ghostSpe[i] = .8;
					}
				}
			}else{
				fade = 0;
			}
			if(fadeTime >= 466 && fadeTime%30 == 0){
				if(fade == 1){
					fade = 0;
				}else{
					fade = 1;
				}
			}
			pacCor[0] += pacSpe[0];
			pacCor[1] += pacSpe[1];
			if(pacCor[0] < -20){
				pacCor[0] = 500;
			}
			else if(pacCor[0] > 500){
				pacCor[0] = -20;
			}
			int ref[] = new int[6];
			for(int i=0; i<ready; i++){
				if(direction[i] == 0){
					if(counter[i] == 0){
						ghostCor[i][1] -= ghostSpe[i];
					}else{
						ghostCor[i][1] -= 1;
					}
					pos(i, ref);
					if(energizer == 1){
						direction[i] = 2;
					}
					counter[i] = 0;
					if((int)ghostCor[i][1]%20 == 0){
						ghostCor[i][1] = (int)(ghostCor[i][1]/20)*20;
						counter[i] = 1;
						if(chase == 1 && scared[i] == 0){
							if(pacCor[0] > 400 && ghostCor[i][0] < 100 && ghostCor[i][0] > 0){
								direction[i] = scatterChase(ghostCor[i][0], ghostCor[i][1], -20, 260, 2);
							}
							else if(pacCor[0] < 100 && ghostCor[i][0] > 400 && ghostCor[i][0] < 500){
								direction[i] = scatterChase(ghostCor[i][0], ghostCor[i][1], 500, 260, 2);
							}
							else if(ref[1] >= 0 && ref[1] < 25 && (maze[ref[1]][ref[5]] != 2 
								&& maze[ref[1]][ref[5]] != 3
								|| maze[ref[3]][ref[5]] != 2
								&& maze[ref[3]][ref[5]] != 3)){
								whichChase(i, 2);
							}
						}
						else if(chase == 0 || scared[i] == 1){
							if(maze[ref[1]][ref[5]] != 2
								&& maze[ref[1]][ref[5]] != 3
								|| maze[ref[3]][ref[5]] != 2
								&& maze[ref[3]][ref[5]] != 3){
								whichScatter(i, 2);
							}
						}
					}
				}
				else if(direction[i] == 1){
					if(counter[i] == 0){
						ghostCor[i][0] -= ghostSpe[i];
					}else{
						ghostCor[i][0] -= 1;
					}
					if(ghostCor[i][0] < -20){
						ghostCor[i][0] = 500;
					}
					pos(i, ref);
					if(energizer == 1){
						direction[i] = 3;
					}
					counter[i] = 0;
					if((int)ghostCor[i][0]%20 == 0){
						ghostCor[i][0] = (int)(ghostCor[i][0]/20)*20;
						counter[i] = 1;
						if(chase == 1 && scared[i] == 0){
							if(ghostCor[i][0] < 20 || ghostCor[i][0] >= 480){
								direction[i] = 1;
							}
							else if(pacCor[0] > 400 && ghostCor[i][0] < 100 && ghostCor[i][0] > 0){
								direction[i] = scatterChase(ghostCor[i][0], ghostCor[i][1], -20, 260, 3);
							}
							else if(pacCor[0] < 100 && ghostCor[i][0] > 400 && ghostCor[i][0] < 500){
								direction[i] = scatterChase(ghostCor[i][0], ghostCor[i][1], 500, 260, 3);
							}
							else if(maze[ref[4]][ref[0]] == 3 && open == 1){
								direction[i] = 0;
							}
							else if(ref[4] >= 0 && ref[4] < 25 && maze[ref[4]][ref[0]] != 2
								&& maze[ref[4]][ref[0]] != 3
								|| maze[ref[4]][ref[2]] != 2
								&& maze[ref[4]][ref[2]] != 3){
								whichChase(i, 3);
							}
						}
						else if(chase == 0 || scared[i] == 1){
							if(open == 1 && ref[0] >= 0 && ref[4] < 25 && maze[ref[4]][ref[0]] == 3){
								direction[i] = 0;
							}
							else if(ref[4] < 25 && ref[4] >= 0 && (maze[ref[4]][ref[0]] != 2
								&& maze[ref[4]][ref[0]] != 3
								|| maze[ref[4]][ref[2]] != 2
								&& maze[ref[4]][ref[2]] != 3)){
								whichScatter(i, 3);
							}
						}
						if(ref[1] >= 0 && ref[4] < 25 && maze[ref[4]][ref[0]] == 2
							&& maze[ref[4]][ref[2]] == 2
							&& maze[ref[1]][ref[5]] == 2){
							direction[i] = 3;
						}
					}
				}
				else if(direction[i] == 2){
					if(counter[i] == 0){
						ghostCor[i][1] += ghostSpe[i];
					}else{
						ghostCor[i][1] += 1;
					}
					pos(i, ref);
					if(energizer == 1){
						direction[i] = 0;
					}
					counter[i] = 0;
					if((int)ghostCor[i][1]%20 == 0){ 
						ghostCor[i][1] = (int)(ghostCor[i][1]/20)*20;
						counter[i] = 1;
						if(chase == 1 && scared[i] == 0){
							if(pacCor[0] > 400 && ghostCor[i][0] < 100 && ghostCor[i][0] > 0){
								direction[i] = scatterChase(ghostCor[i][0], ghostCor[i][1], -20, 260, 0);
							}
							else if(pacCor[0] < 100 && ghostCor[i][0] > 400 && ghostCor[i][0] < 500){
								direction[i] = scatterChase(ghostCor[i][0], ghostCor[i][1], 500, 260, 0);
							}
							else if(ref[1] >= 0 && ref[5] < 25 && (maze[ref[1]][ref[5]] != 2
								&& maze[ref[1]][ref[5]] != 3
								|| maze[ref[3]][ref[5]] != 2
								&& maze[ref[3]][ref[5]] != 3)){
								whichChase(i, 0);
							}
						}
						else if(chase == 0 || scared[i] == 1){
							if(ref[1] >= 0 && ref[1] < 25 && maze[ref[1]][ref[5]] != 2
								&& maze[ref[1]][ref[5]] != 3
								|| maze[ref[3]][ref[5]] != 2
								&& maze[ref[3]][ref[5]] != 3){
								whichScatter(i, 0);
							}
						}
					}
				}
				else if(direction[i] == 3){
					if(counter[i] == 0){
						ghostCor[i][0] += ghostSpe[i];
					}else{
						ghostCor[i][0] += 1;
					}
					if(ghostCor[i][0] > 500){
						ghostCor[i][0] = -20;
					}
					counter[i] = 0;
					pos(i, ref);
					if(energizer == 1){
						direction[i] = 1;
					}
					if((int)ghostCor[i][0]%20 == 0){
						ghostCor[i][0] = (int)(ghostCor[i][0]/20)*20;
						counter[i] = 1;
						if(chase == 1 && scared[i] == 0){
							if(ghostCor[i][0] >= 480 || ghostCor[i][0] < 0){
								direction[i] = 3;
							}
							else if(pacCor[0] > 400 && ghostCor[i][0] < 100 && ghostCor[i][0] > 0){
								direction[i] = scatterChase(ghostCor[i][0], ghostCor[i][1], -20, 260, 1);
							}
							else if(pacCor[0] < 100 && ghostCor[i][0] > 400 && ghostCor[i][0] < 500){
								direction[i] = scatterChase(ghostCor[i][0], ghostCor[i][1], 500, 260, 1);
							}
							else if(maze[ref[4]][ref[0]] == 3 && open == 1){
								direction[i] = 0;
							}
							else if(ref[4] >= 0 && ref[4] < 25 && maze[ref[4]][ref[0]] != 2
								&& maze[ref[4]][ref[0]] != 3
								|| maze[ref[4]][ref[2]] != 2
								&& maze[ref[4]][ref[2]] != 3){
								whichChase(i, 1);
							}
						}
						else if (chase == 0 || scared[i] == 1){
							if(open == 1 && ref[4] >= 0 && ref[4] < 25 && maze[ref[4]][ref[0]] == 3){
								direction[i] = 0;
							}
							else if(ref[4] < 25 && ref[4] >= 0 && (maze[ref[4]][ref[0]] != 2
								&& maze[ref[4]][ref[0]] != 3
								|| maze[ref[4]][ref[2]] != 2
								&& maze[ref[4]][ref[2]] != 3)){
								whichScatter(i, 1);
							}
						}
						if(ref[3] < 25 && ref[4] >= 0 && maze[ref[4]][ref[0]] == 2
							&& maze[ref[4]][ref[2]] == 2
							&& maze[ref[3]][ref[5]] == 2){
							direction[i] = 1;
						}
					}
				}
			}
			energizer = 0;
		}
		repaint();
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getExtendedKeyCode();
		if(code == KeyEvent.VK_UP && turnKey(0, -1)){
			face = 0;
			if(pump == 0){
				setY(-.8);
				setX(0);
			}else{
				setY(-1);
				setX(0);
			}
		}
		else if(pacCor[0] >= 15 && code == KeyEvent.VK_LEFT && turnKey(-1, 0)){
			face = 1;
			if(pump == 0){
				setX(-.8);
				setY(0);
			}else{
				setX(-1);
				setY(0);
			}
		}
		else if(code == KeyEvent.VK_DOWN && turnKey(0, 1)){
			face = 2;
			if(pump == 0){
				setY(.8);
				setX(0);
			}else{
				setY(1);
				setX(0);
			}
		}
		else if(pacCor[0] <= 465 && code == KeyEvent.VK_RIGHT && turnKey(1, 0)){
			face = 3;
			if(pump == 0){
				setX(.8);
				setY(0);
			}else{
				setX(1);
				setY(0);
			}
		}
	}
	public void setX(double x){ pacSpe[0] = x; }
	public void setY(double y){ pacSpe[1] = y; }

	@Override
	public void keyReleased(KeyEvent arg0) {}
	
	@Override
	public void keyTyped(KeyEvent arg0) {}
	
	private double[] checkHit(double[] coor1, double[] coor2, double[] d1, double[] d2, double[] spe){
		if(Math.abs(coor1[0]-coor2[0]) < ((d1[0]+d2[0])/2-.5)
		&& Math.abs(coor1[1]-coor2[1]) < ((d1[1]+d2[1])/2-.5)){
			coor1[0] -= spe[0];
			coor1[1] -= spe[1];
			setX(0);
			setY(0);
		}
		return coor1;
	}
	
	private void coinForMe(double[] coor1, double[] coor2, double[] d1, double[] d2){
		if(Math.abs(coor1[0]-coor2[0]) < ((d1[0]+d2[0])/2-5)
		&& Math.abs(coor1[1]-coor2[1]) < ((d1[1]+d2[1])/2-5)){
			maze[(int)coor2[0]/20][(int)coor2[1]/20] = 0;
			points += 10;
			if(d2[0] == 16){
				energizer = 1;
				fade = 1;
				pump = 1;
				fadeTime = 0;
				for(int i=0; i<4; i++){
					scared[i] = 1;
					ghostSpe[i] = .5;
				}
			}else{
				coinCount--;
				if(coinCount == 0){
					ate = 1;
					c5.schedule(new TimerTask() {
						public void run(){
							ate = 0;
							System.out.println("you win");
							System.exit(0);
						}
					}, 1000);
				}
			}
		}
	}
	
	private boolean turnKey(int a, int b){
		if(a != 0 && (pacCor[1]%20 >= 15 || pacCor[1]%20 <= 5)
		&& maze[(int)(pacCor[0]/20+a)][(int)(pacCor[1]+5)/20] != 2
		&& maze[(int)(pacCor[0]/20+a)][(int)(pacCor[1]+5)/20] != 3){
			pacCor[1] = (int)((int)((pacCor[1]+5)/20))*20;
			return true;
		}
		else if(b != 0 && (pacCor[0]%20 >= 15 || pacCor[0]%20<= 5) 
		&& maze[(int)((pacCor[0]+5)/20)][(int)(pacCor[1]/20+b)] != 2
		&& maze[(int)((pacCor[0]+5)/20)][(int)(pacCor[1]/20+b)] != 3){
			pacCor[0] = (int)((int)((pacCor[0]+5)/20))*20;
			return true;
		}
		else{
			return false;
		}
	}
	
	private int scatterChase(double x, double y, double chaseX, double chaseY, int out){
		double[] distance = {5000000, 5000000, 5000000, 5000000};
		if(maze[(int)x/20][(int)y/20-1] != 2 && maze[(int)x/20][(int)y/20-1] != 3 && out != 0){
			distance[0] = Math.pow(x-chaseX, 2) + Math.pow(y-chaseY-20, 2);
		}
		if(maze[(int)x/20-1][(int)y/20] != 2 && maze[(int)x/20-1][(int)y/20] != 3 && out != 1){
			distance[1] = Math.pow(x-chaseX-20, 2) + Math.pow(y-chaseY, 2);
		}
		if(maze[(int)x/20][(int)y/20+1] != 2 && maze[(int)x/20][(int)y/20+1] != 3 && out != 2){
			distance[2] = Math.pow(x-chaseX, 2) + Math.pow(y-chaseY+20, 2);
		}
		if(maze[(int)x/20+1][(int)y/20] != 2 && maze[(int)x/20+1][(int)y/20] != 3 && out != 3){
			distance[3] = Math.pow(x-chaseX+20, 2) + Math.pow(y-chaseY, 2);
		}
		
		if(distance[0] <= distance[1] && distance[0] <= distance[2] && distance[0] <= distance[3]){
			return 0;
		}
		else if(distance[1] <= distance[0] && distance[1] <= distance[2] && distance[1] <= distance[3]){
			return 1;
		}
		else if(distance[2] <= distance[0] && distance[2] <= distance[1] && distance[2] <= distance[3]){
			return 2;
		}
		else if(distance[3] <= distance[0] && distance[3] <= distance[1] && distance[3] <= distance[2]){
			return 3;
		}
		return (int)Math.random()*3;
	}
	
	private void whichScatter(int i, int notDir){
		if(i == 0){
			direction[i] = scatterChase(ghostCor[i][0], ghostCor[i][1], 450, -40, notDir);
		}	
		else if(i == 1){
			direction[i] = scatterChase(ghostCor[i][0], ghostCor[i][1], 50, -40, notDir);
		}	
		else if(i == 2){
			direction[i] = scatterChase(ghostCor[i][0], ghostCor[i][1], -50, 540, notDir);
		}	
		else{
			direction[i] = scatterChase(ghostCor[i][0], ghostCor[i][1], 550, 540, notDir);
		}
	}
	
	private void pos(int i, int[] ref){
		ref[0] = (int)(ghostCor[i][1]/20)-1;//up
		ref[1] = (int)(ghostCor[i][0]/20)-1;//left
		ref[2] = (int)(ghostCor[i][1]/20+1);//down
		ref[3] = (int)(ghostCor[i][0]/20+1);//right
		ref[4] = (int)(ghostCor[i][0]/20);//x
		ref[5] = (int)(ghostCor[i][1]/20);//y
	}
	
	private void whichChase(int i, int notDir){
		if(i == 0){
			direction[i] = scatterChase(ghostCor[i][0], ghostCor[i][1], pacCor[0], pacCor[1], notDir);
		}
		if(i == 1){
			if(face == 0){
				direction[i] = scatterChase(ghostCor[i][0], ghostCor[i][1], pacCor[0], pacCor[1]-80, notDir);
			}else if(face == 1){
				direction[i] = scatterChase(ghostCor[i][0], ghostCor[i][1], pacCor[0]-80, pacCor[1], notDir);
			}else if(face == 2){
				direction[i] = scatterChase(ghostCor[i][0], ghostCor[i][1], pacCor[0], pacCor[1]+80, notDir);
			}else if(face == 3){
				direction[i] = scatterChase(ghostCor[i][0], ghostCor[i][1], pacCor[0]+80, pacCor[1], notDir);
			}
		}
		if(i == 2){
			double vectorx;
			double vectory;
			if(face == 0){
				vectorx = 2*(pacCor[0]-ghostCor[0][0])+ghostCor[0][0];
				vectory = 2*(pacCor[1]-80-ghostCor[0][1])+ghostCor[0][1];
			}else if(face == 1){
				vectorx = 2*(pacCor[0]-80-ghostCor[0][0])+ghostCor[0][0];
				vectory = 2*(pacCor[1]-ghostCor[0][1])+ghostCor[0][1];
			}else if(face == 2){
				vectorx = 2*(pacCor[0]-ghostCor[0][0])+ghostCor[0][0];
				vectory = 2*(pacCor[1]+80-ghostCor[0][1])+ghostCor[0][1];
			}else {
				vectorx = 2*(pacCor[0]+80-ghostCor[0][0])+ghostCor[0][0];
				vectory = 2*(pacCor[1]-ghostCor[0][1])+ghostCor[0][1];
			}
			direction[i] = scatterChase(ghostCor[i][0], ghostCor[i][1], vectorx, vectory, notDir);
		}
		if(i == 3){
			if(Math.sqrt(Math.pow(pacCor[0]-ghostCor[i][0], 2) + Math.pow(pacCor[1]-ghostCor[i][1], 2)) <= 200){
				direction[i] = scatterChase(ghostCor[i][0], ghostCor[i][1], 550, 540, notDir);
			}else{
				direction[i] = scatterChase(ghostCor[i][0], ghostCor[i][1], pacCor[0], pacCor[1], notDir);
			}
		}
	}
	
	private void eat(double[] coor1, double[] coor2, double[] d1, double[] d2, int i){
		if(Math.abs(coor1[0]-coor2[0]) < ((d1[0]+d2[0])/2-3)
		&& Math.abs(coor1[1]-coor2[1]) < ((d1[1]+d2[1])/2-3)){
			if(scared[i] == 1){
				eaten = 1;
				points += 200*combo;
				combo *= 2;
				ghostCor[i][0] = 240;
				ghostCor[i][1] = 200;
				direction[i] = 1;
				scared[i] = 0;
				gateTime = 1;
				open = 1;
				if(i == 0)
					ghostSpe[i] = .85;
				if(i == 1)
					ghostSpe[i] = .7;
				if(i == 2)
					ghostSpe[i] = .83;
				if(i == 3)
					ghostSpe[i] = .8;
			}else{
				ate = 1;
				c5.schedule(new TimerTask() {
					public void run(){
						pacCor[0] = 240;
						pacCor[1] = 240;
						pacSpe[0] = 0;
						pacSpe[1] = 0;
						ghostCor[0][0] = 220;
						ghostCor[0][1] = 200;	
						ghostCor[1][0] = 240;
						ghostCor[1][1] = 200;
						ghostCor[2][0] = 260;
						ghostCor[2][1] = 200;
						ghostCor[3][0] = 240;
						ghostCor[3][1] = 200;
						ghostSpe[0] = .85;
						ghostSpe[1] = .7;
						ghostSpe[2] = .83;
						ghostSpe[3] = .8;
						direction[0] = 3;
						direction[1] = 3;
						direction[2] = 1;
						direction[3] = 1;
						open = 1;
						ready = 1;
						face = 0;
						energizer = 0;
						counter[0] = 0;
						counter[1] = 0;
						counter[2] = 0;
						counter[3] = 0;
						pump = 0;
						beginTime = 1;
						combo = 1;
						gateTime = 0;
						ate = 0;
						lives--;
						System.out.println("you have "+lives+" remaining");
						if(lives == 0){
							System.out.println("you lose, game over");
							System.exit(0);
						}
					}
				}, 1000);
			}
		}
	}
}
