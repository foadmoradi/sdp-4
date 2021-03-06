import cv
import time
from imageProcFunctions import *
from navigation import *
from server import *
from worldstate import *
from setup import *
import os
import curses

def draw_on_image(image, center_points):
    cv.Circle(image, center_points[0], 2, cv.RGB(0,0,0),-1)	
    cv.Circle(image, center_points[1][0], 2, cv.RGB(0,0,0),-1)	
    cv.Circle(image, center_points[2][0], 2, cv.RGB(0,0,0),-1)
    	
    cv.ShowImage("Processed:", image)
	
def update_worldstate(center_points):
    WorldState.lock.acquire()
    WorldState.ball["position"]["x"] = center_points[0][0]
    WorldState.ball["position"]["y"] = center_points[0][1]
    WorldState.blue["position"]["x"] = center_points[1][0][0]
    WorldState.blue["position"]["y"] = center_points[1][0][1]
    WorldState.blue["rotation"] = center_points[1][1]
    WorldState.yellow["position"]["x"] = center_points[2][0][0]
    WorldState.yellow["position"]["y"] = center_points[2][0][1]
    WorldState.yellow["rotation"] = center_points[2][1]	
    WorldState.lock.release()

setup_system()
cam = cv.CaptureFromCAM(0)
os.system('/home/s0806628/sdp/vision/src/modv4l.sh')
stdscr = curses.initscr()
curses.start_color()

curses.init_pair(1, curses.COLOR_RED, curses.COLOR_BLACK)
curses.init_pair(2, curses.COLOR_BLUE, curses.COLOR_BLACK)
curses.init_pair(3, curses.COLOR_YELLOW, curses.COLOR_BLACK)

old_ball_position = (0,0)
old_blue_position = (0,0)
old_yellow_position = (0,0)

old_time = time.time()

intrinsics = cv.Load("Intrinsics.xml")
distortion = cv.Load("Distortion.xml")

image = cv.QueryFrame(cam)
mapx = cv.CreateImage(cv.GetSize(image), cv.IPL_DEPTH_32F, 1)
mapy = cv.CreateImage(cv.GetSize(image), cv.IPL_DEPTH_32F, 1)
cv.InitUndistortMap(intrinsics, distortion, mapx, mapy)

while (True):
    start = time.time()
    image = cv.QueryFrame(cam)
  
    processed = cv.CloneImage(image)
    cv.Remap(image, processed, mapx, mapy)
    crop_rect = (0, 63, 640, 350)
    cv.SetImageROI(processed, crop_rect)

    ball_center = find_object(processed,"RED")
    ball_center = (int(ball_center[0][0]) + 8, int(ball_center[0][1]) + 8)
    blue_center = find_object(processed,"BLUE")
    blue_center = ((int(blue_center[0][0]) + 8, int(blue_center[0][1]) + 12), blue_center[1])
    yellow_center = find_object(processed,"YELLOW")
    yellow_center = ((int(yellow_center[0][0]) + 8, int(yellow_center[0][1]) + 12), yellow_center[1])
	
    center_points = (ball_center, blue_center, yellow_center)
    draw_on_image(processed, center_points)
	
    update_worldstate(center_points)
    
    now = time.time()
    time_diff = now - old_time   
    stdscr.erase()
    stdscr.addstr(0,0, "Blue Player", curses.A_UNDERLINE | curses.color_pair(2))
    stdscr.addstr(1,0, "Orientation: " + str(blue_center[1]))
    stdscr.addstr(2,0, "Position:" + str(blue_center[0]))
    
    stdscr.addstr(6,0, "Yellow Player", curses.A_UNDERLINE | curses.color_pair(3))
    stdscr.addstr(7,0, "Orientation: " + str(yellow_center[1]))
    stdscr.addstr(8,0, "Position:" + str(yellow_center[0]))

    stdscr.addstr(12,0, "Ball", curses.A_UNDERLINE | curses.color_pair(1))
    stdscr.addstr(13,0, "Position:" + str(ball_center))

    stdscr.addstr(17,0, "FPS:" + str(1/(now-start)))
    stdscr.refresh()

    old_ball_position = ball_center
    old_yellow_position = yellow_center
    old_blue_position = blue_center

    old_time = now	
    cv.WaitKey(3)
