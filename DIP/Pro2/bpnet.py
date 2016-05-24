import random
import math
import json
import os

def rand(a, b):
    return (b-a)*random.random()+a


def makeMatrix(cols, rows, lower, upper):
    wm = []
    for i in range(cols):
        wm_col = []
        for j in range(rows):
            wm_col.append(rand(lower, upper))
        wm.append(wm_col)
    return wm
def sigmoid(x):
    return 1/(1+math.exp(-x))
    #return math.tanh(x)
def dsigmoid(x): 
    return x*(1.0-x)
    #return 1.0 - x**2


class bpnet:
    def __init__(self, ni, nhl, nhmax, no, N=0.5, lower=-0.2, upper=0.2):
        """
        ni is the number of input layer
        nhl is the number of hidden layer
        nhmax is the number of each hidden layer's nodes
        no is the number of output layer
        """
        self.N = N
        self.ni = ni+1
        self.nhl = int(round(nhl))
        self.nhmax = int(round(nhmax))
        self.no = no

        self.layer = []
        # input layer's value
        self.layer.append([1.0]*self.ni)
        nls = [nhmax]*self.nhl +[self.no]
        # hidden and output value
        for num in nls:
            self.layer.append([0.0]*num)

        # weight matrixs 
        self.wms = []
        colsnum = self.ni

        for rowsnum in nls:
            self.wms.append(makeMatrix(colsnum, rowsnum, lower, upper))
            colsnum = rowsnum

    def setwms(self, wms):
        self.wms = wms

    def update(self, inputs):
        if len(inputs) != self.ni-1:
            raise ValueError("Check for the number of input layer")

        # input layer
        for index in range(self.ni-1):
            self.layer[0][index] = inputs[index]

        # hidden and output layer
        inputs = self.layer[0]
        for k in range(1, len(self.layer)):
            for index in range(len(self.layer[k])):
                net = 0.0
                for x in range(len(inputs)):
                    net = net + inputs[x] * self.wms[k-1][x][index]
                self.layer[k][index] = sigmoid(net)
            inputs = self.layer[k]

        #print('layer->', self.layer)
        

    def backpropagrate(self, results, N):
        if len(results) != self.no:
            raise ValueError('Check for the number of output layer')

        deltas = []
        curdeltas = [0.0]*self.no
        for index in range(self.no):
            delta = results[index] - self.layer[-1][index]
            curdeltas[index] = dsigmoid(self.layer[-1][index]) * delta
        #print('output layer deltas ->', curdeltas)

        temp = list(range(len(self.layer)-1))
        temp.reverse()
        lastdeltas = curdeltas
        # calculate delta of each layer
        for k in temp:
            deltas.append(curdeltas)
            lastdeltas = curdeltas
            curdeltas = [0.0] * len(self.layer[k])
            for i in range(len(self.layer[k])):
                delta = 0.0
                for j in range(len(lastdeltas)):
                    delta = delta + self.wms[k][i][j] * lastdeltas[j]
                curdeltas[i] = dsigmoid(self.layer[k][i])*delta

        #print('deltas -> ', deltas)

        deltas.reverse()
        for k in temp:
            for i in range(len(self.layer[k])):
                for j in range(len(self.layer[k+1])):
                    delta = deltas[k][j] * self.layer[k][i]
                    self.wms[k][i][j] = self.wms[k][i][j] + N*delta
        #print('wms ->', self.wms)
        

    def train(self, data, delta, iter=100000000):
        """
        N is learn operator
        iter is the trainning number
        """
        for i in range(iter):
            error = 0.0
            for record in data:
                inputs = record[0]
                results = record[1]
                self.update(inputs)
                self.backpropagrate(results, self.N)
                error = error+self.error(results)
            if error < delta:
                break

    def error(self, results):
        error = 0.0 
        """
        for k in range(len(results)):
            error = error + 0.5*(results[k]-self.layer[-1][k])**2
        """
        error = 0.5*(bin2dec(results) - bin2dec(self.layer[-1]))**2
        return error

    def test(self, data):
        output = open('results.txt', 'w')
        error = 0.0
        correct = 0
        for record in data:
            self.update(record[0])
            error += self.error(record[1])
            results =[int(round(key))for key in self.layer[-1]]
            # results = self.layer[-1]
            context = '['
            for i in range(len(record[0])):
                if(i < len(record[0])-1):
                    context += str(record[0][i])+', '
                else:
                    context += str(record[0][i])+']'
            to = bin2dec(results)
            if to == bin2dec(record[1]):
                correct += 1
            context += " -> ["+str(to)+']\n'
            output.write(context)
        #output.write('error: '+str(error))
        print('accuracy: ', correct/len(data))
        output.write('correct: '+str(correct/len(data)))


    def printy(self):
        print('layer -> ', self.layer)
        print('wms -> ', self.wms)

    def save(self, path):
        config = {}
        config['ni'] = self.ni
        config['no'] = self.no
        config['nhl'] = self.nhl
        config['nhmax'] = self.nhmax
        config['N'] = self.N
        config['wms'] = self.wms
        output = open(path, 'w')
        output.write(json.dumps(config))
    
    @staticmethod
    def load(path):
        jsonstr = ''
        for line in open(path):
            jsonstr += line
        config = json.loads(jsonstr)
        ni = config['ni'] -1
        no = config['no']
        nhl = config['nhl']
        nhmax = config['nhmax']
        N = config['N']
        wms = config['wms']
        bp = bpnet(ni, nhl, nhmax, no, N)
        bp.setwms(wms)
        return bp

        

def loaddata(path, fixlen):
    data = []
    # line = 0,0,0,...0|10
    for line in open(path):
        try:
            inputs, results = line.split('|')
            inputs = [float(key.strip()) for key in inputs.split(',')]
            results = dec2bin(results)
            results = results+[0]*(fixlen - len(results))
            record = []
            record.append(inputs)
            record.append(results)
            data.append(record)
        except Exception:
            pass

    return data

def bin2dec(numbers):
    sum = 0
    for i in range(len(numbers)):
        sum += numbers[i]*math.pow(2, i)
    return int(round(sum))
def dec2bin(str_number):
    number = int(str_number.strip())
    results = []
    while True:
        if number == 0: break
        number, rem = divmod(number, 2)
        results.append(rem)
    return results

def preprocess(path):
    lines = []
    for line in open(path):
        line = line.replace('[', '')
        line = line.replace(']', '')
        lines.extend(line.split('\r'))

    writer = open(path, 'w')
    for line in lines:
        line = line.replace('[', '')
        line = line.replace(']', '')
        line = line.strip()
        writer.write(line)

def demo():
    # pre-process train and test data
    # preprocess('Train.txt')
    # preprocess('Test.txt')

    configpath = 'config.json'
    fixlen = 4
    train_path = 'Train.txt'
    train_data = loaddata(train_path, fixlen)
    test_path = 'Test.txt'
    test_data = loaddata(test_path, fixlen)
    if os.path.exists(configpath):
        bp = bpnet.load(configpath)
        bp.test(test_data)
    else:
        ni = len(train_data[0][0])
        nhmax = 15
        nhl = 2
        no = fixlen
        bp = bpnet(ni, nhl, nhmax, no)
        bp.train(train_data, 4.1)
        # bp.save(configpath)
        bp.test(test_data)
        

demo()
