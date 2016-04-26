import json
import os.path
import numpy as np
import math
from pprint import pprint
import time

class Unit(object):
	def __init__(self, name, measure, symbol, coefficient):
		self.name = name; self.measure = measure; self.symbol = symbol; self.co = coefficient;
	def __hash__():
		return hash((self.name, self.measure, self.symbol, self.co))
	def __eq__(self, other):
		return (isinstance(other, self.__class__) and self.__dict__ == other.__dict__)
	def __ne__(self, other):
		return not self.__eq__(other)

class UnitConverter(object):
	def __init__(self,configuration):
		self.FILE = configuration
		self.baseCase = {}
		self.unitList = {}
		self.loadFile()
	def __make_unit(self, jsonobj):
		coefficient = jsonobj['coefficient']
		result = np.poly1d(map(self.__parse, coefficient))
		return Unit(jsonobj['name'], jsonobj['measure'],jsonobj['symbol'], result)
	def __parse(self, number):
		if isinstance(number, basestring):
			return eval(number)
		elif isinstance(number, (int, long, float, complex)):
			return number
		else:
			raise Exception("[Error] coefficient is neither a string or a number")
	def loadFile(self):
		f = open(self.FILE, 'r')
		data = json.load(f)
		f.close()
		for unit in data['baseCase']:
			current = self.__make_unit(unit)
			self.unitList[unit['measure']+unit['name']] = current
			if unit['measure'] in self.baseCase.keys():
				raise Exception("multiple base case (only one allowed)" + str(unit))
			self.baseCase[unit['measure']] = current
		for unit in data['derivedCase']:
			self.unitList[unit['measure']+unit['name']] = self.__make_unit(unit);
	def listUnit(self, measureName):
		return map(lambda(y): self.unitList[y].name, filter(lambda(key): measure_name in key, self.unitList))
	def listMeasure(self, unitName):
		return map(lambda(y): self.unitList[y].measure, filter(lambda(key): unitName in key, self.unitList))
	def convert(self, measure, fromName, toName, value):
		x = time.time()
		try:
			unitA = self.unitList[measure+fromName]
			unitB = self.unitList[measure+toName]
			# convert unitA to baseCase
			unitBase = self.baseCase[unitA.measure]
			x = time.time()
			result = np.polyval(unitA.co, value)
			if len(unitB.co) == 0:
				a = -unitB.co
			else:
				a = [1.0/unitB.co[1], float(-unitB.co[0])/unitB.co[1]]
			x = time.time()
			result = np.polyval(a, result)

			return result
		except KeyError as e:
			print "[Error] requested unit is missing" + str(e)
			return -1

if __name__ == '__main__':
	converter = UnitConverter('.u.json')
	print converter.convert('angle','degree','radian', 1)
	print converter.listMeasure('celsius')
