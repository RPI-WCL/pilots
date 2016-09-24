import matplotlib.pyplot as plt
import numpy as np
def open_pilots(filename):
	f = open(filename, 'r')
	fields = map(str.strip, f.readline().strip()[1:].split(','))
	result_dict = {}
	for i in fields:
		result_dict[i] = []
	for i in f:
		line_data = map(float, i.split(':')[2].split(','))
		index = 0
		for data in line_data:
			result_dict[fields[index]].append(data)
			index += 1
	return result_dict
def get_parts(dict_, required):
	result_dict = {}
	for i in dict_:
		result_dict[i] = []
	for i in required:
		for d in dict_:
			result_dict[d].extend(dict_[d][i[0]:i[1]+1])
	return result_dict
def parse_log(log_file, app_name):
	log = open(log_file)
	result_dict = {'signature':[],'number':[]}
	for line in log:
		if len(line) != 0 and line[:len(app_name) + 2] == '['+app_name+']' and line.split()[1] != 'Finished':
			sp = line.split(',')
			if len(sp) > 1:
				signature = sp[0].split(' ', 1)[1]
				result_dict['signature'].append(transig(signature))
				number = float(sp[1].split()[0].split('=')[1])
				result_dict['number'].append(number)
			else:
				signature = ""
				result_dict['signature'].append(255)
				number = float (sp[0].split()[1].split('=')[1])
				result_dict['number'].append(number)
	return result_dict
def transig(w):
	if w == 'under weight':
		return -1
	elif w == 'over weight':
		return 1
	elif w == 'Normal':
		return 0
	elif w == 'Unknown':
		return -2
	else:
		raise Exception()
def response_time(sig, ref_sig, window):
	M = []
	ref_M = []
	prev = sig[0]
	ref_prev = ref_sig[0]
	for i in xrange(len(sig)):
		if sig[i] != prev:
			M.append((i, sig[i]))
		if ref_sig[i] != ref_prev:
			ref_M.append((i,ref_sig[i]))
		prev = sig[i]
		ref_prev = ref_sig[i]
	# difference
	r = []
	for tm in xrange(len(ref_M)):# for each true mode
		index = 0
		# suppose current mode time is t(i)
		# find smallest t'(j) in detected mode change, such that t'(j) >= t(i)
		while (index < len(M) and M[index][0] < ref_M[tm][0]):
			index += 1
		if index >= len(M):
			# if not found, then r(i) = t(i+1) - t(i)			
			if tm + 1 < len(ref_M):
				r.append(ref_M[tm+1][0] - ref_M[tm][0])
			else:
				r.append(0)
			continue;
		# find smallest t'(j) in detected mode change, such that t'(j) >= t(i) and m'(j) == m(i) and t'(j) - t(i) <= window, m' is M, m is ref_M
		while (True):
			if index >= len(M) or M[index][0] - ref_M[tm][0] > window:
				# not found
				index = -1
				break;
			if ref_M[tm][1] == M[index][1]:
				break;
			index += 1
		if index == -1:
			# if not found, then r(i) = t(i+1) - t(i)
			if tm + 1 < len(ref_M):
				r.append(ref_M[tm+1][0] - ref_M[tm][0])
			else:
				r.append(0)
		else:
			# else r(i) = t'(j) - t(i)
			r.append(M[index][0] - ref_M[tm][0])
	return r
def accuracy(sig, ref_sig):
	error = 0
	for i in xrange(len(ref_sig)):
		if sig[i] != ref_sig[i]:
			error += 1
	return (1 - float(error)/len(sig))
def old_accuracy(sig, ref_sig):
	p = 0
	m = []
	prev = ref_sig[0]
	t = 0
	for i in ref_sig:
		if i != prev:
			m.append((i, t))
		t += 1
	for i in m:
		if i[0] == sig[i[1]]:
			p += 1
	return (1.0/len(m)*p)

if __name__ == '__main__':
	cruise_phase = [[5,164],[230,395],[470,688],[780,1108]] # measure cruise phase error only
	import sys
	import math
	test_bayes = False
	if not test_bayes:
		origin_data = open_pilots("../../../data/weightErrorTesting/real.txt")
		error_data = open_pilots("../../../data/weightErrorTesting/error.txt")
		log_data = parse_log("corrected_1.txt", "ATR72")
		log_real_data = parse_log("real_1.txt", "ATR72_real")
		origin_data_cruse = get_parts(origin_data, cruise_phase)
		log_data_cruse = get_parts(log_data, cruise_phase)
		error_data_cruse = get_parts(error_data, cruise_phase)
		log_real_data_cruse = get_parts(log_real_data, cruise_phase)

		real_signature = (log_real_data_cruse['signature'])
		estimated_signature = (log_data_cruse['signature'])
		estimated_weight = log_data_cruse['number']
		real_weight = origin_data_cruse['w_real']
		error = 0
		for i in xrange(len(real_signature)):
			if estimated_signature[i] != real_signature[i]:
				error += 1
		RMSE = 0
		for i in xrange(len(real_weight)):
			RMSE += ((estimated_weight[i] - real_weight[i]))**2
		RMSE = math.sqrt(RMSE/len(real_weight))
		r = response_time(estimated_signature, real_signature, 10000)
		print "Accuracy of the error signature with the learning model = ",(1 - float(error)/len(real_signature))*100,'%'
		print "old acc = ", old_accuracy(estimated_signature, real_signature)
		print "RMSE for learning model on testing data = ", RMSE, 'N'
		print "Maximum response time = ", max(r), 's'
		print "Minimum response time = ", min(r), 's'
		print "Average response time = ", float(sum(r))/len(r), 's'
	else:
		origin_data = open_pilots("testing_origin.pltdata")
		error_data = open_pilots("testing_error.pltdata")
		log_data = parse_log("bayes.txt", "ATR72_bayes")
		log_real_data = parse_log("real.txt", "ATR72_real")
		origin_data_cruse = get_parts(origin_data, cruise_phase)
		log_data_cruse = get_parts(log_data, cruise_phase)
		error_data_cruse = get_parts(error_data, cruise_phase)
		log_real_data_cruse = get_parts(log_real_data, cruise_phase)
		real_signature = log_real_data_cruse['signature']
		translated_sig = []
		real_sig = []

		plt.scatter(np.linspace(0,len(log_data_cruse['number']),len(log_data_cruse['number'])),log_data_cruse['number'], marker='x')
		plt.show()
		for i in xrange(len(log_data_cruse['number'])):
			if log_data_cruse['number'][i] == 0:
				sig = 0
			elif log_data_cruse['number'][i] == 1:
				sig = 1
			elif log_data_cruse['number'][i] == 3:
				sig = -1
			else:
				sig = -2
			translated_sig.append(sig)
		
	#		real_sig.append(real_signature[i])
		plt.scatter(np.linspace(0,len(translated_sig),len(translated_sig)),translated_sig, marker='x')
	#	real_signature = real_sig
		plt.show()

		r = response_time(translated_sig, real_signature, 10)
		print "Accuracy of the error signature = ", accuracy(translated_sig, real_signature)
		print "old accuracy = ", old_accuracy(translated_sig, real_signature)
		print "Maximum response time = ", max(r), 's'
		print "Minimum response time = ", min(r), 's'
		print "Average response time = ", float(sum(r))/len(r), 's'