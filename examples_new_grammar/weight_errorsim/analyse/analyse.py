import matplotlib.pyplot as plt
import numpy as np
import csv 

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
def cal_response_time(true_change, detected_change, maximum_time):
	r = []
	for i in xrange(len(true_change)):
		t = true_change[i][0]
		m = true_change[i][1]
		detected_change_index = -1
		for j in xrange(len(detected_change)):
			if detected_change[j][0] >= t and detected_change[j][1] == m:
				detected_change_index = j
				break;
		if detected_change_index == -1:
			if i + 1 > len(true_change) - 1:
				r.append(maximum_time - true_change[i][0])
			else:
				r.append(true_change[i+1][0] - true_change[i][0])
		else:
			r.append(detected_change[detected_change_index][0] - true_change[i][0])
	return r

def response_time(sig, ref_sig):
	mode = []
	true_mode = []
	prev = sig[0]
	ref_prev = ref_sig[0]
	# calculate mode change points
	for i in xrange(len(sig)):
		if sig[i] != prev:
			mode.append((i, sig[i]))
		if ref_sig[i] != ref_prev:
			true_mode.append((i,ref_sig[i]))
		prev = sig[i]
		ref_prev = ref_sig[i]
	return cal_response_time(true_mode, mode, len(sig))

def compute_error(sig, ref_sig):
	error = 0
	for i in xrange(len(ref_sig)):
		if sig[i] != ref_sig[i]:
			error += 1
	return error

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
	cruise_phase_all = [[5,164],[230,395],[470,688],[780,1108]] # measure cruise phase error only

	import sys
	import math
	test_bayes = False
	if not test_bayes:
		origin_data = open_pilots("../../../data/weightErrorTesting/real.txt")
		error_data = open_pilots("../../../data/weightErrorTesting/error.txt")
		log_data = parse_log("corrected_0.99.txt", "ATR72")
		# save result to csv file
		r = []
		r.append(error_data['w']); r.append(log_data['number']); r.append(origin_data['w_real']);
		np.savetxt("corrected_0.99.csv", np.matrix(r).T, delimiter=',', header="measured, estimated, real")
		
		log_real_data = parse_log("real_0.99.txt", "ATR72_real")
		res = []
		data_size = 0; data_error = 0
		for cruise_phase in cruise_phase_all:
			cruise_phase = [cruise_phase]
			origin_data_cruse = get_parts(origin_data, cruise_phase)
			log_data_cruse = get_parts(log_data, cruise_phase)
			error_data_cruse = get_parts(error_data, cruise_phase)
			log_real_data_cruse = get_parts(log_real_data, cruise_phase)

			real_signature = (log_real_data_cruse['signature'])
			estimated_signature = (log_data_cruse['signature'])
			estimated_weight = log_data_cruse['number']
			real_weight = origin_data_cruse['w_real']
			RMSE = 0
			size = 0
			for i in xrange(len(real_weight)):
				if estimated_signature[i] != 0: # if the error is fixed then we count the rmse
					RMSE += ((estimated_weight[i] - real_weight[i]))**2
					size += 1
			RMSE = math.sqrt(RMSE/size)
			r = response_time(estimated_signature, real_signature)
			error = compute_error(estimated_signature, real_signature)
			print "Accuracy of the error signature with the learning model = ",(1 - float(error)/len(real_signature))*100,'%'
			print "old acc = ", old_accuracy(estimated_signature, real_signature)
			print "RMSE for learning model on testing data = ", RMSE, 'N'
			print "Maximum response time = ", max(r), 's'
			print "Minimum response time = ", min(r), 's'
			print "Average response time = ", float(sum(r))/len(r), 's'
			res.extend(r)
			data_error += error; data_size += len(real_signature)
		print '----------'
		print "Overall Accuracy = ", (1 - float(data_error)/data_size), '%'
		print "Average overall response time = ", float(sum(res))/len(res), 's'
		print 'Maximum overall response time = ', max(res), 's'
		print 'Minimum overall response time = ', min(res), 's'
		print '----end---'
	else:
		origin_data = open_pilots("../../../data/weightErrorTesting/real.txt")
		error_data = open_pilots("../../../data/weightErrorTesting/error.txt")
		log_data = parse_log("bayes_0.99.txt", "ATR72_bayes")
		log_real_data = parse_log("real_0.99.txt", "ATR72_real")
		# save result to csv file
		res = []
		data_size = 0
		data_error = 0
		r = []
		r.append(error_data['w']); r.append(log_data['number']); r.append(origin_data['w_real']);
		np.savetxt("bayes_0.99.csv", np.matrix(r).T, delimiter=',', header="measured, estimated, real")
		for cruise_phase in cruise_phase_all:
			cruise_phase = [cruise_phase]
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
			r = response_time(translated_sig, real_signature)
			error = compute_error(translated_sig, real_signature)
			print "Accuracy of the error signature with the learning model = ",(1 - float(error)/len(real_signature))*100,'%'
			print "old accuracy = ", old_accuracy(translated_sig, real_signature)
			print "Maximum response time = ", max(r), 's'
			print "Minimum response time = ", min(r), 's'
			print "Average response time = ", float(sum(r))/len(r), 's'
			data_error += error; data_size += len(real_signature)
			res.extend(r)
		print '----------'
		print "Overall Accuracy = ", (1 - float(data_error)/data_size), '%'
		print "Average overall response time = ", float(sum(res))/len(res), 's'
		print 'Maximum overall response time = ', max(res), 's'
		print 'Minimum overall response time = ', min(res), 's'
		print '----end---'