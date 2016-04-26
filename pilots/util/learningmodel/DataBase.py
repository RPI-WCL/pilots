import matplotlib.pyplot as plt
import numpy as np
import math
import sys
import json
import uuid
import time
import UnitConverter
class DataParser(object):
	def __init__(self, fileName, deliminator):
		self.fileName = fileName
		self.deliminator = deliminator
	def parse(self, dataBase):
		f = open(self.fileName,'rU')
		lines = map(lambda(string): string.replace('\n',''), f.readlines());
		name = []
		nameRead = False
		for i in lines:
			if (len(i) == 0):
				continue
			if not nameRead:
				name = map(lambda(string): string.strip(), i.split(self.deliminator)[:-1])
				nameRead = True
				continue
			current = map(float, map(lambda(string): string.strip(), i.split(self.deliminator)[:-1]))
			if (len(current) != len(name)):
				raise Exception("Error Name/Data mismatch" + str(current) + str(name))
			for index in xrange(0,len(current)):
				dataBase.insert(clusterName = name[index], data = current[index])
		f.close()

class DataBaseViewer(object):
	def __init__(self, dataBase):
		self.dataBase = dataBase
	def visualize(self):
		plt.close("all")
		names = self.dataBase.clusterNames()
		names.sort()
		dictSize = self.dataBase.numofClusters()
		size = self.dataBase.minClusterSize()
		cell = int(math.ceil(math.sqrt(dictSize)))
		f, graphs = plt.subplots(cell, cell)
		index = 0
		for i in xrange(0,cell):
			for j in xrange(0,cell):
				if index >= dictSize:
					break;
				graphs[i][j].set_title(names[index])
				graphs[i][j].plot(np.linspace(0, self.dataBase.clusterSize(names[index]),self.dataBase.clusterSize(names[index]) ), np.array(self.dataBase.select(names[index])))
				index += 1
		f.subplots_adjust(hspace=0.5)
		plt.show()

class DataCluster(object):
	def __init__(self, name, unit=None, type_=None, size=0, key=None):
		if name is None:
			self.name = str(uuid.uuid4().get_hex().upper()[0:6])
		else:
			self.name = name
		if unit is None:
			self.unit = ""
		else:
			self.unit = unit
		if type_ is None:
			self.type = ""
		else:
			self.type = type_
		self.size = size
		if key is None:
			self.key = str(uuid.uuid4().get_hex().upper()[0:6])
		else:
			self.key = key

class DataBase(object):
	# db = [key=Number, value=List]
	# clusters = [key=name, value=DataCluster object]
	# ignoreCase = bool
	def __init__(self, configuration, ignoreCase):
		self.db = {}
		self.clusters = {} # store metadata of a cluster
		if ignoreCase is None:
			self.ignoreCase = False
		else:
			self.ignoreCase = ignoreCase
		self.nextVaild = 0
		self.loadConfiguration(configuration)

	def addData(self, parser):
		parser.parse(self)

	def loadConfiguration(self, configuration, unitConverter=None):
		columns = json.load(open(configuration, 'r'))["columns"]
		for item in columns:
			result = []
			for i in self.clusters.keys():
				if (self.clusters[i].key == item['key']):
					result.append(self.clusters[i])
			if len(result) > 1:
				raise Exception("Multiple key error (only one is allowed)")
			elif len(result) == 0:
				if item['name'] in self.clusters.keys():
					raise Exception("Name Collision: only one name per key")
				else:
					if self.ignoreCase:
						item['name'] = item['name'].lower()
						item['unit'] = item['unit'].lower()
						item['type'] = item['type'].lower()
					self.clusters[item['name']] = DataCluster(item['name'], item['unit'], item['type'], 0, item['key'])
					self.db[item['key']] = []
			else:
				currentCluster = result[0]
				if self.ignoreCase:
					item['name'] = item['name'].lower()
					item['unit'] = item['unit'].lower()
					item['type'] = item['type'].lower()
				new = DataCluster(item['name'], item['unit'], item['type'], currentCluster.size, currentCluster.key)
				if currentCluster.type != new.type:
					raise Exception("Type mismatch")
				del self.clusters[currentCluster.name]
				self.clusters[item['name']] = new
				self.db[new.key] = map(lambda(x): unitConverter.convert(currentCluster.type, currentCluster.unit, new.unit, x), self.db[new.key])
		return True

	def contains(self,clusterName):
		if self.ignoreCase:
			clusterName = clusterName.lower()
		return clusterName in self.clusters.keys()
	def select(self, clusterName, start=None, end=None):
		if self.ignoreCase:
			clusterName = clusterName.lower()
		if self.contains(clusterName):
			if start in range(0, self.clusters[clusterName].size) and end in range(0,self.clusters[clusterName].size):
				return self.db[self.clusters[clusterName].key][start: end]
			elif not start is None and end is None and start in range(0, self.clusters[clusterName].size):
				return self.db[self.clusters[clusterName].key][start : end]
			elif start is None and not end is None and end in range(0, self.clusters[clusterName].size):
				return self.db[self.clusters[clusterName].key][start : end]
			elif start is None and end is None:
				return self.db[self.clusters[clusterName].key][:]

		return []
	def drop(self, clusterName):
		# drop a cluster
		if self.ignoreCase:
			clusterName = clusterName.lower()
		if self.contains(clusterName):
			del self.db[self.clusters[clusterName].key]
			del self.clusters[clusterName]

	def update(self, clusterName, offset, newValue):
		# update a value in some cluster
		if self.ignoreCase:
			clusterName = clusterName.lower()
		if self.contains(clusterName):
			if offset in range(0, self.clusters[clusterName].size):
				self.db[self.clusters[clusterName].key][offset] = newValue
				return True
		return False

	def delete(self, clusterName, offset):
		if self.ignoreCase:
			clusterName = clusterName.lower()
		if self.contains(clusterName):
			if offset in range(0, self.clusters[clusterName].size):
				key = self.clusters[clusterName].key
				return self.db[key].remove(self.db[key][offset])
		return False

	def insert(self, clusterName, data):
		if self.ignoreCase:
			clusterName = clusterName.lower()
		if self.contains(clusterName):
			self.clusters[clusterName].size += 1
			self.db[self.clusters[clusterName].key].append(data)
		else:
			return False
		return True

	def clusterSize(self, clusterName):
		if self.ignoreCase:
			clusterName = clusterName.lower()
		if self.contains(clusterName):
			return self.clusters[clusterName].size
		return 0

	def clusterNames(self):
		return map(lambda(x): self.clusters[x].name, self.clusters)

	def numofClusters(self):
		return len(self.clusters)

	def getClusterProperty(self, clusterName):
		if self.ignoreCase:
			clusterName = clusterName.lower()
		return self.clusters[clusterName]
	def getAllClustersSize(self):
		return map(lambda(x): self.clusters[x].size, self.clusters)
	def minClusterSize(self):
		return min(map(lambda(x): self.clusters[x].size, self.clusters))
if __name__ == '__main__':
	x = time.time()
	db = DataBase('initConfig.json', True)
	viewer = DataBaseViewer(db)
	db.addData(DataParser('../data/Low Power King Air.txt', '|'))
	db.loadConfiguration('afterConfig.json', UnitConverter.UnitConverter('unit.json'))
	viewer.visualize()
