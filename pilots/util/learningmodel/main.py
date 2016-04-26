import UnitConverter as uc
import DataBase as db
import LeastSquare as lm
from LearnModel import getCruisePhase # cruise phase is just for demonstration
import json
import sys

def main():
	if len(sys.argv) != 2:
		print "Usage\npython [this file] [configuration file]"
		return;
	testdata = None
	tdata = None
	tviewer = None
	testviewer = None
	tphase = None
	testphase = None
	print "running file: " + sys.argv[0]
	print "configuration file: " + sys.argv[1]
	f = json.load(open(sys.argv[1],'r'))
	trainFile = f["Training Data Files"]
	trainConfig = f["Training Data Inital Configuration"]
	trainAConfig = f["Training Data Unit Conversion Configuration"]
	trainVisualization = f["Training Data Visualization"]
	testFile = f["Testing Data Files"]
	testConf = f["Testing Data Inital Configuration"]
	testAConf = f["Testing Data Unit Conversion Configuration"]
	testVisualization = f["Testing Data Visualization"]
	XF = f["X Fields"]
	YF = f["Y Fields"]
	testresultVisualization = f["Testing Data Result Visualization"]
	trainresultVisualization = f["Training Data Result Visualization"]
	timeField = f["Cruise Phase Time Field"]
	speedField = f["Cruise Phase Speed Field"]
	minsize = f["Cruise Phase Minimum Length Of Time"]
	minspeed = f["Cruise Phase Minimum Speed"]
	acceleration = f["Cruise Phase Maximum Acceleration"]
	emptyWeight = f["Plane Empty Weight"]
	converter = uc.UnitConverter(f["Unit Converter File"])
	autocruise = f["Automatic Cruise Phase"]
	print "loading training database using initial Configuration <" + trainConfig + ">..."
	tdata = db.DataBase(trainConfig, True)
	tphase = []
	base = 0
	for item in trainFile:
		tdata.addData(db.DataParser(item["name"], item["deliminator"]))
		print item["name"] + ",current database size = " + str(tdata.minClusterSize())
		tphase.extend(map(lambda(x): [x[0]+base,x[1]+base], item["cruise phase"]))
		base = tdata.minClusterSize()
	print "unit conversion using conversion Configuration <" + trainAConfig + ">..."
	tdata.loadConfiguration(trainAConfig, converter)
	print "finished loading " + str(len(trainFile)) + " files"
	if trainVisualization:
		tviewer = db.DataBaseViewer(tdata)
		tviewer.visualize()
	if len(testFile) > 0:
		print "loading testing database using initial Configuration <" + testConf + ">..."
		testdata = db.DataBase(testConf, True)
		testphase = []
		base = 0
		for item in testFile:
			testdata.addData(db.DataParser(item["name"], item["deliminator"]))
			testphase.extend(map(lambda(x): [x[0]+base,x[1]+base], item["cruise phase"]))
			base = testdata.minClusterSize()
			print item["name"] + ",current database size = " + str(testdata.minClusterSize())
		print "unit conversion using conversion Configuration <" + testAConf + ">..."
		testdata.loadConfiguration(testAConf, converter)
		print "finished loading " + str(len(testFile)) + " files"
		if testVisualization:
			testviewer = db.DataBaseViewer(testdata)
			testviewer.visualize()
	print "accessing cruise phase..."
	trans = lm.SpecialLinearTransformation(XF,YF)
	trans.setWeight(emptyWeight)
	model = lm.LeastSquare(trans)
	if autocruise:
		print "trigger auto cruise finder"
		tphase = getCruisePhase(tdata, timeField, speedField, acceleration, minspeed, minsize)
	print str(len(tphase)) + " phases are loaded"
	if len(tphase) == 0:
		raise Exception("No Cruise phase available, aborted")
	for p in tphase:
		model.readData(tdata, p[0], p[1])
	print "done\nTraining Data result:"
	result = model.train()
	print result
	print "in sample error:"
	print model.inSampleError(trainresultVisualization)
	print "out sample error:"
	model.resetData() # not a good implementation
	if not testdata is None:
		if autocruise:
			print "trigger auto cruise finder"
			testphase = getCruisePhase(testdata, timeField, speedField, acceleration, minspeed, minsize)
		print str(len(testphase)) + " phases are loaded"
		if len(testphase) == 0:
			raise Exception("No Cruise phase available, aborted")
		for p in testphase:
			model.readData(testdata, p[0],p[1])
		print model.inSampleError(testresultVisualization) # not a good implementation
	print "done"

if __name__ == '__main__':
	main()
