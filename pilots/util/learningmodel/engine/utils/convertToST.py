import datetime
def f(filename):
    x = open(filename,"rU")
    x_out = open(filename+".pltdata","w+")
    l = x.readlines()
    x_out.write('#a,w,p,t,v,cruise,cl\n')
    start = 1469162916
    for i in xrange(1,len(l)):
        x_out.write(datetime.datetime.fromtimestamp(start + i).strftime(':%Y-%m-%d %H%M%S000-0400:') + l[i])
f("20160802_required_right_unit.csv")
