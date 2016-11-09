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

list = []
for i in range(20):
    list.append(adc.read())

# start horizontal value
HORIZONTAL = sum(l)/len(l)

# BandWidth
BW = max(l) - min(l)


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

# get on the ramp
inclination = adc.read()
while inclination < HORIZONTAL + BW and inclination > HORIZONTAL - BW:
    forward(2)
    inclination = adc.read()

# indicate it knows it is on ramp
for i in range(2):
    blue.toggle()
    time.sleep(2) 

# BangBang-controller to go forward/backward depending on inclination
stable = False
while not stable:
    inclination = adc.read()
    if inclination < 1.05*HORIZONTAL and inclination > 0.95*HORIZONTAL:
        stable = True
    elif inclination < 0.5*HORIZONTAL:
        forward(2)
    elif inclination < HORIZONTAL:
        forward(1)
    elif inclination > 2*HORIZONTAL:
        backward(2)
    elif inclin	ation > HORIZONTAL:
        backward(1)

# indicate it knows it is stable
for i in range(2):
    green.toggle()
    blue.toggle()
    time.sleep(2) 
