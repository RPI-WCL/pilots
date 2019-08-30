import datetime
def f(filename):
    x = open(filename,"rU")
    x_out = open(filename+".pltdata","w+")
    l = x.readlines()
    x_out.write('#a,b,c\n')
    start = 1502434788
    for i in xrange(1,len(l)):
        x_out.write(datetime.datetime.fromtimestamp(start + i).strftime(':%Y-%m-%d %H%M%S000-0400:') + l[i])
f("../data/sum/error.csv")
