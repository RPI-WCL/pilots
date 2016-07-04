import datetime
def f(filename):
    x = open(filename,"rU")
    x_out = open(filename+".pltdata","w+")
    l = x.readlines()
    x_out.write('#speed,pressure,temperature,angle,weight\n')
    start_time = 100000
    duration = 1
    current_time = start_time
    for i in xrange(1,len(l)):
        x_out.write(datetime.datetime.fromtimestamp(current_time).strftime(':%Y-%m-%d %H%M%S000-0400:') + l[i])
        current_time += duration
f("thetest_with_error.csv")
