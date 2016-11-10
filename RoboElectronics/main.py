import time
from pyb import LED, Pin, ADC

#### initialize pins and LED's ####

# output pins to control H-Bridge
pin1 = pyb.Pin('X1', pyb.Pin.OUT_PP)
pin2 = pyb.Pin('X2', pyb.Pin.OUT_PP) 

# input pin for accelerometer
adc = ADC(Pin('X12'))

# status leds
red, green, yellow, blue = LED(1), LED(2), LED(3), LED(4)

# yellow on -> stand by
# red on -> motor backward
# green on -> motor frontward
# blue on -> inclination stabalized

#### Constants ####

# adc value of zero inclination
HORIZONTAL = 10

# allowed bandwidth for bangbang controller
BW = 1

# adjust HORIZONTAL and BW to runtime adc values
def calibrate():
    global HORIZONTAL
    global BW

    list = []
    for i in range(20):
        list.append(adc.read())

    # start horizontal value
    HORIZONTAL = sum(list)/len(list)

    # BandWidth
    BW = max(list) - min(list)

# move until inclination changes
def getOnRamp():
    global HORIZONTAL
    global BW

    inclination = adc.read()
    while inclination < HORIZONTAL + BW and inclination > HORIZONTAL - BW:
        forward(2)
        inclination = adc.read()

# BangBang-controller to go forward/backward depending on inclination
def bb():
    global HORIZONTAL
    global BW

    stable = False
    inclination = adc.read()
    while not stable:
        if inclination < HORIZONTAL+BW and inclination > HORIZONTAL-BW:
            stable = True
        elif inclination < HORIZONTAL - 2*BW:
            forward(2)
        elif inclination < HORIZONTAL - BW:
            forward(1)
        elif inclination > HORIZONTAL + 2*BW:
            backward(2)
        elif inclination > HORIZONTAL + BW:
            backward(1)
        
        time.sleep(1)

# motor forward for t seconds (green LED)
def forward(t):
    green.on()
    pin1.low()
    pin2.high()
    time.sleep(t)
    pin1.high()
    pin2.high()
    green.off()

# motor backward for t seconds (green LED)
def backward(t):
    red.on()
    pin1.high()
    pin2.low()
    time.sleep(t)
    pin1.high()
    pin2.high()
    red.off()

#### Entry point #### 

# startup, no action until all lights turn off                                            
yellow.on()
time.sleep(2)
yellow.off()

# calibrate
calibrate()

# indicate it is calibrated
yellow.on()
time.sleep(2)
yellow.off()

# get on ramp
getOnRamp()

yellow.on()
time.sleep(2)
yellow.off()

# indicate it knows it is on ramp
blue.on()
time.sleep(2)
blue.off()

# stabilize
bb()

# indicate it knows it is stable
green.on()
blue.on()
time.sleep(2)
green.off()
gblue.off()
