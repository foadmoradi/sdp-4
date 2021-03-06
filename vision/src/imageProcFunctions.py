import cv
import math
from navigation import *

mods = [0.0, 0.4, 0.4, 0.05, 1.0, 1.0, 0.3, 0.3, 0.5, 0.8, 1.0, 1.0, 0.1, 0.36, 0.37, 0.2, 1.0, 1.0, 0.0, 0.0, 0.8, 1.0, 0.16, 1.0]

def onHLowerRChange(position):	mods[0] = position/255.0
def onSLowerRChange(position):	mods[1] = position/255.0
def onVLowerRChange(position):	mods[2] = position/255.0
def onHUpperRChange(position):	mods[3] = position/255.0
def onSUpperRChange(position):	mods[4] = position/255.0
def onVUpperRChange(position):	mods[5] = position/255.0

def onHLowerBChange(position):	mods[6] = position/255.0
def onSLowerBChange(position):	mods[7] = position/255.0
def onVLowerBChange(position):	mods[8] = position/255.0
def onHUpperBChange(position):	mods[9] = position/255.0
def onSUpperBChange(position):	mods[10] = position/255.0
def onVUpperBChange(position):	mods[11] = position/255.0

def onHLowerYChange(position):	mods[12] = position/255.0
def onSLowerYChange(position):	mods[13] = position/255.0
def onVLowerYChange(position):	mods[14] = position/255.0
def onHUpperYChange(position):  mods[15] = position/255.0
def onSUpperYChange(position):	mods[16] = position/255.0
def onVUpperYChange(position):	mods[17] = position/255.0

def onHLowerBkChange(position):	mods[18] = position/255.0
def onSLowerBkChange(position):	mods[19] = position/255.0
def onVLowerBkChange(position):	mods[20] = position/255.0
def onHUpperBkChange(position):	mods[21] = position/255.0
def onSUpperBkChange(position): mods[22] = position/255.0
def onVUpperBkChange(position):	mods[23] = position/255.0

def find_orientation(mask, center_point):
    cv.Circle(mask, center_point, 19, cv.RGB(0,0,0), -1)

    moments = cv.Moments(mask, 1)
    M00 = cv.GetSpatialMoment(moments,0,0)
    M10 = cv.GetSpatialMoment(moments,1,0)
    M01 = cv.GetSpatialMoment(moments,0,1)
    
    if M00 == 0:
        M00 = 0.01

    center_of_mass = (round(M10/M00), round(M01/M00))

    return (int(calculate_bearing(center_of_mass, center_point)) - 180) % 360

def find_object(img, colour):
    '''
    Finds the objects in an image with given colour.
    Arguments:
    img	    -- the image to be processed
    colour  -- the colour to look for (red, blue or yellow)
    
    Returns:
    Point representing object's centre of mass

    '''
    # Convert to hsv
    size = cv.GetSize(img)
    tempImage = cv.CreateImage((size[0] / 2, size[1] / 2), 8, 3)
    
    # Reduce noise by down- and up-scaling input image
    cv.PyrDown(img, tempImage, 7)
    cv.PyrUp(tempImage, img, 7)
    hsv = cv.CreateImage(size, cv.IPL_DEPTH_8U, 3)
    cv.CvtColor(img, hsv, cv.CV_BGR2HSV)
    # Convert to binary image based on colour

    mask = cv.CreateMat(size[1], size[0], cv.CV_8UC1)
    maskSize = cv.GetSize(mask)
    if (colour == "RED"):
    	redLower = cv.Scalar(mods[0]*256, mods[1]*256, mods[2]*256)
    	redUpper = cv.Scalar(mods[3]*256, mods[4]*256, mods[5]*256)
    	cv.InRangeS(hsv, redLower, redUpper, mask)		
    	cv.ShowImage("Red:", mask)
    elif (colour == "BLUE"):
    	blueLower = cv.Scalar(mods[6]*256, mods[7]*256, mods[8]*256)
    	blueUpper = cv.Scalar(mods[9]*256, mods[10]*256, mods[11]*256)
    	cv.InRangeS(hsv, blueLower, blueUpper, mask)
	cv.ShowImage("Blue:", mask)
    elif (colour == "YELLOW"):
    	yellowLower = cv.Scalar(mods[12]*256, mods[13]*256, mods[14]*256)
	yellowUpper = cv.Scalar(mods[15]*256, mods[16]*256, mods[17]*256)
	cv.InRangeS(hsv, yellowLower, yellowUpper, mask)
	cv.ShowImage("Yellow:", mask)
    elif (colour == "YWHITE"):
    	blackLower = cv.Scalar(mods[18]*256, mods[19]*256, mods[20]*256)
    	blackUpper = cv.Scalar(mods[21]*256, mods[22]*256, mods[23]*256)
	cv.InRangeS(hsv, blackLower, blackUpper, mask)
	cv.ShowImage("YellowWhite:", mask)            
    elif (colour == "BWHITE"):
	blackLower = cv.Scalar(mods[18]*256, mods[19]*256, mods[20]*256)
	blackUpper = cv.Scalar(mods[21]*256, mods[22]*256, mods[23]*256)
	cv.InRangeS(hsv, blackLower, blackUpper, mask)
	cv.ShowImage("BlueWhite:", mask)        

		
    # Count white pixels to make sure program doesn't crash if it finds nothing
    if (cv.CountNonZero(mask) < 3):
	return ((0, 0), 0)

    # Clean up the image to reduce anymore noise in the binary image
    cv.Smooth(mask, mask, cv.CV_GAUSSIAN, 9, 9, 0, 0)
    convKernel = cv.CreateStructuringElementEx(9, 9, 0, 0, cv.CV_SHAPE_RECT)
    cv.Erode(mask, mask, convKernel, 1)
    cv.Dilate(mask, mask, convKernel, 1)
    
    moments = cv.Moments(mask, 1)
    M00 = cv.GetSpatialMoment(moments,0,0)
    M10 = cv.GetSpatialMoment(moments,1,0)
    M01 = cv.GetSpatialMoment(moments,0,1)
    
    if M00 == 0:
        M00 = 0.01

    center_of_mass = (round(M10/M00), round(M01/M00))

    if (colour == "BLUE" or colour == "YELLOW"):
	return(center_of_mass, find_orientation(mask, center_of_mass))
    else:
	return(center_of_mass, 0)
