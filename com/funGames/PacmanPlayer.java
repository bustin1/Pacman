package com.funGames;

import java.awt.Color;
import java.io.*;

import javax.swing.JFrame;
public class PacmanPlayer {

	public static void main(String[] args) {
		
		int[][] maze = new int[25][25];
		try{
			//int i = 0;
			int j = 0;
			String line = null;
			FileReader file = new FileReader("C:\\Users\\juhsu\\Github\\Pacman\\com\\funGames\\MazeMap.txt");
			BufferedReader buffer = new BufferedReader(file);
			while((line = buffer.readLine()) != null){
				String[] token = line.split(" ");
				for(int i=0; i<25; i++){
					maze[i][j] = Integer.parseInt(token[i]);
				}
				j++;
			}
			buffer.close();
		}
		catch(FileNotFoundException e){
			System.out.println("file not found");
		}
		catch(IOException e){
			System.out.println("problem reading file");
		}
		JFrame frame = new JFrame("PaCmAn");
		frame.add(new Pacman(maze, frame));
		frame.setSize(516, 538);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		/**
		 * 0 is empty space
		 * 1 is coin
		 * 2 is wall
		 * 3 is closed gate
		 * 4 is red ghost
		 * 5 is pink ghost
		 * 6 is orange ghost
		 * 7 is cyan ghost
		 * 9 is energizer
		 */
	}

}
